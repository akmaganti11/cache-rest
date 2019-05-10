package com.cache.rest.registry.service;

import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.cache.rest.model.Cache;

public interface CacheRegistry {

	void register(String cacheName, String host);
	
	boolean register(String cacheName, List<String> hosts);
	
	void register(String cacheName, List<String> hosts, RestTemplate restTemplate);
	
	boolean isRegistered(String cacheName);

	String update(final String cacheName, final Cache cache);

	String read(final String cacheName, final String key);
	
	boolean sync(final String cacheName);
	
	

}
