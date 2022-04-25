package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.AgentCredential;

public interface AgentCredentialRepository extends JpaRepository<AgentCredential, Long> {

	public AgentCredential findByAgent(Agent agent);
}
