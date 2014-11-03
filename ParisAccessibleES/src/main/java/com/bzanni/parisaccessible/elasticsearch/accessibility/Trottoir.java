package com.bzanni.parisaccessible.elasticsearch.accessibility;

import io.searchbox.annotations.JestId;

import com.bzanni.parisaccessible.elasticsearch.business.GeoShape;
import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

public class Trottoir implements JestBusiness {

	@JestId
	private String id;
	private GeoShape shape;

	private String info;
	private String dist;
	private String niveau;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GeoShape getShape() {
		return shape;
	}

	public void setShape(GeoShape shape) {
		this.shape = shape;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getDist() {
		return dist;
	}

	public void setDist(String dist) {
		this.dist = dist;
	}

	public String getNiveau() {
		return niveau;
	}

	public void setNiveau(String niveau) {
		this.niveau = niveau;
	}

}
