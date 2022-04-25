/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

@Configuration
public class BeanConfig {

    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

    @Bean
    public SessionFactory getSessionFactory() {
//        if (entityManagerFactory.unwrap(SessionFactory.class) == null) {
//            throw new NullPointerException("factory is not a hibernate factory");
//        }
//        return entityManagerFactory.unwrap(SessionFactory.class);
        return null;
    }
}
