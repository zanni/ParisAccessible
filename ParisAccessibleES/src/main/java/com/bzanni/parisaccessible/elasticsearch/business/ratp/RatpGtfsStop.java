package com.bzanni.parisaccessible.elasticsearch.business.ratp;

import io.searchbox.annotations.JestId;

import java.util.ArrayList;
import java.util.List;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

public class RatpGtfsStop implements JestBusiness {

	@JestId
	private String id;

	private String codeStiff;

	private String name;

	private String description;

	private Boolean accessibleUFR;

	private Boolean annonceSonorProchainArret;

	private Boolean annonceVisuelleProchainArret;

	private Boolean annonceSonoreSituationPerturbe;

	private Boolean annonceVisuelleSituationPerturbe;

	private GeoPoint location;

	private List<RatpGtfsTransfert> transferts;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCodeStiff() {
		return codeStiff;
	}

	public void setCodeStiff(String codeStiff) {
		this.codeStiff = codeStiff;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getAccessibleUFR() {
		return accessibleUFR;
	}

	public void setAccessibleUFR(Boolean accessibleUFR) {
		this.accessibleUFR = accessibleUFR;
	}

	public Boolean getAnnonceSonorProchainArret() {
		return annonceSonorProchainArret;
	}

	public void setAnnonceSonorProchainArret(Boolean annonceSonorProchainArret) {
		this.annonceSonorProchainArret = annonceSonorProchainArret;
	}

	public Boolean getAnnonceVisuelleProchainArret() {
		return annonceVisuelleProchainArret;
	}

	public void setAnnonceVisuelleProchainArret(
			Boolean annonceVisuelleProchainArret) {
		this.annonceVisuelleProchainArret = annonceVisuelleProchainArret;
	}

	public Boolean getAnnonceSonoreSituationPerturbe() {
		return annonceSonoreSituationPerturbe;
	}

	public void setAnnonceSonoreSituationPerturbe(
			Boolean annonceSonoreSituationPerturbe) {
		this.annonceSonoreSituationPerturbe = annonceSonoreSituationPerturbe;
	}

	public Boolean getAnnonceVisuelleSituationPerturbe() {
		return annonceVisuelleSituationPerturbe;
	}

	public void setAnnonceVisuelleSituationPerturbe(
			Boolean annonceVisuelleSituationPerturbe) {
		this.annonceVisuelleSituationPerturbe = annonceVisuelleSituationPerturbe;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public GeoPoint getLocation() {
		return location;
	}

	public void setLocation(GeoPoint location) {
		this.location = location;
	}

	public List<RatpGtfsTransfert> getTransferts() {
		return transferts;
	}

	public void setTransferts(List<RatpGtfsTransfert> transferts) {
		this.transferts = transferts;
	}

	public void addTransfert(String toStopId, String type, Double time) {
		if (this.transferts == null) {
			this.transferts = new ArrayList<RatpGtfsTransfert>();
		}
		if(!this.transferts.contains(toStopId)){
			this.transferts.add(new RatpGtfsTransfert(toStopId, type, time));
		}
		
	}
}
