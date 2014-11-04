package com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.opendataratp;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsRoute;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs.GtfsRouteRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class RatpAccessibilityRouteCsvImport extends
		GenericCsvImporter<GtfsRoute> {

	@Resource
	private GtfsRouteRepository repository;

	@Override
	protected List<GtfsRoute> convert(String[] list) throws Exception {
		List<GtfsRoute> findByShortName = repository
				.findByShortName(list[1]);
		if (findByShortName == null) {
			return null;
		}
		for (GtfsRoute prepare : findByShortName) {
			prepare.setName(list[1]);
			prepare.setOrigin(list[2]);
			prepare.setDestination(list[3]);
			if (list[4].equals("1")) {
				prepare.setAccessibleUFR(true);
			} else {
				prepare.setAccessibleUFR(false);
			}
			if (list[5].equals("1")) {
				prepare.setPlancherBas(true);
			} else {
				prepare.setPlancherBas(false);
			}
			if (list[6].equals("1")) {
				prepare.setPalette(true);
			} else {
				prepare.setPalette(false);
			}
			if (list[7].equals("1")) {
				prepare.setAnnonceSonoreProchainArret(true);
			} else {
				prepare.setAnnonceSonoreProchainArret(false);
			}
			if (list[8].equals("1")) {
				prepare.setAnnonceVisuelleProchainArret(true);
			} else {
				prepare.setAnnonceVisuelleProchainArret(false);
			}
		}
		return findByShortName;
	}

	@Override
	protected void savePack(List<GtfsRoute> pack) throws Exception {
		repository.save(pack);
	}
	
	@Override
	public char delimiter() {
		return ';';
	}

}
