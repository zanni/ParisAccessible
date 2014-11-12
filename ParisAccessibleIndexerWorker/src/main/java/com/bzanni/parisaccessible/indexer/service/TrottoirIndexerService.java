package com.bzanni.parisaccessible.indexer.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
import com.bzanni.parisaccessible.neo.service.MemcachedService;

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
	private LocationPublisher rabbitPublisher;

	@Resource
	private MemcachedService cache;

	@Value("${index_cost_pieton_speed}")
	private Double pietonSpeed;

	@Value("${index_cost_trottoir_speed}")
	private Double trottoirSpeed;

	@Value("${index_match_trottoir_passagepieton_distance}")
	private Double matchTrottoirPassagePietonDistance;

	@Value("${index_match_trottoir_stop_distance}")
	private Double matchTrottoirStopDistance;

	// Map<String, Location> cache = new HashMap<String, Location>();

	private Location prepareLocation(String label, String key,
			List<Double> point) {
		// Location startPieton;
		// Object obj = null;//cache.get(key);
		// if (obj == null) {
		// startPieton = new Location(label, key, point.get(0), point.get(1));
		// cache.set(key, startPieton);

		// }
		// else {
		// startPieton = (Location) obj;
		// }
		// rabbitPublisher.addLocationToInserter(startPieton);
		return new Location(label, key, point.get(0), point.get(1));
	}

	private Location prepareLocation(PassagePieton passage, boolean isStart) {
		String start = (isStart) ? "start" : "end";
		String key = "pieton_" + passage.getId() + "_" + start;
		Double lat = (isStart) ? passage.getStart().getLat() : passage.getEnd()
				.getLat();
		Double lon = (isStart) ? passage.getStart().getLon() : passage.getEnd()
				.getLon();
		return prepareLocation("PIETON", key, Arrays.asList(lat, lon));

	}

	private Location prepareLocation(GtfsStop stop) {
		String key = "stop_" + stop.getId();

		return prepareLocation("STOP", key, Arrays.asList(stop.getLocation()
				.getLat(), stop.getLocation().getLon()));
	}

	private TrottoirPath map(Location trottoir, PassagePieton passage,
			boolean isStart) {

		Location startPieton = prepareLocation(passage, isStart);

		TrottoirPath mapPassagePietonStart = trottoir.mapTrottoir(startPieton,
				this.trottoirSpeed);

		return mapPassagePietonStart;
	}

	private void connectInterPassagePieton(Integer index_worker,
			Integer total_worker, PassagePieton passage) {
		Location start = prepareLocation(passage, true);
		Location end = prepareLocation(passage, false);

		PassagePietonPath passagePietonPath = start.mapPassagePieton(end,
				pietonSpeed);

		rabbitPublisher.addBidirectionalToInserter(index_worker, total_worker,
				passagePietonPath);

	}

	private List<TrottoirPath> connectTrottoirLocationToPassagePieton(
			Integer index_worker, Integer total_worker, Location trottoir,
			List<Double> positionTrottoir) {
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
				connectInterPassagePieton(index_worker, total_worker, passage);
				res.add(map);
			}
			for (PassagePieton passage : end) {
				TrottoirPath map = map(trottoir, passage, false);
				connectInterPassagePieton(index_worker, total_worker, passage);
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
					positionTrottoir.get(1), positionTrottoir.get(0),
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

	public void indexTrottoir(int index_worker, int total_worker) {

		rabbitPublisher.startWorker(index_worker, total_worker);

		Iterator<List<Trottoir>> findAll = trottoirRepository.findAllWorker(
				index_worker, total_worker);
		while (findAll.hasNext()) {
			List<Trottoir> trottoirs = findAll.next();
			System.out.println("downloaded: " + trottoirs.size());
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
				
				List<TrottoirPath> tempTrottoir = null;
				List<TrottoirPath> tempLink = null;

				if (multilines != null) {
					int i = 0;
					tempTrottoir = new ArrayList<TrottoirPath>();
					tempLink = new ArrayList<TrottoirPath>();
					for (List<List<Double>> line : multilines) {
						String id = "trottoir_" + trottoir.getId() + "_" + i;
						Location prevLocation = null;

						Location firstLocation = null;
						boolean isFirst = true;
						// for each point in trottoir
						int j = 0;
						for (List<Double> point : line) {
							// create location node for corresponing trottoir
							// edge
							String key = id + "_" + j;
							Location loc = new Location("SIDWAY", key,
									point.get(0), point.get(1));
							// rabbitPublisher.addLocationToInserter(loc);
							// link this location with previously created one if
							// exists
							if (prevLocation != null) {
								TrottoirPath mapTrottoir = loc.mapTrottoir(
										prevLocation, pietonSpeed);

								tempTrottoir.add(mapTrottoir);
//								rabbitPublisher
//										.addBidirectionalToInserter(
//												index_worker, total_worker,
//												mapTrottoir);
							}

							List<TrottoirPath> connectTrottoirLocationToPassagePieton = connectTrottoirLocationToPassagePieton(
									index_worker, total_worker, loc, point);
							
							tempLink.addAll(connectTrottoirLocationToPassagePieton);

//							rabbitPublisher.addBidirectionalToInserter(
//									index_worker, total_worker,
//									connectTrottoirLocationToPassagePieton);

							List<TrottoirPath> connectTrottoirLocationToStop = connectTrottoirLocationToStop(
									loc, point);
							
							tempLink.addAll(connectTrottoirLocationToStop);

//							rabbitPublisher.addBidirectionalToInserter(
//									index_worker, total_worker,
//									connectTrottoirLocationToStop);

							prevLocation = loc;
							if (isFirst) {
								isFirst = false;
								firstLocation = prevLocation;
							}
							i++;
							j++;
						}

						// match last to first
						TrottoirPath mapTrottoir = firstLocation.mapTrottoir(
								prevLocation, pietonSpeed);
						
						tempTrottoir.add(mapTrottoir);

						rabbitPublisher.addBidirectionalToInserter(
								index_worker, total_worker, mapTrottoir);
					}
					
					if(!tempLink.isEmpty()){
						rabbitPublisher.addBidirectionalToInserter(
								index_worker, total_worker, tempTrottoir);
						rabbitPublisher.addBidirectionalToInserter(
								index_worker, total_worker, tempLink);
					}
				}

			}
		}
		// batchInserter.flushAndShutdown();
		rabbitPublisher.emptyBulk(index_worker, total_worker);
		rabbitPublisher.endWorker(index_worker, total_worker);
	}
}
