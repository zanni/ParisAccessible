package com.bzanni.parisaccessible.neo.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.bzanni.parisaccessible.neo.business.Location;

public interface LocationRepository extends GraphRepository<Location>{

}
