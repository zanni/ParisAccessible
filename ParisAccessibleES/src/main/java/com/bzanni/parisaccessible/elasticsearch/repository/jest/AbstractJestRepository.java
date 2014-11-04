package com.bzanni.parisaccessible.elasticsearch.repository.jest;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;

import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

public abstract class AbstractJestRepository<T extends JestBusiness> {

	private final static int MAX_RETRY = 3;
	@Resource
	private ParisAccessibleJestClient client;

	protected abstract String getIndex();

	protected abstract String getType();

	private Class<T> klass;

	// @PostConstruct
	// public void initilization() {
	// client = new ParisAccessibleJestClient(host);
	// }

	@PreDestroy
	public void destroy() {
		if (client != null) {
			client.shutdownClient();
		}
	}

	protected boolean mappings(Class<T> k, int shards, int replicas,
			RootObjectMapper.Builder rootObjectMapperBuilder) throws Exception {
		ImmutableSettings.Builder settingsBuilder = ImmutableSettings
				.settingsBuilder();
		klass = k;
		settingsBuilder.put("number_of_shards", shards);
		settingsBuilder.put("number_of_replicas", replicas);
		JestResult execute;
		execute = client.getClient().execute(
				new CreateIndex.Builder(this.getIndex()).settings(
						settingsBuilder.build().getAsMap()).build());

		DocumentMapper documentMapper = new DocumentMapper.Builder(
				this.getIndex(), null, rootObjectMapperBuilder).build(null);
		String expectedMappingSource = documentMapper.mappingSource()
				.toString();
		PutMapping putMapping = new PutMapping.Builder(this.getIndex(),
				this.getType(), expectedMappingSource).build();

		execute = client.getClient().execute(putMapping);

		return execute.isSucceeded();
	}

	public void save(T object) throws Exception {
		Index index = new Index.Builder(object).index(this.getIndex())
				.type(this.getType()).build();
		client.getClient().execute(index);
	}
	
	public void save(List<T> object) throws Exception {
		this.save(object, 0);
	}

	public void save(List<T> object, int retry) throws Exception {
		Builder builder = new Bulk.Builder();
		for (T t : object) {
			builder.addAction(new Index.Builder(t).refresh(false)
					.index(this.getIndex()).type(this.getType()).build());
		}

		try {
			client.getClient().execute(builder.build());
		} catch (SocketTimeoutException e) {
			retry++;
			if(retry < AbstractJestRepository.MAX_RETRY){
//				this.save(object, retry);
				client.getClient().execute(builder.build());
			}
		}
	}

	public T findById(String id) throws Exception {
		Get get = new Get.Builder(this.getIndex(), id).type(this.getType())
				.build();

		JestResult result = client.getClient().execute(get);

		return result.getSourceAsObject(klass);
	}

	public T prepare(String id) throws Exception {
		T object = this.findById(id);
		if (object == null) {
			object = klass.newInstance();
			object.setId(id);
		}
		return object;
	}

	public JestClient getClient() {
		return client.getClient();
	}
}
