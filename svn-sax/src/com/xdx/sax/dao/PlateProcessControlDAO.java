package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.PlateProcessControlBO;
import com.xdx.sax.exceptions.SaxException;

/**
 * Home object for domain model class ProcessBO.
 * @see com.xdx.sax.bo.ProcessBO
 * @author Hibernate Tools
 */
public class PlateProcessControlDAO extends HibernateDao<Long, PlateProcessControlBO> {

	@SuppressWarnings("unchecked")
	public PlateProcessControlBO findByPlateSetSection(long plateSetId, long plateSectionId) {
		log.debug("finding PlateProcessControlBO instance by example");
		try {
			Criteria c = session.createCriteria("com.xdx.sax.bo.PlateProcessControlBO");
			c.createCriteria("plateset").add(Restrictions.idEq(plateSetId));
			c.createCriteria("platesection").add(Restrictions.idEq(plateSectionId));
			List<PlateProcessControlBO> results = c.list();
			log.debug("find by plateset and section successful, result size: "
					+ results.size());

			if (results.size() == 0)
				return null;
			
			if (results.size() > 1) {
				throw new SaxException("Only one processcontrol per plateset/section allowed");
			}
			
			return results.get(0);
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

}
