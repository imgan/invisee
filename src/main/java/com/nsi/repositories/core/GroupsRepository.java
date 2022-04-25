package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.Groups;

public interface GroupsRepository extends JpaRepository<Groups, Long>{

	public Groups findByNameAndRowStatus(String name, Boolean rowStatus);
	public Groups findByCodeAndRowStatus(String code, Boolean rowStatus);
}
