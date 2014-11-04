package com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.util.List;

import javax.annotation.PostConstruct;

import org.elasticsearch.index.mapper.core.BooleanFieldMapper;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.geo.GeoPointFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper.Builder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsStop;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.AbstractJestRepository;

@Service
public class GtfsStopRepository extends AbstractJestRepository<GtfsStop> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	public final static String type = GtfsStop.class.getSimpleName()
			.toLowerCase();

	private boolean mappings() throws Exception {
		Builder root = new RootObjectMapper.Builder(GtfsStopRepository.type)
				.add(new GeoPointFieldMapper.Builder("location").store(true)
						.index(true))
				.add(new StringFieldMapper.Builder("name").store(false).index(
						false))
				.add(new StringFieldMapper.Builder("description").store(false)
						.index(false))
				.add(new BooleanFieldMapper.Builder("accessibleUFR").store(
						false).index(false))
				.add(new BooleanFieldMapper.Builder("annonceSonoreProchainArret").store(
						false).index(false))
				.add(new BooleanFieldMapper.Builder("annonceVisuelleProchainArret").store(
						false).index(false))
				.add(new BooleanFieldMapper.Builder("annonceSonoreSituationPerturbe").store(
						false).index(false))
				.add(new BooleanFieldMapper.Builder("annonceVisuelleSituationPerturbe").store(
						false).index(false));

		return super.mappings(GtfsStop.class, 5, 0, root);
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
				.addIndex(this.index).addType(GtfsStopRepository.type).build();

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
