package com.bzanni.parisaccessible.elasticsearch.opendataparis;

import io.searchbox.annotations.JestId;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

/**
 * 
 * type;nom;numero;voie;code_postal;remarques;handicap_moteur;handicap_visuel;
 * handicap_auditif;lien;coordx_wgs;coordy_wgs;column_14
 * 
 * @author bertrandzanni
 *
 */
public class Equipement implements JestBusiness {

	@JestId
	private String id;

	public String type;

	public String nom;

	public String numero;

	private String voie;

	public String code_postal;

	public String remarques;

	public Boolean handicapMoteur;

	public Boolean handicapVisuel;

	public Boolean handicapAuditif;

	private String lien;

	private GeoPoint location;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getCode_postal() {
		return code_postal;
	}

	public void setCode_postal(String code_postal) {
		this.code_postal = code_postal;
	}

	public String getRemarques() {
		return remarques;
	}

	public void setRemarques(String remarques) {
		this.remarques = remarques;
	}

	public Boolean getHandicapMoteur() {
		return handicapMoteur;
	}

	public void setHandicapMoteur(Boolean handicapMoteur) {
		this.handicapMoteur = handicapMoteur;
	}

	public Boolean getHandicapVisuel() {
		return handicapVisuel;
	}

	public void setHandicapVisuel(Boolean handicapVisuel) {
		this.handicapVisuel = handicapVisuel;
	}

	public Boolean getHandicapAuditif() {
		return handicapAuditif;
	}

	public void setHandicapAuditif(Boolean handicapAuditif) {
		this.handicapAuditif = handicapAuditif;
	}

	public GeoPoint getLocation() {
		return location;
	}

	public void setLocation(GeoPoint location) {
		this.location = location;
	}

	public String getVoie() {
		return voie;
	}

	public void setVoie(String voie) {
		this.voie = voie;
	}

	public String getLien() {
		return lien;
	}

	public void setLien(String lien) {
		this.lien = lien;
	}
}
