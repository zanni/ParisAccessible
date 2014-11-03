package com.bzanni.parisaccessible.elasticsearch.repository.gtfs;

import io.searchbox.client.JestResult;
import io.searchbox.core.Search;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsRoute;
import com.bzanni.parisaccessible.elasticsearch.repository.AbstractJestRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.ParisAccessibleJestClient;

@Service
@Configurable
public class GtfsRouteRepository extends
		AbstractJestRepository<GtfsRoute> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	private boolean mappings() throws Exception {

		return super.mappings(GtfsRoute.class, 3, 0,
				new RootObjectMapper.Builder(this.getType()));
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
