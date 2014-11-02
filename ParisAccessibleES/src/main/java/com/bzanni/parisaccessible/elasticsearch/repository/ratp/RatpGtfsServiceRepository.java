package com.bzanni.parisaccessible.elasticsearch.repository.ratp;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.ratp.RatpGtfsService;
import com.bzanni.parisaccessible.elasticsearch.repository.AbstractJestRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.ParisAccessibleJestClient;

@Service
public class RatpGtfsServiceRepository extends
		AbstractJestRepository<RatpGtfsService> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	public final static String type = RatpGtfsService.class.getSimpleName()
			.toLowerCase();

	@Resource
	private ParisAccessibleJestClient client;
	
	@Override
	protected String getIndex() {
		return this.index;
	}

	@Override
	protected String getType() {
		return RatpGtfsService.class.getSimpleName().toLowerCase();
	}

	private boolean mappings() throws Exception {
		return super.mappings(RatpGtfsService.class, 3, 0,
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
