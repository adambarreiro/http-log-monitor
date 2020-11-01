package com.adambarreiro.monitor.capture;

import io.reactivex.rxjava3.functions.Consumer;

public final class HttpObserver implements Observer {

	// TODO: We could observe other sources like HTTP endpoints, messaging queues, a database...

	@Override
	public void observe(Consumer<? super Object> onNext) {
	}

}
