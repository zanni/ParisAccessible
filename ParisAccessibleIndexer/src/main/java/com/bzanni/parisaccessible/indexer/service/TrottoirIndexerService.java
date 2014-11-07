package com.bzanni.parisaccessible.indexer.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;
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

@Service
@Configurable
public class TrottoirIndexerService {

	private final static String DISTANCE_MATCH_TROTTOIR_PASSAGEPIETON = "5m";

	private final static String DISTANCE_MATCH_TROTTOIR_STOP = "5m";

	private final static DynamicRelationshipType PIETON_RELATIONSHIP_TAG = DynamicRelationshipType
			.withName("PIETON");

	private final static DynamicRelationshipType TROTTOIR_RELATIONSHIP_TAG = DynamicRelationshipType
			.withName("TROTTOIR");

	private final static DynamicRelationshipType TRANSPORT_RELATIONSHIP_TAG = DynamicRelationshipType
			.withName("TRANSPORT");

	private final static Label locationLabel = DynamicLabel.label("Location");
	@Resource
	private TrottoirRepository trottoirRepository;

	@Resource
	private PassagePietonRepository passagePietonRepository;

	@Resource
	private GtfsStopRepository stopRepository;

	@Value("${neo4j_data_path}")
	private String neoDataPath;

	@Value("${index_cost_pieton_speed}")
	private Double pietonSpeed;

	@Value("${index_cost_trottoir_speed}")
	private Double trottoirSpeed;

	Map<String, Location> cache = new HashMap<String, Location>();
	BatchInserter inserter;
	BatchInserterIndexProvider indexProvider;

	long currentLocationIndexMarker = 1;
	long currentPietonIndexMarker = 1;
	long currentTrottoirIndexMarker = 1;
	long currentBulkMarker = 1;

	@PostConstruct
	public void init() {
		// Map<String, String> config = new HashMap<>();
		// config.put("neostore.nodestore.db.mapped_memory", "90M");
		// neostore.nodestore.db.mapped_memory=25M
		// neostore.relationshipstore.db.mapped_memory=50M
		// neostore.propertystore.db.mapped_memory=90M
		// neostore.propertystore.db.strings.mapped_memory=130M
		// neostore.propertystore.db.arrays.mapped_memory=130M
		inserter = BatchInserters.inserter(neoDataPath);

		inserter.createDeferredSchemaIndex(TrottoirIndexerService.locationLabel)
				.on("id").create();
		indexProvider = new LuceneBatchInserterIndexProvider(inserter);

	}

	private void flushAndShutdown() {
		indexProvider.shutdown();
		inserter.shutdown();
	}

	private void addLocationToInserter(Location location) {
		currentBulkMarker++;
		// inserter.c
		long createNode = inserter.createNode(location.getMap(),
				TrottoirIndexerService.locationLabel);
		System.out.println("Create node: " + createNode);
		location.setGraphId(createNode);
	}

	private void addPassagePietonToInserter(List<TrottoirPath> list) {
		for (TrottoirPath p : list) {
			currentBulkMarker++;

			long createRelationship = inserter.createRelationship(p.getStart()
					.getGraphId(), p.getEnd().getGraphId(),
					TrottoirIndexerService.TROTTOIR_RELATIONSHIP_TAG, p
							.getMap());
			System.out.println("Create Relationship: " + createRelationship);
			createRelationship = inserter.createRelationship(p.getEnd()
					.getGraphId(), p.getStart().getGraphId(),
					TrottoirIndexerService.TROTTOIR_RELATIONSHIP_TAG, p
							.getMap());
			System.out.println("Create Relationship: " + createRelationship);
		}
	}

	private void addTrottoirToInserter(List<TrottoirPath> list) {
		for (TrottoirPath p : list) {
			addTrottoirToInserter(p);
		}
	}

	private void addTrottoirToInserter(TrottoirPath trottoir) {
		currentBulkMarker++;
		long createRelationship = inserter.createRelationship(trottoir
				.getStart().getGraphId(), trottoir.getEnd().getGraphId(),
				TrottoirIndexerService.TROTTOIR_RELATIONSHIP_TAG, trottoir
						.getMap());

		System.out.println("Create Relationship: " + createRelationship);

		createRelationship = inserter.createRelationship(trottoir.getEnd()
				.getGraphId(), trottoir.getStart().getGraphId(),
				TrottoirIndexerService.TROTTOIR_RELATIONSHIP_TAG, trottoir
						.getMap());

		System.out.println("Create Relationship: " + createRelationship);
	}

	private Location prepareLocation(String key, List<Double> point) {
		Location startPieton = cache.get(key);
		if (startPieton == null) {
			startPieton = new Location(key, point.get(0), point.get(1));
			cache.put(key, startPieton);
			addLocationToInserter(startPieton);
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

		inserter.createRelationship(start.getGraphId(), end.getGraphId(),
				TrottoirIndexerService.PIETON_RELATIONSHIP_TAG,
				passagePietonPath.getMap());

		inserter.createRelationship(end.getGraphId(), start.getGraphId(),
				TrottoirIndexerService.PIETON_RELATIONSHIP_TAG,
				passagePietonPath.getMap());

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
							addLocationToInserter(loc);
							// link this location with previously created one if
							// exists
							if (prev != null && prevLocation != null) {
								TrottoirPath mapTrottoir = loc.mapTrottoir(
										prevLocation, pietonSpeed);

								addTrottoirToInserter(mapTrottoir);
							}

							List<TrottoirPath> connectTrottoirLocationToPassagePieton = connectTrottoirLocationToPassagePieton(
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
