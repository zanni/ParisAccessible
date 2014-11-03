package com.bzanni.parisaccessible.elasticsearch.repository;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ParisAccessibleJestClient {

	@Value("${elasticsearch_url}")
	private String host;

	private JestClient client;

	@PostConstruct
	public void init() {
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder(host)
				.multiThreaded(true).build());

		client = factory.getObject();

	}

	public void shutdownClient() {
		client.shutdownClient();
	}

	public JestClient getClient() {
		return client;
	}
}
