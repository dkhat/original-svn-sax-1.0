package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.AggSectionResultBO;

/**
 * Home object for domain model class AggSectionResultBO.
 * @see com.xdx.sax.bo.AggSectionResultBO
 * @author Hibernate Tools
 */
public class AggSectionResultDAO extends HibernateDao<Long, AggSectionResultBO> {

	public AggSectionResultBO findBySetSection(long plateSetId, long plateSectionId) {
		log.debug("getting AggSectionResultBO instance with setid: " + plateSetId + " sectionid " + plateSectionId);
		try {
			Criteria c = session.createCriteria("com.xdx.sax.bo.AggSectionResultBO");
			c.createCriteria("plateset").add(Restrictions.idEq(plateSetId));
			c.createCriteria("platesection").add(Restrictions.idEq(plateSectionId));
			AggSectionResultBO instance = (AggSectionResultBO) c.uniqueResult();
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
