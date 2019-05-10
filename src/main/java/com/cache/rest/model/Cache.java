package com.cache.rest.model;

public class Cache {

    private String cache;
    private String key;
    private String value;

    public String getCache() {
	return cache;
    }

    public void setCache(String cache) {
	this.cache = cache;
    }

    public String getKey() {
	return key;
    }

    public void setKey(String key) {
	this.key = key;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    @Override
    public String toString() {
	return "Cache [key=" + key + ", value=" + value + "]";
    }

}
