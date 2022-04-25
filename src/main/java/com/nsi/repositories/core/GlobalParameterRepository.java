package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.GlobalParameter;

public interface GlobalParameterRepository extends JpaRepository<GlobalParameter, Long>{

	public GlobalParameter findByCategory(String category);
	public GlobalParameter findByName(String name);
	GlobalParameter findByCategoryAndName(String category, String name);
}
