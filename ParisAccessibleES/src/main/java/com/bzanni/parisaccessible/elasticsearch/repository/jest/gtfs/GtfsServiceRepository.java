package com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsService;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.AbstractJestRepository;

@Service
public class GtfsServiceRepository extends AbstractJestRepository<GtfsService> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	public final static String type = GtfsService.class.getSimpleName()
			.toLowerCase();

	@Override
	protected String getIndex() {
		return this.index;
	}

	@Override
	protected String getType() {
		return GtfsService.class.getSimpleName().toLowerCase();
	}

	private boolean mappings() throws Exception {

		// Builder root = new RootObjectMapper.Builder(this.getType())
		// .add(new DateFieldMapper.Builder("startDate").store(false)
		// .index(false))
		// .add(new DateFieldMapper.Builder("endDate").store(false).index(
		// false))
		// .add(new DateFieldMapper.Builder("calendar").store(false)
		// .index(false));
		// return super.mappings(GtfsService.class, 5, 0, root);
		
		return super.mappings(GtfsService.class, 5, 0);
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

}
