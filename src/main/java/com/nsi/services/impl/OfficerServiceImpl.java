package com.nsi.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.AgentEmail;
import com.nsi.domain.core.Channel;
import com.nsi.domain.core.ChannelCommission;
import com.nsi.domain.core.Groups;
import com.nsi.domain.core.User;
import com.nsi.repositories.core.AgentEmailRepository;
import com.nsi.repositories.core.AgentRepository;
import com.nsi.repositories.core.ChannelCommissionRepository;
import com.nsi.repositories.core.ChannelRepository;
import com.nsi.repositories.core.GroupsRepository;
import com.nsi.repositories.core.UserRepository;
import com.nsi.repositories.core.UtTransactionsRepository;
import com.nsi.services.ChannelService;
import com.nsi.services.OfficerService;

@Service
public class OfficerServiceImpl implements OfficerService {
	@Autowired
	ChannelRepository channelRepository;
	@Autowired
	AgentRepository agentRepository;
	@Autowired
	AgentEmailRepository agentEmailRepository;
	@Autowired
	ChannelCommissionRepository channelCommissionRepository;
	@Autowired
	GroupsRepository groupsRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UtTransactionsRepository utTransactionsRepository;
	@Autowired
	ChannelService channelService;

	@Override
	public Map detailChannel(User user, Map map) {
		Map resultMap = new HashMap<>();
		String channelCode = (String) map.get("channel_code");
		Channel channel = channelRepository.findByCodeAndRowStatus(channelCode, true);
		if(channel==null){
			resultMap.put("code", 50);
			resultMap.put("info", "Data not found");
		}
		resultMap.put("code", 0);
		resultMap.put("info", "Channel DetailResponse successfully loaded");
		
		Map dataMap = new HashMap<>();
		dataMap.put("code", channel.getCode());
		dataMap.put("name", channel.getName());
		dataMap.put("description", channel.getDescription());
		ChannelCommission commission = channelCommissionRepository.findByChannelAndRowStatus(channel, true);
		dataMap.put("commission", commission.getCommission());
		
		//TODO: Cari Agent Defaul yg terdaftar di Group -> AccessGroup -> Agent
		Groups group = groupsRepository.findByNameAndRowStatus("AGENT_DEFAULT", true);		 
		List<Agent> agents = agentRepository.findAllByChannelAndAccessGroupAndRowStatus(channel,group, true);
		Map agentMap = new HashMap<>();
		if(!agents.isEmpty()){			
			AgentEmail agentEmail = agentEmailRepository.findByAgent(agents.get(0));
			agentMap.put("name", agents.get(0).getName());
			agentMap.put("email", agentEmail.getEmail().getValue());
		}
		dataMap.put("agent_default", agentMap);
		
		//TODO: Total Customer = total user yg terdaftar dalam channel tersebut meliputi seluruh agent
		Long totalCustomer = channelService.getTotalCustomer(channel);
		dataMap.put("total_customer", totalCustomer);
		
		//TODO: Total Subscription = count ut_transaction where status='ALL' (Subs/Topup) dg user dari channel yg telah ditentukan
		Long totalSubscription = utTransactionsRepository.countSubsAndTopupWithQuery(channel);
		dataMap.put("total_subscription", totalSubscription);
		
		//TODO: Total Redemption = count ut_transaction where status='STL' (Redemp) dg user dari channel yg telah ditentukan
		Long totalRedemption = utTransactionsRepository.countRedemptionWithQuery(channel);
		dataMap.put("total_redemption", totalRedemption);
		
		//TODO: Total Subscription = SUM ut_transaction where status='ALL' (Subs/Topup) dg user dari channel yg telah ditentukan
		BigDecimal amountSubscription = BigDecimal.ZERO;
		try {
			amountSubscription = new BigDecimal(utTransactionsRepository.sumSubsAndTopupWithQuery(channel)).setScale(2, RoundingMode.HALF_UP);
		} catch (Exception e) { }
		dataMap.put("amount_subscription", amountSubscription);
		
		//TODO: Total Subscription = SUM ut_transaction where status='STL' (Redemp) dg user dari channel yg telah ditentukan
		BigDecimal amountRedemption = BigDecimal.ZERO;
		try {
			amountRedemption = new BigDecimal(utTransactionsRepository.sumRedemptionWithQuery(channel)).setScale(2, RoundingMode.HALF_UP);
		} catch (Exception e) { }
		dataMap.put("amount_redemption", amountRedemption);
		dataMap.put("amount_commission", 0);
		
		resultMap.put("data", dataMap);
		
		return resultMap;
	}

	@Override
	public Map monitorCustomer(User user) {
		Map resultMap = new HashMap<>();
		resultMap.put("code", 0);
		resultMap.put("info", "Monitor info successfully loaded");
		Map dataMap = new HashMap<>();
		dataMap.put("total_customers", userRepository.countByOtherInviseeWithQuery());
		dataMap.put("channel_customer_subscription", utTransactionsRepository.countChannelCustomerSubscription());
		dataMap.put("channel_customer_redemption", utTransactionsRepository.countChannelCustomerRedemption());
		dataMap.put("channel_amount_subscription", utTransactionsRepository.channelAmountSubscription());
		dataMap.put("channel_amount_redemption", utTransactionsRepository.channelAmountRedemption());
		resultMap.put("data", dataMap);
		
		return resultMap;
	}

	@Override
	public Map listChannel(User user, Map map) {
		Map dataMap = new HashMap<>();
		List<Map> maps = new ArrayList<>();
		List<Channel> channels = new ArrayList<>();
		
		if(String.valueOf(map.get("channel_code"))=="" && String.valueOf(map.get("channel_name"))=="" && Integer.parseInt(String.valueOf(map.get("channel_commission")))==0){
			//GET ALL
			channels = channelRepository.findAllByRowStatus(true);
		}else if(String.valueOf(map.get("channel_code"))!="" && String.valueOf(map.get("channel_name"))=="" && Integer.parseInt(String.valueOf(map.get("channel_commission")))==0){
			//Get By Channel Code
			channels = channelRepository.findAllByCodeContainingIgnoreCase(String.valueOf(map.get("channel_code")));
		}else if(String.valueOf(map.get("channel_code"))=="" && String.valueOf(map.get("channel_name"))!="" && Integer.parseInt(String.valueOf(map.get("channel_commission")))==0){
			//Get By Channel Name
			channels = channelRepository.findAllByNameContainingIgnoreCase(String.valueOf(map.get("channel_name")));
		}else if(String.valueOf(map.get("channel_code"))=="" && String.valueOf(map.get("channel_name"))=="" && Integer.parseInt(String.valueOf(map.get("channel_commission")))>0){
			//Get By Channel Commission
			channels = channelCommissionRepository.findAllByCommissionWithQuery(Integer.parseInt(String.valueOf(map.get("channel_commission"))));
		}else if(String.valueOf(map.get("channel_code"))!="" && String.valueOf(map.get("channel_name"))!="" && Integer.parseInt(String.valueOf(map.get("channel_commission")))==0){
			//Get By Channel Code And Channel Name
			channels = channelRepository.findAllByCodeContainingIgnoreCaseAndNameContainingIgnoreCase(String.valueOf(map.get("channel_code")),String.valueOf(map.get("channel_name")));
		}else if(String.valueOf(map.get("channel_code"))!="" && String.valueOf(map.get("channel_name"))=="" && Integer.parseInt(String.valueOf(map.get("channel_commission")))>0){
			//Get By Channel Code And Channel Commission
			channels = channelCommissionRepository.findAllByCommissionAndCodeWithQuery(Integer.parseInt(String.valueOf(map.get("channel_commission"))),String.valueOf(map.get("channel_code")).toLowerCase());
		}else if(String.valueOf(map.get("channel_code"))=="" && String.valueOf(map.get("channel_name"))!="" && Integer.parseInt(String.valueOf(map.get("channel_commission")))>0){
			//Get By Channel Name And Channel Commission
			channels = channelCommissionRepository.findAllByCommissionAndNameWithQuery(Integer.parseInt(String.valueOf(map.get("channel_commission"))),String.valueOf(map.get("channel_name")).toLowerCase());
		}else if(String.valueOf(map.get("channel_code"))!="" && String.valueOf(map.get("channel_name"))!="" && Integer.parseInt(String.valueOf(map.get("channel_commission")))>0){
			//Get By Channel Code And Channel Name And Channel Commission
			channels = channelCommissionRepository.findAllByCommissionAndNameAndCodeWithQuery(Integer.parseInt(String.valueOf(map.get("channel_commission"))),String.valueOf(map.get("channel_name")).toLowerCase(),String.valueOf(map.get("channel_code")).toLowerCase());
		}
		if(!channels.isEmpty()){
			for(Channel channel : channels){
				Map cMap = new HashMap<>();
				cMap.put("code", channel.getCode());
				cMap.put("name", channel.getName());
				ChannelCommission commission = channelCommissionRepository.findByChannelAndRowStatus(channel, true);
				cMap.put("commission", commission == null ? 0 : commission.getCommission());
				Groups groups = groupsRepository.findByNameAndRowStatus("AGENT_DEFAULT", true);
				if(groups!=null){
					Map agentDefaultMap = new HashMap<>();
					List<Agent> agents = agentRepository.findAllByChannelAndAccessGroupAndRowStatus(channel, groups, true);
					if(!agents.isEmpty()){
						agentDefaultMap.put("name", agents.get(0).getName());
						AgentEmail agentEmails = agentEmailRepository.findByAgent(agents.get(0));
						agentDefaultMap.put("email", agentEmails.getEmail().getValue());
					}
					cMap.put("agent_default", agentDefaultMap);
				}
				maps.add(cMap);
			}
			dataMap.put("channel", maps);
		}
		Map resultMap = new HashMap<>();
		resultMap.put("code", 0);
		resultMap.put("info", "Channel list successfully loaded");
		resultMap.put("data", dataMap);
		return resultMap;
	}

}
