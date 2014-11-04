package com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.geo.GeoPointFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.opendataparis.PassagePieton;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.AbstractJestRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.ParisAccessibleJestClient;

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
		Builder root = new RootObjectMapper.Builder(
				PassagePietonRepository.type)
				.add(new GeoPointFieldMapper.Builder("start").store(true)
						.index(true))
				.add(new GeoPointFieldMapper.Builder("end").store(true).index(
						true))
				.add(new StringFieldMapper.Builder("info").store(false).index(
						false))
				.add(new StringFieldMapper.Builder("libelle").store(false)
						.index(false))
				.add(new StringFieldMapper.Builder("info_ft_style")
						.store(false).index(false))
				.add(new StringFieldMapper.Builder("libelle_ft_style").store(
						false).index(false))
				.add(new StringFieldMapper.Builder("import_notes").store(false)
						.index(false));
		return super.mappings(PassagePieton.class, 5, 0, root);
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
