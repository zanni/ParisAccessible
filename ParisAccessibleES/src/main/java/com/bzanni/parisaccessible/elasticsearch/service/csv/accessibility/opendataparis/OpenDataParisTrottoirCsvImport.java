package com.bzanni.parisaccessible.elasticsearch.service.csv.accessibility.opendataparis;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShape;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeLineString;
import com.bzanni.parisaccessible.elasticsearch.opendataparis.Trottoir;
import com.bzanni.parisaccessible.elasticsearch.repository.opendataparis.TrottoirRepository;
import com.bzanni.parisaccessible.elasticsearch.service.util.GenericCsvImporter;
import com.google.gson.Gson;

/**
 * 
 * geom_x_y;geom;niveau;info;libelle
 * 
 * @author bertrandzanni
 *
 */
@Service
public class OpenDataParisTrottoirCsvImport extends
		GenericCsvImporter<Trottoir> {

	@Resource
	private TrottoirRepository trottoirRepository;

	@Override
	public char delimiter() {
		return ';';
	}

	@Override
	protected List<Trottoir> convert(String[] line) throws Exception {

		if (line.length < 5) {
			return null;
		}

		Gson gson = new Gson();
		GeoShape shape = null;
		Trottoir eq = new Trottoir();

		Double lat = Double.valueOf(line[0].split(",")[0]);
		Double lon = Double.valueOf(line[0].split(",")[1]);
		eq.setLocation(new GeoPoint(lat, lon));
		shape = gson.fromJson(line[1], GeoShapeLineString.class);
		eq.setShape(shape);
		eq.setNiveau(line[2]);
		eq.setId(line[3]);
		eq.setLibelle(line[4]);
		List<Trottoir> res = new ArrayList<Trottoir>();
		res.add(eq);
		return res;
	}

	@Override
	protected void savePack(List<Trottoir> pack) throws Exception {
		trottoirRepository.save(pack);
	}

}
