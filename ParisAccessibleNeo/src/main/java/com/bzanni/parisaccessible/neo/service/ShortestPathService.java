package com.bzanni.parisaccessible.neo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.geotools.data.neo4j.StyledImageExporter;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.LayerIndexReader;
import org.neo4j.gis.spatial.ShapefileExporter;
import org.neo4j.gis.spatial.SimplePointLayer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.gis.spatial.rtree.Envelope;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.tooling.GlobalGraphOperations;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.parisaccessible.neo.business.Location;
import com.vividsolutions.jts.geom.Coordinate;

@Service
@Configurable
public class ShortestPathService {

	public final static Double MATCH_DISTANCE_IN_METER = 5D;

	public final static int BULK_INDEX = 10000;

	@Value("${neo4j_data_path}")
	private String neoDataPath;

	GraphDatabaseService database;
	SpatialDatabaseService spatialService;

	SimplePointLayer mainPointsLayer;

	private static enum SHORTEST_PATH implements RelationshipType {
		SIDWAY
	}

	// SimplePointLayer createSimplePointLayer;

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

		if (spatialService.containsLayer("location")) {
			mainPointsLayer = (SimplePointLayer) spatialService
					.getLayer("location");
		} else {
			mainPointsLayer = spatialService.createSimplePointLayer("location",
					"lat", "lon");


			tx = database.beginTx();

			Iterable<Node> allNodes = GlobalGraphOperations.at(database)
					.getAllNodes();
			List<Node> buffer = new ArrayList<Node>();
			int i = 0;
			int j = 0;
			for (Node n : allNodes) {

				buffer.add(n);
				i++;
				if (i % ShortestPathService.BULK_INDEX == 0) {

					database.beginTx();
					for (Node node : buffer) {
						if (node.hasProperty("lat") && node.hasProperty("lon")) {
							mainPointsLayer.add(node);
							j++;
						}
					}
					System.out.println("Indexed: " + j + " over " + i);
					buffer = new ArrayList<Node>();
					tx.success();
					tx.close();
					tx = database.beginTx();

				}

			}
			database.beginTx();
			for (Node node : buffer) {
				if (node.hasProperty("lat") && node.hasProperty("lon")) {
					mainPointsLayer.add(node);
				}
			}
			System.out.println("Indexed: " + i);
			buffer = new ArrayList<Node>();
			tx.success();
			tx.close();
		}

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

	private Node findNode(List<Double> point) {
		Transaction tx = database.beginTx();

		double distance = 1;
		int num = 1;
		// List<GeoPipeFlow> findClosestPointsTo = mainPointsLayer
		// .findClosestPointsTo(new Coordinate(point.get(0), point.get(1),
		// distance));
		//

		LayerIndexReader index = mainPointsLayer.getIndex();
		Envelope bbox = index.getBoundingBox();
		double[] centre = bbox.centre();

		GeoPipeline startNearestNeighborLatLonSearch = GeoPipeline
				.startNearestNeighborLatLonSearch(mainPointsLayer,
						new Coordinate(point.get(0), point.get(1)), 1.0D).sort(
						"OrthodromicDistance");
		List<SpatialDatabaseRecord> results = startNearestNeighborLatLonSearch
				.toSpatialDatabaseRecordList();

		while (results.iterator().hasNext()) {
			SpatialDatabaseRecord record = results.iterator().next();
			System.out.println(record.getNodeId() + " : "
					+ record.getGeomNode().getId());
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

	public Location findLocation(List<Double> point) {
		Node n = this.findNode(point);
		return fromNode(n);

	}

	public List<Location> findShortestPath(List<Double> start, List<Double> end) {
		// EstimateEvaluator<Double> estimateEvaluator = new
		// EstimateEvaluator<Double>() {
		// @Override
		// public Double getCost(final Node node, final Node goal) {
		// return 1D;
		// }
		// };
		Transaction tx = database.beginTx();
		// PathFinder<WeightedPath> astar = GraphAlgoFactory.aStar(
		// PathExpanders.allTypesAndDirections(),
		// CommonEvaluators.doubleCostEvaluator("cost", 1),
		// estimateEvaluator);

		//

		Node startNode = this.findNode(start);
		Node endNode = this.findNode(end);
		// WeightedPath findSinglePath = astar.findSinglePath(startNode,
		// endNode);

		// PathFinder<Path> finder = GraphAlgoFactory.shortestPath(PathExpanders
		// .forTypesAndDirections(
		// DynamicRelationshipType.withName("TROTTOIR"),
		// Direction.BOTH,
		// DynamicRelationshipType.withName("PIETON"),
		// Direction.BOTH,
		// DynamicRelationshipType.withName("TRANSPORT"),
		// Direction.BOTH), 15);

		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(
				PathExpanders.forDirection(Direction.OUTGOING), 100000);

		Iterable<Path> findAllPaths = finder.findAllPaths(startNode, endNode);
		Path path = findAllPaths.iterator().next();
		if (path == null)
			return null;
		// EstimateEvaluator<Double> estimateEvaluator = new
		// EstimateEvaluator<Double>() {
		// @Override
		// public Double getCost(final Node node, final Node goal) {
		//
		// if (node.hasProperty("lat") && node.hasProperty("lon")) {
		// double dx = (Double) node.getProperty("lat")
		// - (Double) goal.getProperty("lat");
		// double dy = (Double) node.getProperty("lon")
		// - (Double) goal.getProperty("lon");
		// double result = Math
		// .sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		//
		// return result;
		// }
		//
		// return 100000000D;
		// }
		// };
		// PathFinder<WeightedPath> astar = GraphAlgoFactory
		// .aStar(PathExpanders.allTypesAndDirections(),
		// CommonEvaluators.doubleCostEvaluator("cost"),
		// estimateEvaluator);

		// WeightedPath path = astar.findSinglePath(startNode, endNode);

		Iterator<PropertyContainer> iterator = path.iterator();
		while (iterator.hasNext()) {
			PropertyContainer next = iterator.next();
			System.out.println(next);
			for (String str : next.getPropertyKeys()) {
				System.out.println(str + " : " + next.getProperty(str));
			}

		}

		// for (Relationship rel : path.relationships()) {
		// System.out
		// .println("-----------------------------------------------");
		// System.out.println("rel----------------");
		// for (String string : rel.getPropertyKeys()) {
		// System.out.println(string + " : " + rel.getProperty(string));
		// }
		//
		// Node startNode2 = rel.getStartNode();
		// System.out.println("start----------------");
		// for (String string : startNode2.getPropertyKeys()) {
		// System.out.println(string + " : "
		// + startNode2.getProperty(string));
		// }
		// System.out.println("end----------------");
		// Node getEndNode = rel.getEndNode();
		// for (String string : getEndNode.getPropertyKeys()) {
		// System.out.println(string + " : "
		// + getEndNode.getProperty(string));
		// }
		// }
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
}
