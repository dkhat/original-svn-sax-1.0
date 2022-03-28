/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.domain;

import com.xdx.sax.util.ws.ProcessControlItem;

/**
 * @author smeier
 *
 */
public interface ProcessControlHandler {

	public void registerNewLot (
			String testcode, 
			String lotName, 
			Double expectedRawScore,
			Double rawScoreStandardDeviation,
			ProcessControlItem[] processControlData);
	
}
