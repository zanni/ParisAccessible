package com.bzanni.parisaccessible.neo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.neo4j.gis.spatial.indexprovider.LayerNodeIndex;
import org.neo4j.gis.spatial.indexprovider.SpatialIndexProvider;
import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.EstimateEvaluator;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Configurable
public class ShortestPathService {

	public final static Double MATCH_DISTANCE_IN_METER = 5D;

	@Value("${neo4j_data_path}")
	private String neoDataPath;

	Index<Node> index;

	@PostConstruct
	public void init() {
//		GraphDatabaseService database = new GraphDatabaseFactory()
//				.newEmbeddedDatabase(neoDataPath);
//		Map<String, String> config = SpatialIndexProvider.SIMPLE_POINT_CONFIG;
//		IndexManager indexMan = database.index();
//		index = indexMan.forNodes("LOCATION", config);
	}

	private Node findNode(List<Double> point, Double distance) {

		// Transaction tx = database.beginTx();
		Map<String, Object> params = new HashMap<String, Object>();

		params.put(LayerNodeIndex.POINT_PARAMETER, new Double[] { point.get(0),
				point.get(1) });
		params.put(LayerNodeIndex.DISTANCE_IN_KM_PARAMETER, distance);
		IndexHits<Node> query = index.query(
				LayerNodeIndex.WITHIN_DISTANCE_QUERY, params);

		if (query.hasNext()) {
			return query.getSingle();
		}
		return null;
	}

	public WeightedPath findShortestPath(List<Double> start, List<Double> end) {
		EstimateEvaluator<Double> estimateEvaluator = new EstimateEvaluator<Double>() {
			@Override
			public Double getCost(final Node node, final Node goal) {
				double dx = (Double) node.getProperty("x")
						- (Double) goal.getProperty("x");
				double dy = (Double) node.getProperty("y")
						- (Double) goal.getProperty("y");
				double result = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
				return result;
			}
		};
		PathFinder<WeightedPath> astar = GraphAlgoFactory.aStar(
				PathExpanders.allTypesAndDirections(),
				CommonEvaluators.doubleCostEvaluator("length"),
				estimateEvaluator);
		
		Node startNode = this.findNode(start, ShortestPathService.MATCH_DISTANCE_IN_METER / 1000);
		Node endNode = this.findNode(end, ShortestPathService.MATCH_DISTANCE_IN_METER / 1000);
		return astar.findSinglePath(startNode, endNode);
	}
}
