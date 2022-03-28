/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 */
package com.xdx.sax;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
  * An abstraction of a domain object (JavaBean).
  *
  * @author gtrester
  * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
  *
  */
public abstract class BusinessObject implements Serializable {

	//
	private static final long serialVersionUID = 1L;


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

	/**
	 * Uses <i>Reflection /i> to publish the instance variables' values.
	 */
	public String new_toString() {

		StringBuilder sb= new StringBuilder();

		Method[] methods= getClass().getDeclaredMethods();

		try {

			sb.append("\n\n");
			sb.append(getClass().getName());
			sb.append("...\n ");

			for (int i= 0; i < methods.length; i++ ) {

				if (methods[i].getName().startsWith("get")) {

					Object result= methods[i].invoke(this, new Object[0]);

					sb.append("\n Name: ");
					sb.append(methods[i].getName());
					sb.append(" :: ");
					sb.append(result);
				}
			}
		}
		catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		catch(InvocationTargetException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	
    /**
     * get stack trace in a printable form
     * 
     * @param aThrowable exception
     * @return printable stack trace
     */
	protected static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

}