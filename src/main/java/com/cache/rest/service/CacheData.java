package com.cache.rest.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface CacheData {

    void save(final String cacheName, final String key, final String value);

    void delete(final String cacheName, final String key);

    String get(final String cacheName, final String key);

    Map<String, String> getCache(final String cacheName);

    List<String> getAllCacheNames();

}
