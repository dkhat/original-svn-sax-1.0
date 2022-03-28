package com.xdx.sax.to;

// Generated Feb 13, 2009 8:27:50 PM by Hibernate Tools 3.2.4.CR1

import java.io.Serializable;

/**
 * Processcontrol generated by hbm2java
 */
public class ProcessControlTO implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private long id;
	private TestcodeTO testcode;
	private String externalid;

	public ProcessControlTO() {
	}

	public ProcessControlTO(long id, TestcodeTO testcode, String externalid) {
		this.id = id;
		this.testcode = testcode;
		this.externalid = externalid;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TestcodeTO getTestcode() {
		return this.testcode;
	}

	public void setTestcode(TestcodeTO testcode) {
		this.testcode = testcode;
	}

	public String getExternalid() {
		return this.externalid;
	}

	public void setExternalid(String externalid) {
		this.externalid = externalid;
	}

}
