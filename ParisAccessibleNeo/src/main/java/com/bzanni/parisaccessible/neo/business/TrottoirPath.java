package com.bzanni.parisaccessible.neo.business;

import java.io.Serializable;

public class TrottoirPath extends Path implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8453759213686653544L;

	public TrottoirPath(Location start, Location end) {
		super(start, end);
	}

	@Override
	public String getType() {
		return "TROTTOIR";
	}

}
