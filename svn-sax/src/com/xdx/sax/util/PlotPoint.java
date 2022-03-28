/**
 *  $Id: PlotPoint.java,v 1.0 - 11 June, 2008 5:55:55 PM gtrester Exp $
 *  Diagnostic Expression Inc. Software Development Group
 *
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax.util;


public class PlotPoint extends Object {
	   public double x, y;

	   /**
	    * Constructor
	    * @param x x-coordinate
	    * @param y y-coordinate
	    */
	   public PlotPoint(double x, double y) {
	      this.x = x;
	      this.y = y;
	   }
}
