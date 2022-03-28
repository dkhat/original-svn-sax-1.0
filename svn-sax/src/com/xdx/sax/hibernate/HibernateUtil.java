package com.xdx.sax.hibernate;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.xdx.sax.exceptions.SaxException;

/**
 * Hibernate helper class.
 *
 * @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
public class HibernateUtil {

    public static final String SAX_SESSION_FACTORY_NAME = "java:/hibernate/SaxSessionFactory";
    
    private static SessionFactory saxSessionFactory = null;

	private static final Log log = LogFactory.getLog(HibernateUtil.class);

    /**
     * @return Returns the SAX database SessionFactory.
     */
    public static SessionFactory getSaxSessionFactory() {
        try {
        	if (saxSessionFactory == null)
        		setSaxSessionFactory((SessionFactory)new InitialContext().lookup(SAX_SESSION_FACTORY_NAME));
        	return saxSessionFactory;
        } catch (NamingException ne) {
            log.error("Cannot get SAX session factory from JNDI at '" + SAX_SESSION_FACTORY_NAME + "'.");
            throw new SaxException(ne);
        }
    }
    
    /**
	 * @param sessionFactory The saxSessionFactory to set.
	 */
	public static void setSaxSessionFactory(SessionFactory sessionFactory) {
		saxSessionFactory = sessionFactory;
	}

	/**
     * Returns the current (contextual) session if one is available, otherwise opens a new one.
     *
     * @return Session
     */
    public static Session getCurrentSaxSession() {
        return getSaxSessionFactory().getCurrentSession();
    }    
}
