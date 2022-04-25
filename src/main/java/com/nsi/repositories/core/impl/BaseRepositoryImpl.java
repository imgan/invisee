/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.repositories.core.impl;

import com.nsi.repositories.core.BaseRepository;
import com.nsi.util.ParameterDao;
import com.nsi.util.ResultBaseRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Hatta Palino
 */
@Repository
public class BaseRepositoryImpl implements BaseRepository {

    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    @Override
    public ResultBaseRepository getList(ParameterDao parameterDao) {
        Criteria criteria1 = entityManager.unwrap(Session.class).createCriteria(parameterDao.getClazz());
        Criteria criteria2 = entityManager.unwrap(Session.class).createCriteria(parameterDao.getClazz());
        for (String key : parameterDao.getEquals().keySet()) {
            criteria1.add(Restrictions.eq(key, parameterDao.getEquals().get(key)));
            criteria2.add(Restrictions.eq(key, parameterDao.getEquals().get(key)));
        }
        for (String key : parameterDao.getNotEquals().keySet()) {
            criteria1.add(Restrictions.not(Restrictions.eq(key, parameterDao.getNotEquals().get(key))));
            criteria2.add(Restrictions.not(Restrictions.eq(key, parameterDao.getNotEquals().get(key))));
        }
        for (String key : parameterDao.getLikes().keySet()) {
            criteria1.add(Restrictions.like(key, parameterDao.getLikes().get(key)));
            criteria2.add(Restrictions.like(key, parameterDao.getLikes().get(key)));
        }
        for (String key : parameterDao.getNotLikes().keySet()) {
            criteria1.add(Restrictions.not(Restrictions.like(key, parameterDao.getEquals().get(key))));
            criteria2.add(Restrictions.not(Restrictions.like(key, parameterDao.getEquals().get(key))));
        }
        for (String key : parameterDao.getIns().keySet()) {
            criteria1.add(Restrictions.in(key, parameterDao.getIns().get(key)));
            criteria2.add(Restrictions.in(key, parameterDao.getIns().get(key)));
        }
        for (String key : parameterDao.getNotIns().keySet()) {
            criteria1.add(Restrictions.not(Restrictions.in(key, parameterDao.getIns().get(key))));
            criteria2.add(Restrictions.not(Restrictions.in(key, parameterDao.getIns().get(key))));
        }
        for (String key : parameterDao.getGreaterThans().keySet()) {
            criteria1.add(Restrictions.gt(key, parameterDao.getGreaterThans().get(key)));
            criteria2.add(Restrictions.gt(key, parameterDao.getGreaterThans().get(key)));
        }
        for (String key : parameterDao.getEqualGreaterThans().keySet()) {
            criteria1.add(Restrictions.ge(key, parameterDao.getEqualGreaterThans().get(key)));
            criteria2.add(Restrictions.ge(key, parameterDao.getEqualGreaterThans().get(key)));
        }
        for (String key : parameterDao.getLessThans().keySet()) {
            criteria1.add(Restrictions.lt(key, parameterDao.getLessThans().get(key)));
            criteria2.add(Restrictions.lt(key, parameterDao.getLessThans().get(key)));
        }
        for (String key : parameterDao.getEqualLessThans().keySet()) {
            criteria1.add(Restrictions.le(key, parameterDao.getEqualLessThans().get(key)));
            criteria2.add(Restrictions.le(key, parameterDao.getEqualLessThans().get(key)));
        }
        for (String key : parameterDao.getBetweens().keySet()) {
            Object[] objects = parameterDao.getBetweens().get(key);
            criteria1.add(Restrictions.between(key, objects[0], objects[1]));
            criteria2.add(Restrictions.between(key, objects[0], objects[1]));
        }
        
        Integer count = 0;
        
        if(parameterDao.getMaxResult() > 0) {
            criteria2.setProjection(Projections.rowCount());
            count = ((Long) criteria2.uniqueResult()).intValue();
        
            criteria1.setFirstResult(parameterDao.getFirst());
            criteria1.setMaxResults(parameterDao.getMaxResult());
        }
        
        for (String[] order : parameterDao.getOrders()) {
            String key = order[0];
            String type = order[1];
            if (type.equalsIgnoreCase("0")) {
                criteria1.addOrder(Order.asc(key));
            } else if (type.equalsIgnoreCase("1")) {
                criteria1.addOrder(Order.desc(key));
            }
        }
        
        return new ResultBaseRepository(count, criteria1.list());
    }

}
