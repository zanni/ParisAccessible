package com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs;

import javax.annotation.PostConstruct;

import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsTrip;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.AbstractJestRepository;

@Service
public class GtfsTripRepository extends AbstractJestRepository<GtfsTrip> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	public final static String type = GtfsTrip.class.getSimpleName()
			.toLowerCase();

	@Override
	protected String getIndex() {
		return this.index;
	}

	@Override
	protected String getType() {
		return GtfsTrip.class.getSimpleName().toLowerCase();
	}

	private boolean mappings() throws Exception {
		Builder root = new RootObjectMapper.Builder(this.getType()).add(
				new StringFieldMapper.Builder("route_id").store(true)
						.index(true)).add(
				new StringFieldMapper.Builder("service_id").store(true)
						.index(true));
		return super.mappings(GtfsTrip.class, 5, 0, root);
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
