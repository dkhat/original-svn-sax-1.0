/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.util.ws;

import java.io.Serializable;

import com.xdx.sax.bo.PlateBO;
import com.xdx.sax.dao.PlateDAO;
import com.xdx.sax.exceptions.SaxException;

/**
 * @author smeier
 *
 */
public class PlateQcResult implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	
	private String qcResult;
	private String qcFailReason;
	private String qcComment;
	
	public PlateQcResult() {}

	public PlateQcResult(String barcode) {
		PlateBO plate = new PlateDAO().findByBarcode(barcode);
		
		if (plate == null)
			throw new SaxException(buildString("Plate does not exist"));
		
		// TODO: This works around a STARLIMS bug where NULL values for
		//       strings are not processed correctly. Needs removed !!!!!
		if (plate.getQcResult() == null)
			qcResult = "";
		else
			qcResult = plate.getQcResult();
		
		if (plate.getQcFailReason() == null)
			qcFailReason = "";
		else
			qcFailReason = plate.getQcFailReason();
		
		if (plate.getQcComment() == null)
			qcComment = "";
		else
			qcComment = plate.getQcComment();
	}

	/**
	 * @return the qcResult
	 */
	public String getQcResult() {
		return qcResult;
	}

	/**
	 * @param qcResult the qcResult to set
	 */
	public void setQcResult(String qcResult) {
		this.qcResult = qcResult;
	}

	/**
	 * @return the qcFailReason
	 */
	public String getQcFailReason() {
		return qcFailReason;
	}

	/**
	 * @param qcFailReason the qcFailReason to set
	 */
	public void setQcFailReason(String qcFailReason) {
		this.qcFailReason = qcFailReason;
	}

	/**
	 * @return the qcComment
	 */
	public String getQcComment() {
		return qcComment;
	}

	/**
	 * @param qcComment the qcComment to set
	 */
	public void setQcComment(String qcComment) {
		this.qcComment = qcComment;
	}

	/**
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
     * Utility method tests for absence of a nullity state
     * in a <i>Null Object Pattern</i> design pattern manner.
     */
    protected final boolean assertNotNull(Object object) {
    	return object != null;
	}

    /**
     * Utility method tests for presence of a nullity state
     * in a <i>Null Object Pattern</i> design pattern manner.
     */
    protected final boolean assertNull(Object object) {
    	return  !  assertNotNull(object);
	}

    /**
     * Utility method builds Strings; avoids string concatenation.
     */
    protected final String buildString(Object... values) {
    	
    	StringBuilder sb= new StringBuilder();

    	for (Object object : values) {
    		sb.append((assertNull(object))  ?  "" :  object.toString());
		}

    	return sb.toString();
	}

}
