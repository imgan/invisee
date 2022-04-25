package com.nsi.services.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nsi.domain.core.AccessPermission;
import com.nsi.domain.core.Agent;
import com.nsi.domain.core.AgentCredential;
import com.nsi.domain.core.ChannelCredential;
import com.nsi.domain.core.GroupAccess;
import com.nsi.domain.core.Groups;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.MstFeeAgent;
import com.nsi.domain.core.User;
import com.nsi.domain.core.UtTransactionsAgentFee;
import com.nsi.dto.AgentDto;
import com.nsi.repositories.core.AgentContactRepository;
import com.nsi.repositories.core.AgentCredentialRepository;
import com.nsi.repositories.core.AgentRepository;
import com.nsi.repositories.core.ChannelCredentialRepository;
import com.nsi.repositories.core.GroupAccessRepository;
import com.nsi.repositories.core.KycRepository;
import com.nsi.repositories.core.MstFeeAgentRepository;
import com.nsi.repositories.core.UserRepository;
import com.nsi.repositories.core.UtTransactionsAgentFeeRepository;
import com.nsi.services.AgentService;
import com.nsi.services.ChannelService;
import com.nsi.services.InvestmentService;
import com.nsi.util.ConstantUtil;
import com.nsi.util.Validator;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class AgentServiceImpl extends BaseService implements AgentService {

	private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	ChannelService channelService;
	@Autowired
	ChannelCredentialRepository channelCredentialRepository;
	@Autowired
	AgentRepository agentRepository;
	@Autowired    
	GroupAccessRepository groupAccessRepository;
	@Autowired
	AgentCredentialRepository agentCredentialRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	UserRepository userRepository;
	@Autowired
	KycRepository kycRepository;
	@Autowired
	AgentContactRepository agentContactRepository;

	@Autowired
	private InvestmentService investmentService;

	@Autowired
	private UtTransactionsAgentFeeRepository utTransactionsAgentFeeRepository;

	@Autowired
	private MstFeeAgentRepository mstFeeAgentRepository;

	@Override
	public Boolean checkSignatureAgent(String agentCode, String signature) {

		Agent agent = agentRepository.findByCodeAndRowStatus(agentCode, true);
		if(agent == null) {
			logger.info("agentCode : " + agentCode + " not found");
			return false;
		}

		ChannelCredential channelCre = channelCredentialRepository.findByChannelAndRowStatus(agent.getChannel(), true);
		if (channelCre == null) {
			logger.info("ChannelCredential : " + agent.getChannel() + " not found");
			return false;
		}

		Groups groups = agent.getAccessGroup();
		List<GroupAccess> access = groupAccessRepository.findAllByGroupAndRowStatus(groups, true);
		if (access == null || access.isEmpty()) {
			logger.info("GroupAccess : " + agent.getChannel().getCode() + " not found");
			return false;
		}

		if(!checkAccessPermission(access)) {
			logger.info("GroupAccess for found code PER002");
			return false;
		}

		String sha256 = channelService.generateHashSHA256(agent.getChannel().getCode().concat(channelCre.getAccessKey()));
		String sign = channelService.generateHashSHA384(sha256.concat(agent.getCode()));

		logger.info("sha256 : " + sha256);
		logger.info("sign : " + sign);

		return sign.equals(signature);
	}

	@Override
	public Boolean checkAccessPermission(List<GroupAccess> groupAccesses) {
		Boolean valid_role = false;
		for (GroupAccess acces : groupAccesses) {
			AccessPermission accessPermission = acces.getAccess();
			if (accessPermission.getCode().equals("PER002") && accessPermission.getRowStatus().equals(true)) {
				valid_role = true;
				break;
			}
		}
		return valid_role;
	}    

	@Override
	public Boolean checkSignatureAgent(Agent agent, String signature) {
		ChannelCredential channelCre = channelCredentialRepository.findByChannelAndRowStatus(agent.getChannel(), true);
		String sha256 = channelService.generateHashSHA256(agent.getChannel().getCode().concat(channelCre.getAccessKey()));
		String sign = channelService.generateHashSHA384(sha256.concat(agent.getCode()));
		logger.info("checkSignatureAgent : " + sign);
		return sign.equals(signature);
	}

	@Override
	public Boolean checkSignatureCustomer(User user, String signature) {
		String sha256 = channelService.generateHashSHA256(user.getAgent().getCode());
		String sign = channelService.generateHashSHA384(sha256.concat(user.getCustomerKey()));
		logger.info("checkSignatureCustomer : " + sign);
		return sign.equals(signature);
	}

	public Map login(String agentCode, String password, String ip){
		Map result = new HashMap();

		if (!isExistingDataAndStringValue(agentCode) || !isExistingDataAndStringValue(password)) {
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, null, null));
			return result;
		}

		Agent agent = agentRepository.findByCodeAndRowStatus(agentCode, true);
		if (agent == null) {
			logger.error("Agent with code & password: '" + agent + "' and '" + password + "' not found");
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "login", null));
			return result;
		}

		AgentCredential credential = agentCredentialRepository.findByAgent(agent);
		if(credential == null){
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "credential not setup", null));
			return result;
		}

		if(!passwordEncoder.matches(password, credential.getPassword())){
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "agentCode atau password tidak valid", null));
			return result;
		}

		String visibleToken = agent.generateNewToken(ip);
		agent.setModifiedOn(new Date());
		agentRepository.save(agent);

		Map dataResult = new LinkedHashMap();
		User user = userRepository.findById(Long.valueOf(6946));
		String generalToken = "";
		if(user != null){
			generalToken = user.generateNewToken("INVISEE");
			userRepository.save(user);
		}

		dataResult.put("agent_code", agent.getCode());
		dataResult.put("agent_name", agent.getName());
		dataResult.put("phone_number", agentContactRepository.findByAgent_CodeAndContact_ContactType_Code(agentCode, "MOB").getContact().getValue());
		dataResult.put("token", visibleToken);
		dataResult.put("general_token", generalToken);

		result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
		result.put(ConstantUtil.DATA, dataResult);

		return result;
	}

	@Override
	public Map clientList(Agent agent){
		Map result = new HashMap();
		List listCustomer = new ArrayList();
		List<Kyc> listKyc = kycRepository.findAllByAccount_AgentOrderByIdDesc(agent);
		for(int i=0; i<listKyc.size(); i++){
			Kyc kyc = listKyc.get(i);
			User user = kyc.getAccount();

			String tokenUser = user.generateNewToken("INVISEE");
			user.setRecordLogin(new Date());
			user.setLastLogin(user.getRecordLogin());
			userRepository.save(user);

			Map data = new LinkedHashMap();
			data.put("token", tokenUser);
			data.put("email", kyc.getEmail());
			data.put("name", kyc.getFirstName());
			data.put("phone_number", kyc.getMobileNumber());
			data.put("customer_cif", kyc.getPortalcif());
			data.put("customer_status", user.getUserStatus());

			listCustomer.add(data);
		}

		if(listCustomer.size() == 0){
			result.put(ConstantUtil.CODE.toLowerCase(), ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.DATA_NOT_FOUND);
			return result;
		}

		result.put(ConstantUtil.CODE.toLowerCase(), ConstantUtil.STATUS_SUCCESS);
		result.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
		result.put(ConstantUtil.DATA.toLowerCase(), listCustomer);

		return result;
	}

	@Override
	public Map<String, Object> getClientMap(Agent spv, Agent agent, PageRequest pageRequest){
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> listCustomer = new ArrayList<>();
		List<Agent> agentList = new ArrayList<Agent>();
		if(Validator.isNotNullOrEmpty(agent)) {
			agentList.add(agent);
		}else {
			logger.error("[INFO] agent is null");
			MstFeeAgent mstFeeAgent= mstFeeAgentRepository.findByRole("AG");
			if(Validator.isNotNullOrEmpty(mstFeeAgent)) {
				Page<Agent> pageAgent = agentRepository.findAllBySpvAndAgentFee(spv, mstFeeAgent, new PageRequest(0, Integer.MAX_VALUE));
				if(Validator.isNotNullOrEmpty(pageAgent) && Validator.isNotNullOrEmpty(pageAgent.getContent())) {
					agentList.addAll(pageAgent.getContent());        		
				}else {
					logger.error("[INFO] No downline found for spv : {}", spv.getName());
				}
			}else {
				logger.error("[WARN] mstFeeAgent not found");
			}
			agentList.add(spv);        		
		}

		Page<Kyc> listKyc = kycRepository.findAllByAccount_AgentInList(agentList, pageRequest);
		for (Kyc kyc : listKyc.getContent()) {
			Map<String, Object> data = new LinkedHashMap<>();
			Map<String, Object> investMap = investmentService.getAumByCustomer(kyc.getId());
			User user = kyc.getAccount();
			data.put("email", kyc.getEmail());
			data.put("name", kyc.getFirstName());
			data.put("phone_number", kyc.getMobileNumber());
			data.put("customer_cif", kyc.getPortalcif());
			data.put("customer_status", user.getUserStatus());
			data.put("agent_name", user.getAgent().getName());
			data.put("total_market_value", investMap.get("totalAum"));
			listCustomer.add(data);
		}


		if(listKyc.getContent().size() < 1){
			result.put(ConstantUtil.CODE.toLowerCase(), ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.DATA_NOT_FOUND);
			return result;
		}

		result.put(ConstantUtil.CODE.toLowerCase(), ConstantUtil.STATUS_SUCCESS);
		result.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
		result.put(ConstantUtil.DATA.toLowerCase(), listCustomer);
		result.put("page", listKyc.getNumber());
		result.put("totalPages", listKyc.getTotalPages());

		return result;
	}

	@Override
	public Map getCommision(Agent agent){
		Map result = new HashMap();
		String sql = "SELECT " +
				"SUM( trx.fee_amount ) " +
				"FROM " +
				"ut_transactions trx " +
				"JOIN kyc kyc ON ( trx.kyc_id_id = kyc.customer_id ) " +
				"JOIN _user usr ON ( usr.ID = kyc.account_id ) " +
				"JOIN agent agent ON ( agent.ID = usr.agent_id ) " +
				"WHERE " +
				"agent.id = :agentId " +
				"AND trx.trx_status='ALL' " +
				"GROUP BY " +
				"agent.ID";

		Double data = (Double) entityManager.createNativeQuery(sql).setParameter("agentId", agent.getId()).getSingleResult();

		Map mapData = new HashMap();
		mapData.put("agentCommision", data);

		result.put(ConstantUtil.CODE.toLowerCase(), ConstantUtil.STATUS_SUCCESS);
		result.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
		result.put(ConstantUtil.DATA.toLowerCase(), mapData);
		mapData = null;

		return result;
	}


	@Override
	public List<AgentDto> getAgentList(List<Agent> agentList, Date fromDate, Date toDate) throws Exception {
		List<AgentDto> agentDtoList = new ArrayList<>();
		String sql = 
				"SELECT SUM(total_aum), AUM_CUSTOMER.agent_id  FROM ( " +
						"SELECT " + 
						"	_user.agent_id, " + 
						"	cb.total_aum " + 
						"FROM " + 
						"	_user " + 
						"	INNER JOIN kyc ON kyc.account_id = _user.id " + 
						"	JOIN (SELECT " + 
						"	SUM(cb.current_amount) as total_aum, " + 
						"	cb.customer_id " + 
						"FROM customer_balance cb " + 
						"JOIN (SELECT MAX(price_date) as price_date, products_id FROM ut_product_fund_prices GROUP BY products_id) fp ON (fp.price_date=cb.balance_date AND fp.products_id=cb.ut_product_id) " + 
						"GROUP BY cb.customer_id) cb ON (cb.customer_id=kyc.customer_id) "
						+ ") AS AUM_CUSTOMER "
						+ "INNER JOIN agent ON AUM_CUSTOMER.agent_id = agent.id "
						+ "WHERE agent_id = :agentId "
						+ "GROUP BY agent_id ";
		logger.error(sql);
		for (Agent agent : agentList) {
			AgentDto agentDto = new AgentDto();
			agent.setSpv(null);
			agent.setToken(null);
			agent.setAccessGroup(null);
			agent.setChannel(null);
			BeanUtils.copyProperties(agentDto, agent);
			Query query = entityManager.createNativeQuery(sql).setParameter("agentId", agent.getId()).setMaxResults(1);
			Object[] obj =  (Object[]) query.getResultList().stream().findFirst().orElse(null);
			if(Validator.isNotNullOrEmpty(obj)) {
				agentDto.setTotalAumAgent(new BigDecimal(obj[0].toString()).setScale(2, RoundingMode.DOWN));
			}
			UtTransactionsAgentFee transactionsAgentFee = utTransactionsAgentFeeRepository.getSummaryByAgentIdAndPeriod(agent.getId(), fromDate, toDate);
			if(Validator.isNotNullOrEmpty(transactionsAgentFee)) {
				agentDto.setAvaragePercentage(new BigDecimal(transactionsAgentFee.getFeePercentage(), MathContext.DECIMAL64).setScale(8, RoundingMode.DOWN));
				agentDto.setTotalOrderAmount(new BigDecimal(transactionsAgentFee.getOrderAmount(), MathContext.DECIMAL64).setScale(2, RoundingMode.DOWN));
				agentDto.setTotalCommission(new BigDecimal(transactionsAgentFee.getFeeAmount(), MathContext.DECIMAL64).setScale(2, RoundingMode.DOWN));
			}
			agentDtoList.add(agentDto);
		}
		return agentDtoList;
	}

	@Override
	public Map<String, Object> logout(Agent agent) {
		Map<String, Object> result = new HashMap<>();
		agent.setToken("");
		agentRepository.save(agent);
		result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
		result.put(ConstantUtil.CODE, ConstantUtil.STATUS_SUCCESS);
		result.put(ConstantUtil.INFO, "Logout Succesful!");
		return result;
	}
}
