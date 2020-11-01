package com.adambarreiro.monitor.export;

import com.adambarreiro.monitor.alert.AlertManager;
import com.adambarreiro.monitor.process.log.CommonLogFormatLogProcessor;
import com.adambarreiro.monitor.stats.Stats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exports the gathered statistics and alerts to stdin, in a readable format.
 */
public final class ConsoleExporter implements Exporter {

	/**
	 * Writes the statistics to stdin.
	 *
	 * @param statistics the statistics to write.
	 */
	public void exportStatistics(Stats statistics) {
		String separator = Stream.generate(() -> "-").limit(30).collect(Collectors.joining());
		System.out.printf("%s%n\uD83D\uDD52 %s%n%s%n\uD83D\uDD25 Top site hits: %s%n\u23E9 Requests per second: %.2f%n\u274C Error rate: %.2f%%%n\uD83D\uDCE6 Total traffic data: %d Bytes%n%s%n",
				separator.replaceAll("-","_"),
				new Date(),
				separator,
				getPrintableTopSiteHits(statistics),
				statistics.getRequestsRate(),
				statistics.getErrorRate(),
				statistics.getTotalTransmittedData(),
				separator);
	}

	/**
	 * Writes the alerts to stdin.
	 *
	 * @param alertManager the alerts to write.
	 */
	@Override
	public void exportAlerts(AlertManager alertManager) {
		alertManager.getExpiredAlerts().forEach(alert ->
			System.out.printf("\u2705 The high traffic alert raised on %s was solved at %s%n",
					formatDate(alert.getCreationTimestamp()),
					formatDate(alert.getResolutionTimestamp().orElseGet(Date::new))));
		alertManager.getActiveAlerts().forEach(alert -> {
			if (alert.getHits() > 1) {
				System.out.printf("\uD83D\uDEA8 Alert still ongoing: %s - hits = %s, triggered at %s%n", alert.getMessage(), alert.getHits(), alert.getCreationTimestamp());
			} else {
				System.out.printf("\uD83D\uDEA8 Alert raised: %s - hits = %s, triggered at %s%n", alert.getMessage(), alert.getHits(), alert.getCreationTimestamp());
			}
		});
	}

	private String getPrintableTopSiteHits(Stats statistics) {
		StringBuilder topSites = new StringBuilder();
		int i=1;
		statistics.getTopSiteHits().forEach((site, hits) -> {
			topSites.append(String.format("%d. %s (%d hits) | ", i, site, hits));
		});
		if ("".equals(topSites.toString())) {
			return "N/A";
		}
		return topSites.toString();
	}

	private String formatDate(Date date) {
		return new SimpleDateFormat(CommonLogFormatLogProcessor.STRFTIME_FORMAT).format(date);
	}

}
