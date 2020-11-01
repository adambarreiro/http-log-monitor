package com.adambarreiro.monitor.config;

import java.util.Objects;
import java.util.Properties;

/**
 * This class manages the configuration for the application.
 */
public final class ConfigurationContainer {

	private static final String OPTION_TOKEN = "-";

	private static ConfigurationContainer instance;
	private final Properties properties;

	/**
	 * All the available configuration options
	 */
	private static class ConfigurationOptions {
		private static final String ALERT_INTERVAL_SECONDS = "alertInterval";
		private static final String LOG_FILE = "logFile";
		private static final Object REQUEST_RATE_THRESHOLD = "requestRateThreshold";
		private static final String SCHEDULE_INTERVAL_SECONDS = "scheduleInterval";
	}

	/**
	 * The default configuration values. This is what the application will have if not overridden.
	 */
	private static class DefaultConfigurationValues {
		private static final float REQUEST_RATE_THRESHOLD = 10.0f;
		private static final int SCHEDULE_INTERVAL_SECONDS = 10;
		private static final int ALERT_INTERVAL_SECONDS = 120;
		private static final String LOG_FILE = "/tmp/access.log";
	}

	private ConfigurationContainer() {
		this.properties = new Properties();
	}

	public static ConfigurationContainer getInstance() {
		if (Objects.isNull(instance)) {
			instance = new ConfigurationContainer();
		}
		return instance;
	}

	/**
	 * Sets the configuration passed as arguments.
	 *
	 * @param args The arguments passed to the program through the shell.
	 */
	public void add(String... args) {
		setDefaults();
		if (args.length > 0) {
			overrideDefaults(args);
		}
	}

	/**
	 * Gets the log file path.
	 *
	 * @return The log file path.
	 */
	public String getLogfile() {
		return (String) this.properties.get(ConfigurationOptions.LOG_FILE);
	}

	/**
	 * Gets the interval in which the statistics are updated.
	 *
	 * @return the scheduler interval.
	 */
	public int getScheduleIntervalSeconds() {
		return Integer.parseInt(String.valueOf(this.properties.get(ConfigurationOptions.SCHEDULE_INTERVAL_SECONDS)));
	}

	/**
	 * Gets the interval in which alerts are checked.
	 *
	 * @return the alert interval.
	 */
	public int getAlertIntervalSeconds() {
		return Integer.parseInt(String.valueOf(this.properties.get(ConfigurationOptions.ALERT_INTERVAL_SECONDS)));
	}

	/**
	 * Returns the request rate threshold to create alerts.
	 *
	 * @return the request rate threshold to create alerts
	 */
	public float getRequestRateAlertThreshold() {
		return Float.parseFloat(String.valueOf(this.properties.get(ConfigurationOptions.REQUEST_RATE_THRESHOLD)));
	}

	/**
	 * Sets the minimum set of default options for the application to work.
	 */
	private void setDefaults() {
		this.properties.put(ConfigurationOptions.LOG_FILE, DefaultConfigurationValues.LOG_FILE);
		this.properties.put(ConfigurationOptions.SCHEDULE_INTERVAL_SECONDS, DefaultConfigurationValues.SCHEDULE_INTERVAL_SECONDS);
		this.properties.put(ConfigurationOptions.ALERT_INTERVAL_SECONDS, DefaultConfigurationValues.ALERT_INTERVAL_SECONDS);
		this.properties.put(ConfigurationOptions.REQUEST_RATE_THRESHOLD, DefaultConfigurationValues.REQUEST_RATE_THRESHOLD);
	}

	/**
	 * Overrides the default settings with the ones provided through command line arguments.
	 *
	 * @param args The arguments passed to the program through the shell.
	 */
	private void overrideDefaults(String... args) {
		int i = 0;
		while (i < args.length - 1) {
			if (args[i].startsWith(OPTION_TOKEN) && this.properties.containsKey(args[i].replaceFirst(OPTION_TOKEN, ""))) {
				this.properties.put(args[i].replaceFirst(OPTION_TOKEN, ""), args[i + 1]);
			}
			i++;
		}
	}
}
