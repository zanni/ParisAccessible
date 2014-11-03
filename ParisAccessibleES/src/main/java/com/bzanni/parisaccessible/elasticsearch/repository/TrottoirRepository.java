package com.bzanni.parisaccessible.elasticsearch.repository;

import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Count;
import io.searchbox.core.CountResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.elasticsearch.common.geo.builders.CircleBuilder;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.geo.GeoShapeFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.accessibility.Trottoir;

@Service
public class TrottoirRepository {
	public final static String index = Trottoir.class.getSimpleName()
			.toLowerCase() + "_mappings";

	public final static String type = Trottoir.class.getSimpleName()
			.toLowerCase();

	@Resource
	private ParisAccessibleJestClient client;

	private boolean mappings() throws Exception {

		ImmutableSettings.Builder settingsBuilder = ImmutableSettings
				.settingsBuilder();
		settingsBuilder.put("number_of_shards", 3);
		settingsBuilder.put("number_of_replicas", 0);
		client.getClient().execute(
				new CreateIndex.Builder(TrottoirRepository.index).settings(
						settingsBuilder.build().getAsMap()).build());

		RootObjectMapper.Builder rootObjectMapperBuilder = new RootObjectMapper.Builder(
				TrottoirRepository.type).add(new GeoShapeFieldMapper.Builder(
				"shape"));

		DocumentMapper documentMapper = new DocumentMapper.Builder(
				TrottoirRepository.index, null, rootObjectMapperBuilder)
				.build(null);
		String expectedMappingSource = documentMapper.mappingSource()
				.toString();
		PutMapping putMapping = new PutMapping.Builder(
				TrottoirRepository.index, TrottoirRepository.type,
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

	public void save(Trottoir object) throws Exception {
		Index index = new Index.Builder(object).index(TrottoirRepository.index)
				.type(TrottoirRepository.type).build();
		client.getClient().execute(index);
	}

	public void save(List<Trottoir> object) throws Exception {
		Builder builder = new Bulk.Builder();
		for (Trottoir t : object) {
			builder.addAction(new Index.Builder(t)
					.index(TrottoirRepository.index)
					.type(TrottoirRepository.type).build());
		}

		client.getClient().execute(builder.build());
	}

	public Trottoir findById(String id) throws Exception {
		Get get = new Get.Builder(TrottoirRepository.index, id).type(
				TrottoirRepository.type).build();

		JestResult result = client.getClient().execute(get);

		return result.getSourceAsObject(Trottoir.class);
	}

	public Trottoir prepare(String id) throws Exception {
		Trottoir object = this.findById(id);
		if (object == null) {
			object = new Trottoir();
			object.setId(id);
		}
		return object;
	}

	public Double count(Double lat, Double lon, String distance)
			throws Exception {

		GeoShapeQueryBuilder geoShapeQuery = QueryBuilders.geoShapeQuery(
				"shape", new CircleBuilder().center(lat, lon).radius(distance));

		String query = "{ \"query\":" + geoShapeQuery.buildAsBytes().toUtf8()
				+ "}";
		Count count = new Count.Builder().query(query)

				// multiple index or types can be added.
				.addIndex(TrottoirRepository.index)
				.addType(TrottoirRepository.type).build();

		CountResult result = client.getClient().execute(count);

		return result.getCount();
	}
}
