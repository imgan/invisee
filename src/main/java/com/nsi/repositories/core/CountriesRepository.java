package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.Countries;
import java.util.List;

public interface CountriesRepository extends JpaRepository<Countries, Long> {

    public Countries findByAlpha3Code(String code);
    public Countries findById(Long id);
    public List<Countries> findAllByOrderByCountryNameAsc();
        
}
