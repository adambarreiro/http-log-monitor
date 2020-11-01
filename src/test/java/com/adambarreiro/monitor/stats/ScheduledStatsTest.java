package com.adambarreiro.monitor.stats;


import com.adambarreiro.monitor.exporter.DummyExporter;
import com.adambarreiro.monitor.process.log.vo.LogData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

/**
 * These are the tests for the scheduled stats provider. As it's a completely async process running in a separated thread,
 * we use the timeout feature of JUnit 5.
 */
public class ScheduledStatsTest {

	/**
	 * We create a stats provider that never refreshes (the interval is 9999).
	 * We create some dummy log entries and wait for the request rate to change.
	 */
	@Test
	@DisplayName("Request rate is more than zero when we get a request and stats are never refreshed")
	public void requestRateIsMoreThanZeroWhenWeGetARequestAndStatsAreNeverRefreshedTest() {
		Stats stats = new ScheduledStats(9999);
		generateDummyData(5, stats, "/ships", 200);
		stats.expose(new DummyExporter());
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
			while (stats.getRequestsRate() == 0.0f) {
			}
		});
	}

	/**
	 * We create a stats provider that never refreshes (the interval is 9999).
	 * We create some dummy log entries with error 500 for the request rate to change.
	 */
	@Test
	@DisplayName("Error rate is more than zero when we get a request and stats are never refreshed")
	public void errorRateIsMoreThanZeroWhenWeGetARequestAndStatsAreNeverRefreshedTest() {
		Stats stats = new ScheduledStats(9999);
		generateDummyData(5, stats, "/ships", 500);
		stats.expose(new DummyExporter());
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
			while (stats.getErrorRate() == 0.0f) {
			}
		});
	}

	/**
	 * We create a stats provider that never refreshes (the interval is 9999).
	 * We create multiple log entries for the multiple sites and check that the top 3 site is correct.
	 */
	@Test
	@DisplayName("Top 3 site is correct after some requests")
	public void topThreeSiteIsCorrectAfterSomeRequestsTest() {
		Stats stats = new ScheduledStats(9999);
		generateDummyData(30, stats, "/ships", 200);
		generateDummyData(29, stats, "/shops", 200);
		generateDummyData(28, stats, "/shups", 200);
		generateDummyData(27, stats, "/shaps", 200);
		stats.expose(new DummyExporter());
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
			while (stats.getTopSiteHits().isEmpty()) {
			}
		});
		Assertions.assertEquals(30, (int) stats.getTopSiteHits().get("/ships"));
		Assertions.assertEquals(29, (int) stats.getTopSiteHits().get("/shops"));
		Assertions.assertEquals(28, (int) stats.getTopSiteHits().get("/shups"));
		Assertions.assertFalse(stats.getTopSiteHits().containsKey("/shaps"));
	}

	/**
	 * We create a stats provider that never refreshes (the interval is 9999).
	 * We create some dummy log entries and wait for the total size to change.
	 */
	@Test
	@DisplayName("Total size is higher than zero after some requests")
	public void totalSizeIsHigherThanZeroAfterSomeRequestsTest() {
		Stats stats = new ScheduledStats(10);
		generateDummyData(5, stats, "/ships", 200);
		stats.expose(new DummyExporter());
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
			while (stats.getTotalTransmittedData() == 0) {
			}
		});
	}

	private void generateDummyData(int requests, Stats stats, String path, int statusCode) {
		Stream.generate(() -> new LogData("1.2.3.4", "-", "han solo", Instant.now(), new LogData.Request("GET", path, "HTTP/1.0"), statusCode, 1))
				.limit(requests)
				.forEach(stats::add);
	}
}
