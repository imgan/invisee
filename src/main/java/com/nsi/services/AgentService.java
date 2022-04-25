package com.nsi.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.GroupAccess;
import com.nsi.domain.core.User;
import com.nsi.dto.AgentDto;

@SuppressWarnings("rawtypes")
public interface AgentService {

	public Boolean checkSignatureAgent(String agent, String signature);
	public Boolean checkAccessPermission(List<GroupAccess> groupAccesses);

	public Boolean checkSignatureAgent(Agent agent, String signature);

	public Boolean checkSignatureCustomer(User user, String signature);

	Map login(String agentCode, String password, String ip);
	Map<String, Object> logout(Agent agent);
	public Map clientList(Agent agent);
	public Map<String, Object> getClientMap(Agent Spv, Agent agent, PageRequest pageRequest);
	public Map getCommision(Agent agent);
	public List<AgentDto> getAgentList(List<Agent> agentList, Date fromDate, Date toDate) throws Exception;
}
