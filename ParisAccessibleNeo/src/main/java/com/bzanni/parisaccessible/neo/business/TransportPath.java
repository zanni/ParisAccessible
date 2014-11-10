package com.bzanni.parisaccessible.neo.business;

import java.io.Serializable;

public class TransportPath extends Path implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2148870457829963876L;

	public TransportPath(Location start, Location end, Double distance,
			Double cost) {
		super(start, end, distance, cost);
	}

	@Override
	public String getType() {
		return "TRANSPORT";
	}
}
