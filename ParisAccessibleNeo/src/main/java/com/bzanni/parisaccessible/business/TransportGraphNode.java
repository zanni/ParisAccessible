package com.bzanni.parisaccessible.business;

import java.util.Set;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class TransportGraphNode {

	 @GraphId
	 private Long id;
	 @RelatedTo
	 private Set<TransportGraphNode> connections;
}
