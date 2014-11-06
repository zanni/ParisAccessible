package com.bzanni.parisaccessible.indexer.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.springframework.data.neo4j.support.Neo4jTemplate;
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
import com.bzanni.parisaccessible.neo.repository.LocationRepository;

@Service
public class TrottoirIndexerService {

	private final static long BULK_IMPORT = 2000;

	private final static String DISTANCE_MATCH_TROTTOIR_PASSAGEPIETON = "5m";

	private final static String DISTANCE_MATCH_TROTTOIR_STOP = "5m";
	@Resource
	private TrottoirRepository trottoirRepository;

	@Resource
	private PassagePietonRepository passagePietonRepository;

	@Resource
	private GtfsStopRepository stopRepository;

	@Resource
	private LocationRepository locationRepository;

	@Resource
	private Neo4jTemplate neoTempalte;

	Map<String, Location> cache = new HashMap<String, Location>();
	BatchInserter inserter;
	BatchInserterIndexProvider indexProvider;

	BatchInserterIndex locations;
	BatchInserterIndex pietonRelation;
	BatchInserterIndex trottoirRelation;
	long currentLocationIndexMarker = 0;
	long currentPietonIndexMarker = 0;
	long currentTrottoirIndexMarker = 0;
	long currentBulkMarker = 0;

	public TrottoirIndexerService() {
		inserter = BatchInserters.inserter("/var/lib/neo4j-server/data/graph.db");

		indexProvider = new LuceneBatchInserterIndexProvider(inserter);

		locations = indexProvider.nodeIndex("locations",
				MapUtil.stringMap("type", "exact"));

		pietonRelation = indexProvider.relationshipIndex("PIETON",
				MapUtil.stringMap("type", "exact"));

		trottoirRelation = indexProvider.relationshipIndex("TROTTOIR",
				MapUtil.stringMap("type", "exact"));
	}

	private void flushAndShutdown() {
		locations.flush();
		pietonRelation.flush();
		trottoirRelation.flush();
		indexProvider.shutdown();
		inserter.shutdown();
	}

	private void addLocationToInserter(Location location) {
		currentBulkMarker++;
		locations.add(currentLocationIndexMarker++, location.getMap());
	}

	private void addPassagePietonToInserter(List<PassagePietonPath> list) {
		for (PassagePietonPath p : list) {
			currentBulkMarker++;
			pietonRelation.add(currentPietonIndexMarker++, p.getMap());
		}
	}

	private void addTrottoirToInserter(List<TrottoirPath> list) {
		for (TrottoirPath p : list) {
			addTrottoirToInserter(p);
		}
	}

	private void addTrottoirToInserter(TrottoirPath trottoir) {
		currentBulkMarker++;
		pietonRelation.add(currentPietonIndexMarker++, trottoir.getMap());
	}

	private Location prepareLocation(String key) {
		Location startPieton = cache.get(key);
		if (startPieton == null) {
			startPieton = new Location(key);
			cache.put(key, startPieton);
			addLocationToInserter(startPieton);
		}
		return startPieton;
	}

	private Location prepareLocation(PassagePieton passage, boolean isStart) {
		String start = (isStart) ? "start" : "end";
		String key = "pieton_" + passage.getId() + "_" + start;
		return prepareLocation(key);

	}

	private Location prepareLocation(GtfsStop stop) {
		String key = "stop_" + stop.getId();
		return prepareLocation(key);
	}

	private PassagePietonPath map(Location trottoir,
			List<Double> positionTrottoir, PassagePieton passage,
			boolean isStart) {

		Location startPieton = prepareLocation(passage, isStart);
		Double lat = (isStart) ? passage.getStart().getLat() : passage.getEnd()
				.getLat();
		Double lon = (isStart) ? passage.getStart().getLat() : passage.getEnd()
				.getLat();
		List<Double> startPietonPoint = Arrays.asList(lat, lon);

		PassagePietonPath mapPassagePietonStart = trottoir.mapPassagePieton(
				startPieton, computeCost(positionTrottoir, startPietonPoint));

		return mapPassagePietonStart;
	}

	private List<PassagePietonPath> connectTrottoirLocationToPassagePieton(
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

			List<PassagePietonPath> res = new ArrayList<PassagePietonPath>();
			for (PassagePieton passage : start) {
				PassagePietonPath map = map(trottoir, positionTrottoir,
						passage, true);
				res.add(map);
			}
			for (PassagePieton passage : end) {
				PassagePietonPath map = map(trottoir, positionTrottoir,
						passage, false);
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

				List<Double> stopPoint = Arrays.asList(stop.getLocation()
						.getLat(), stop.getLocation().getLon());
				TrottoirPath mapTrottoir = trottoir.mapTrottoir(location,
						computeCost(positionTrottoir, stopPoint));
				res.add(mapTrottoir);
			}
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	private Double deg2rad(Double deg) {
		return deg * (Math.PI / 180);
	}

	private Double computeCost(List<Double> a, List<Double> b) {
		Double R = 6371D; // Radius of the earth in km

		Double dLat = deg2rad(a.get(0) - b.get(0));
		Double dLon = deg2rad(a.get(1) - b.get(1));

		Double v1 = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(deg2rad(a.get(0))) * Math.cos(deg2rad(b.get(0)))
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);

		Double v2 = 2 * Math.atan2(Math.sqrt(v1), Math.sqrt(1 - v1));
		Double d = R * v2; // Distance in km
		return d;
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
							Location loc = new Location(key);
							addLocationToInserter(loc);

							// link this location with previously created one if
							// exists
							if (prev != null && prevLocation != null) {
								TrottoirPath mapTrottoir = loc.mapTrottoir(
										prevLocation, computeCost(point, prev));

								addTrottoirToInserter(mapTrottoir);
							}

							List<PassagePietonPath> connectTrottoirLocationToPassagePieton = connectTrottoirLocationToPassagePieton(
									loc, point);

							addPassagePietonToInserter(connectTrottoirLocationToPassagePieton);

							List<TrottoirPath> connectTrottoirLocationToStop = connectTrottoirLocationToStop(
									loc, point);

							addTrottoirToInserter(connectTrottoirLocationToStop);

							prev = point;
							prevLocation = loc;
							i++;
						}
					}
				}

			}
		}
		flushAndShutdown();
	}
}
