package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.Cities;
import com.nsi.domain.core.States;

public interface CitiesRepository extends JpaRepository<Cities, Long> {

	public List<Cities> findAllByStatesOrderByCityNameAsc(States states);
	public Cities findByCityCode(String code);
	public Cities findById(Long id);
}
