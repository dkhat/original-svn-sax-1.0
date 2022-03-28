/**
 * 
 */
package com.xdx.sax.dao;

import java.util.List;

/**
 * @author smeier
 *
 */
public interface Dao<K, E> {
      void persist(E entity);
      void delete(E entity);
      E findById(K id);
      void attachDirty(E entity);
      void attachClean(E entity);
      E merge(E detachedInstance);
      List<E> findByExample(E instance);
}
