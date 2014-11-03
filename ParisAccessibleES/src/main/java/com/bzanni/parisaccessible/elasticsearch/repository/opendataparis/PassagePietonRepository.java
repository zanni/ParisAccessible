package com.bzanni.parisaccessible.elasticsearch.repository.opendataparis;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.elasticsearch.index.mapper.geo.GeoPointFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.opendataparis.PassagePieton;
import com.bzanni.parisaccessible.elasticsearch.repository.AbstractJestRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.ParisAccessibleJestClient;

@Service
public class PassagePietonRepository extends
		AbstractJestRepository<PassagePieton> {

	@Value("${accesibility_index_name}")
	private String index;

	@Override
	protected String getIndex() {
		return index;
	}

	@Override
	protected String getType() {
		return PassagePieton.class.getSimpleName().toLowerCase();
	}

	public final static String type = PassagePieton.class.getSimpleName()
			.toLowerCase();

	private boolean mappings() throws Exception {
		return super.mappings(
				PassagePieton.class,
				3,
				0,
				new RootObjectMapper.Builder(PassagePietonRepository.type).add(
						new GeoPointFieldMapper.Builder("start")).add(
						new GeoPointFieldMapper.Builder("end")));
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
