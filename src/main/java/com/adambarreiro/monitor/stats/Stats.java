package com.adambarreiro.monitor.stats;

import com.adambarreiro.monitor.export.Exporter;
import com.adambarreiro.monitor.process.log.vo.LogData;

import java.util.Map;

/**
 * Component that is responsible of ingesting new log data {@link LogData} and calculate several metrics that
 * can be exposed to the outside world with an {@link Exporter}.
 */
public interface Stats {

	/**
	 * Ingests log data. Returns the number of buffered records.
	 *
	 * @param data the data.
	 * @return
	 */
	void add(LogData data);

	/**
	 * Gets the top visited sites. The keys in the map are the sites
	 * and the value the hits.
	 *
	 * @return the top visited sites.
	 */
	Map<String, Integer> getTopSiteHits();

	/**
	 * Calculates the request rate.
	 *
	 * @return the error rate.
	 */
	float getRequestsRate();

	/**
	 * Calculates the error rate.
	 *
	 * @return the error rate.
	 */
	float getErrorRate();

	/**
	 * Calculates the transmitted data.
	 *
	 * @return  the transmitted data.
	 */
	long getTotalTransmittedData();

	/**
	 * Exposes the metrics through the exporter.
	 *
	 * @param exporter the exporter to expose metrics to the outside world.
	 */
	void expose(Exporter exporter);
}
