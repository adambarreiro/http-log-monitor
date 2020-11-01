package com.adambarreiro.monitor.stats;

import com.adambarreiro.monitor.export.Exporter;
import com.adambarreiro.monitor.process.log.vo.LogData;

import java.net.HttpURLConnection;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Component that ingests new log data {@link LogData} periodically and calculates several metrics are exposed with
 * an {@link Exporter}. The period (aka interval) can be configurable.
 */
public final class ScheduledStats implements Stats {

	private final ConcurrentLinkedQueue<LogData> data;
	private final Map<String, Integer> topSiteHits;
	private final int intervalSeconds;

	private float requestRate;
	private float errorRate;
	private long totalSize;

	private ScheduledExecutorService scheduler;

	public ScheduledStats(final int intervalSeconds) {
		this.intervalSeconds = intervalSeconds;
		this.data = new ConcurrentLinkedQueue<>();
		this.topSiteHits = new HashMap<>();
	}

	/**
	 * Ingests log data, discarding the log entries older than 'intervalSeconds'.
	 *
	 * @param data the data.
	 */
	@Override
	public void add(LogData data) {
		if (data.getTimestamp().compareTo(Instant.now().minusSeconds(intervalSeconds)) > 0) {
			this.data.add(data);
		}
	}

	/**
	 * Calculates the top visited sites.
	 *
	 * @return the top visited sites.
	 */
	@Override
	public Map<String, Integer> getTopSiteHits() {
		return this.topSiteHits;
	}

	/**
	 * Calculates the request rate.
	 *
	 * @return the error rate.
	 */
	@Override
	public float getRequestsRate() {
		return this.requestRate;
	}

	/**
	 * Calculates the error rate.
	 *
	 * @return the error rate.
	 */
	@Override
	public float getErrorRate() {
		return this.errorRate;
	}

	/**
	 * Calculates the transmitted data.
	 *
	 * @return  the transmitted data.
	 */
	@Override
	public long getTotalTransmittedData() {
		return this.totalSize;
	}

	/**
	 * Starts the metric calculation in a separate thread and exposes them through the exporter.
	 *
	 * @param exporter the exporter to expose metrics to the outside world.
	 */
	@Override
	public void expose(final Exporter exporter) {
		if (Objects.isNull(scheduler)) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(()-> {
				this.process();
				exporter.exportStatistics(this);
			},0L, intervalSeconds, TimeUnit.SECONDS);
		}
	}

	/**
	 * Process all the metrics.
	 */
	private void process() {
		Instant now = Instant.now();
		LogData data = this.data.poll();
		long requests=0;
		long errors = 0;
		Map<String, Integer> siteMap = new HashMap<>();

		this.totalSize = 0;
		while(data != null && data.getTimestamp().compareTo(now) < 1) {
			siteMap.put(getSite(data), siteMap.getOrDefault(getSite(data), 0) + 1);
			if (isErrorRequest(data)) {
				errors++;
			}
			this.totalSize += data.getSize();
			requests++;
			data = this.data.poll();
		}
		processRequestRate(requests);
		processErrorRate(requests, errors);
		generateTopSites(siteMap);
	}

	/**
	 * Updates the request rate.
	 *
	 * @param requests Requests during last interval.
	 */
	private void processRequestRate(long requests) {
		this.requestRate=((float)requests/this.intervalSeconds);
	}

	/**
	 * Updates the error rate.
	 *
	 * @param requests Requests during last interval.
	 * @param errors Errors during last interval.
	 */
	private void processErrorRate(long requests, long errors) {
		if (requests > 0) {
			this.errorRate=((float)errors / requests)*100;
		} else {
			this.errorRate = 0;
		}
	}

	/**
	 * Generates the top 3 visited sites during the interval.
	 *
	 * @param siteMap All the visited sites in the whole interval.
	 */
	private void generateTopSites(Map<String, Integer> siteMap) {
		this.topSiteHits.clear();
		this.topSiteHits.putAll(siteMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(3)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	/**
	 * Given a full site like /foo/bar, returns /foo.
	 *
	 * @param data The log entry
	 *
	 * @return The root path.
	 */
	private String getSite(LogData data) {
		return "/"+Paths.get(data.getRequest().getPath()).getName(0).toString();
	}

	/**
	 * Returns true if it's an error request. False otherwise.
	 *
	 * @param data The log entry
	 *
	 * @return true if it's an error request. False otherwise.
	 */
	private boolean isErrorRequest(LogData data) {
		return data.getStatusCode() >= HttpURLConnection.HTTP_BAD_REQUEST && data.getStatusCode() <= HttpURLConnection.HTTP_VERSION;
	}

}
