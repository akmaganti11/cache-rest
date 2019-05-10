package com.cache.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan
public class CacheRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(CacheRestApplication.class, args);
	}
	
	@Bean("CacheRestTemplate")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
