package com.cache.rest.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cache.rest.model.Cache;
import com.cache.rest.service.CacheData;

@RestController
@RequestMapping("/cache")
public class CacheRest {

	@Autowired
	private CacheData cacheData;
	
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String ping() {
		return "Running !";
	}

	@RequestMapping(value = "/rest", method = RequestMethod.GET)
	public String getFromCache(@RequestParam(value = "cacheName") String cacheName, @RequestParam(value = "key") String key) {
		return cacheData.get(cacheName, key);
	}

	@RequestMapping(value = "/rest", method = RequestMethod.POST)
	public void postToCache(@RequestBody Cache cache) {
		cacheData.save(cache.getCache(), cache.getKey(), cache.getValue());
	}

	@RequestMapping(value = "/rest", method = RequestMethod.DELETE)
	public void deleteFromCache(@RequestBody Cache cache) {
		cacheData.delete(cache.getCache(), cache.getKey());
	}
}
