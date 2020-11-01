package com.adambarreiro.monitor.capture;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class allows the main thread to subscribe to a text file and retrieve its contents indefinitely. It polls the
 * file every second and processes every line in a separated thread, managed by the {@link Schedulers} single
 * thread pool.
 */
public final class FileObserver implements Observer {

	private static final String READ_MODE = "r";

	private final AtomicLong offset;
	private final long idleTimeMillis;

	private RandomAccessFile observedFile;
	private boolean initialized;

	private FileObserver(RandomAccessFile observedFile, long offset, long idleTimeMillis) {
		this.observedFile = observedFile;
		this.offset = new AtomicLong(offset);
		this.idleTimeMillis = idleTimeMillis;
		this.initialized = false;
	}

	/**
	 * Creates a file observer that is ready to be subscribed to the given file.
	 *
	 * @param pathToFile An existent plain text file in your disk.
	 * @return An instance of this class
	 *
	 * @throws FileNotFoundException If the file does not exist in the given path
	 */
	public static FileObserver of(final String pathToFile) throws FileNotFoundException {
		return new FileObserver(
				new RandomAccessFile(new File(pathToFile), READ_MODE),
				0L,
				1000L);
	}

	/**
	 * Subscribes to the file and starts polling its contents indefinitely. Warning, this subscription process
	 * runs in the main thread.
	 *
	 * @param onNext a function called on every processed line of the file. It runs in a separated thread pool.
	 */
	public void observe(Consumer<? super Object> onNext) {
		if (initialized || Objects.isNull(observedFile)) {
			return;
		}
		initialized = true;
		Flowable.create(subscriber -> {
			while (initialized) {
				try {
					if (this.isObservedFileRotated()) {
						this.resetOffsetOfObservedFile();
					}
					if (this.isNewDataAvailable()) {
						this.processData(subscriber);
					}
					Thread.sleep(this.idleTimeMillis);
				} catch (Exception e) {
					subscriber.onError(e);
				}
			}
		}, BackpressureStrategy.BUFFER)
				.observeOn(Schedulers.single())
				.subscribe(onNext);
	}

	/**
	 * Stops the subscription process and makes everything ready to consume any other file or the same file again.
	 *
	 * @throws IOException if the file cannot be closed for any reason.
	 */
	public void stop() throws IOException {
		initialized = false;
		if (Objects.nonNull(observedFile)) {
			observedFile.close();
			observedFile = null;
		}
	}

	/**
	 * Updates the current file offset to start reading from the last position, retrieves new lines and
	 * sends to the subscriber function.
	 *
	 * @param subscriber a function that will handle the read line.
	 * @throws IOException if something goes wrong with the current file.
	 */
	private void processData(FlowableEmitter<Object> subscriber) throws IOException {
		this.observedFile.seek( offset.get() );
		String line = this.observedFile.readLine();
		while( line != null ) {
			subscriber.onNext(line);
			line = this.observedFile.readLine();
		}
		offset.set(this.observedFile.getFilePointer());
	}

	/**
	 * Returns true if the file has been emptied. False otherwise.
	 *
	 * @return true if the file has been emptied. False otherwise.
	 * @throws IOException if something goes wrong with the current file.
	 */
	private boolean isObservedFileRotated() throws IOException {
		return this.observedFile.length() < this.offset.get();
	}

	/**
	 * Returns true if the file has new data available. False otherwise.
	 *
	 * @return true if the file has new data available. False otherwise.
	 * @throws IOException if something goes wrong with the current file.
	 */
	private boolean isNewDataAvailable() throws IOException {
		return this.observedFile.length() > this.offset.get();
	}

	/**
	 * Resets the file offset to the beginning.
	 *
	 * @throws IOException if something goes wrong with the current file.
	 */
	private void resetOffsetOfObservedFile() throws IOException {
		this.observedFile.seek(0L);
		offset.set(0L);
	}
}
