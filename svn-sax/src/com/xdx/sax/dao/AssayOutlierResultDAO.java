package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.AssayOutlierResultBO;

/**
 * Home object for domain model class AggMarkerResultBO.
 * @see com.xdx.sax.bo.AggMarkerResultBO
 * @author Hibernate Tools
 */
public class AssayOutlierResultDAO extends HibernateDao<Long, AssayOutlierResultBO> {

	@SuppressWarnings("unchecked")
	public List<AssayOutlierResultBO> findByPlatesetSection(long platesetId, long platesectionId) {
		log.debug("finding AssayOutlierResultBO instance by example");
		try {
			List<AssayOutlierResultBO> results;
			Criteria c = session.createCriteria("com.xdx.sax.bo.AssayOutlierResultBO");
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

	public AssayOutlierResultBO findByPlatesetSectionMarker(long platesetId,
			long platesectionId, long markerId) {
		log.debug("finding AssayOutlierResultBO instance by set/section/marker");
		try {
			AssayOutlierResultBO result;
			Criteria c = session.createCriteria("com.xdx.sax.bo.AssayOutlierResultBO");
			c.createCriteria("plateset").add(Restrictions.eq("id", platesetId));
			c.createCriteria("platesection").add(Restrictions.eq("id", platesectionId));
			c.createCriteria("marker").add(Restrictions.eq("id", markerId));
			result = (AssayOutlierResultBO) c.uniqueResult();
			
			if (result != null) {
				log.debug("find by set/section/marker successful");
			} else {
				log.debug("no result found");
			}
			return result;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
