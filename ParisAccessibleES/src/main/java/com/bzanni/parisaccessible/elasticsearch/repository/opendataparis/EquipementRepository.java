package com.bzanni.parisaccessible.elasticsearch.repository.opendataparis;

import javax.annotation.PostConstruct;

import org.elasticsearch.index.mapper.geo.GeoPointFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.opendataparis.Equipement;
import com.bzanni.parisaccessible.elasticsearch.repository.AbstractJestRepository;

@Service
public class EquipementRepository extends AbstractJestRepository<Equipement> {

	@Value("${accesibility_index_name}")
	private String index;

	@Override
	protected String getIndex() {
		return index;
	}

	@Override
	protected String getType() {
		return Equipement.class.getSimpleName().toLowerCase();
	}

	private boolean mappings() throws Exception {
		return super.mappings(Equipement.class, 3, 0,
				new RootObjectMapper.Builder(this.getType()).add(
						new GeoPointFieldMapper.Builder("location")));
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
