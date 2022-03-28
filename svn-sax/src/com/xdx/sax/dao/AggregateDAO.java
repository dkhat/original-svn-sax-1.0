package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.AggregateBO;

/**
 * Home object for domain model class AggregateBO.
 * @see com.xdx.sax.bo.AggregateBO
 * @author Hibernate Tools
 */
public class AggregateDAO extends HibernateDao<Long, AggregateBO>{

	public AggregateBO findByNameSection(long sectionId, String name) {
		log.debug("getting AggregateBO instance with id: " + sectionId + " and name " + name);
		try {
			AggregateBO instance = (AggregateBO) session.createCriteria("com.xdx.sax.bo.AggregateBO")
			.add(Restrictions.eq("name", name))
			.createCriteria("plateSection")
			.add(Restrictions.idEq(sectionId))
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
