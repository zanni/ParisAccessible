package com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.util.Iterator;
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

	private final static String PASSAGEPIETON_TAG = "PVPPAPI";

	@Value("${accesibility_index_name}")
	private String index;

	@Resource
	private JestQueryEngine queryEngine;

	@Override
	public String getIndex() {
		return index;
	}

	@Override
	public String getType() {
		return PassagePieton.class.getSimpleName().toLowerCase();
	}

	public final static String type = PassagePieton.class.getSimpleName()
			.toLowerCase();

	private boolean mappings() throws Exception {
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

		Search count = new Search.Builder(queryEngine.geoDistanceQuery("start",
				lat, lon, distance)).addIndex(this.getIndex())
				.addType(this.getType()).build();

		SearchResult result = this.getClient().execute(count);

		return result.getSourceAsObjectList(PassagePieton.class);

	}

	public List<PassagePieton> findEnd(Double lat, Double lon, String distance)
			throws Exception {

		Search count = new Search.Builder(queryEngine.geoDistanceQuery("end",
				lat, lon, distance)).addIndex(this.getIndex())
				.addType(this.getType()).build();

		SearchResult result = this.getClient().execute(count);

		return result.getSourceAsObjectList(PassagePieton.class);
	}

	public Iterator<List<PassagePieton>> findAllFiltered(int index_worker,
			int total_worker) {
		String query = queryEngine.termQuery("info",
				PassagePietonRepository.PASSAGEPIETON_TAG);
		return this.findAllWorker(index_worker, total_worker, query);
	}

}
