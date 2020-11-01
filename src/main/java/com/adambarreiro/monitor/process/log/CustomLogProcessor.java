package com.adambarreiro.monitor.process.log;

import com.adambarreiro.monitor.process.log.vo.LogData;
import java.util.Optional;

public final class CustomLogProcessor implements LogProcessor {

	// TODO: We can implement other logging formats

	@Override
	public Optional<LogData> process(String logEntry) {
		return Optional.empty();
	}
}
