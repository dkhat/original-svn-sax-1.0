package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.MarkerBO;

/**
 * Home object for domain model class MarkerBO.
 * @see com.xdx.sax.bo.MarkerBO
 * @author Hibernate Tools
 */
public class MarkerDAO extends HibernateDao<Long, MarkerBO> {

	public MarkerBO findByName(String name) {
		log.debug("getting MarkerBO instance with name: " + name);
		try {
			MarkerBO instance = (MarkerBO) session.createCriteria("com.xdx.sax.bo.MarkerBO")
			.add(Restrictions.eq("name", name))
			.uniqueResult();
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
