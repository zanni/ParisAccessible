package com.bzanni.parisaccessible.elasticsearch.repository;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Configurable
public class ParisAccessibleJestClient {
	private JestClient client;

	@Value("${elasticsearch_url}")
	private String host;

	@PostConstruct
	public void init() {
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder(host)
				.multiThreaded(true).build());

		client = factory.getObject();

	}

	@PreDestroy
	public void destro() {
		if (client != null) {
			client.shutdownClient();
		}
	}

	public JestClient getClient() {
		return client;
	}
}
