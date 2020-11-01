package com.adambarreiro.monitor.capture.file;

import com.adambarreiro.monitor.capture.FileObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

public class FileObserverTest {

	private static final String FILE_NAME = "fooTest.txt";
	private String logPath;

	@BeforeEach
	public void setup() {
		logPath = Objects.requireNonNull(getClass().getClassLoader().getResource(FILE_NAME)).getPath();
	}

	@Test
	@DisplayName("A created and valid file observer emits the file contents")
	public void aCreatedAndValidFileObserverEmitsTheFileContentsTest() throws IOException {
		final StringBuilder builder = new StringBuilder();
		FileObserver fileObserver = FileObserver.of(logPath);
		fileObserver.observe(line -> {
			builder.append(line);
			fileObserver.stop();
		});
		Assertions.assertEquals("Hello World", builder.toString());
	}

}
