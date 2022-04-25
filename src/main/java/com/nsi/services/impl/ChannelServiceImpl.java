package com.nsi.services.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.AgentEmail;
import com.nsi.domain.core.Channel;
import com.nsi.domain.core.ChannelCommission;
import com.nsi.domain.core.ChannelCredential;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import com.nsi.domain.core.UtProducts;
import com.nsi.enumeration.GroupsAgentEnumeration;
import com.nsi.repositories.core.AgentEmailRepository;
import com.nsi.repositories.core.AgentRepository;
import com.nsi.repositories.core.ChannelCommissionRepository;
import com.nsi.repositories.core.ChannelCredentialRepository;
import com.nsi.repositories.core.CustomerBalanceRepository;
import com.nsi.repositories.core.FundPackageProductsRepository;
import com.nsi.repositories.core.KycRepository;
import com.nsi.repositories.core.UserRepository;
import com.nsi.repositories.core.UtTransactionsRepository;
import com.nsi.services.ChannelService;
import com.nsi.services.CustomerService;

@Service
public class ChannelServiceImpl implements ChannelService{

	@Autowired
	AgentRepository agentRepository;
	@Autowired
	ChannelCredentialRepository channelCredentialRepository;
	@Autowired
	ChannelCommissionRepository channelCommissionRepository;
	@Autowired
	AgentEmailRepository agentEmailRepository;
	@Autowired
	KycRepository kycRepository;
	@Autowired
	CustomerService customerService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UtTransactionsRepository utTransactionRepository;
	@Autowired
	CustomerBalanceRepository customerBalanceRepository;
	@Autowired
	FundPackageProductsRepository fundPackageProductsRepository;

	@Override
	public Map generateAgentSignature(String agentCode, String signature) {
		Map result = new HashMap<>();
                
                System.out.println("agentCode : " + agentCode);
		Agent agent = agentRepository.findByCodeAndRowStatus(agentCode, true);
		if(agent == null){
			result.put("code", 12);
			result.put("info", "invalid access");
			return result;
		}

		ChannelCredential credential = channelCredentialRepository.findByChannelAndRowStatus(agent.getChannel(), true);

		//TODO: Generate SHA256
		String channelAccessKey = agent.getChannel().getCode()+""+credential.getAccessKey();
		String hashChannelAccessKey = this.generateHashSHA256(channelAccessKey);

		String hashChannelAccessKeyAgent = hashChannelAccessKey;
		if(hashChannelAccessKey!=null){
			hashChannelAccessKey = hashChannelAccessKey+""+agentCode;
			hashChannelAccessKeyAgent = this.generateHashSHA384(hashChannelAccessKey);
		}
                
                System.out.println("hashChannelAccessKeyAgent : " + hashChannelAccessKeyAgent);

		if(!hashChannelAccessKeyAgent.equals(signature)){
			result.put("code", 12);
			result.put("info", "invalid access");
			return result;
		}

		result.put("code", 0);
		result.put("info", "Access valid");
		return result;
	}

	public String generateHashSHA256(String input) {
		try {
			MessageDigest objSHA = MessageDigest.getInstance("SHA-256");
			byte[] bytSHA = objSHA.digest(input.getBytes());
			BigInteger intNumber = new BigInteger(1, bytSHA);
			String strHashCode = intNumber.toString(16);

			// pad with 0 if the hexa digits are less then 64.
			while (strHashCode.length() < 64) {
				strHashCode = "0" + strHashCode;
			}
			return strHashCode;
		} catch (Exception e) {
			return null;
		}

	}

	public String generateHashSHA384(String input) {
		try {
			MessageDigest objSHA = MessageDigest.getInstance("SHA-384");
			byte[] bytSHA = objSHA.digest(input.getBytes());
			BigInteger intNumber = new BigInteger(1, bytSHA);
			String strHashCode = intNumber.toString(16);

			// pad with 0 if the hexa digits are less then 96.
			while (strHashCode.length() < 96) {
				strHashCode = "0" + strHashCode;
			}
			return strHashCode;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Map viewChannel(Agent agent) {
		Map dataMap = new HashMap<>();
		//		Channel channel = channelRepository.findByCodeAndRowStatus(user.getChannelCustomer(), true);
		Channel channel = agent.getChannel();
		if(channel!=null){

			Map channelsMap = new HashMap<>();
			channelsMap.put("name", channel.getName());
			channelsMap.put("description", channel.getDescription());

			ChannelCommission commission = channelCommissionRepository.findByChannelAndRowStatus(channel, true);
			channelsMap.put("commission", commission.getCommission());
			dataMap.put("channels", channelsMap);

			AgentEmail agentEmail = agentEmailRepository.findByAgent(agent);
			Map agentMap = new HashMap<>();
			agentMap.put("name", agent.getName());
			agentMap.put("email", agentEmail.getEmail().getValue());
			dataMap.put("agent_default", agentMap);

			Map sharesMap = new HashMap<>();
			sharesMap.put("total_customer", this.getTotalCustomer(channel));
			sharesMap.put("total_redemption", utTransactionRepository.countRedemptionWithQuery(channel));
			sharesMap.put("total_subscription", utTransactionRepository.countSubsAndTopupWithQuery(channel));
			sharesMap.put("amount_subscription", utTransactionRepository.sumSubsAndTopupWithQuery(channel));
			sharesMap.put("amount_redemption", utTransactionRepository.sumRedemptionWithQuery(channel));
			sharesMap.put("amount_commission", null);

			dataMap.put("shares", sharesMap);

		}
		return dataMap;
	}

	@Override
	public Map updateChannel(Map map, User user) {
		Map resultMap = new HashMap<>();

		return resultMap;
	}

	@Override
	public Map detailCustomer(Map map) {
		Map resultMap = new HashMap<>();
		Kyc kyc = kycRepository.findByPortalcif(String.valueOf(map.get("customer_cif")));
		if(kyc == null){
			resultMap.put("code", 50);
			resultMap.put("info", "Data not found");
		}
		resultMap.put("code", 0);
		resultMap.put("info", "Customer DetailResponse successfully loaded");

		Map dataMap = new HashMap<>();
		dataMap.put("customer_cif", kyc.getPortalcif());

		Map nameMap = new HashMap<>();
		nameMap.put("first", kyc.getFirstName());
		nameMap.put("middle", kyc.getMiddleName());
		nameMap.put("last", kyc.getLastName());

		dataMap.put("name", nameMap);

		dataMap.put("email", kyc.getEmail());
		dataMap.put("customer_status", kyc.getAccount().getUserStatus());

		dataMap.put("kyc", customerService.completenessKyc(kyc));
		dataMap.put("fatca", customerService.completenessFatca(kyc));
		dataMap.put("risk", customerService.completenessRiskProfile(kyc));
		dataMap.put("total_subscription", utTransactionRepository.countSubsTopByKycWithQuery(kyc));
		dataMap.put("total_redemption", utTransactionRepository.countRedemptionByKycWithQuery(kyc));
		dataMap.put("amount_subscription", utTransactionRepository.sumSubsAndTopupByKycWithQuery(kyc));
		dataMap.put("amount_redemption", utTransactionRepository.sumRedemptionByKycWithQuery(kyc));
		dataMap.put("market_value", this.getTotalMarketValue(kyc).setScale(2, RoundingMode.HALF_UP));

		resultMap.put("data", dataMap);
		return resultMap;
	}

	@Override
	public Long getTotalCustomer(Channel channel) {
		List<Agent> agents = agentRepository.findAllByChannelAndRowStatus(channel, true);
		Long totalCustomer = new Long(0);
		for(Agent agent : agents){
			Long totalUserPerAgent = userRepository.countByAgentChannelWithQuery(agent);
			totalCustomer = totalCustomer + totalUserPerAgent;
		}
		return totalCustomer;
	}

	@Override
	public BigDecimal getTotalMarketValue(Kyc kyc) {
		BigDecimal totalMarketValue = BigDecimal.ZERO;
		// TODO Auto-generated method stub
		List<InvestmentAccounts> investmentAccounts = customerBalanceRepository.findAllByCustomerAndCurrentAmountWithCustomQuery(kyc.getId());
		if(!investmentAccounts.isEmpty()){
			//TODO: Get Last Date per Investment Account
			List<Map> maps = this.getLastDatePerinvestment(investmentAccounts);
			//TODO: Get Total Market Value
			for(Map map : maps){
				Double totalPerInvestment = customerBalanceRepository.getTotalMarketValueWithQuery((InvestmentAccounts)map.get("investment"), (Date)map.get("balance_date"));
				totalMarketValue = totalMarketValue.add(BigDecimal.valueOf(totalPerInvestment));
			}
		}
		return totalMarketValue;
	}

	private List<Map> getLastDatePerinvestment(List<InvestmentAccounts> investmentAccounts){
		List<Map> maps = new ArrayList<>();
		for(InvestmentAccounts accounts : investmentAccounts){
			Date tempBalanceDate = null;
			List<UtProducts> products = fundPackageProductsRepository.findUtProductsByFundPackages(accounts.getFundPackages());
			for(UtProducts product : products){
				if(tempBalanceDate==null){
					tempBalanceDate = customerBalanceRepository.getMaxDatePerInvestmentAndProductWithQuery(accounts, product);
				}else{
					Date maxBalanceDate = customerBalanceRepository.getMaxDatePerInvestmentAndProductWithQuery(accounts, product);
					if(tempBalanceDate.compareTo(maxBalanceDate)!=0){
						if(tempBalanceDate.compareTo(maxBalanceDate)>0){
							tempBalanceDate = maxBalanceDate;
						}
					}
				}
			}
			Map map = new HashMap<>();
			map.put("investment", accounts);
			map.put("balance_date", tempBalanceDate);
			maps.add(map);
		}
		return maps;
	}

	@Override
	public Map getListCustomer(Map map, Agent agent) {
		Map dataMap = new HashMap<>();
		List<Map> customers = new ArrayList<>();
		List<Kyc> users = new ArrayList<>();
		//		TODO: FILTER
		if(GroupsAgentEnumeration.AGENT_DEFAULT.getStatus().equals(agent.getAccessGroup().getName())){
			if(String.valueOf(map.get("customer_name"))=="" && String.valueOf(map.get("customer_email"))=="" && String.valueOf(map.get("agent_name"))=="" && String.valueOf(map.get("agent_email"))==""){
				//Get ALL Customer with same channel
				users = kycRepository.findAllByAccount_Agent_Channel(agent.getChannel());
			}else if(String.valueOf(map.get("customer_name"))!="" && String.valueOf(map.get("customer_email"))=="" && String.valueOf(map.get("agent_name"))=="" && String.valueOf(map.get("agent_email"))==""){
				//Get ALL Customer with same channel and Customer name
				users = kycRepository.findAllByChannelAndCustomerName(agent.getChannel(), String.valueOf(map.get("customer_name")).replace(" ", ""));
			}else if(String.valueOf(map.get("customer_name"))=="" && String.valueOf(map.get("customer_email"))!="" && String.valueOf(map.get("agent_name"))=="" && String.valueOf(map.get("agent_email"))==""){
				//Get ALL Customer with same channel and Customer Email
				users = kycRepository.findAllByAccount_Agent_ChannelAndAccount_EmailContainingIgnoreCase(agent.getChannel(), String.valueOf(map.get("customer_email")));
			}else if(String.valueOf(map.get("customer_name"))=="" && String.valueOf(map.get("customer_email"))=="" && String.valueOf(map.get("agent_name"))!="" && String.valueOf(map.get("agent_email"))==""){
				//Get ALL Customer with same channel and Agent Name
				List<Agent> agentNames = agentRepository.findAllByChannelAndRowStatusAndNameContainingIgnoreCase(agent.getChannel(), true, String.valueOf(map.get("agent_name")));
				for(Agent agentx : agentNames){
					List<Kyc> kycs = kycRepository.findAllByAccount_Agent(agentx);
					users.addAll(kycs);
				}

			}else if(String.valueOf(map.get("customer_name"))=="" && String.valueOf(map.get("customer_email"))=="" && String.valueOf(map.get("agent_name"))=="" && String.valueOf(map.get("agent_email"))!=""){
				//Get ALL Customer with same channel and Agent Email
				List<Agent> agentEmails = agentEmailRepository.findAllByAgentEmailWithQuery(agent.getChannel(), String.valueOf(map.get("agent_email")).toLowerCase());
				for(Agent agentx : agentEmails){
					List<Kyc> kycs = kycRepository.findAllByAccount_Agent(agentx);
					users.addAll(kycs);
				}

			}else if(String.valueOf(map.get("customer_name"))!="" && String.valueOf(map.get("customer_email"))!="" && String.valueOf(map.get("agent_name"))=="" && String.valueOf(map.get("agent_email"))==""){
				//Get ALL Customer with same channel , Customer Name and Customer Email
				users = kycRepository.findAllByChannelAndCustomerNameAndEmail(agent.getChannel(), String.valueOf(map.get("customer_name")).replace(" ", ""), String.valueOf(map.get("customer_email")).toLowerCase());
			}else if(String.valueOf(map.get("customer_name"))!="" && String.valueOf(map.get("customer_email"))=="" && String.valueOf(map.get("agent_name"))!="" && String.valueOf(map.get("agent_email"))==""){
				//Get ALL Customer with same channel , Customer Name and Agent Name
				users = kycRepository.findAllByChannelAndCustomerNameAndAgentName(agent.getChannel(), String.valueOf(map.get("customer_name")).replace(" ", ""), String.valueOf(map.get("agent_name")).toLowerCase());
			}else if(String.valueOf(map.get("customer_name"))!="" && String.valueOf(map.get("customer_email"))=="" && String.valueOf(map.get("agent_name"))=="" && String.valueOf(map.get("agent_email"))!=""){
				//Get ALL Customer with same channel , Customer Name and Agent Email
				//				users = kycRepository.findAllByChannelAndCustomerNameAndAgentEmail(agent.getChannel(), String.valueOf(map.get("customer_name")).replace(" ", ""), String.valueOf(map.get("agent_email")));
			}
		}else if(GroupsAgentEnumeration.AGENT.getStatus().equals(agent.getAccessGroup().getName())){
			if(String.valueOf(map.get("customer_name"))=="" && String.valueOf(map.get("customer_email"))==""){
				//Get All Customer in Agent
				users = kycRepository.findAllByAccount_Agent(agent);
			}else if(String.valueOf(map.get("customer_name"))!="" && String.valueOf(map.get("customer_email"))==""){
				//Get All Customer by Customer Name in Agent
				String str = String.valueOf(map.get("customer_name")).replace(" ", "");
				users = kycRepository.findAllByAgentAndCustomerName(agent, str);
			}else if(String.valueOf(map.get("customer_name"))=="" && String.valueOf(map.get("customer_email"))!=""){
				//Get All Customer by Customer Email in Agent
				users = kycRepository.findAllByAccount_AgentAndAccount_EmailContainingIgnoreCase(agent, String.valueOf(map.get("customer_email")));
			}else if(String.valueOf(map.get("customer_name"))!="" && String.valueOf(map.get("customer_email"))!=""){
				//Get All Customer by Customer Name And Email in Agent
				users = kycRepository.findAllByAgentAndEmailAndCustomerName(agent, String.valueOf(map.get("customer_name")).replace(" ", ""), String.valueOf(map.get("customer_email")));
			}
		}
		if(!users.isEmpty()){
			for(Kyc kyc : users){
				Map kycMap = new HashMap<>();
				kycMap.put("customer_cif", kyc.getPortalcif());
				kycMap.put("email", kyc.getEmail());
				kycMap.put("customer_status", kyc.getAccount().getUserStatus());
				kycMap.put("kyc_progress", customerService.completenessKyc(kyc));
				kycMap.put("fatca_progress", customerService.completenessFatca(kyc));
				kycMap.put("risk_profile_progress", customerService.completenessRiskProfile(kyc));
				Map nameMap = new HashMap<>();
				nameMap.put("first", kyc.getFirstName());
				nameMap.put("middle", kyc.getMiddleName());
				nameMap.put("last", kyc.getLastName());
				kycMap.put("name", nameMap);
				customers.add(kycMap);
			}
		}
		dataMap.put("customers", customers);
		Map resultMap = new HashMap<>();
		resultMap.put("code", 0);
		resultMap.put("info", "Customer list successfully loaded");
		resultMap.put("data", dataMap);
		return resultMap;
	}

}
