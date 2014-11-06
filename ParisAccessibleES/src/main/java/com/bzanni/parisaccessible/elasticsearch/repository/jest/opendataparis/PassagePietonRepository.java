package com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.opendataparis.PassagePieton;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.AbstractJestRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.JestQueryEngine;

@Service
public class PassagePietonRepository extends
		AbstractJestRepository<PassagePieton> {

	@Value("${accesibility_index_name}")
	private String index;

	@Resource
	private JestQueryEngine queryEngine;
	
	@Override
	protected String getIndex() {
		return index;
	}

	@Override
	protected String getType() {
		return PassagePieton.class.getSimpleName().toLowerCase();
	}

	public final static String type = PassagePieton.class.getSimpleName()
			.toLowerCase();

	private boolean mappings() throws Exception {
		// Builder root = new RootObjectMapper.Builder(
		// PassagePietonRepository.type)
		// .add(new GeoPointFieldMapper.Builder("start").store(true)
		// .index(true))
		// .add(new GeoPointFieldMapper.Builder("end").store(true).index(
		// true))
		// .add(new StringFieldMapper.Builder("info").store(false).index(
		// false))
		// .add(new StringFieldMapper.Builder("libelle").store(false)
		// .index(false))
		// .add(new StringFieldMapper.Builder("info_ft_style")
		// .store(false).index(false))
		// .add(new StringFieldMapper.Builder("libelle_ft_style").store(
		// false).index(false))
		// .add(new StringFieldMapper.Builder("import_notes").store(false)
		// .index(false));
		// return super.mappings(PassagePieton.class, 5, 0, root);
		
		return super.mappings(PassagePieton.class, 5, 0);
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

	public List<PassagePieton> findStart(Double lat, Double lon, String distance)
			throws Exception {

//		FilteredQueryBuilder filteredQuery = QueryBuilders.filteredQuery(
//				QueryBuilders.matchAllQuery(),
//				FilterBuilders.geoDistanceFilter("start").lat(lat).lon(lon)
//						.distance(distance));
//
//		String query = "{ \"query\":" + filteredQuery.buildAsBytes().toUtf8()
//				+ "}";

		Search count = new Search.Builder(queryEngine.geoDistanceQuery("start", lat, lon, distance)).addIndex(this.getIndex())
				.addType(this.getType()).build();

		SearchResult result = this.getClient().execute(count);

		return result.getSourceAsObjectList(PassagePieton.class);
		
	}

	public List<PassagePieton> findEnd(Double lat, Double lon, String distance)
			throws Exception {

//		FilteredQueryBuilder filteredQuery = QueryBuilders.filteredQuery(
//				QueryBuilders.matchAllQuery(),
//				FilterBuilders.geoDistanceFilter("end").lat(lat).lon(lon)
//						.distance(distance));
//
//		String query = "{ \"query\":" + filteredQuery.buildAsBytes().toUtf8()
//				+ "}";

		Search count = new Search.Builder(queryEngine.geoDistanceQuery("end", lat, lon, distance)).addIndex(this.getIndex())
				.addType(this.getType()).build();

		SearchResult result = this.getClient().execute(count);

		return result.getSourceAsObjectList(PassagePieton.class);
	}

}
