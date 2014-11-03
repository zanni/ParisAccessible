package com.bzanni.parisaccessible.elasticsearch.repository.ratp;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsAgency;
import com.bzanni.parisaccessible.elasticsearch.repository.AbstractJestRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.ParisAccessibleJestClient;

@Service
@Configurable
public class RatpGtfsAgencyRepository extends
		AbstractJestRepository<RatpGtfsAgency> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	@Resource
	private ParisAccessibleJestClient client;

	private boolean mappings() throws Exception {

		return super.mappings(RatpGtfsAgency.class, 1, 0,
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
		return RatpGtfsAgency.class.getSimpleName().toLowerCase();
	}
}
