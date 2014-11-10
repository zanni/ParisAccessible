package com.bzanni.parisaccessible.neo.business;

import java.io.Serializable;

public class TrottoirPath extends Path implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8453759213686653544L;

	public TrottoirPath(Location start, Location end, Double distance,
			Double cost) {
		super(start, end, distance, cost);
	}

	@Override
	public String getType() {
		return "TROTTOIR";
	}

}
