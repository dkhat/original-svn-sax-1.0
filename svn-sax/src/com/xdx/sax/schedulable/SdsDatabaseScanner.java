package com.xdx.sax.schedulable;

import javax.ejb.Local;

@Local
public interface SdsDatabaseScanner {

	public static final String JNDI_NAME = "AbiService/local";
	
	public void scanSdsDatabase();

}