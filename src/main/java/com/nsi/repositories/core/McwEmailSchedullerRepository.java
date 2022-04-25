/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.repositories.core;
import com.nsi.domain.core.McwEmailScheduller;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author hatta.palino
 */
public interface McwEmailSchedullerRepository extends JpaRepository<McwEmailScheduller, Long> {
    
    public List<McwEmailScheduller> findAllByStatus(Integer status);
    public List<McwEmailScheduller> findAllByStatusAndValueAndApiEmail(Integer status, String value, String apiEmail);
    
}
