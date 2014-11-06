package com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsStop;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.AbstractJestRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.JestQueryEngine;

@Service
public class GtfsStopRepository extends AbstractJestRepository<GtfsStop> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	@Resource
	private JestQueryEngine queryEngine;

	public final static String type = GtfsStop.class.getSimpleName()
			.toLowerCase();

	private boolean mappings() throws Exception {
		// Builder root = new RootObjectMapper.Builder(GtfsStopRepository.type)
		// .add(new GeoPointFieldMapper.Builder("location").store(true)
		// .index(true))
		// .add(new StringFieldMapper.Builder("name").store(true).index(
		// true))
		// .add(new StringFieldMapper.Builder("description").store(false)
		// .index(false))
		// .add(new BooleanFieldMapper.Builder("accessibleUFR")
		// .store(true).index(true))
		// .add(new BooleanFieldMapper.Builder(
		// "annonceSonoreProchainArret").store(true).index(true))
		// .add(new BooleanFieldMapper.Builder(
		// "annonceVisuelleProchainArret").store(true).index(true))
		// .add(new BooleanFieldMapper.Builder(
		// "annonceSonoreSituationPerturbe").store(true).index(
		// true))
		// .add(new BooleanFieldMapper.Builder(
		// "annonceVisuelleSituationPerturbe").store(true).index(
		// true));
		//
		// return super.mappings(GtfsStop.class, 5, 0, root);

		return super.mappings(GtfsStop.class, 5, 0);
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

	public List<GtfsStop> findLocation(Double lat, Double lon, String distance)
			throws Exception {

//		FilteredQueryBuilder filteredQuery = QueryBuilders.filteredQuery(
//				QueryBuilders.matchAllQuery(), FilterBuilders
//						.geoDistanceFilter("location").lat(lat).lon(lon)
//						.distance(distance));
//
//		String query = "{ \"query\":" + filteredQuery.buildAsBytes().toUtf8()
//				+ "}";
		Search count = new Search.Builder(queryEngine.geoDistanceQuery("location", lat, lon, distance))
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
