/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.repositories.core;

import com.nsi.util.ParameterDao;
import com.nsi.util.ResultBaseRepository;
import javax.persistence.EntityManager;

/**
 *
 * @author Hatta Palino
 */
public interface BaseRepository {
    
    ResultBaseRepository getList(ParameterDao parameterDao);
    EntityManager getEntityManager();
    
}
