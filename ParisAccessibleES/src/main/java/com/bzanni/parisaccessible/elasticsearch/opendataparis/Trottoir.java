package com.bzanni.parisaccessible.elasticsearch.opendataparis;

import io.searchbox.annotations.JestId;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.business.GeoShape;
import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

/**
 * 
 * geom_x_y;geom;niveau;info;libelle
 * 
 * @author bertrandzanni
 *
 */
public class Trottoir implements JestBusiness {

	@JestId
	private String id;
//	private GeoPoint location;
	private GeoShape shape;

	private String info;
//	private String libelle;
//	private String niveau;

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

//	public String getNiveau() {
//		return niveau;
//	}
//
//	public void setNiveau(String niveau) {
//		this.niveau = niveau;
//	}
//
//	public String getLibelle() {
//		return libelle;
//	}
//
//	public void setLibelle(String libelle) {
//		this.libelle = libelle;
//	}
//
//	public GeoPoint getLocation() {
//		return location;
//	}
//
//	public void setLocation(GeoPoint location) {
//		this.location = location;
//	}

}
