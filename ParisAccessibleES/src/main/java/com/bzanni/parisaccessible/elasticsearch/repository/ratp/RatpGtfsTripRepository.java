package com.bzanni.parisaccessible.elasticsearch.repository.ratp;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsTrip;
import com.bzanni.parisaccessible.elasticsearch.repository.AbstractJestRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.ParisAccessibleJestClient;

@Service
public class RatpGtfsTripRepository extends
		AbstractJestRepository<RatpGtfsTrip> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	public final static String type = RatpGtfsTrip.class.getSimpleName()
			.toLowerCase();

	@Resource
	private ParisAccessibleJestClient client;

	@Override
	protected String getIndex() {
		return this.index;
	}

	@Override
	protected String getType() {
		return RatpGtfsTrip.class.getSimpleName().toLowerCase();
	}

	private boolean mappings() throws Exception {
		return super.mappings(RatpGtfsTrip.class, 3, 0,
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
