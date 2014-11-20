package com.bzanni.parisaccessible.elasticsearch.business.gtfs;

import io.searchbox.annotations.JestId;

import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

public class GtfsRoute implements JestBusiness{

	@JestId
	private String id;

	private String name;

	private String longName;

	private String description;

	private String route_color;

	private String route_color_text;

	private Integer type;

	private String origin;

	private String destination;

	private Boolean accessibleUFR;

	private Boolean plancherBas;

	private Boolean palette;

	private Boolean annonceSonoreProchainArret;

	private Boolean annonceVisuelleProchainArret;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Boolean getAccessibleUFR() {
		return accessibleUFR;
	}

	public void setAccessibleUFR(Boolean accessibleUFR) {
		this.accessibleUFR = accessibleUFR;
	}

	public Boolean getPlancherBas() {
		return plancherBas;
	}

	public void setPlancherBas(Boolean plancherBas) {
		this.plancherBas = plancherBas;
	}

	public Boolean getPalette() {
		return palette;
	}

	public void setPalette(Boolean palette) {
		this.palette = palette;
	}

	public Boolean getAnnonceVisuelleProchainArret() {
		return annonceVisuelleProchainArret;
	}

	public void setAnnonceVisuelleProchainArret(
			Boolean annonceVisuelleProchainArret) {
		this.annonceVisuelleProchainArret = annonceVisuelleProchainArret;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRoute_color() {
		return route_color;
	}

	public void setRoute_color(String route_color) {
		this.route_color = route_color;
	}

	public String getRoute_color_text() {
		return route_color_text;
	}

	public void setRoute_color_text(String route_color_text) {
		this.route_color_text = route_color_text;
	}

	public Boolean getAnnonceSonoreProchainArret() {
		return annonceSonoreProchainArret;
	}

	public void setAnnonceSonoreProchainArret(Boolean annonceSonoreProchainArret) {
		this.annonceSonoreProchainArret = annonceSonoreProchainArret;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
