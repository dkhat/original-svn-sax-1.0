package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.ProcessControlBO;
import com.xdx.sax.bo.ProcessControlRawScoreBO;

/**
 * Home object for domain model class ProcessBO.
 * @see com.xdx.sax.bo.ProcessBO
 * @author Hibernate Tools
 */
public class ProcessControlRawScoreDAO extends HibernateDao<Long, ProcessControlRawScoreBO> {

	public ProcessControlRawScoreBO findByProcessControl(ProcessControlBO pc) {
		log.debug(buildString("getting ", this.entityClass.getName(), " instances with process control ", pc.getExternalid()));
		try {
			Criteria crit = session.createCriteria(this.entityClass);
			crit.createCriteria("processControl").add(Restrictions.idEq(pc.getId()));
			ProcessControlRawScoreBO result = this.entityClass.cast(crit.uniqueResult());
			
			if (result == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return result;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

}
