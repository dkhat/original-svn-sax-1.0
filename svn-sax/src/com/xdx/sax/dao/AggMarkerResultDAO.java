package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.AggMarkerResultBO;

/**
 * Home object for domain model class AggMarkerResultBO.
 * @see com.xdx.sax.bo.AggMarkerResultBO
 * @author Hibernate Tools
 */
public class AggMarkerResultDAO extends HibernateDao<Long, AggMarkerResultBO>{

	@SuppressWarnings("unchecked")
	public List<AggMarkerResultBO> findByPlatesetSection(long platesetId, long platesectionId) {
		log.debug("finding AggMarkerResultBO instance by example");
		try {
			List<AggMarkerResultBO> results;
			Criteria c = session.createCriteria("com.xdx.sax.bo.AggMarkerResultBO");
			c.createCriteria("plateset").add(Restrictions.eq("id", platesetId));
			c.createCriteria("platesection").add(Restrictions.eq("id", platesectionId));
			results = c.list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

}
