package com.adambarreiro.monitor.process;


import com.adambarreiro.monitor.process.log.CommonLogFormatLogProcessor;
import com.adambarreiro.monitor.process.log.LogProcessor;
import com.adambarreiro.monitor.process.log.vo.LogData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class CommonLogFormatLogProcessorTest {

	public static final String LOG_OK = "127.0.0.1 - james [09/May/2018:16:00:39 +0000] \"GET /report HTTP/1.0\" 200 123";
	private LogProcessor logProcessor;

	@BeforeEach
	public void setup() {
		logProcessor = new CommonLogFormatLogProcessor();
	}

	@Test
	@DisplayName("No log is processed if it's not in Common Log Format")
	public void noLogIsProcessedIfItsNotInCLFTest() {
		Optional<LogData> data = logProcessor.process("ksdhgakgdshagsdjsad");
		Assertions.assertTrue(data.isEmpty());
	}

	@Test
	@DisplayName("The client is obtained from the log when processed")
	public void clientIsObtainedWhenLogIsProcessedTest() {
		Optional<LogData> data = logProcessor.process(LOG_OK);
		Assertions.assertEquals("127.0.0.1", data.orElseThrow().getClient());
	}

	@Test
	@DisplayName("The identity is obtained from the log when processed")
	public void identityIsObtainedWhenLogIsProcessedTest() {
		Optional<LogData> data = logProcessor.process(LOG_OK);
		Assertions.assertEquals("-", data.orElseThrow().getIdentity());
	}

	@Test
	@DisplayName("The user ID is obtained from the log when processed")
	public void userIdIsObtainedWhenLogIsProcessedTest() {
		Optional<LogData> data = logProcessor.process(LOG_OK);
		Assertions.assertEquals("james", data.orElseThrow().getUserId());
	}

	@Test
	@DisplayName("The timestamp is obtained from the log when processed")
	public void timestampIsObtainedWhenLogIsProcessedTest() {
		Optional<LogData> data = logProcessor.process(LOG_OK);
		Assertions.assertEquals("2018-05-09T14:00:39Z", data.orElseThrow().getTimestamp().toString());
	}

	@Test
	@DisplayName("The request verb is obtained from the log when processed")
	public void requestVerbIsObtainedWhenLogIsProcessedTest() {
		Optional<LogData> data = logProcessor.process(LOG_OK);
		Assertions.assertEquals("GET", data.orElseThrow().getRequest().getVerb());
	}

	@Test
	@DisplayName("The request path is obtained from the log when processed")
	public void requestPathIsObtainedWhenLogIsProcessedTest() {
		Optional<LogData> data = logProcessor.process(LOG_OK);
		Assertions.assertEquals("/report", data.orElseThrow().getRequest().getPath());
	}

	@Test
	@DisplayName("The request protocol version is obtained from the log when processed")
	public void requestVersionIsObtainedWhenLogIsProcessedTest() {
		Optional<LogData> data = logProcessor.process(LOG_OK);
		Assertions.assertEquals("HTTP/1.0", data.orElseThrow().getRequest().getVersion());
	}

	@Test
	@DisplayName("The status code is obtained from the log when processed")
	public void statusCodeIsObtainedWhenLogIsProcessedTest() {
		Optional<LogData> data = logProcessor.process(LOG_OK);
		Assertions.assertEquals(200, data.orElseThrow().getStatusCode());
	}

	@Test
	@DisplayName("The size is obtained from the log when processed")
	public void sizeIsObtainedWhenLogIsProcessedTest() {
		Optional<LogData> data = logProcessor.process(LOG_OK);
		Assertions.assertEquals(123, data.orElseThrow().getSize());
	}
}
