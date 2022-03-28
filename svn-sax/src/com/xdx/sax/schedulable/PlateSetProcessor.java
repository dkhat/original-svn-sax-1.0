package com.xdx.sax.schedulable;

import javax.ejb.Local;

@Local
public interface PlateSetProcessor {

	public static final String JNDI_NAME = "sax/PlateSetProcessor/local";

	public abstract void processNextPlate();

}