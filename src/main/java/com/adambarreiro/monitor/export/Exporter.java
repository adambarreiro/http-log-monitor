package com.adambarreiro.monitor.export;

import com.adambarreiro.monitor.alert.AlertManager;
import com.adambarreiro.monitor.stats.Stats;

/**
 * Exports the gathered statistics and alerts to a given destination.
 */
public interface Exporter {

	void exportStatistics(Stats statistics);

	void exportAlerts(AlertManager alertManager);
}
