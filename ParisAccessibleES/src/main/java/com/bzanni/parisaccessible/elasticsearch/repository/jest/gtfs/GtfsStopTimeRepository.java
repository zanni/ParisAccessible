package com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs;

import javax.annotation.PostConstruct;

import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsStopTime;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.AbstractJestRepository;

@Service
@Configurable
public class GtfsStopTimeRepository extends
		AbstractJestRepository<GtfsStopTime> {

	@Value("${ratp_gtfs_index_name}")
	private String index;

	public boolean mappings() throws Exception {
		return super.mappings(
				GtfsStopTime.class,
				5,
				0,
				new RootObjectMapper.Builder(this.getType())
						.add(new StringFieldMapper.Builder("time").store(false)
								.index(false))
						.add(new StringFieldMapper.Builder("trip_id").store(
								false).index(false))
						.add(new StringFieldMapper.Builder("stop_id").store(
								false).index(false))
						.add(new StringFieldMapper.Builder("seq").store(false)
								.index(false)));
	}

	@PostConstruct
	private void init() {
		try {
			mappings();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected String getIndex() {
		return index;
	}

	@Override
	protected String getType() {
		return GtfsStopTime.class.getSimpleName().toLowerCase();
	}

}
