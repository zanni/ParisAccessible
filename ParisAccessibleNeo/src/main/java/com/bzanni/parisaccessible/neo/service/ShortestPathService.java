package com.bzanni.parisaccessible.neo.service;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.SimplePointLayer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.pipes.GeoPipeFlow;
import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.EstimateEvaluator;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
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

	@Value("${neo4j_data_path}")
	private String neoDataPath;

	GraphDatabaseService database;
	Index<Node> sidwayIndex;
	SpatialDatabaseService spatialService;
	Layer layer;
	Index<Node> forNodes;
	SimplePointLayer mainPointsLayer;

	// SimplePointLayer createSimplePointLayer;

	public void init(String dataFolder) {
		database = new GraphDatabaseFactory().newEmbeddedDatabase(neoDataPath
				+ "/" + dataFolder);

		SpatialDatabaseService spatialService = new SpatialDatabaseService(
				database);

		Transaction tx = database.beginTx();

		if (spatialService.containsLayer("LOC")) {
			mainPointsLayer = (SimplePointLayer) spatialService.getLayer("LOC");
		} else {
			mainPointsLayer = spatialService.createSimplePointLayer("LOC",
					"lat", "lon");
		}

		Iterable<Node> allNodes = GlobalGraphOperations.at(database)
				.getAllNodes();
		int z = 0;
		for (Node n : allNodes) {
			z++;
			mainPointsLayer.add(n);
			if (z > 100)
				break;
		}

		tx.success();
		tx.close();
	}

	private Node findNode(List<Double> point) {
		Transaction tx = database.beginTx();

		List<GeoPipeFlow> findClosestPointsTo = mainPointsLayer
				.findClosestPointsTo(new Coordinate(point.get(0), point.get(1)));
		
		while (findClosestPointsTo.iterator().hasNext()) {
			GeoPipeFlow next = findClosestPointsTo.iterator().next();
			SpatialDatabaseRecord record = next.getRecord();
			return record.getGeomNode();
		}
		tx.close();
		return null;
	}

	private Location fromNode(Node n) {
		Double lat = (Double) n.getProperty("lat");
		Double lon = (Double) n.getProperty("lon");
		String id = (String) n.getProperty("id");
		Location loc = new Location();
		loc.setId(id);
		loc.setLat(lat);
		loc.setLon(lon);
		loc.setGraphId(n.getId());
		return loc;
	}

	public Location findLocation(List<Double> point) {
		Node n = this.findNode(point);
		return fromNode(n);

	}

	public List<Location> findShortestPath(List<Double> start, List<Double> end) {
		EstimateEvaluator<Double> estimateEvaluator = new EstimateEvaluator<Double>() {
			@Override
			public Double getCost(final Node node, final Node goal) {
				return 1D;
			}
		};
		PathFinder<WeightedPath> astar = GraphAlgoFactory.aStar(
				PathExpanders.allTypesAndDirections(),
				CommonEvaluators.doubleCostEvaluator("cost", 1),
				estimateEvaluator);

		Node startNode = this.findNode(start);
		Node endNode = this.findNode(end);
		WeightedPath findSinglePath = astar.findSinglePath(startNode, endNode);

		List<Location> list = new ArrayList<Location>();

		for (Node n : findSinglePath.nodes()) {
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
			} else {
				System.out.println(n.toString());
			}

		}

		return list;
	}
}
