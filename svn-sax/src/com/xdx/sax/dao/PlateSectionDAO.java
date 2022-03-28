package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.PlateSectionBO;

/**
 * Home object for domain model class PlateSectionBO.
 * @see com.xdx.sax.bo.PlateSectionBO
 * @author Hibernate Tools
 */
public class PlateSectionDAO extends HibernateDao<Long, PlateSectionBO> {

	public PlateSectionBO findByDesignSectionname(long plateDesignId,
			String plateSectionName) {
		log.debug("finding PlateSectionBO instance by design " + plateDesignId + " and sectionname " + plateSectionName);
		try {
			PlateSectionBO instance = (PlateSectionBO) session.createCriteria("com.xdx.sax.bo.PlateSectionBO")
					.add(Restrictions.eq("name", plateSectionName))
					.createCriteria("platedesign")
					.add(Restrictions.idEq(plateDesignId))
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
