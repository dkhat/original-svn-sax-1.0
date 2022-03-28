/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.service;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.BusinessObject;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.processor.PlateProcessorImpl;
import com.xdx.sax.util.ws.PlateRegParameter;
import com.xdx.sax.util.ws.Result;

/**
 * @author smeier
 *
 */
@MessageDriven(activationConfig =
        { @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
          @ActivationConfigProperty(propertyName="destination",     propertyValue=SaxCoreServiceBean.QUEUE_NAME) },
               name="SaxCoreService")
public class SaxCoreServiceBean implements MessageListener {
	
	public static final String QUEUE_NAME = "queue/sax/core";
	
	public static final String METHOD_PARAM = "method";

	public static final String PROCESS_PLATE_METHOD = "processPlate";
	public static final String PROCESS_PLATE_BARCODE_PARAM = "barcode";

	public static final String REGISTER_PLATE_METHOD = "registerPlates";
	public static final String REGISTER_PLATE_PARAM = "plateRegData";

	private static Log log = LogFactory.getLog(SaxCoreServiceBean.class);
	
	public void onMessage(Message msg) {
		try {
			String method = msg.getStringProperty(METHOD_PARAM);
			if (method == PROCESS_PLATE_METHOD)
				processPlate(msg.getStringProperty(PROCESS_PLATE_BARCODE_PARAM));
			else if (method == REGISTER_PLATE_METHOD) {
				ObjectMessage omsg = (ObjectMessage)msg;
				registerPlates((PlateRegParameter[]) omsg.getObject());
			}
		} catch (JMSException e) {
			throw new SaxException(e);
		}
	}

    private void registerPlates(PlateRegParameter[] plateRegData) {
    	log.info(buildString("Registering ", plateRegData.length, " plates through messaging queue"));
    	
    	// Need to use the local interface of the session bean here
    	// to make sure we get a new transaction context
    	// for every method call to registerPlate()
    	SaxApiServiceBean bean=null;
    	
		try {
			bean = (SaxApiServiceBean) new InitialContext().lookup(SaxApiServiceBean.JNDI_NAME);
		} catch (NamingException e) {
			throw new SaxException(e);
		}

		Result res;
    	for (PlateRegParameter p: plateRegData) {
			res = bean.registerPlate(
					p.getExternalPlateSetId(), 
					p.getPlateNum(), 
					p.getTestcode(), 
					p.getPlateBarcode(), 
					p.getPlateDesignId(),
					true,
					p.getProcessControls(),
					p.getEmptySections());
			
			if (! res.getOk())
				log.error(res.getErrorMessage());
    	}
		
    	log.info(buildString("=== done ==="));
	}

	public void processPlate(String plateBarcode) {
    	new PlateProcessorImpl().process(plateBarcode);
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
