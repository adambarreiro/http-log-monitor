package com.adambarreiro.monitor.process.log;

import com.adambarreiro.monitor.process.log.vo.LogData;
import com.adambarreiro.monitor.stats.Stats;

import java.util.Optional;

/**
 * Transforms a raw piece of data in a specific logging format to the {@link LogData} value object, to be able
 * to be used in a {@link Stats} handler.
 */
public interface LogProcessor {

	Optional<LogData> process(String logEntry);
}
