package com.adambarreiro.monitor.export;

import com.adambarreiro.monitor.alert.AlertManager;
import com.adambarreiro.monitor.stats.Stats;

public final class HttpExporter implements Exporter {

	// TODO: We could export the data to other destinations, like an HTTP endpoint.

	@Override
	public void exportStatistics(Stats statistics) {

	}

	@Override
	public void exportAlerts(AlertManager alertManager) {

	}
}
