package com.cache.rest.service.impl;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cache.rest.service.CacheData;

@Service
public class CacheDataImpl implements CacheData {

    private Map<String, Map<String, Deque<String>>> masterCache = new HashMap<>();

    @Override
    public void save(String cacheName, String key, String value) {
	Map<String, Deque<String>> cache = new HashMap<>();
	Deque<String> queue = new ArrayDeque<String>();

	if (null != value) {
	    queue.addLast(value);
	    cache.put(key, queue);
	} else {
	    throw new IllegalArgumentException("Cannot save null in Cache!");
	}

	masterCache.put(cacheName, cache);
    }

    @Override
    public String get(String cacheName, String key) {
	Map<String, Deque<String>> cache = masterCache.get(cacheName);
	Deque<String> valueQueue = cache.get(key);
	Queue<String> resultQueue = new ArrayDeque<String>();

	if (valueQueue != null) {
	    resultQueue = Collections.asLifoQueue(valueQueue);
	    return resultQueue.iterator().next();
	}

	return null;
    }

    @Override
    public void delete(final String cacheName, String key) {
	Map<String, Deque<String>> cache = masterCache.get(cacheName);
	if (cache.get(key) != null) {
	    cache.remove(key);
	} else {
	    throw new IllegalArgumentException("No record found to delete for the given Key :" + key);
	}
    }

    @Override
    public Map<String, String> getCache(String cacheName) {
	Map<String, Deque<String>> cache = masterCache.get(cacheName);
	cache.get(cacheName);
	return null;
    }

    @Override
    public List<String> getAllCacheNames() {
	return masterCache.keySet().stream().collect(Collectors.toList());
    }

}
