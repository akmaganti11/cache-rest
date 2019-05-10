package com.cache.rest.registry.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cache.rest.model.Cache;
import com.cache.rest.registry.core.CacheRegistryCore;
import com.cache.rest.registry.service.CacheRegistry;
import com.cache.rest.util.CacheConstants;

@Component
public class CacheRegistryImpl implements CacheRegistry {

	@Autowired
	private CacheRegistryCore cacheRegistryCore;

	@Override
	public void register(String cacheName, String host) {
		cacheRegistryCore.registry.add(cacheName, host + CacheConstants.CACHE_BASE_MAPPING);
	}

	@Override
	public boolean register(String cacheName, List<String> hosts) {
		if (Strings.isNotBlank(cacheName)) {
			hosts.stream().filter(host -> Strings.isNotBlank(host)).forEach(host -> {
				cacheRegistryCore.registry.add(cacheName, host + CacheConstants.CACHE_BASE_MAPPING);
			});
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void register(String cacheName, List<String> hosts, RestTemplate restTemplate) {
		if (Strings.isNotBlank(cacheName) && !(hosts.isEmpty())) {
			hosts.stream().filter(host -> Strings.isNotBlank(host)).forEach(host -> {
				cacheRegistryCore.registry.add(cacheName, host + CacheConstants.CACHE_BASE_MAPPING);
			});

			CacheRegistryCore.REST_TEMPLATE = (null != restTemplate) ? restTemplate : new RestTemplate();
		} else {
			throw new IllegalArgumentException("Cache name or Hosts list cannot be empty");
		}
	}

	@Override
	public String update(String cacheName, Cache cache) {
		Map<String, ResponseEntity<String>> responseMap = new HashMap<>();
		Map<String, String> replyMap = new HashMap<>();

		if (cacheRegistryCore.registry.get(cacheName) != null) {
			return cacheRegistryCore.updateToAllServices(cacheName, cache, responseMap, replyMap);
		} else {
			throw new IllegalArgumentException("No Cache !");
		}
	}

	@Override
	public String read(String cacheName, String key) {

		Map<String, String> params = new HashMap<>();
		params.put("key", key);
		params.put("cacheName", cacheName);

		if (cacheRegistryCore.CONSISTENT) {
			return cacheRegistryCore.readFromLocalCache(cacheName, key);
		} else {
			return cacheRegistryCore.readFromAllServices(cacheName, key, params);
		}
	}

	@Override
	public boolean isRegistered(String cacheName) {
	    return !cacheRegistryCore.registry.isEmpty();
	}

	@Override
	public boolean sync(String cacheName) {
	    
	    return false;
	}

}
