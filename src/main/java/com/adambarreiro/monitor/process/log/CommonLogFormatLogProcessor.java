package com.adambarreiro.monitor.process.log;

import com.adambarreiro.monitor.process.log.vo.LogData;
import com.adambarreiro.monitor.stats.Stats;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Transforms a raw piece of data in the Common Log Format (https://en.wikipedia.org/wiki/Common_Log_Format)
 * to the {@link LogData} value object, to be able to be used in a {@link Stats} handler.
 */
public final class CommonLogFormatLogProcessor implements LogProcessor {

	public static final String STRFTIME_FORMAT = "dd/MMM/yyyy:HH:mm:ss Z";

	private static final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(STRFTIME_FORMAT);
	private static final Pattern CLF_PATTERN = Pattern.compile("^(.+) (.+) (.+) \\[(.+)] \"(.+) (.+) (.+)\" (.+) (.+)$");

	/**
	 * Transforms a raw piece of data in the Common Log Format (https://en.wikipedia.org/wiki/Common_Log_Format)
	 * to the {@link LogData} value object, to be able to be used in a {@link Stats} handler.
	 *
	 * @param logEntry the raw data in CLF format.
	 *
	 * @return the same data inside the VO.
	 */
	@Override
	public Optional<LogData> process(String logEntry) {
		Matcher matcher = CLF_PATTERN.matcher(logEntry);
		if (!matcher.matches()) {
			return Optional.empty();
		}
		return Optional.of(new LogData(
				matcher.group(1),
				matcher.group(2),
				matcher.group(3),
				formatDate(matcher.group(4)),
				new LogData.Request(matcher.group(5), matcher.group(6), matcher.group(7)),
				Integer.parseInt(matcher.group(8)),
				Integer.parseInt(matcher.group(9))));
	}

	private Instant formatDate(String date) {
		return LocalDateTime
				.parse(date, builder.toFormatter(Locale.US))
				.atZone(ZoneId.systemDefault())
				.toInstant();
	}
}
