package com.cache.rest.util;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.stereotype.Component;

@Component
public class CacheExecutorService {
	
	public ExecutorService createExecutorService(int threads) {
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		return executorService;
	}

	public void waitForCacheUpdatesAndStopExecutorService(ExecutorService executorService, List<Future<?>> futures) {
		waitForTaskCompletion(futures);
		executorService.shutdown();
	}

	public void waitForTaskCompletion(List<Future<?>> futures) {
		futures.forEach(future -> {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});
	}
}
