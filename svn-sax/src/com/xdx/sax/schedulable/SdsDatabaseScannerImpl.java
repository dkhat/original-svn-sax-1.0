/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.schedulable;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.varia.scheduler.Schedulable;

import com.xdx.abi.ws.AbiService;
import com.xdx.sax.domain.ConfigurationModel;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.service.SaxApiServiceBean;


/**
 * @author smeier
 *
 */
@Stateless(name="SdsDatabaseScanner")
public class SdsDatabaseScannerImpl implements Schedulable, SdsDatabaseScanner {

	private Log log = LogFactory.getLog(this.getClass());

	/* (non-Javadoc)
	 * @see org.jboss.varia.scheduler.Schedulable#perform(java.util.Date, long)
	 */
	public void perform(Date arg0, long arg1) {
		log.debug(buildString("Database scanner fired on ", arg0));

		scanSdsDatabase();
	}
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.schedulable.SdsDatabaseScanner#scanSdsDatabase()
	 */
	public void scanSdsDatabase() {
		log.debug("Calling ABI service");

		Boolean deleteFile = ConfigurationModel.lookup("HTx", "SYSTEM", "DELETE_SDS_FILE").equals("TRUE") ? true : false;
		
		AbiService abiServiceBean = (AbiService) getLocalBeanInterface(JNDI_NAME);
		SaxApiServiceBean saxBean = (SaxApiServiceBean) getLocalBeanInterface(SaxApiServiceBean.JNDI_NAME);
		
		List<String> newBarcodes = saxBean.getNewBarcodes();
		
		for (String barcode : newBarcodes) {
			log.debug(buildString("Getting SDS data for barcode ", barcode));
			String fileName = abiServiceBean.getDocument(barcode);

			if (fileName != null) {
				log.info(buildString("Processing file ", fileName));
				saxBean.processFile(fileName);
			
				// delete file after processing
				if (deleteFile) {
					File f = new File(fileName);
					f.delete();
				}
			}
		}
	}

	/**
	 * 
	 * @param jndiName
	 * @return
	 */
	private Object getLocalBeanInterface(String jndiName) {
		log.debug(buildString("Obtaining local bean interface ", jndiName));
		
		Object res;
		try {
			res = new InitialContext().lookup(jndiName);
		} catch(NamingException e) {
			log.error(buildString("Error looking up local interface ", jndiName));
			throw new SaxException(e);
		}
		return res;
	}
	
	/**
	 * Utility method tests for abscence of a nullity state
	 * in a <i>Null Object Pattern</i> design pattern manner.
	 */
	protected final static boolean assertNotNull(Object object) {
	 	return object != null;
	}

	/**
	 * Utility method tests for presence of a nullity state
	 * in a <i>Null Object Pattern</i> design pattern manner.
	 */
	protected final static boolean assertNull(Object object) {
	 	return  !  assertNotNull(object);
	}

	/**
	 * Utility method builds Strings; avoids string concatenation.
	 *
	 * @param values
	 * @return the concatenated string
	 */
	protected final static String buildString(Object... values) {

		StringBuilder sb= new StringBuilder();

		for (Object object : values) {
			sb.append((assertNull(object))  ?  "" :  object.toString());
		}

		return sb.toString();
	}

}
