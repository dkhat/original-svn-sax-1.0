package com.xdx.sax.to;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.io.Serializable;

/**
 * MarkerTypeBO generated by hbm2java
 */
public class MarkerTypeTO implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name;

	public MarkerTypeTO() {
	}

	public MarkerTypeTO(long id, String name) {
		this.id = id;
		this.name = name;
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

}
