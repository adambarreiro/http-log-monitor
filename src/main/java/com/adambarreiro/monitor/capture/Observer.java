package com.adambarreiro.monitor.capture;


import io.reactivex.rxjava3.functions.Consumer;

public interface Observer {

	/**
	 * Subscribes to any source and starts polling its data.
	 *
	 * @param onNext a function called on every processed piece of data.
	 */
	void observe(Consumer<? super Object> onNext);

}
