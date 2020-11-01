package com.adambarreiro.monitor.alert;

/**
 * Configuration for alerts. This can be used in {@link AlertManager} to configure how alerts
 * are triggered.
 */
public class AlertConfig {

	private final float requestRateAlertThreshold;

	public AlertConfig(float requestRateAlertThreshold) {
		this.requestRateAlertThreshold = requestRateAlertThreshold;
	}

	public float getRequestRateAlertThreshold() {
		return this.requestRateAlertThreshold;
	}
}
