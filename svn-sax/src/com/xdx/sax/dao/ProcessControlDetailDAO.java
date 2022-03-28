package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.ProcessControlDetailBO;
import com.xdx.sax.exceptions.SaxException;

/**
 * Home object for domain model class ProcessBO.
 * @see com.xdx.sax.bo.ProcessBO
 * @author Hibernate Tools
 */
public class ProcessControlDetailDAO extends HibernateDao<Long, ProcessControlDetailBO> {

	@SuppressWarnings("unchecked")
	public ProcessControlDetailBO findByProcessControlMarker(long processControlId, long markerId) {
		log.debug("finding ProcessControlDetailBO instance by processcontrol/marker");
		try {
			Criteria c = session.createCriteria("com.xdx.sax.bo.ProcessControlDetailBO");
			c.createCriteria("processControl").add(Restrictions.idEq(processControlId));
			c.createCriteria("marker").add(Restrictions.idEq(markerId));
			List<ProcessControlDetailBO> results = c.list();
			log.debug("find by processcontrol and marker successful, result size: "
					+ results.size());

			if (results.size() == 0)
				return null;
			
			if (results.size() > 1) {
				throw new SaxException("Only one detail record per processcontrol/marker allowed");
			}
			
			return results.get(0);
		} catch (RuntimeException re) {
			log.error("find by processcontrol/marker failed", re);
			throw re;
		}
	}

	public ProcessControlDetailBO findByLotName(String lotName) {
		log.debug("finding ProcessControlDetailBO instance by lot name");
		try {
			List<?> results = (List<?>) session.createCriteria("com.xdx.sax.bo.ProcessControlDetailBO")
			.add(Restrictions.eq("lot", lotName))
			.list();
			log.debug("find by lot name successful, result size: "
					+ results.size());

			if (results.size() == 0)
				return null;
			
			if (results.size() > 1) {
				throw new SaxException("Only one detail record per lot name allowed");
			}
			
			return (ProcessControlDetailBO) results.get(0);
		} catch (RuntimeException re) {
			log.error("find by lot name failed", re);
			throw re;
		}
	}

}
