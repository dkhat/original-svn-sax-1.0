package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.WellTypeBO;

/**
 * Home object for domain model class WellTypeBO.
 * @see com.xdx.sax.bo.WellTypeBO
 * @author Hibernate Tools
 */
public class WellTypeDAO extends HibernateDao<Long, WellTypeBO> {

	public WellTypeBO findByName(String wellTypeName) {
		log.debug("getting WellTypeBO instance with name: " + wellTypeName);
		try {
			WellTypeBO instance = (WellTypeBO) session.createCriteria("com.xdx.sax.bo.WellTypeBO")
					.add(Restrictions.eq("name", wellTypeName))
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
