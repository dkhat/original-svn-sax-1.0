package com.xdx.sax.dao;

// Generated Jan 21, 2009 4:04:43 PM by Hibernate Tools 3.2.4.CR1

import org.hibernate.criterion.Restrictions;

import com.xdx.sax.bo.RawWellDataBO;

/**
 * Home object for domain model class RawWellDataBO.
 * @see com.xdx.sax.bo.RawWellDataBO
 * @author Hibernate Tools
 */
public class RawWellDataDAO extends HibernateDao<Long, RawWellDataBO> {

	public RawWellDataBO findByPlatesetRowCol(long plateSetId, long plateNumberInSet, long rowNumber, long colNumber) {
		log.debug("finding RawWellDataBO instance by plateSetId" + plateSetId
				+ ", plateNumberInSet " + plateNumberInSet + ",  row " + rowNumber
				+ ", well " + colNumber);
		try {
			RawWellDataBO result = (RawWellDataBO) session.createCriteria("com.xdx.sax.bo.RawWellDataBO")
					.add(Restrictions.eq("rownumber", rowNumber))
					.add(Restrictions.eq("columnnumber", colNumber))
					.createCriteria("plate")
					.add(Restrictions.eq("numberinset", plateNumberInSet))
					.createCriteria("plateset").add(Restrictions.idEq(plateSetId))
					.uniqueResult();
			log.debug("find by plateId, row, well successful");
			return result;
		} catch (RuntimeException re) {
			log.error("find by plateId, row, well failed", re);
			throw re;
		}
	}

}
