package com.adambarreiro.monitor.service;

import com.adambarreiro.monitor.alert.AlertManager;
import com.adambarreiro.monitor.capture.Observer;
import com.adambarreiro.monitor.export.ConsoleExporter;
import com.adambarreiro.monitor.export.Exporter;
import com.adambarreiro.monitor.process.log.LogProcessor;
import com.adambarreiro.monitor.process.log.vo.LogData;
import com.adambarreiro.monitor.stats.Stats;

import java.util.Optional;

/**
 * This service orchestrates all the application components.
 */
public final class LogMonitorService {

	private final Observer observer;
	private final LogProcessor logProcessor;
	private final Stats stats;
	private final AlertManager alertManager;

	public LogMonitorService(Observer observer, LogProcessor logProcessor, Stats stats, AlertManager alertManager) {
		this.observer = observer;
		this.logProcessor = logProcessor;
		this.stats = stats;
		this.alertManager = alertManager;
	}

	/**
	 * Exposes the metrics that are observed in the monitored log file and enables the alert management.
	 */
	public void start() {
		Exporter exporter = new ConsoleExporter();
		this.stats.expose(exporter);
		this.alertManager.start(exporter);
		this.observer.observe(line -> {
			Optional<LogData> data = this.logProcessor.process((String)line);
			data.ifPresent(this.stats::add);
		});
	}
}
