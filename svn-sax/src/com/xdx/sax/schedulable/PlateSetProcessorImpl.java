package com.xdx.sax.schedulable;

import java.util.Date;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.varia.scheduler.Schedulable;

import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.service.SaxApiServiceBean;

@Stateless(name="PlateSetProcessor")
public class PlateSetProcessorImpl implements Schedulable, PlateSetProcessor {

	private static final Log log = LogFactory.getLog(PlateSetProcessorImpl.class);

	public void perform(Date arg0, long arg1) {
		log.info("Scheduler fired on " + arg0);
		PlateSetProcessor bean;
		try {
			bean = (PlateSetProcessor) new InitialContext().lookup(JNDI_NAME);
		} catch(NamingException e) {
			throw new SaxException(e);
		}
		bean.processNextPlate();
	}
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.schedulable.SdsFileScanner#scanDirectory()
	 */
	public void processNextPlate() {
		// Need to use local interface to the session been at this point
		// to ensure that we are getting a new transaction context for each call
		// to the processFile() method.
		SaxApiServiceBean bean;
		
		try {
			bean = (SaxApiServiceBean) new InitialContext().lookup(SaxApiServiceBean.JNDI_NAME);
		} catch (NamingException e) {
			throw new SaxException(e);
		}
			
		// list directory (filter from configuration)
		bean.processNextPlate();					
	}

}
