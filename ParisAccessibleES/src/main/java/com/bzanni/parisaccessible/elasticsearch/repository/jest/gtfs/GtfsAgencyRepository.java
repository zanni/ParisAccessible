package com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs;

import javax.annotation.PostConstruct;

import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper.Builder;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsAgency;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.AbstractJestRepository;

@Service
@Configurable
public class GtfsAgencyRepository extends AbstractJestRepository<GtfsAgency> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	private boolean mappings() throws Exception {
		Builder root = new RootObjectMapper.Builder(this.getType())
				.add(new StringFieldMapper.Builder("name").store(true).index(
						true))
				.add(new StringFieldMapper.Builder("url").store(false).index(
						false))
				.add(new StringFieldMapper.Builder("timezone").store(false)
						.index(false))
				.add(new StringFieldMapper.Builder("lang").store(false).index(
						false))
				.add(new StringFieldMapper.Builder("phone").store(false).index(
						false));

		return super.mappings(GtfsAgency.class, 5, 0, root);
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
