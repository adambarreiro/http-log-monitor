package com.adambarreiro.monitor.stats;

import com.adambarreiro.monitor.export.Exporter;
import com.adambarreiro.monitor.process.log.vo.LogData;

import java.util.HashMap;
import java.util.Map;

public class DummyStats implements Stats {

	private final Map<String, Integer> hits;
	private float requestDate;
	private final float errorDate;
	private final long totalTransmittedData;

	public DummyStats(String hits, float requestDate, float errorDate, long totalTransmittedData) {
		this.hits = new HashMap<>();
		this.requestDate = requestDate;
		this.errorDate = errorDate;
		this.totalTransmittedData = totalTransmittedData;
	}

	@Override
	public void add(LogData data) {

	}

	@Override
	public Map<String, Integer> getTopSiteHits() {
		return this.hits;
	}

	@Override
	public float getRequestsRate() {
		return this.requestDate;
	}

	public void setRequestDate(float requestDate) {
		this.requestDate = requestDate;
	}

	@Override
	public float getErrorRate() {
		return this.errorDate;
	}

	@Override
	public long getTotalTransmittedData() {
		return this.totalTransmittedData;
	}

	@Override
	public void expose(Exporter exporter) {

	}
}
