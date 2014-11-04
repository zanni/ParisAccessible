package com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs;

import io.searchbox.client.JestResult;
import io.searchbox.core.Search;

import java.util.List;

import javax.annotation.PostConstruct;

import org.elasticsearch.index.mapper.core.BooleanFieldMapper;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper.Builder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsRoute;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.AbstractJestRepository;

@Service
@Configurable
public class GtfsRouteRepository extends AbstractJestRepository<GtfsRoute> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	private boolean mappings() throws Exception {
		Builder root = new RootObjectMapper.Builder(this.getType())
				.add(new StringFieldMapper.Builder("name").store(false).index(
						false))
				.add(new StringFieldMapper.Builder("longName").store(false)
						.index(false))
				.add(new StringFieldMapper.Builder("description").store(false)
						.index(false))
				.add(new StringFieldMapper.Builder("route_color").store(false)
						.index(false))
				.add(new StringFieldMapper.Builder("route_color_text").store(
						false).index(false))
				.add(new StringFieldMapper.Builder("origin").store(false)
						.index(false))
				.add(new StringFieldMapper.Builder("destination").store(false)
						.index(false))
				.add(new BooleanFieldMapper.Builder("accessibleUFR").store(
						false).index(false))
				.add(new BooleanFieldMapper.Builder("plancherBas").store(false)
						.index(false))
				.add(new BooleanFieldMapper.Builder("palette").store(false)
						.index(false))
				.add(new BooleanFieldMapper.Builder(
						"annonceSonoreProchainArret").store(false).index(false))
				.add(new BooleanFieldMapper.Builder(
						"annonceVisuelleProchainArret").store(false).index(
						false));
		return super.mappings(GtfsRoute.class, 3, 0, root);
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

	public List<GtfsRoute> findByShortName(String name) throws Exception {

		TermQueryBuilder termQuery = QueryBuilders.termQuery("name", name);

		String query = "{ \"query\":" + termQuery.buildAsBytes().toUtf8() + "}";

		Search search = new Search.Builder(query)
		// multiple index or types can be added.
				.addIndex(this.index).addType(this.getType()).build();

		JestResult result = this.getClient().execute(search);

		return result.getSourceAsObjectList(GtfsRoute.class);
	}

	@Override
	protected String getIndex() {
		return this.index;
	}

	@Override
	protected String getType() {
		return GtfsRoute.class.getSimpleName().toLowerCase();
	}
}
