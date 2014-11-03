package com.bzanni.parisaccessible.elasticsearch.repository.gtfs;

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

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsStop;
import com.bzanni.parisaccessible.elasticsearch.repository.AbstractJestRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.ParisAccessibleJestClient;

@Service
public class GtfsStopRepository extends
		AbstractJestRepository<GtfsStop> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	public final static String type = GtfsStop.class.getSimpleName()
			.toLowerCase();

	private boolean mappings() throws Exception {
		return super.mappings(GtfsStop.class, 3, 0,
				new RootObjectMapper.Builder(GtfsStopRepository.type)
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

	public List<GtfsStop> search(Double lat, Double lon, String distance)
			throws Exception {

		FilteredQueryBuilder filteredQuery = QueryBuilders.filteredQuery(
				QueryBuilders.matchAllQuery(), FilterBuilders
						.geoDistanceFilter("location").lat(lat).lon(lon)
						.distance(distance));

		String query = "{ \"query\":" + filteredQuery.buildAsBytes().toUtf8()
				+ "}";
		Search count = new Search.Builder(query)
				// multiple index or types can be added.
				.addIndex(this.index).addType(GtfsStopRepository.type)
				.build();

		SearchResult result = this.getClient().execute(count);

		return result.getSourceAsObjectList(GtfsStop.class);
	}

	@Override
	protected String getIndex() {
		return this.index;
	}

	@Override
	protected String getType() {
		return GtfsStop.class.getSimpleName().toLowerCase();
	}
}
