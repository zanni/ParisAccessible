package com.bzanni.parisaccessible.indexer.service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.elasticsearch.business.GeoShape;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeLineString;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShapeMultiLineString;
import com.bzanni.parisaccessible.elasticsearch.opendataparis.Trottoir;
import com.bzanni.parisaccessible.elasticsearch.repository.jest.opendataparis.TrottoirRepository;
import com.bzanni.parisaccessible.neo.business.CostCompute;
import com.bzanni.parisaccessible.neo.business.Location;
import com.bzanni.parisaccessible.neo.business.TrottoirPath;

@Service
@Configurable
public class TrottoirIndexerService {

	private static final int DISTANCE_MATCH_TROTTOIR_START_END = 2;
	@Resource
	private TrottoirRepository trottoirRepository;

	@Resource
	private LocationPublisher rabbitPublisher;

	public void indexTrottoir(int index_worker, int total_worker) {

		Iterator<List<Trottoir>> findAll = trottoirRepository
				.findAllTrottoirWorker(index_worker, total_worker);
		int z = 0;
		while (findAll.hasNext() && z < 8 ) {
			z++;
			
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

				if (multilines != null) {
					int i = 0;

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
								TrottoirPath mapTrottoir = loc
										.mapTrottoir(prevLocation);

								rabbitPublisher
										.addBidirectionalToInserter(
												index_worker, total_worker,
												mapTrottoir);
							}

							prevLocation = loc;
							if (isFirst) {
								isFirst = false;
								firstLocation = prevLocation;
							}
							i++;
							j++;
						}

						Double computeDistance = CostCompute.computeDistance(
								firstLocation, prevLocation);

						if (computeDistance < TrottoirIndexerService.DISTANCE_MATCH_TROTTOIR_START_END) {
							// match last to first if distance between them <
							// DISTANCE_MATCH_TROTTOIR_START_END
							TrottoirPath mapTrottoir = firstLocation
									.mapTrottoir(prevLocation);
							rabbitPublisher.addBidirectionalToInserter(
									index_worker, total_worker, mapTrottoir);
						}

					}

				}

			}
		}

		rabbitPublisher.emptyBulk(index_worker, total_worker);

	}
}
