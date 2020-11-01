package com.adambarreiro.monitor.alert;

import java.util.Date;
import java.util.Optional;

/**
 * The basic layout of an alert managed by the {@link AlertManager}
 */
public interface Alert {

	Date getCreationTimestamp();

	Optional<Date> getResolutionTimestamp();

	boolean isActive();

	String getMessage();

	int getHits();

	void addHit();

	void expire();

}
