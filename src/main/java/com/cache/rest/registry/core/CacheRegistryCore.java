package com.cache.rest.registry.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cache.rest.model.Cache;
import com.cache.rest.service.CacheData;
import com.cache.rest.util.CacheConstants;
import com.cache.rest.util.CacheExecutorService;

@Component
public class CacheRegistryCore {

    private static final String VALUE_DELIMITER = " :: ";

    public static RestTemplate REST_TEMPLATE = null;

    @Autowired
    @Qualifier("CacheRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private CacheData cacheData;

    @Autowired
    private CacheExecutorService cacheExecutorService;

    public static boolean CONSISTENT = false;

    public MultiValueMap<String, String> registry = new LinkedMultiValueMap<>();

    public String updateToAllServices(String cacheName, Cache cache, Map<String, ResponseEntity<String>> responseMap,
	    Map<String, String> replyMap) {

	String cacheValue = cache.getValue();
	if (cacheValue.contains(VALUE_DELIMITER)) {
	    throw new IllegalArgumentException(
		    "Char sequence: " + VALUE_DELIMITER + " not supported, conflicts with internal delimiter");
	}
	cache.setValue(cacheValue + VALUE_DELIMITER + LocalDateTime.now());

	ExecutorService executorService = cacheExecutorService.createExecutorService(registry.size());
	List<Future<?>> futures = new ArrayList<>();

	registry.get(cacheName).forEach(service -> {
	    Future<?> cacheUpdateFuture = executorService.submit(() -> {
		updateCache(cacheName, cache, responseMap, service);
	    });

	    futures.add(cacheUpdateFuture);
	});

	cacheExecutorService.waitForCacheUpdatesAndStopExecutorService(executorService, futures);

	responseMap.forEach((service, response) -> {
	    if (response != null) {
		if (response.getStatusCode().equals(HttpStatus.OK)) {
		    replyMap.put(service, response.getStatusCode().toString());
		}
	    } else {
		replyMap.put(service, null);
	    }
	});

	return deligateReply(replyMap);
    }

    private void updateCache(String cacheName, Cache cache, Map<String, ResponseEntity<String>> responseMap,
	    String service) {
	ResponseEntity<String> respose = null;
	try {
	    respose = REST_TEMPLATE.postForEntity(service, cache, String.class);
	    responseMap.put(cacheName + VALUE_DELIMITER + service, respose);
	} catch (RestClientException e) {
	    // not throwing any exception here, ignoring the service and continuing with
	    // other update.
	    responseMap.put(cacheName + VALUE_DELIMITER + service, null);
	    CONSISTENT = false;
	}
    }

    public String readFromLocalCache(String cacheName, String key) {
	String responseFromCache = null;
	responseFromCache = cacheData.get(cacheName, key);
	String[] valueArray = responseFromCache.split(VALUE_DELIMITER);
	if (valueArray.length > 0) {
	    return valueArray[0];
	} else {
	    return null;
	}
    }

    public String readFromAllServices(String cacheName, String key, Map<String, String> params) {
	Map<String, String> responseCache = new HashMap<>();
	if (!registry.isEmpty() && null != registry.get(cacheName)) {

	    ExecutorService executorService = cacheExecutorService.createExecutorService(registry.size());
	    List<Future<?>> futures = new ArrayList<>();

	    registry.get(cacheName).forEach(service -> {
		Future<?> cacheUpdateFuture = executorService.submit(() -> {
		    readFromCache(cacheName, key, params, responseCache, service);
		});

		futures.add(cacheUpdateFuture);
	    });

	    cacheExecutorService.waitForCacheUpdatesAndStopExecutorService(executorService, futures);

	} else {
	    throw new IllegalArgumentException("Given key or Cache doesnot exist !");
	}
	return returnLatestCacheRecord(responseCache);
    }

    private void readFromCache(String cacheName, String key, Map<String, String> params,
	    Map<String, String> responseCache, String service) {
	String responseFromCache = null;
	try {
	    responseFromCache = REST_TEMPLATE.getForObject(
		    service + CacheConstants.CACHE_PARAM + cacheName + CacheConstants.GET_PARAM + key, String.class,
		    params);
	} catch (RestClientException e) {
	    // not throwing any exception here, ignoring the service and continuing with
	    // other update.
	}

	if (Strings.isNotBlank(responseFromCache)) {
	    responseCache.put(service, responseFromCache);
	}
    }

    private String returnLatestCacheRecord(Map<String, String> responseCache) {
	Map<LocalDateTime, String> responseCacheLatest = getLatestCacheRecord(responseCache);
	if (!responseCacheLatest.isEmpty()) {

	    Optional<LocalDateTime> latestTimeStamp = responseCacheLatest.keySet().stream()
		    .max(LocalDateTime::compareTo);
	    return responseCacheLatest.get(latestTimeStamp.get());
	} else {
	    return null;
	}
    }

    private Map<LocalDateTime, String> getLatestCacheRecord(Map<String, String> responseCache) {
	Map<LocalDateTime, String> responseCacheLatest = new HashMap<>();

	if (!responseCache.isEmpty()) {
	    responseCache.values().forEach(value -> {
		String[] valueArray = value.split(VALUE_DELIMITER);
		if (valueArray.length > 0) {
		    String timeStamp = valueArray[1];
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CacheConstants.DATE_TIME_FORMAT);
		    LocalDateTime dateTime = LocalDateTime.parse(timeStamp, formatter);

		    responseCacheLatest.put(dateTime, valueArray[0]);
		}
	    });
	}
	return responseCacheLatest;
    }

    private String deligateReply(Map<String, String> replyMap) {
	if (replyMap.values().contains(null)) {
	    CONSISTENT = false;
	    return "Inconsistent";
	} else {
	    CONSISTENT = true;
	    return "Success";
	}
    }

}
