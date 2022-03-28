/*
 * Created on Jun 7, 2005
 */
package com.xdx.sax.assembler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xdx.sax.exceptions.SaxException;

/**
 * Assembler of Transfer Objects.  This class contains static utility methods
 * for creating new Transfer Objects using Business Objects and updating existing
 * Business Objects using Transfer Objects.
 * <p>
 * There are conventions about the structure of the Transfer Object graph and
 * the relationship between Transfer Objects and Business Objects, as outlined
 * in the <a href="../to/package-summary.html">Transfer Object package description</a>.
 * 
 * WARNING: Tranfer Object graph is assumed not to contain cycles.  If this graph is
 * cyclical, create* and update* operations will go into infinite loops.  TODO: this needs
 * to be addressed in a future version.
 * 
 */
public class TOAssembler {

	private static final Log log = LogFactory.getLog(TOAssembler.class);
	
    public static final String BO_PACKAGE_NAME = "com.xdx.sax.bo";
    public static final String TO_PACKAGE_NAME = "com.xdx.sax.to";
    
    /**
     * Updates a business object and it's dependent business objects.
     * 
     * <p>
     * WARNING: This method will cause an exception to be thrown by Hibernate during commit time if
     * you're changing the identifier property of a persistent object. 
     * </p>
     * @param businessObject
     * @param transferObject
     */
    public static void updateBO(Object businessObject, Object transferObject) {
        if (! BO_PACKAGE_NAME.equals(businessObject.getClass().getPackage().getName())) {
            throw new IllegalArgumentException("Non-business object supplied in businessObject param: " + businessObject);
        }
        
        if (! TO_PACKAGE_NAME.equals(transferObject.getClass().getPackage().getName())) {
            throw new IllegalArgumentException("Non-transfer object supplied in transferObject param: " + transferObject);
        }
        
        try {
            PropertyDescriptor[] toProperties = PropertyUtils.getPropertyDescriptors(transferObject);
            
            for (int i = 0; i < toProperties.length; i++) {
                Class propertyType = toProperties[i].getPropertyType();
                String propertyName = toProperties[i].getName();
                
                Object propertyValue;
                
                if (propertyType.getPackage() != null &&
                        TO_PACKAGE_NAME.equals(propertyType.getPackage().getName())) {
                    
                    updateBO(PropertyUtils.getProperty(businessObject, propertyName), PropertyUtils.getProperty(transferObject, propertyName));
                    
                }
                else {
                    
                    propertyValue = PropertyUtils.getProperty(transferObject, propertyName);
                    
                    if (PropertyUtils.isWriteable(businessObject, propertyName)) {
                        PropertyUtils.setProperty(businessObject, propertyName, propertyValue);
                    }
                    
                }
            }
            
        }
        catch (Exception e) {
            throw new SaxException(e);
        }
    }
    
    /**
     * 
     * Constructs a new Business Object from an existing Transfer Object.
     * 
     * @param boClass Business Object type.  This is the type of the returned object.
     * @param transferObject TransferObject from which Business Object is constructed.
     * @return A new business object constructed using the provided <code>transferObject</code>.
     */
    public static Object createBO(Class boClass, Object transferObject) {
       
    	// check TO package
        if (! TO_PACKAGE_NAME.equals(transferObject.getClass().getPackage().getName())) {
            throw new IllegalArgumentException("Only transfer objects allowed as input: " + transferObject);
        }
        
        // check BO package
        if (! BO_PACKAGE_NAME.equals(boClass.getPackage().getName())) {
            throw new IllegalArgumentException("Non-business object class supplied as parameter: " + boClass);
        }
        
        // check TO / BO name
        String boClassBaseName = boClass.getSimpleName().substring(0, boClass.getSimpleName().length()-2);
        String toClassBaseName = transferObject.getClass().getSimpleName().substring(0, transferObject.getClass().getSimpleName().length()-2);
        if (! boClassBaseName.equals(toClassBaseName)) {
        	throw new IllegalArgumentException("Busines object class and transfer object class are not compatible.");
        }
        
        try {
            Object result = boClass.newInstance();
        
            PropertyDescriptor[] toProperties = PropertyUtils.getPropertyDescriptors(transferObject);
            
            for (int i = 0; i < toProperties.length; i++) {
                Class propertyType = toProperties[i].getPropertyType();
                String propertyName = toProperties[i].getName();
                
                Object propertyValue;
                
                if (propertyType.getPackage() != null &&
                        TO_PACKAGE_NAME.equals(propertyType.getPackage().getName())) {
                    
                    propertyValue = createBO(PropertyUtils.getPropertyType(result, propertyName), PropertyUtils.getProperty(transferObject, propertyName));
                }
                else {
                    
                    propertyValue = PropertyUtils.getProperty(transferObject, propertyName);
                }
                
                if (PropertyUtils.isWriteable(result, propertyName)) {
                    PropertyUtils.setProperty(result, propertyName, propertyValue);
                }
            } 
            
            return result;
        }
        catch (Exception e) {
            throw new SaxException(e);
        }
    }
    
    /**
     * 
     * Constructs a new Tranfer Object from an existing Business Object.
     * 
     * @param toClass Transfer Object class.  This is also the type of the returned object.
     * @param businessObject Business Object from which Transfer Object is constructed.
     * @return A new tranfer object constructed using the provided <code>businessObject</code>.
     */
    public static Object createTO(Class toClass, Object businessObject) {
        log.debug(buildString("creating transfer object"));
        
        if (businessObject == null) {
        	log.debug(buildString("Object is null"));
            return null;
        }
       
        if (! BO_PACKAGE_NAME.equals(businessObject.getClass().getPackage().getName())) {
            throw new IllegalArgumentException("Only business objects allowed as input: " + businessObject);
        }
        
        if (! TO_PACKAGE_NAME.equals(toClass.getPackage().getName())) {
            throw new IllegalArgumentException("Non-transfer object class supplied as parameter: " + toClass);
        }
        
        try {
        	log.debug(buildString("Building new transfer object"));
            Object result = toClass.newInstance();
        
            log.debug(buildString("Fetching property descriptors"));
            PropertyDescriptor[] toProperties = PropertyUtils.getPropertyDescriptors(result);
            
            for (int i = 0; i < toProperties.length; i++) {
            	log.debug(buildString("Process property ", toProperties[i].getName()));

            	Class propertyType = toProperties[i].getPropertyType();
                
                Object propertyValue;
                
                if (propertyType.getPackage() != null &&
                        TO_PACKAGE_NAME.equals(propertyType.getPackage().getName())) {
                    
                    propertyValue = createTO(propertyType, PropertyUtils.getProperty(businessObject, toProperties[i].getName()));
                }
                else {
                    
                    propertyValue = PropertyUtils.getProperty(businessObject, toProperties[i].getName());
                }
                if (PropertyUtils.isWriteable(result, toProperties[i].getName())) {
                	log.debug(buildString("Setting result property"));
                    PropertyUtils.setProperty(result, toProperties[i].getName(), propertyValue);
                }
            } 
            
            log.debug(buildString("--- done ---"));
            return result;
        } catch (Exception e) {
            throw new SaxException(e);
        }
    }
    
    /**
     * Returns the Business Object class corresponding to the provided Tranfer Object class, or null
     * if one cannot be found.
     * @param to Transfer Object class
     * @return
     */
    public static Class getCorrespondingBOClass(Class to) {
    	
    	try {
    		return Class.forName(BO_PACKAGE_NAME + '.' + to.getSimpleName().substring(0, to.getSimpleName().length()-2));
    	}
    	catch (ClassNotFoundException cnfe) {
    		return null;    		
    	}
    }
    
    /**
     * Returns the Transfer Object class corresponding to the provided Business Object class, or null
     * if one cannot be found.
     * @param bo Business Object class
     * @return
     */
    public static Class getCorrespondingTOClass(Class bo) {
    	
    	try {
    		return Class.forName(TO_PACKAGE_NAME + '.' + bo.getSimpleName() + "TO");
    	}
    	catch (ClassNotFoundException cnfe) {
    		return null;    		
    	}
    }
    
    public static Object[] createTOArray(Class toClass, Collection boCollection) {
        
        Object[] result = (Object[])Array.newInstance(toClass, boCollection.size());
        
        Iterator iter = boCollection.iterator();
        
        for (int i = 0; iter.hasNext(); i++) {
            result[i] = createTO(toClass, iter.next());
        }
        
        return result;
    }

	/**
	 * Utility method tests for absence of a nullity state
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
	 */
	protected final static String buildString(Object... values) {

		StringBuilder sb= new StringBuilder();

		for (Object object : values) {
			sb.append((assertNull(object))  ?  "" :  object.toString());
		}

		return sb.toString();
	}

}
