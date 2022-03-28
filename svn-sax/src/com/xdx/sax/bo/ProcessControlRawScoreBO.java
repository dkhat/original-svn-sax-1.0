package com.xdx.sax.bo;

import java.io.Serializable;

/**
 * ProcessControlRawScore
 */
public class ProcessControlRawScoreBO implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private long id;
	private ProcessControlBO processControl;
	private double expectedRawScore;
	private double bound;
	
	public ProcessControlRawScoreBO() {
	}

	public ProcessControlRawScoreBO(long id, ProcessControlBO processcontrol,
			MarkerBO marker, double expectedvalue, double standardDeviation) {
		this.id = id;
		this.processControl = processcontrol;
		this.expectedRawScore = expectedvalue;
		this.bound = standardDeviation;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ProcessControlBO getProcessControl() {
		return this.processControl;
	}

	public void setProcessControl(ProcessControlBO processcontrol) {
		this.processControl = processcontrol;
	}

	public double getExpectedRawScore() {
		return this.expectedRawScore;
	}

	public void setExpectedRawScore(double expectedvalue) {
		this.expectedRawScore = expectedvalue;
	}

	/**
	 * @return the lot
	 */
	public double getBound() {
		return bound;
	}

	/**
	 * @param lot the lot to set
	 */
	public void setBound(double bound) {
		this.bound = bound;
	}

}
