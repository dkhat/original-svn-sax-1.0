/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.workflow;

import com.xdx.sax.domain.SaxDataSet;

/**
 * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 *
 */
public interface OrchestratorStep {

	public void initialize (SaxDataSet dataSet);
	
	public void execute();
}
