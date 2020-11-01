package com.adambarreiro.monitor.exporter;

import com.adambarreiro.monitor.alert.AlertManager;
import com.adambarreiro.monitor.export.Exporter;
import com.adambarreiro.monitor.stats.Stats;

public class DummyExporter implements Exporter {

	@Override
	public void exportStatistics(Stats statistics) {

	}

	@Override
	public void exportAlerts(AlertManager alertManager) {

	}
}
