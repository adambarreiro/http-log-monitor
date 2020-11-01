package com.adambarreiro.monitor.export;

import com.adambarreiro.monitor.alert.AlertManager;
import com.adambarreiro.monitor.stats.Stats;

public final class JMXExporter implements Exporter {

	// TODO: We could export the data to other destinations, like JMX.

	@Override
	public void exportStatistics(Stats statistics) {

	}

	@Override
	public void exportAlerts(AlertManager alertManager) {

	}
}
