package com.bzanni.parisaccessible.neo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.geotools.data.neo4j.StyledImageExporter;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.ShapefileExporter;
import org.neo4j.gis.spatial.SimplePointLayer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.neo.business.Location;
import com.vividsolutions.jts.geom.Coordinate;

@Service
@Configurable
public class ShortestPathService {

	public final static Double MATCH_DISTANCE_IN_METER = 5D;

	public final static int BULK_INDEX = 1000;

	@Value("${neo4j_data_path}")
	private String neoDataPath;

	GraphDatabaseService database;
	SpatialDatabaseService spatialService;

	SimplePointLayer sidwayLayer;
	SimplePointLayer pietonLayer;
	SimplePointLayer stopLayer;

	private SimplePointLayer getOrCreateLayer(
			SpatialDatabaseService spatialService, String name) {
		if (spatialService.containsLayer(name)) {
			return (SimplePointLayer) spatialService.getLayer(name);
		} else {
			return spatialService.createSimplePointLayer(name, "lat", "lon");
		}
	}

	public void init(String dataFolder) {
		database = new GraphDatabaseFactory()
				.newEmbeddedDatabaseBuilder(neoDataPath + "/" + dataFolder)
				.setConfig(GraphDatabaseSettings.nodestore_mapped_memory_size,
						"500M")
				.setConfig(
						GraphDatabaseSettings.relationshipstore_mapped_memory_size,
						"2G")
				.setConfig(
						GraphDatabaseSettings.relationshipstore_mapped_memory_size,
						"2G")
				.setConfig(GraphDatabaseSettings.string_block_size, "60")
				.setConfig(GraphDatabaseSettings.array_block_size, "300")
				.newGraphDatabase();

		SpatialDatabaseService spatialService = new SpatialDatabaseService(
				database);

		Transaction tx = database.beginTx();

		sidwayLayer = getOrCreateLayer(spatialService,
				SpatialIndexerService.SIDWAY_LAYER_NAME);
		pietonLayer = getOrCreateLayer(spatialService,
				SpatialIndexerService.PIETON_LAYER_NAME);
		stopLayer = getOrCreateLayer(spatialService,
				SpatialIndexerService.STOP_LAYER_NAME);

		// saveLayerAsImage(mainPointsLayer, 600, 600);

		tx.success();
		tx.close();

	}

	private void saveLayerAsImage(Layer layer, int width, int height) {
		ShapefileExporter shpExporter = new ShapefileExporter(database);
		shpExporter.setExportDir("target");
		StyledImageExporter imageExporter = new StyledImageExporter(database);
		imageExporter.setExportDir("target");
		imageExporter.setZoom(0.9);
		imageExporter.setSize(width, height);
		try {
			imageExporter.saveLayerImage(layer.getName());
			shpExporter.exportLayer(layer.getName());
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public Location findClosestSidwayToPoint(List<Double> point) {
		Node n = findClosestNodeToPoint(sidwayLayer, point);
		return fromNode(n);
	}

	public Location findClosestStopToPoint(List<Double> point) {
		Node n = findClosestNodeToPoint(stopLayer, point);
		return fromNode(n);
	}

	public Location findClosestPietonToPoint(List<Double> point) {
		Node n = findClosestNodeToPoint(pietonLayer, point);
		return fromNode(n);
	}

	public List<Location> findAllInEnvelope(List<Double> p1, List<Double> p2) {
		List<Node> res = new ArrayList<Node>();
		res.addAll(findNodeInEnvelope(sidwayLayer, p1, p2));
		res.addAll(findNodeInEnvelope(pietonLayer, p1, p2));
		res.addAll(findNodeInEnvelope(stopLayer, p1, p2));
		List<Location> loc = new ArrayList<Location>();
		for(Node n : res){
			loc.add(fromNode(n));
		}
		return loc;
	}

	private List<Node> findNodeInEnvelope(SimplePointLayer layer,
			List<Double> p1, List<Double> p2) {
		List<SpatialDatabaseRecord> results = GeoPipeline.startContainSearch(
				layer,
				layer.getGeometryFactory().toGeometry(
						new com.vividsolutions.jts.geom.Envelope(15.0, 16.0,
								56.0, 57.0))).toSpatialDatabaseRecordList();

		List<Node> res = new ArrayList<Node>();
		while (results.iterator().hasNext()) {
			SpatialDatabaseRecord record = results.iterator().next();

			res.add(record.getGeomNode());
		}
		return res;
	}

	private Node findClosestNodeToPoint(SimplePointLayer layer,
			List<Double> point) {
		Transaction tx = database.beginTx();

		// double distance = 1;
		// int num = 1;
		//
		// LayerIndexReader index = layer.getIndex();
		// Envelope bbox = index.getBoundingBox();
		// double[] centre = bbox.centre();

		GeoPipeline startNearestNeighborLatLonSearch = GeoPipeline
				.startNearestNeighborLatLonSearch(layer,
						new Coordinate(point.get(0), point.get(1)), 1.0D).sort(
						"OrthodromicDistance");
		List<SpatialDatabaseRecord> results = startNearestNeighborLatLonSearch
				.toSpatialDatabaseRecordList();

		while (results.iterator().hasNext()) {
			SpatialDatabaseRecord record = results.iterator().next();

			return database.getNodeById(record.getNodeId());
		}
		tx.close();
		return null;

	}

	private Location fromNode(Node n) {
		Transaction tx = database.beginTx();
		Double lat = (Double) n.getProperty("lat");
		Double lon = (Double) n.getProperty("lon");
		String id = (String) n.getProperty("id");
		Location loc = new Location();
		loc.setId(id);
		loc.setLat(lat);
		loc.setLon(lon);
		loc.setGraphId(n.getId());
		tx.close();
		return loc;
	}

	public List<Location> findShortestPath(List<Double> start, List<Double> end) {

		Transaction tx = database.beginTx();

		Node startNode = this.findClosestNodeToPoint(sidwayLayer, start);
		Node endNode = this.findClosestNodeToPoint(sidwayLayer, end);

		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(
				PathExpanders.forDirection(Direction.OUTGOING), 100000);

		Iterable<Path> findAllPaths = finder.findAllPaths(startNode, endNode);

		Iterator<Path> iterator2 = findAllPaths.iterator();
		if (iterator2.hasNext()) {
			Path path = findAllPaths.iterator().next();
			if (path == null)
				return null;

			Iterator<PropertyContainer> iterator = path.iterator();
			while (iterator.hasNext()) {
				PropertyContainer next = iterator.next();
				System.out.println(next);
				for (String str : next.getPropertyKeys()) {
					System.out.println(str + " : " + next.getProperty(str));
				}

			}

			List<Location> list = new ArrayList<Location>();

			for (Node nodeById : path.nodes()) {
				Node n = database.getNodeById(nodeById.getId());
				if (n.hasProperty("lat")) {
					Double lat = (Double) n.getProperty("lat");
					Double lon = (Double) n.getProperty("lon");
					String id = (String) n.getProperty("id");
					Location loc = new Location();
					loc.setId(id);
					loc.setLat(lat);
					loc.setLon(lon);
					loc.setGraphId(n.getId());
					list.add(loc);
				}

			}
			tx.close();

			if (list.size() <= 2)
				return Collections.emptyList();
			return list;
		}
		return null;

	}
}
