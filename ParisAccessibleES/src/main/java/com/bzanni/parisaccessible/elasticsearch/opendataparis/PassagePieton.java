package com.bzanni.parisaccessible.elasticsearch.opendataparis;

import io.searchbox.annotations.JestId;

import com.bzanni.parisaccessible.elasticsearch.business.GeoPoint;
import com.bzanni.parisaccessible.elasticsearch.business.JestBusiness;

/**
 * 
 * geometry;geometry_vertex_count;info;libelle;info_ft_style;libelle_ft_style;import_notes
 * @author bertrandzanni
 *
 */
public class PassagePieton implements JestBusiness {

	@JestId
	private String id;
	
	private String info;
	
	private String libelle;
	
	private String info_ft_style;
	
	private String libelle_ft_style;
	
	private String import_notes;
	
	private GeoPoint start;

	private GeoPoint end;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GeoPoint getStart() {
		return start;
	}

	public void setStart(GeoPoint start) {
		this.start = start;
	}

	public GeoPoint getEnd() {
		return end;
	}

	public void setEnd(GeoPoint end) {
		this.end = end;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getInfo_ft_style() {
		return info_ft_style;
	}

	public void setInfo_ft_style(String info_ft_style) {
		this.info_ft_style = info_ft_style;
	}

	public String getLibelle_ft_style() {
		return libelle_ft_style;
	}

	public void setLibelle_ft_style(String libelle_ft_style) {
		this.libelle_ft_style = libelle_ft_style;
	}

	public String getImport_notes() {
		return import_notes;
	}

	public void setImport_notes(String import_notes) {
		this.import_notes = import_notes;
	}

}
