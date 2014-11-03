package com.bzanni.parisaccessible.elasticsearch.repository.gtfs;

import javax.annotation.PostConstruct;

import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsService;
import com.bzanni.parisaccessible.elasticsearch.repository.AbstractJestRepository;

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
		return super.mappings(GtfsService.class, 3, 0,
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

}
