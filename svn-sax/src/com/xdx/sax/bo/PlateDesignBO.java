package com.xdx.sax.bo;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * PlateDesignBO generated by hbm2java
 */
public class PlateDesignBO implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name;
	private long numberofplates;
	private long numberrowsperplate;
	private long numbercolumnsperplate;
	private Double mastermixvolume;
	private Double availablewellvolume;
	private Set<PlateDesignDetailBO> platedesigndetails = new HashSet<PlateDesignDetailBO>(0);
	private Set<PlateSetBO> platesets = new HashSet<PlateSetBO>(0);
	private Set<AggSectionResultBO> aggsectionresults = new HashSet<AggSectionResultBO>(0);
	private Set<PlateSectionBO> platesections = new HashSet<PlateSectionBO>(0);
	private Set<TestcodeBO> testcodes = new HashSet<TestcodeBO>(0);

	public PlateDesignBO() {
	}

	public PlateDesignBO(long id, String name, long numberofplates,
			long numberrowsperplate, long numbercolumnsperplate) {
		this.id = id;
		this.name = name;
		this.numberofplates = numberofplates;
		this.numberrowsperplate = numberrowsperplate;
		this.numbercolumnsperplate = numbercolumnsperplate;
	}

	public PlateDesignBO(long id, String name, long numberofplates,
			long numberrowsperplate, long numbercolumnsperplate,
			Double mastermixvolume, Double availablewellvolume,
			Set<PlateDesignDetailBO> platedesigndetails, Set<PlateSetBO> platesets, Set<AggSectionResultBO> aggsectionresults,
			Set<PlateSectionBO> platesections, Set<TestcodeBO> testcodes) {
		this.id = id;
		this.name = name;
		this.numberofplates = numberofplates;
		this.numberrowsperplate = numberrowsperplate;
		this.numbercolumnsperplate = numbercolumnsperplate;
		this.mastermixvolume = mastermixvolume;
		this.availablewellvolume = availablewellvolume;
		this.platedesigndetails = platedesigndetails;
		this.platesets = platesets;
		this.aggsectionresults = aggsectionresults;
		this.platesections = platesections;
		this.testcodes = testcodes;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getNumberofplates() {
		return this.numberofplates;
	}

	public void setNumberofplates(long numberofplates) {
		this.numberofplates = numberofplates;
	}

	public long getNumberrowsperplate() {
		return this.numberrowsperplate;
	}

	public void setNumberrowsperplate(long numberrowsperplate) {
		this.numberrowsperplate = numberrowsperplate;
	}

	public long getNumbercolumnsperplate() {
		return this.numbercolumnsperplate;
	}

	public void setNumbercolumnsperplate(long numbercolumnsperplate) {
		this.numbercolumnsperplate = numbercolumnsperplate;
	}

	public Double getMastermixvolume() {
		return this.mastermixvolume;
	}

	public void setMastermixvolume(Double mastermixvolume) {
		this.mastermixvolume = mastermixvolume;
	}

	public Double getAvailablewellvolume() {
		return this.availablewellvolume;
	}

	public void setAvailablewellvolume(Double availablewellvolume) {
		this.availablewellvolume = availablewellvolume;
	}

	public Set<PlateDesignDetailBO> getPlatedesigndetails() {
		return this.platedesigndetails;
	}

	public void setPlatedesigndetails(Set<PlateDesignDetailBO> platedesigndetails) {
		this.platedesigndetails = platedesigndetails;
	}

	public Set<PlateSetBO> getPlatesets() {
		return this.platesets;
	}

	public void setPlatesets(Set<PlateSetBO> platesets) {
		this.platesets = platesets;
	}

	public Set<AggSectionResultBO> getAggsectionresults() {
		return this.aggsectionresults;
	}

	public void setAggsectionresults(Set<AggSectionResultBO> aggsectionresults) {
		this.aggsectionresults = aggsectionresults;
	}

	public Set<PlateSectionBO> getPlatesections() {
		return this.platesections;
	}

	public void setPlatesections(Set<PlateSectionBO> platesections) {
		this.platesections = platesections;
	}

	public Set<TestcodeBO> getTestcodes() {
		return this.testcodes;
	}

	public void setTestcodes(Set<TestcodeBO> testcodes) {
		this.testcodes = testcodes;
	}

}
