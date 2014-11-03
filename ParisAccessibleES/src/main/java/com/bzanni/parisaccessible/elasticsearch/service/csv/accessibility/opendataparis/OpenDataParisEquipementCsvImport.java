package com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.opendataparis;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.opendataparis.Equipement;
import com.bzanni.parisaccessible.elasticsearch.repository.opendataparis.EquipementRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

/**
 * 
 * type;nom;numero;voie;code_postal;remarques;handicap_moteur;handicap_visuel;
 * handicap_auditif;lien;coordx_wgs;coordy_wgs;column_14
 * 
 * @author bertrandzanni
 *
 */
@Service
public class OpenDataParisEquipementCsvImport extends
		GenericCsvImporter<Equipement> {

	@Resource
	private EquipementRepository equipementRepository;

	@Override
	public char delimiter() {
		return ';';
	}

	@Override
	protected List<Equipement> convert(String[] line) throws Exception {
		if (line.length < 14) {
			Equipement eq = new Equipement();
			eq.setId(line[1] + line[2]);
			eq.setNom(line[1]);
			eq.setNumero(line[2]);
			eq.setVoie(line[3]);
			eq.setCode_postal(line[4]);
			eq.setRemarques(line[5]);
			boolean bool;
			bool = (line[6].equals("1")) ? true : false;
			eq.setHandicapMoteur(bool);
			bool = (line[7].equals("1")) ? true : false;
			eq.setHandicapVisuel(bool);
			bool = (line[8].equals("1")) ? true : false;
			eq.setHandicapAuditif(bool);
			eq.setLien(line[9]);
			Double lat = Double.valueOf(line[12].split(",")[0]);
			Double lon = Double.valueOf(line[12].split(",")[1]);
			eq.setLocation(new GeoPoint(lat, lon));
			List<Equipement> res = new ArrayList<Equipement>();
			res.add(eq);
			return res;
		}
		return null;
	}

	@Override
	protected void savePack(List<Equipement> pack) throws Exception {
		equipementRepository.save(pack);
	}

}
