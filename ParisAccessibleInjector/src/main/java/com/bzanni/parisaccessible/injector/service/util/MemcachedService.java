package com.bzanni.parisaccessible.injector.service.util;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.spy.memcached.MemcachedClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MemcachedService {

	private final static Integer EXPIRATION = 6 * 3600;

	@Value("${memcached_url}")
	private String memcachedHost;

	@Value("${memcached_port}")
	private Integer memcachedPort;

	private MemcachedClient client;

	public void init() {
		try {
			client = new MemcachedClient(new InetSocketAddress(memcachedHost,
					memcachedPort));

			client.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object get(String key) {
		if (client == null) {
			return null;
		}
		return client.get(key);
	}

	public void set(String key, Object obj) {
		if (client != null && obj != null) {
			client.set(key, MemcachedService.EXPIRATION, obj);
		}
	}
}
