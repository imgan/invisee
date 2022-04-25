package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.MstFeeAgent;

public interface MstFeeAgentRepository extends JpaRepository<MstFeeAgent, Long>{
	public MstFeeAgent findByRole(String role);
}