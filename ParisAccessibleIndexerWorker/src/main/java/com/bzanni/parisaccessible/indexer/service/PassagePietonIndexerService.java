package com.bzanni.parisaccessible.indexer.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.GeoShape;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeLineString;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeMultiLineString;
import com.bzanni.parisaccessible.elasticsearch.opendataparis.PassagePieton;
import com.bzanni.parisaccessible.elasticsearch.opendataparis.Trottoir;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis.PassagePietonRepository;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis.TrottoirRepository;
import com.bzanni.parisaccessible.neo.business.CostCompute;
import com.bzanni.parisaccessible.neo.business.Location;
import com.bzanni.parisaccessible.neo.business.PassagePietonPath;
import com.bzanni.parisaccessible.neo.business.TrottoirPath;

@Service
public class PassagePietonIndexerService {

	private static final long delay = 30 * 1000;

	private static final String DISTANCE_MATCH_TROTTOIR_PASSAGEPIETON = "5m";

	@Resource
	private TrottoirRepository trottoirRepository;

	@Resource
	private PassagePietonRepository passagePietonRepository;

	@Resource
	private LocationPublisher rabbitPublisher;

	public class HeartbeatTask extends TimerTask {

		private int index_worker;
		private int total_worker;

		public HeartbeatTask(int index_worker, int total_worker) {
			this.index_worker = index_worker;
			this.total_worker = total_worker;
		}

		@Override
		public void run() {
			rabbitPublisher.heartbeatWorker(index_worker, total_worker);
		}
	}

	private Location prepareLocation(PassagePieton passage, boolean isStart) {
		String start = (isStart) ? "start" : "end";
		String key = "pieton_" + passage.getId() + "_" + start;
		Double lat = (isStart) ? passage.getStart().getLat() : passage.getEnd()
				.getLat();
		Double lon = (isStart) ? passage.getStart().getLon() : passage.getEnd()
				.getLon();
		return new Location("PIETON", key, lat, lon);

	}

	private List<TrottoirPath> analyseTrottoir(String pointId, Double lat,
			Double lon, Trottoir trottoir) {
		List<TrottoirPath> res = new ArrayList<TrottoirPath>();
		Double nearest = null;
		List<Double> nearestPoint = null;
		String nearestId = null;
		Double secondNearest = null;
		List<Double> secondNearestPoint = null;
		String secondNearestId = null;

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
				String id = "trottoir_" + trottoir.getId() + "_" + i;
				int j = 0;
				for (List<Double> point : line) {
					// create location node for corresponing trottoir
					// edge
					String key = id + "_" + j;

					Double computeDistance = CostCompute.computeDistance(point,
							Arrays.asList(lat, lon));

					if (nearest == null || computeDistance < nearest) {
						nearest = computeDistance;
						nearestId = key;
						nearestPoint = point;
					} else if (secondNearest == null
							|| computeDistance < secondNearest) {
						secondNearest = computeDistance;
						secondNearestId = key;
						secondNearestPoint = point;
					}
					j++;
				}
				i++;
			}
		}

		Location stopLocation = new Location("PIETON", pointId, lat, lon);
		if (nearestPoint != null && secondNearestPoint != null) {
			Double distanceNearestSecondNearest = CostCompute.computeDistance(
					nearestPoint, secondNearestPoint);

			Location nearestLoc = new Location("SIDWAY", nearestId,
					nearestPoint.get(1), nearestPoint.get(0));

			Location secondNearestLoc = new Location("SIDWAY", secondNearestId,
					nearestPoint.get(1), nearestPoint.get(0));

			if (distanceNearestSecondNearest > nearest
					&& distanceNearestSecondNearest > secondNearest) {

				res.add(new TrottoirPath(secondNearestLoc, stopLocation));
			}

			res.add(new TrottoirPath(nearestLoc, stopLocation));
		} else if (nearestPoint != null) {
			Location nearestLoc = new Location("SIDWAY", nearestId,
					nearestPoint.get(1), nearestPoint.get(0));
			res.add(new TrottoirPath(nearestLoc, stopLocation));
		}

		return res;
	}

	public void indexPassagePieton(int index_worker, int total_worker) {

		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(
				new HeartbeatTask(index_worker, total_worker), 0, delay);

		Iterator<List<PassagePieton>> findAllFiltered = passagePietonRepository
				.findAllFiltered(index_worker, total_worker);
		while (findAllFiltered.hasNext()) {
			List<PassagePieton> next = findAllFiltered.next();
			for (PassagePieton pieton : next) {

				Location start = prepareLocation(pieton, true);
				String startId = "pieton_" + pieton.getId() + "_start";
				Location end = prepareLocation(pieton, false);
				String endId = "pieton_" + pieton.getId() + "_end";

				// passage pieton interconnection
				PassagePietonPath passagePietonPath = new PassagePietonPath(
						start, end);
				rabbitPublisher.addBidirectionalToInserter(index_worker,
						total_worker, passagePietonPath);

				try {
					// connect start with trottoir
					List<Trottoir> search = trottoirRepository
							.search(start.getLat(),
									start.getLon(),
									PassagePietonIndexerService.DISTANCE_MATCH_TROTTOIR_PASSAGEPIETON);

					for (Trottoir t : search) {

						List<TrottoirPath> analyseTrottoir = analyseTrottoir(
								startId, start.getLat(), start.getLon(), t);

						rabbitPublisher.addBidirectionalToInserter(
								index_worker, total_worker, analyseTrottoir);
					}
					// connect end with trottoir
					search = trottoirRepository
							.search(end.getLat(),
									end.getLon(),
									PassagePietonIndexerService.DISTANCE_MATCH_TROTTOIR_PASSAGEPIETON);

					for (Trottoir t : search) {

						List<TrottoirPath> analyseTrottoir = analyseTrottoir(
								endId, end.getLat(), end.getLon(), t);

						rabbitPublisher.addBidirectionalToInserter(
								index_worker, total_worker, analyseTrottoir);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		rabbitPublisher.emptyBulk(index_worker, total_worker);
		timer.cancel();
	}
}
