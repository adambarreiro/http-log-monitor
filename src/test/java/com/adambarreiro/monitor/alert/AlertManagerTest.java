package com.adambarreiro.monitor.alert;

import com.adambarreiro.monitor.exporter.DummyExporter;
import com.adambarreiro.monitor.stats.DummyStats;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * These are the tests for the alert manager. As it's a completely async process running in a separated thread,
 * we use the timeout feature of JUnit 5.
 */
public class AlertManagerTest {

	/**
	 * We create a dummy stats provider that always says that the request rate is 999.9req/s.
	 * The alert manager is config to raise alerts for higher request rate than 999.8req/s.
	 * The alert eventually will trigger, in less than 10s, and it should never expire,
	 * as the alert manager is never updated (the inverval is 99999)
	 */
	@Test
	@DisplayName("Alert is triggered when threshold is reached")
	public void alertIsTriggeredWhenThresholdIsReached() {
		AlertManager alertManager = new AlertManager(new DummyStats("", 999.9f, 0, 0), new AlertConfig(999.8f), 99999);
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
			alertManager.start(new DummyExporter());
			while (alertManager.getActiveAlerts().isEmpty()) {
			}
		});
		Assertions.assertEquals(1, alertManager.getActiveAlerts().stream().findFirst().orElseThrow().getHits());
	}

	/**
	 * Here we create a dummy stats with a high request rate, to raise an alert.
	 * The first infinite loop assures that the alert is there, active.
	 * Then, we lower the request rate in the dummy stats provider, and we wait
	 * until the alert is expired.
	 *
	 * As the alert expiration cleans up the alerts, we need to store it in an auxiliary
	 * list.
	 */
	@Test
	@DisplayName("Alert is expired when the interval time has passed")
	public void alertIsExpiredWhenTheIntervalTimeHasPassed() {
		final List<Alert> expiredAlerts = new ArrayList<>();
		DummyStats stats = new DummyStats("", 999.9f, 0, 0);
		AlertManager alertManager = new AlertManager(stats, new AlertConfig(999.8f), 1);
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
			alertManager.start(new DummyExporter());
			while (alertManager.getActiveAlerts().isEmpty()) {
			}
			stats.setRequestDate(1.0f);
			while (expiredAlerts.isEmpty()) {
				expiredAlerts.addAll(alertManager.getExpiredAlerts());
			}
		});
		Assertions.assertEquals(1, expiredAlerts.size());
	}

	/**
	 * We set a high request rate that is higher than the alert configuration. The interval in which the alert manager
	 * runs is very low (1 second). We want to check that the number of hits in this case will be always higher to 1
	 * after a period of time less than 10 seconds.
	 */
	@Test
	@DisplayName("Alert has more than 1 hit after 1 interval of 1 second")
	public void alertHasTwoHitsAfterTwoIntervalsOfTwoSeconds() {
		AlertManager alertManager = new AlertManager(new DummyStats("", 999.9f, 0, 0), new AlertConfig(1.0f), 1);
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
			alertManager.start(new DummyExporter());
			while (alertManager.getActiveAlerts().isEmpty() || alertManager.getActiveAlerts().stream().findFirst().orElseThrow().getHits() <= 1) {
			}
		});
	}

}
