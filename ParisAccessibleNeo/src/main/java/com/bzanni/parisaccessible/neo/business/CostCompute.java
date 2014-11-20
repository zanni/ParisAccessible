package com.bzanni.parisaccessible.neo.business;

import java.util.Arrays;
import java.util.List;

public class CostCompute {
	

	public static Double computeCost(Location a, Location b,
			Double transportSpeed) {

		Double computeDistance = CostCompute.computeDistance(
				Arrays.asList(a.getLat(), a.getLon()),
				Arrays.asList(b.getLat(), b.getLon()));

		return computeDistance * transportSpeed;
	}

	private static Double deg2rad(Double deg) {
		return deg * (Math.PI / 180);
	}

	/**
	 * Retreive distance in m between two lat/lon points
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Double computeDistance(List<Double> a, List<Double> b) {
		Double R = 6371D; // Radius of the earth in km

		Double dLat = deg2rad(a.get(0) - b.get(0));
		Double dLon = deg2rad(a.get(1) - b.get(1));

		Double v1 = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(deg2rad(a.get(0))) * Math.cos(deg2rad(b.get(0)))
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);

		Double v2 = 2 * Math.atan2(Math.sqrt(v1), Math.sqrt(1 - v1));
		Double d = R * v2 * 1000; // Distance in m
		return d;
	}

	public static Double computeDistance(Location a, Location b) {
		return CostCompute.computeDistance(
				Arrays.asList(a.getLat(), a.getLon()),
				Arrays.asList(b.getLat(), b.getLon()));
	}

}
