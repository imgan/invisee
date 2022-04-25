package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.Channel;
import com.nsi.domain.core.Groups;
import com.nsi.domain.core.MstFeeAgent;

public interface AgentRepository extends JpaRepository<Agent, Long>{

	public Agent findByCodeAndRowStatus(String code, Boolean rowStatus);
	public List<Agent> findAllByChannelAndAccessGroupAndRowStatus(Channel channel, Groups groups, Boolean rowStatus);
	public List<Agent> findAllByChannelAndRowStatus(Channel channel, Boolean rowStatus);
	public List<Agent> findAllByChannelAndRowStatusAndNameContainingIgnoreCase(Channel channel, Boolean rowStatus, String name);
	public List<Agent> findAllByChannelAndRowStatusAndCodeContainingIgnoreCase(Channel channel, Boolean rowStatus, String code);
	public Agent findByToken(String token);
	public Page<Agent> findAllBySpv(Agent spv, Pageable pageable);
	
	@Query("select agent from Agent agent where agent.spv = ?1 and agent.agentFee = ?2")
	public Page<Agent> findAllBySpvAndAgentFee(Agent spv, MstFeeAgent mstFeeAgent, Pageable pageable);
}
