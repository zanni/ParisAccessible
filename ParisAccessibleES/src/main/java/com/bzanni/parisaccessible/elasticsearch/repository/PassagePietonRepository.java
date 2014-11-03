package com.bzanni.parisaccessible.elasticsearch.repository;

import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.geo.GeoPointFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.accessibility.PassagePieton;

@Service
public class PassagePietonRepository {

	public final static String index = PassagePieton.class.getSimpleName()
			.toLowerCase() + "_mappings";

	public final static String type = PassagePieton.class.getSimpleName()
			.toLowerCase();

	@Resource
	private ParisAccessibleJestClient client;

	private boolean mappings() throws Exception {

		ImmutableSettings.Builder settingsBuilder = ImmutableSettings
				.settingsBuilder();
		settingsBuilder.put("number_of_shards", 3);
		settingsBuilder.put("number_of_replicas", 0);
		client.getClient().execute(
				new CreateIndex.Builder(PassagePietonRepository.index)
						.settings(settingsBuilder.build().getAsMap()).build());

		RootObjectMapper.Builder rootObjectMapperBuilder = new RootObjectMapper.Builder(
				PassagePietonRepository.type).add(
				new GeoPointFieldMapper.Builder("start")).add(
				new GeoPointFieldMapper.Builder("end"));

		DocumentMapper documentMapper = new DocumentMapper.Builder(
				PassagePietonRepository.index, null, rootObjectMapperBuilder)
				.build(null);
		String expectedMappingSource = documentMapper.mappingSource()
				.toString();
		PutMapping putMapping = new PutMapping.Builder(
				PassagePietonRepository.index, PassagePietonRepository.type,
				expectedMappingSource).build();

		JestResult execute = client.getClient().execute(putMapping);

		return execute.isSucceeded();
	}

	@PostConstruct
	public void init() {
		try {
			mappings();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save(PassagePieton object) throws Exception {
		Index index = new Index.Builder(object)
				.index(PassagePietonRepository.index)
				.type(PassagePietonRepository.type).build();
		client.getClient().execute(index);
	}

	public void save(List<PassagePieton> object) throws Exception {
		Builder builder = new Bulk.Builder();
		for (PassagePieton t : object) {
			builder.addAction(new Index.Builder(t)
					.index(PassagePietonRepository.index)
					.type(PassagePietonRepository.type).build());
		}

		client.getClient().execute(builder.build());
	}

	public PassagePieton findById(String id) throws Exception {
		Get get = new Get.Builder(PassagePietonRepository.index, id).type(
				PassagePietonRepository.type).build();

		JestResult result = client.getClient().execute(get);

		return result.getSourceAsObject(PassagePieton.class);
	}

	public PassagePieton prepare(String id) throws Exception {
		PassagePieton object = this.findById(id);
		if (object == null) {
			object = new PassagePieton();
			object.setId(id);
		}
		return object;
	}
}
