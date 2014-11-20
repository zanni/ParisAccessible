package com.bzanni.parisaccessible.neo.business;

import java.io.Serializable;

public class TransportPath extends Path implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2148870457829963876L;

	public TransportPath(Location start, Location end) {
		super(start, end);
	}

	@Override
	public String getType() {
		return "TRANSPORT";
	}
}
