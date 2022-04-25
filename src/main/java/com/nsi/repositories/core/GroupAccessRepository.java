package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsi.domain.core.GroupAccess;
import com.nsi.domain.core.Groups;

public interface GroupAccessRepository extends JpaRepository<GroupAccess, Long>{
	public GroupAccess findByGroupAndRowStatus(Groups group, Boolean rowStatus);
	public List<GroupAccess> findAllByGroupAndRowStatus(Groups group, Boolean rowStatus);
}