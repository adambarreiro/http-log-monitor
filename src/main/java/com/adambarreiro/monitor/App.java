package com.adambarreiro.monitor;

import com.adambarreiro.monitor.alert.AlertConfig;
import com.adambarreiro.monitor.alert.AlertManager;
import com.adambarreiro.monitor.capture.FileObserver;
import com.adambarreiro.monitor.config.ConfigurationContainer;
import com.adambarreiro.monitor.process.log.CommonLogFormatLogProcessor;
import com.adambarreiro.monitor.service.LogMonitorService;
import com.adambarreiro.monitor.stats.ScheduledStats;
import com.adambarreiro.monitor.stats.Stats;

import java.io.FileNotFoundException;

/**
 * Main class, where all dependencies are injected and the application
 * starts.
 */
final class App {

	/**
	 * Entrypoint of the application. We build all the dependencies here and inject them. Normally we would delegate
	 * this exercise to a DI container like the one Spring Boot has, but I avoided the usage of heavy frameworks
	 * in this coding challenge.
	 *
	 * @param args Arguments of the application. See the docs for the full reference.
	 */
	public static void main(String... args) {
		ConfigurationContainer.getInstance().add(args);
		try {
			// Dependencies
			Stats stats = new ScheduledStats(ConfigurationContainer.getInstance().getScheduleIntervalSeconds());
			LogMonitorService logMonitorService = new LogMonitorService(
					FileObserver.of(ConfigurationContainer.getInstance().getLogfile()),
					new CommonLogFormatLogProcessor(), stats,
					new AlertManager(stats, new AlertConfig(ConfigurationContainer.getInstance().getRequestRateAlertThreshold()),
							ConfigurationContainer.getInstance().getAlertIntervalSeconds()));

			logMonitorService.start();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
