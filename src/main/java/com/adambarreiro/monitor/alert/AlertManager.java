package com.adambarreiro.monitor.alert;

import com.adambarreiro.monitor.export.Exporter;
import com.adambarreiro.monitor.stats.Stats;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This class manages alerts reading data from the available stats {@link Stats} and exposes them
 * through a {@link Exporter} to the outside world. The alerts are configurable through {@link AlertConfig}.
 */
public final class AlertManager {

	private final Stats stats;
	private final AlertConfig config;
	private final Set<Alert> alerts;
	private final int intervalSeconds;

	private ScheduledExecutorService scheduler;

	public AlertManager(Stats stats, final AlertConfig config, int intervalSeconds) {
		this.stats = stats;
		this.config = config;
		this.alerts = new HashSet<>();
		this.intervalSeconds = intervalSeconds;
	}

	/**
	 * Gets the alerts that are currently open and unsolved.
	 *
	 * @return the alerts that are currently open and unsolved.
	 */
	public synchronized Set<Alert> getActiveAlerts() {
		return this.alerts.stream().filter(Alert::isActive).collect(Collectors.toSet());
	}

	/**
	 * Gets the alerts that are already resolved and cleans them.
	 *
	 * @return the alerts that are already resolved.
	 */
	public synchronized Set<Alert> getExpiredAlerts() {
		Set<Alert> alertsToShow = this.alerts.stream().filter(alert -> !alert.isActive()).collect(Collectors.toSet());
		this.alerts.removeAll(alertsToShow);
		return alertsToShow;

	}

	/**
	 * Creates a separate executor thread that manages the alerts, creating and expiring them.
	 */
	public void start(final Exporter exporter) {
		if (Objects.isNull(this.scheduler)) {
			this.scheduler = Executors.newSingleThreadScheduledExecutor();
			this.scheduler.scheduleWithFixedDelay(()-> {
				this.processAlerts();
				exporter.exportAlerts(this);
			},0L, this.intervalSeconds, TimeUnit.SECONDS);
		}
	}

	/**
	 * Processes the request rate and generates an alert if the threshold is surpassed.
	 * If we're not under any alert situation, it's time to clean up and expire alerts.
	 */
	private void processAlerts() {
		synchronized (this) {
			if (this.stats.getRequestsRate() > config.getRequestRateAlertThreshold()) {
				Optional<Alert> alert = this.alerts.stream().filter(a -> a instanceof HighRequestRateAlert).findFirst();
				alert.ifPresentOrElse(Alert::addHit, () -> this.alerts.add(new HighRequestRateAlert()));
			} else {
				this.expireAlerts();
			}
		}
	}

	/**
	 * Transforms active alerts in expired alerts, if they're older than the interval.
	 */
	private void expireAlerts() {
		this.alerts.stream().filter(data ->
				data.getCreationTimestamp().toInstant().getEpochSecond() < Instant.now().getEpochSecond() - this.intervalSeconds
		).forEach(Alert::expire);
	}

}
