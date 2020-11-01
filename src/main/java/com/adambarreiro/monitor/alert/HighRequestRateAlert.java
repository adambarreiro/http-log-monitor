package com.adambarreiro.monitor.alert;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Alert layout for HTTP Request rate. It's used by the {@link AlertManager} to
 * trigger alerts of this type.
 */
class HighRequestRateAlert implements Alert {

	private final UUID id;
	private final Date creationTimestamp;
	private final AtomicInteger hits;
	private Optional<Date> resolutionTimestamp;

	public HighRequestRateAlert() {
		this.id = UUID.randomUUID();
		this.creationTimestamp = new Date();
		this.resolutionTimestamp = Optional.empty();
		this.hits = new AtomicInteger(1);
	}

	@Override
	public Date getCreationTimestamp() {
		return this.creationTimestamp;
	}

	@Override
	public Optional<Date> getResolutionTimestamp() {
		return this.resolutionTimestamp;
	}

	@Override
	public int getHits() {
		return hits.get();
	}

	@Override
	public boolean isActive() {
		return hits.get() > 0;
	}

	@Override
	public String getMessage() {
		return "High traffic detected";
	}

	@Override
	public void addHit() {
		this.hits.addAndGet(1);
	}

	@Override
	public void expire() {
		this.resolutionTimestamp = Optional.of(new Date());
		this.hits.set(0);
	}

	@Override
	public int hashCode() {
		return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37)
				.append(id)
				.toHashCode();
	}
}
