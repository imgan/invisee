package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.Countries;
import com.nsi.domain.core.States;

public interface StatesRepository extends JpaRepository<States, String> {

	public List<States> findAllByCountriesOrderByCountriesAsc(Countries countries);
	public List<States> findAllByCountriesOrderByStateNameAsc(Countries countries);
	public States findByStateCode(String code);
}
