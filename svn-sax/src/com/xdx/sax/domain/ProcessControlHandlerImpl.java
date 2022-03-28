/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.domain;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.BusinessObject;
import com.xdx.sax.bo.MarkerBO;
import com.xdx.sax.bo.ProcessControlBO;
import com.xdx.sax.bo.ProcessControlDetailBO;
import com.xdx.sax.bo.ProcessControlRawScoreBO;
import com.xdx.sax.bo.TestcodeBO;
import com.xdx.sax.dao.MarkerDAO;
import com.xdx.sax.dao.ProcessControlDAO;
import com.xdx.sax.dao.ProcessControlDetailDAO;
import com.xdx.sax.dao.ProcessControlRawScoreDAO;
import com.xdx.sax.dao.TestcodeDAO;
import com.xdx.sax.exceptions.SaxException;
import com.xdx.sax.util.ws.ProcessControlItem;

/**
 * @author smeier
 *
 */
public class ProcessControlHandlerImpl implements ProcessControlHandler {

	private static final Log log = LogFactory.getLog(ProcessControlHandlerImpl.class);
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.domain.ProcessControlHandler#registerNewLot(java.lang.String, com.xdx.sax.util.ws.ProcessControlItem[])
	 */
	public void registerNewLot(
			String testcode,
			String lotName,
			Double expectedRawScore,
			Double rawScoreStandardDeviation,
			ProcessControlItem[] processControlData) {
		log.debug(buildString("Registering process control lot ", lotName));
		
//		if (processControlData.length == 0)
//			throw new SaxException(buildString("No process control data"));
		
		ProcessControlDAO pcDao = new ProcessControlDAO();
		
		ArrayList<ProcessControlDetailBO> details = new ArrayList<ProcessControlDetailBO>();
		ProcessControlBO newProcessControl;		
		newProcessControl = pcDao.findByExternalId(lotName);
		
		if (newProcessControl != null)
			throw new SaxException(buildString("Lot name ", lotName, " already exists."));

		TestcodeBO testcodeObj = new TestcodeDAO().findByName(testcode);
		newProcessControl = new ProcessControlBO();
		newProcessControl.setTestcode(testcodeObj);
		newProcessControl.setExternalid(lotName);

		ProcessControlRawScoreBO pcRawScore = new ProcessControlRawScoreBO();
		pcRawScore.setProcessControl(newProcessControl);
		pcRawScore.setExpectedRawScore(expectedRawScore);
		pcRawScore.setBound(rawScoreStandardDeviation);
		
		for (ProcessControlItem item : processControlData) {
			MarkerBO marker = new MarkerDAO().findByName(item.getMarkerName());
			if (marker == null)
				throw new SaxException(buildString("Cannot find marker ", item.getMarkerName()));
			
			ProcessControlDetailBO detail = new ProcessControlDetailBO();
			detail.setMarker(marker);
			detail.setExpectedValue(item.getExpectedValue());
			detail.setLot(lotName);
			details.add(detail);
		}

		pcDao.persist(newProcessControl);
		log.debug(buildString("Persisted new process control lot ", newProcessControl.getExternalid(), ": id ", newProcessControl.getId()));

		new ProcessControlRawScoreDAO().persist(pcRawScore);
		log.debug(buildString("Persisted process control raw score ", pcRawScore.getExpectedRawScore(), ": id ", pcRawScore.getId()));
		
		for (ProcessControlDetailBO detail : details) {
			detail.setProcessControl(newProcessControl);
			new ProcessControlDetailDAO().persist(detail);

			log.debug(buildString("Persisting process control detail for marker ", detail.getMarker().getName(), " - value ", detail.getExpectedValue()));
		}
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
