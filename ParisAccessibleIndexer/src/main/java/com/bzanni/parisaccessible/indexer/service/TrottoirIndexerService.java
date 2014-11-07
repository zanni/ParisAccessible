package com.bzanni.parisaccessible.indexer.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.GeoShape;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeLineString;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeMultiLineString;
import com.bzanni.parisaccessible.elasticsearch.business.gtfs.GtfsStop;
import com.bzanni.parisaccessible.elasticsearch.opendataparis.PassagePieton;
import com.bzanni.parisaccessible.elasticsearch.opendataparis.Trottoir;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.gtfs.GtfsStopRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis.PassagePietonRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis.TrottoirRepository;
import com.bzanni.parisaccessible.neo.business.Location;
import com.bzanni.parisaccessible.neo.business.PassagePietonPath;
import com.bzanni.parisaccessible.neo.business.TrottoirPath;
import com.bzanni.parisaccessible.neo.service.BatchInserterService;

@Service
@Configurable
public class TrottoirIndexerService {

	private final static String DISTANCE_MATCH_TROTTOIR_PASSAGEPIETON = "5m";

	private final static String DISTANCE_MATCH_TROTTOIR_STOP = "5m";

	@Resource
	private TrottoirRepository trottoirRepository;

	@Resource
	private PassagePietonRepository passagePietonRepository;

	@Resource
	private GtfsStopRepository stopRepository;

	@Resource
	private BatchInserterService batchInserter;

	@Value("${index_cost_pieton_speed}")
	private Double pietonSpeed;

	@Value("${index_cost_trottoir_speed}")
	private Double trottoirSpeed;
	
//	index_match_trottoir_passagepieton = 5
//			index_match_trottoir_stop = 5
	
	@Value("${index_match_trottoir_passagepieton_distance}")
	private Double matchTrottoirPassagePietonDistance;
	
	@Value("${index_match_trottoir_stop_distance}")
	private Double matchTrottoirStopDistance;

	Map<String, Location> cache = new HashMap<String, Location>();

	private Location prepareLocation(String key, List<Double> point) {
		Location startPieton = cache.get(key);
		if (startPieton == null) {
			startPieton = new Location(key, point.get(0), point.get(1));
			cache.put(key, startPieton);
			batchInserter.addLocationToInserter(startPieton);
		}
		return startPieton;
	}

	private Location prepareLocation(PassagePieton passage, boolean isStart) {
		String start = (isStart) ? "start" : "end";
		String key = "pieton_" + passage.getId() + "_" + start;
		Double lat = (isStart) ? passage.getStart().getLat() : passage.getEnd()
				.getLat();
		Double lon = (isStart) ? passage.getStart().getLon() : passage.getEnd()
				.getLon();
		return prepareLocation(key, Arrays.asList(lat, lon));

	}

	private Location prepareLocation(GtfsStop stop) {
		String key = "stop_" + stop.getId();

		return prepareLocation(key, Arrays.asList(stop.getLocation().getLat(),
				stop.getLocation().getLon()));
	}

	private TrottoirPath map(Location trottoir, PassagePieton passage,
			boolean isStart) {

		Location startPieton = prepareLocation(passage, isStart);

		TrottoirPath mapPassagePietonStart = trottoir.mapTrottoir(startPieton,
				this.trottoirSpeed);

		return mapPassagePietonStart;
	}

	private void connectInterPassagePieton(PassagePieton passage) {
		Location start = prepareLocation(passage, true);
		Location end = prepareLocation(passage, false);

		PassagePietonPath passagePietonPath = start.mapPassagePieton(end,
				pietonSpeed);

		batchInserter.addBidirectionalToInserter(passagePietonPath);

	}

	private List<TrottoirPath> connectTrottoirLocationToPassagePieton(
			Location trottoir, List<Double> positionTrottoir) {
		try {
			List<PassagePieton> start = passagePietonRepository
					.findStart(
							positionTrottoir.get(0),
							positionTrottoir.get(1),
							TrottoirIndexerService.DISTANCE_MATCH_TROTTOIR_PASSAGEPIETON);
			List<PassagePieton> end = passagePietonRepository
					.findEnd(
							positionTrottoir.get(0),
							positionTrottoir.get(1),
							TrottoirIndexerService.DISTANCE_MATCH_TROTTOIR_PASSAGEPIETON);

			List<TrottoirPath> res = new ArrayList<TrottoirPath>();
			for (PassagePieton passage : start) {
				TrottoirPath map = map(trottoir, passage, true);
				connectInterPassagePieton(passage);
				res.add(map);
			}
			for (PassagePieton passage : end) {
				TrottoirPath map = map(trottoir, passage, false);
				connectInterPassagePieton(passage);
				res.add(map);
			}
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	private List<TrottoirPath> connectTrottoirLocationToStop(Location trottoir,
			List<Double> positionTrottoir) {
		try {
			List<GtfsStop> findLocation = stopRepository.findLocation(
					positionTrottoir.get(0), positionTrottoir.get(1),
					TrottoirIndexerService.DISTANCE_MATCH_TROTTOIR_STOP);

			List<TrottoirPath> res = new ArrayList<TrottoirPath>();
			for (GtfsStop stop : findLocation) {
				Location location = prepareLocation(stop);

				TrottoirPath mapTrottoir = trottoir.mapTrottoir(location,
						trottoirSpeed);

				res.add(mapTrottoir);
			}
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	public void indexTrottoir() {

		Iterator<List<Trottoir>> findAll = trottoirRepository.findAll();
		while (findAll.hasNext()) {
			List<Trottoir> trottoirs = findAll.next();
			for (Trottoir trottoir : trottoirs) {
				GeoShape shape = trottoir.getShape();
				List<List<List<Double>>> multilines = null;
				// trottoir shape can be either line or multiline
				// deal with it ...
				if (shape instanceof GeoShapeLineString) {
					GeoShapeLineString obj = (GeoShapeLineString) shape;
					multilines = Arrays.asList(obj.getCoordinates());

				} else if (shape instanceof GeoShapeMultiLineString) {
					GeoShapeMultiLineString obj = (GeoShapeMultiLineString) shape;
					multilines = obj.getCoordinates();
				}

				if (multilines != null) {
					int i = 0;
					for (List<List<Double>> line : multilines) {
						String key = "trottoir_" + trottoir.getId() + "_" + i;
						List<Double> prev = null;
						Location prevLocation = null;
						// for each point in trottoir
						for (List<Double> point : line) {
							// create location node for corresponing trottoir
							// edge
							Location loc = new Location(key, point.get(0),
									point.get(1));
							batchInserter.addLocationToInserter(loc);
							// link this location with previously created one if
							// exists
							if (prev != null && prevLocation != null) {
								TrottoirPath mapTrottoir = loc.mapTrottoir(
										prevLocation, pietonSpeed);

								batchInserter
										.addBidirectionalToInserter(mapTrottoir);
							}

							List<TrottoirPath> connectTrottoirLocationToPassagePieton = connectTrottoirLocationToPassagePieton(
									loc, point);

							batchInserter
									.addBidirectionalToInserter(connectTrottoirLocationToPassagePieton);

							List<TrottoirPath> connectTrottoirLocationToStop = connectTrottoirLocationToStop(
									loc, point);

							batchInserter
									.addBidirectionalToInserter(connectTrottoirLocationToStop);

							prev = point;
							prevLocation = loc;
							i++;
						}
					}
				}

			}
		}
		batchInserter.flushAndShutdown();
	}
}
