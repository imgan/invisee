/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.repositories.core;

import com.nsi.domain.core.CronEmail;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author hatta.palino
 */
public interface CronEmailRepository extends JpaRepository<CronEmail, Long>{
    
    public List<CronEmail> findAllByCifAndTypeAndStatus(String cif, String type, Integer status);
    
    @Query("select ce from CronEmail ce where ce.createdDate <= ?1 and ce.status = ?2")
    public List<CronEmail> findAllByStatus(Date date, Integer status);
    
    
}
