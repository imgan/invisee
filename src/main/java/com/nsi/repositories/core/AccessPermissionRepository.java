package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsi.domain.core.AccessPermission;

public interface AccessPermissionRepository extends JpaRepository<AccessPermission, Long>{
	public AccessPermission findByCodeAndRowStatus(String code, Boolean rowStatus);
	public AccessPermission findById(Long id);
}
