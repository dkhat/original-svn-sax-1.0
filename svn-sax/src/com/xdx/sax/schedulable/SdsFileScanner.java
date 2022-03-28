package com.xdx.sax.schedulable;

import javax.ejb.Local;

@Local
public interface SdsFileScanner {

	public static final String JNDI_NAME = "sax/SdsFileScanner/local";

	public abstract void scanDirectory();

	public abstract void processNextSdsFile();

}