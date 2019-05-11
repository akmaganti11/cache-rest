# cache-rest
Distributed Cache for SpringBoot Micro Services - This jar provides easy REST based in-memory cache for Microservices.
This is a Key-Value based cache, purely based on Java, Spring and REST. 
No extra installation required to have a simple distributed cache or datastore.

Features:
In-memory cache
Distributed
In-Sync data store
Syncs all Caches with all MicroServices which are registered.

Quick Start:
Follow below steps.

1. Add Dependency in each of you micro service:
<dependency>
  <groupId>com.cache.rest</groupId>
	<artifactId>cache-rest</artifactId>
	<version>1.0-SNAPSHOT</version>
 </dependency> 

2. Import CacheRestApplication.java into your SprinBootApplication class
@Import(CacheRestApplication.class)

3. Autowire CacheRegistry.java into your class where you want to register and use Cache
@Autowired
private CacheRegistry cacheRegistry.;

4. Register Cache with microservice endpoints with restTemplate using below API
cacheRegistry.register(String cacheName, List<String> hosts, RestTemplate restTemplate)

Example:
String cacheName = "UniqueCacheName";

List<String> hosts = new ArrayList<>();
  hosts.add("http://localhost:4040");
  hosts.add("http://localhost:4041");
  
 RestTemplate: This is your restTemplate. 

5. Update Cache with Key-Value as below
String cacheName = "UniqueCacheName";
Cache cacheObject = new Cache();
cache.setCache(cacheName);
cache.setKey("K1");
cache.value("V1");

cacheRegistry.update(cacheName, cache);

4. Read Cache with Key as below:
cacheRegistry.read(cacheName, "K1");


Let me know if you need any support:
AK,
akmaganti04@gmail.com


