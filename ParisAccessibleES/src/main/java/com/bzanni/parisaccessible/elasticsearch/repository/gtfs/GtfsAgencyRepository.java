package com.bzanni.parisaccessible.elasticsearch.repository.gtfs;

import javax.annotation.PostConstruct;

import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsAgency;
import com.bzanni.parisaccessible.elasticsearch.repository.AbstractJestRepository;

@Service
@Configurable
public class GtfsAgencyRepository extends AbstractJestRepository<GtfsAgency> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	private boolean mappings() throws Exception {

		return super.mappings(GtfsAgency.class, 1, 0,
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

	@Override
	protected String getIndex() {
		return this.index;
	}

	@Override
	protected String getType() {
		return GtfsAgency.class.getSimpleName().toLowerCase();
	}
}
