package com.bzanni.parisaccessible.injector.service.csv.accessibility.opendataparis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.GeoShape;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeLineString;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeMultiLineString;
import com.bzanni.parisaccessible.elasticsearch.opendataparis.Trottoir;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis.TrottoirRepository;
import com.bzanni.parisaccessible.injector.service.util.GenericCsvImporter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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

	private final static String TROTTOIR_TAG = "BOR";
	
	private final static Logger LOGGER = LoggerFactory
			.getLogger(OpenDataParisTrottoirCsvImport.class);

	public Logger getLogger() {
		return OpenDataParisTrottoirCsvImport.LOGGER;
	}

	@Resource
	private TrottoirRepository trottoirRepository;

	@Override
	public char delimiter() {
		return ';';
	}

	public List<Double> adjustPoint(List<Double> point) {
		Double lon = point.get(1) - 0.00006D;
		Double lat = point.get(0) - 0.00070D;
		return Arrays.asList(lat, lon);
	}

	private List<List<Double>> adjustLine(List<List<Double>> origin) {
		List<List<Double>> des = new ArrayList<List<Double>>();

		for (List<Double> point : origin) {
			des.add(adjustPoint(point));
		}
		return des;
	}

	@Override
	protected List<Trottoir> convert(String[] line) throws Exception {

		if (line.length < 5) {
			return null;
		}
		
		if(!line[3].equals(OpenDataParisTrottoirCsvImport.TROTTOIR_TAG)){
			return null;
		}
		

		Gson gson = new Gson();
		GeoShape shape = null;
		Trottoir eq = new Trottoir();

		try {
			GeoShapeLineString obj = gson.fromJson(line[1],
					GeoShapeLineString.class);

			List<List<Double>> adjustLine = adjustLine(obj.getCoordinates());

			obj.setCoordinates(adjustLine);

			shape = obj;

		} catch (JsonSyntaxException e) {

			GeoShapeMultiLineString obj = gson.fromJson(line[1],
					GeoShapeMultiLineString.class);

			List<List<List<Double>>> des = new ArrayList<List<List<Double>>>();
			for (List<List<Double>> list : obj.getCoordinates()) {
				List<List<Double>> adjustLine = adjustLine(list);
				des.add(adjustLine);
			}
			
			obj.setCoordinates(des);
			shape = obj;
			
		}
		eq.setShape(shape);
		// eq.setNiveau(line[2]);
		eq.setInfo(line[3]);
		// eq.setLibelle(line[4]);
		List<Trottoir> res = new ArrayList<Trottoir>();
		res.add(eq);
		return res;
	}

	@Override
	protected void savePack(List<Trottoir> pack) throws Exception {
		trottoirRepository.save(pack);
	}

}
