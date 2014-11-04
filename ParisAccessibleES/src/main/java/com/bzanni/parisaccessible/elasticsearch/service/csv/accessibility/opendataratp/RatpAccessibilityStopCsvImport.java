package com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.opendataratp;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsStop;
import com.bzanni.parisaccessible.elasticsearch.business.lambert.Lambert;
import com.bzanni.parisaccessible.elasticsearch.business.lambert.LambertPoint;
import com.bzanni.parisaccessible.elasticsearch.business.lambert.LambertZone;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs.GtfsStopRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;

@Service
public class RatpAccessibilityStopCsvImport extends
		GenericCsvImporter<GtfsStop> {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(RatpAccessibilityStopCsvImport.class);

	public Logger getLogger() {
		return RatpAccessibilityStopCsvImport.LOGGER;
	}

	@Resource
	private GtfsStopRepository repository;

	@Override
	public char delimiter() {
		return ';';
	}

	@Override
	protected List<GtfsStop> convert(String[] list) throws Exception {
		if (list.length < 8) {
			return null;
		}
		try {
			Double v1 = Double.valueOf(list[4]);
			Double v2 = Double.valueOf(list[5]);

			LambertPoint pt = Lambert.convertToWGS84Deg(v1, v2,
					LambertZone.LambertIIExtended);

			List<GtfsStop> search = repository.search(pt.getY(), pt.getX(),
					"10m");
			for (GtfsStop stop : search) {
				boolean bool = (list[6].equals("1")) ? true : false;
				stop.setAccessibleUFR(bool);
				bool = (list[7].equals("1")) ? true : false;
				stop.setAnnonceSonoreProchainArret(bool);
				bool = (list[8].equals("1")) ? true : false;
				stop.setAnnonceVisuelleProchainArret(bool);
				bool = (list[9].equals("1")) ? true : false;
				stop.setAnnonceSonoreSituationPerturbe(bool);
				bool = (list[10].equals("1")) ? true : false;
				stop.setAnnonceVisuelleSituationPerturbe(bool);
			}
			return search;
		} catch (NumberFormatException e) {

		}
		return null;
	}

	@Override
	protected void savePack(List<GtfsStop> pack) throws Exception {
		repository.save(pack);
	}

}
