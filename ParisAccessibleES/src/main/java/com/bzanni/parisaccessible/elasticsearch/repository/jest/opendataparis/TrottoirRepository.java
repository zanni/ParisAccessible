package com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis;

import io.searchbox.core.Count;
import io.searchbox.core.CountResult;

import javax.annotation.PostConstruct;

import org.elasticsearch.common.geo.builders.CircleBuilder;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.geo.GeoPointFieldMapper;
import org.elasticsearch.index.mapper.geo.GeoShapeFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.opendataparis.Trottoir;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.AbstractJestRepository;

@Service
public class TrottoirRepository extends AbstractJestRepository<Trottoir> {
	@Value("${accesibility_index_name}")
	private String index;

	@Override
	protected String getIndex() {
		return index;
	}

	@Override
	protected String getType() {
		return Trottoir.class.getSimpleName().toLowerCase();
	}


	private boolean mappings() throws Exception {

		return super.mappings(
				Trottoir.class,
				5,
				0,
				new RootObjectMapper.Builder(this.getType())
//						.add(new GeoPointFieldMapper.Builder("location"))
						.add(new GeoShapeFieldMapper.Builder("shape"))
						.add(new StringFieldMapper.Builder("info")
								.store(true).index(true)));
//						.add(new StringFieldMapper.Builder("libelle")
//								.store(false).index(false))
//						.add(new StringFieldMapper.Builder("niveau")
//								.store(false).index(false)));

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

	public Double count(Double lat, Double lon, String distance)
			throws Exception {

		GeoShapeQueryBuilder geoShapeQuery = QueryBuilders.geoShapeQuery(
				"shape", new CircleBuilder().center(lat, lon).radius(distance));

		String query = "{ \"query\":" + geoShapeQuery.buildAsBytes().toUtf8()
				+ "}";
		Count count = new Count.Builder().query(query)

		// multiple index or types can be added.
				.addIndex(this.getIndex()).addType(this.getType()).build();

		CountResult result = this.getClient().execute(count);

		return result.getCount();
	}
}
