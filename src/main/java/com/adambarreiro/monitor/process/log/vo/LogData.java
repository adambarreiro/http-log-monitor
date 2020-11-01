package com.adambarreiro.monitor.process.log.vo;

import java.time.Instant;

/**
 * Log data value object.
 */
public class LogData {

	private final String client;
	private final String identity;
	private final String userId;
	private final Instant timestamp;
	private final Request request;
	private final int statusCode;
	private final int size;

	public LogData(String client, String identity, String userId, Instant timestamp, Request request, int statusCode, int size) {
		this.client = client;
		this.identity = identity;
		this.userId = userId;
		this.timestamp = timestamp;
		this.request = request;
		this.statusCode = statusCode;
		this.size = size;
	}

	public String getClient() {
		return client;
	}

	public String getIdentity() {
		return identity;
	}

	public String getUserId() {
		return userId;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public Request getRequest() {
		return request;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public int getSize() {
		return size;
	}

	public static class Request {

		private final String verb;
		private final String path;
		private final String version;

		public Request(String verb, String path, String version) {
			this.verb = verb;
			this.path = path;
			this.version = version;
		}

		public String getVerb() {
			return verb;
		}

		public String getPath() {
			return path;
		}

		public String getVersion() {
			return version;
		}
	}
}
