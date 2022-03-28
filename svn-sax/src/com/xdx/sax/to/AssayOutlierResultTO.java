package com.xdx.sax.to;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.io.Serializable;

/**
 * AggMarkerResultBO generated by hbm2java
 */
public class AssayOutlierResultTO implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private long id;
	private PlateSetTO plateset;
	private PlateSectionTO platesection;
	private MarkerTO marker;
	private Boolean pass;

	public AssayOutlierResultTO() {
	}

	public AssayOutlierResultTO(long id, PlateSetTO plateset,
			PlateSectionTO platesection) {
		this.id = id;
		this.plateset = plateset;
		this.platesection = platesection;
	}

	public AssayOutlierResultTO(long id, PlateSetTO plateset,
			PlateSectionTO platesection, MarkerTO aggregate,
			Boolean pass) {
		this.id = id;
		this.plateset = plateset;
		this.platesection = platesection;
		this.marker = aggregate;
		this.pass = pass;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PlateSetTO getPlateset() {
		return this.plateset;
	}

	public void setPlateset(PlateSetTO plateset) {
		this.plateset = plateset;
	}

	public PlateSectionTO getPlatesection() {
		return this.platesection;
	}

	public void setPlatesection(PlateSectionTO platesection) {
		this.platesection = platesection;
	}

	public MarkerTO getMarker() {
		return this.marker;
	}

	public void setMarker(MarkerTO aggregate) {
		this.marker = aggregate;
	}

	public Boolean getPass() {
		return this.pass;
	}

	public void setPass(Boolean pass) {
		this.pass = pass;
	}

}
