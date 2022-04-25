package com.nsi.services;

import java.math.BigDecimal;
import java.util.Map;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.Channel;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;

public interface ChannelService {

	public Map generateAgentSignature(String agentCode, String signature);
	public String generateHashSHA384(String input);
	public String generateHashSHA256(String input);
	
	public Map viewChannel(Agent agent);
	public Map updateChannel(Map map,User user);
	public Map detailCustomer(Map map);
	
	public Long getTotalCustomer(Channel channel);
	public BigDecimal getTotalMarketValue(Kyc kyc);
	
	public Map getListCustomer(Map map, Agent agent);
}
