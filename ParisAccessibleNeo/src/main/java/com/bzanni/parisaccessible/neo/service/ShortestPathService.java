package com.bzanni.parisaccessible.neo.service;

import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.EstimateEvaluator;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PathExpanders;
import org.springframework.stereotype.Service;

@Service
public class ShortestPathService {

	public void findShortestPath(Node nodeA, Node nodeB) {
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
		
		WeightedPath path = astar.findSinglePath(nodeA, nodeB);
	}

}
