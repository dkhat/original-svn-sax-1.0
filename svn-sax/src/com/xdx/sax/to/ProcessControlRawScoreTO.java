package com.xdx.sax.to;

import java.io.Serializable;

/**
 * ProcessControlRawScore
 */
public class ProcessControlRawScoreTO implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private long id;
	private ProcessControlTO processControl;
	private double expectedRawScore;
	private double bound;
	
	public ProcessControlRawScoreTO() {
	}

	public ProcessControlRawScoreTO(long id, ProcessControlTO processcontrol,
			double expectedvalue, double standardDeviation) {
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

	public ProcessControlTO getProcessControl() {
		return this.processControl;
	}

	public void setProcessControl(ProcessControlTO processcontrol) {
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
	public void setBound(double standardDeviation) {
		this.bound = standardDeviation;
	}

}
