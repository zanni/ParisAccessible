package com.bzanni.parisaccessible.elasticsearch.repository.ratp;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.elasticsearch.index.mapper.geo.GeoPointFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsStop;
import com.bzanni.parisaccessible.elasticsearch.repository.AbstractJestRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.ParisAccessibleJestClient;

@Service
public class RatpGtfsStopRepository extends
		AbstractJestRepository<RatpGtfsStop> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	public final static String type = RatpGtfsStop.class.getSimpleName()
			.toLowerCase();

	@Resource
	private ParisAccessibleJestClient client;

	private boolean mappings() throws Exception {
		return super.mappings(RatpGtfsStop.class, 3, 0,
				new RootObjectMapper.Builder(RatpGtfsStopRepository.type)
						.add(new GeoPointFieldMapper.Builder("location")));
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

	public List<RatpGtfsStop> search(Double lat, Double lon, String distance)
			throws Exception {

		FilteredQueryBuilder filteredQuery = QueryBuilders.filteredQuery(
				QueryBuilders.matchAllQuery(), FilterBuilders
						.geoDistanceFilter("location").lat(lat).lon(lon)
						.distance(distance));

		String query = "{ \"query\":" + filteredQuery.buildAsBytes().toUtf8()
				+ "}";
		Search count = new Search.Builder(query)
				// multiple index or types can be added.
				.addIndex(this.index).addType(RatpGtfsStopRepository.type)
				.build();

		SearchResult result = client.getClient().execute(count);

		return result.getSourceAsObjectList(RatpGtfsStop.class);
	}

	@Override
	protected String getIndex() {
		return this.index;
	}

	@Override
	protected String getType() {
		return RatpGtfsStop.class.getSimpleName().toLowerCase();
	}
}
