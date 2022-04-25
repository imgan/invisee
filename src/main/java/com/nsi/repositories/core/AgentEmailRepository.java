package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.AgentEmail;
import com.nsi.domain.core.Channel;

public interface AgentEmailRepository extends JpaRepository<AgentEmail, Long>{

	public AgentEmail findByAgent(Agent agent);
	@Query("select ae.agent from AgentEmail ae where ae.agent.channel=?1 and ae.email.value like %?2% ")
	public List<Agent> findAllByAgentEmailWithQuery(Channel channel, String email);
}
 