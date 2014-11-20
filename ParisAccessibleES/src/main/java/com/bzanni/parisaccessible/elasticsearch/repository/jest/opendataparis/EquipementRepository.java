package com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.opendataparis.Equipement;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.AbstractJestRepository;

@Service
public class EquipementRepository extends AbstractJestRepository<Equipement> {

	@Value("${accesibility_index_name}")
	private String index;

	@Override
	public String getIndex() {
		return index;
	}

	@Override
	public String getType() {
		return Equipement.class.getSimpleName().toLowerCase();
	}

	private boolean mappings() throws Exception {
		// Builder root = new RootObjectMapper.Builder(this.getType())
		// .add(new GeoPointFieldMapper.Builder("location").store(true)
		// .index(true))
		// .add(new StringFieldMapper.Builder("type").store(true).index(
		// true))
		// .add(new StringFieldMapper.Builder("nom").store(true).index(
		// true))
		// .add(new StringFieldMapper.Builder("numero").store(false)
		// .index(false))
		// .add(new StringFieldMapper.Builder("voie").store(false).index(
		// false))
		// .add(new StringFieldMapper.Builder("code_postal").store(false)
		// .index(false))
		// .add(new StringFieldMapper.Builder("remarques").store(false)
		// .index(false))
		// .add(new StringFieldMapper.Builder("lien").store(false).index(
		// false))
		// .add(new BooleanFieldMapper.Builder("handicapMoteur").store(
		// true).index(true))
		// .add(new BooleanFieldMapper.Builder("handicapVisuel").store(
		// true).index(true))
		// .add(new BooleanFieldMapper.Builder("handicapAuditif").store(
		// true).index(true));
		// return super.mappings(Equipement.class, 5, 0, root);
		
		return super.mappings(Equipement.class, 5, 0);
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
