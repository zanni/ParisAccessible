package com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs;

import io.searchbox.client.JestResult;
import io.searchbox.core.Search;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsRoute;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.AbstractJestRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.JestQueryEngine;

@Service
@Configurable
public class GtfsRouteRepository extends AbstractJestRepository<GtfsRoute> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	@Resource
	private JestQueryEngine queryEngine;

	private boolean mappings() throws Exception {
		// Builder root = new RootObjectMapper.Builder(this.getType())
		// .add(new StringFieldMapper.Builder("name").store(true).index(
		// true))
		// .add(new StringFieldMapper.Builder("longName").store(true)
		// .index(true))
		// .add(new StringFieldMapper.Builder("description").store(false)
		// .index(false))
		// .add(new StringFieldMapper.Builder("route_color").store(false)
		// .index(false))
		// .add(new StringFieldMapper.Builder("route_color_text").store(
		// false).index(false))
		// .add(new StringFieldMapper.Builder("origin").store(false)
		// .index(false))
		// .add(new StringFieldMapper.Builder("destination").store(false)
		// .index(false))
		// .add(new BooleanFieldMapper.Builder("accessibleUFR").store(
		// true).index(true))
		// .add(new BooleanFieldMapper.Builder("plancherBas").store(true)
		// .index(false))
		// .add(new BooleanFieldMapper.Builder("palette").store(true)
		// .index(true))
		// .add(new BooleanFieldMapper.Builder(
		// "annonceSonoreProchainArret").store(true).index(true))
		// .add(new BooleanFieldMapper.Builder(
		// "annonceVisuelleProchainArret").store(true).index(
		// true));
		// return super.mappings(GtfsAgency.class, 5, 0, root);

		return super.mappings(GtfsRoute.class, 5, 0);
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

//		TermQueryBuilder termQuery = QueryBuilders.termQuery("name", name);
//
//		String query = "{ \"query\":" + termQuery.buildAsBytes().toUtf8() + "}";

		Search search = new Search.Builder(queryEngine.termQuery("name", name))
		// multiple index or types can be added.
				.addIndex(this.index).addType(this.getType()).build();

		JestResult result = this.getClient().execute(search);

		return result.getSourceAsObjectList(GtfsRoute.class);
	}

	@Override
	public String getIndex() {
		return this.index;
	}

	@Override
	public String getType() {
		return GtfsRoute.class.getSimpleName().toLowerCase();
	}
}
