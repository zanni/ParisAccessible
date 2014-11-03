package com.bzanni.parisaccessible.elasticsearch.repository.ratp;

import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
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
