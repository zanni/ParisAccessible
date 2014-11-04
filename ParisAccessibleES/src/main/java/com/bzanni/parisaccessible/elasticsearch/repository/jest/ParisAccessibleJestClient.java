package com.bzanni.parisaccessible.elasticsearch.repository.jest;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.settings.UpdateSettings;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ParisAccessibleJestClient {

	private static final int REFRESH_INTERVAL = 5;
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

	public boolean indexing(boolean isIndexing) throws Exception {
		int index = (isIndexing) ? ParisAccessibleJestClient.REFRESH_INTERVAL
				: -1;
		String body = "{ \"index\" : { " + "\"refresh_interval\" : " + index
				+ "} }";

		UpdateSettings updateSettings = new UpdateSettings.Builder(body)
				.build();

		JestResult execute = client.execute(updateSettings);
		
		client.executeAsync(updateSettings, new JestResultHandler<JestResult>() {
            @Override
            public void completed(JestResult result) {
//                executeTestCase(result);
            }

            @Override
            public void failed(Exception ex) {
//                fail("Failed during the running asynchronous call");
            }

        });
		
		return execute.isSucceeded();
	}

	public void shutdownClient() {
		client.shutdownClient();
	}

	public JestClient getClient() {
		return client;
	}
}
