/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.processor.modules.extra;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.bo.AssayOutlierResultBO;
import com.xdx.sax.bo.MarkerBO;
import com.xdx.sax.dao.AssayOutlierResultDAO;
import com.xdx.sax.dao.MarkerDAO;
import com.xdx.sax.exceptions.SaxException;

/**
 * @author smeier
 *
 */
public class AssayOutlierCheckImpl extends AbstractAssayOutlierCheck {

	private static final Log log = LogFactory.getLog(AssayOutlierCheckImpl.class);
	
	/* (non-Javadoc)
	 * @see com.xdx.sax.processor.modules.extra.AssayOutlierCheck#runCheck()
	 */
	public void runCheck() {
		log.debug(buildString("Running AssayOutlier check"));

		check("ITGA4", 29.0);
		
		check("PDCD1", 34.5);
	}
	
	private void check(String markerName, double cutOff) {
		// Check value
		Double val;
		
		try {
			val = dataSet.getAssay(markerName).getResult().getWsmnormct();
		} catch (NullPointerException e) {
			return;
		}
		
		boolean pass = true;
		
		if ((val == null) || (val >= cutOff))
			pass = false;
		
		// find marker
		MarkerBO marker = new MarkerDAO().findByName(markerName);
		if (marker == null)
			throw new SaxException(buildString("Cannot find marker ", markerName));
		
		AssayOutlierResultDAO dao = new AssayOutlierResultDAO();
		
		// Check for existing result object to update
		AssayOutlierResultBO result = dao.findByPlatesetSectionMarker(
				dataSet.getPlateSet().getId(),
				dataSet.getPlateSection().getId(),
				marker.getId());
		
		if (result == null) {
			// new object
			result = new AssayOutlierResultBO();
			result.setPlateset(dataSet.getPlateSet());
			result.setPlatesection(dataSet.getPlateSection());
			result.setMarker(marker);
			result.setPass(pass);
			dao.persist(result);
		} else {		
			// update existing result
			result.setPass(pass);
		}
		
	}

}
