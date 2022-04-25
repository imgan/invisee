package com.nsi.controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.GlobalParameter;
import com.nsi.domain.core.UtTransactions;
import com.nsi.domain.core.UtTransactionsAgentFee;
import com.nsi.exception.ValidationException;
import com.nsi.interceptor.NeedLogin;
import com.nsi.publisher.JmsPublisher;
import com.nsi.repositories.core.AgentRepository;
import com.nsi.repositories.core.GlobalParameterRepository;
import com.nsi.repositories.core.UtTransactionsAgentFeeRepository;
import com.nsi.repositories.core.UtTransactionsRepository;
import com.nsi.services.AgentService;
import com.nsi.services.InvestmentService;
import com.nsi.services.UtTransactionsAgentFeeService;
import com.nsi.services.UtilService;
import com.nsi.util.ConstantUtil;
import com.nsi.util.Validator;

@SuppressWarnings({ "rawtypes", "unchecked" })
@RestController
@RequestMapping("/agent")
public class AgentController extends BaseController {
	
    @Autowired
    AgentService agentService;
    
    @Autowired
    UtilService utilService;
    
    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private JmsPublisher jmsPublisher;
    
    @Autowired
    private UtTransactionsRepository utTransactionsRepository;
    
    @Autowired
    private UtTransactionsAgentFeeRepository utTransactionsAgentFeeRepository;
    
    @Autowired
    private GlobalParameterRepository globalParameterRepository;
    
    @Autowired
    private UtTransactionsAgentFeeService utTransactionsAgentFeeService;
    
    @Autowired
    InvestmentService investmentService;
    
    private final String X_ACCESS_KEY = "x-access_key";

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Map> login(HttpServletRequest request, @RequestBody(required = false) Map map) {
        Map resultMap = new HashMap();
        try {
            if(!isExistingData(map)){
                resultMap.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
                resultMap.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, null, null));
                return new ResponseEntity<>(resultMap, HttpStatus.OK);
            }

            if(!isExistingData(map.get("agent_code"))){
                resultMap.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
                resultMap.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "agent_code", null));
                return new ResponseEntity<>(resultMap, HttpStatus.OK);
            }

            if(!isExistingData(map.get("password"))){
                resultMap.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
                resultMap.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "password", null));
                return new ResponseEntity<>(resultMap, HttpStatus.OK);
            }

            resultMap = agentService.login(map.get("agent_code").toString().trim(), map.get("password").toString().trim(), getIpAddress(request));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "login", null);
        }
        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    @NeedLogin
    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request){
    	Map<String, Object> resultMap = agentService.logout((Agent) request.getAttribute(ConstantUtil.LOGINED_AGENT));
    	return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

	@RequestMapping(value = "/clients", method = RequestMethod.GET)
    public ResponseEntity<Map> clientList(HttpServletRequest request) {
        Map resultMap = new HashMap();
        try {
            if(!isExistingData(request.getHeader("x-agent_token"))){
                resultMap.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
                resultMap.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "x-agent_token", null));
                return new ResponseEntity<>(resultMap, HttpStatus.OK);
            }

            Map data = utilService.checkTokenAgent(request.getHeader("x-agent_token"), getIpAddress(request));
            if ((int) (data.get("code")) != 1) {
                return new ResponseEntity<>(data, HttpStatus.OK);
            }

            resultMap = agentService.clientList((Agent) data.get("agent"));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "clients", null);
        }
        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

	@RequestMapping(value = "/commisionSummary", method = RequestMethod.GET)
    public ResponseEntity<Map> getCommision(HttpServletRequest request) {
        Map resultMap = new HashMap();
        try {
            if(!isExistingData(request.getHeader("x-agent_token"))){
                resultMap.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
                resultMap.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "x-agent_token", null));
                return new ResponseEntity<>(resultMap, HttpStatus.OK);
            }

            Map data = utilService.checkTokenAgent(request.getHeader("x-agent_token"), getIpAddress(request));
            if ((int) (data.get("code")) != 1) {
                return new ResponseEntity<>(data, HttpStatus.OK);
            }

            resultMap = agentService.getCommision((Agent) data.get("agent"));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "commisionSummary", null);
        }
        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
	
	@NeedLogin
	@GetMapping(value = "/subordinate/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getSubordinates(HttpServletRequest request, 
    		@RequestParam(required = false, name = ConstantUtil.X_AGENT_TOKEN) String xAgentToken,
    		@RequestParam(defaultValue = "0") int offset,
    		@RequestParam(defaultValue = "10") int limit
    		) {
    	Map<String, Object> resultMap = new HashMap<>();
    	try {
    		int page = offset/limit;
    		Date fromDate = clearDate(new Date(), 0);
    		fromDate.setDate(1);
    		Date toDate = clearDate(new Date(), 1);
    		Agent spv = getLoginedAgent(request);
    		if(spv != null) {
    			Page<Agent> agentPage = agentRepository.findAllBySpv(spv, new PageRequest(page, limit, new Sort(Sort.Direction.ASC, "name")));
    			resultMap.put(ConstantUtil.CODE.toLowerCase(), ConstantUtil.STATUS_SUCCESS);
    			resultMap.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
    			resultMap.put(ConstantUtil.DATA, agentService.getAgentList(agentPage.getContent(), fromDate, toDate));
    			resultMap.put("offset", offset);
    			resultMap.put("page", agentPage.getNumber());
    			resultMap.put("totalPages", agentPage.getTotalPages());
    		}
			
		} catch (Exception e) {
			logger.error("[FATAL]", e);
			logger.error(e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "/subordinate/list", null);
		}
    	loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
    	return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }
	
    
	@NeedLogin
    @GetMapping(value = "/subordinate/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getSubordinateClients(HttpServletRequest request, 
    		@RequestParam(required = false, name = ConstantUtil.X_AGENT_TOKEN) String xAgentToken,
    		@RequestParam(required = false) String agentCode,
    		@RequestParam(defaultValue = "0") int offset,
    		@RequestParam(defaultValue = "10") int limit){
		logger.error("start getSubordinateClients .....");
    	Map<String, Object> resultMap = new HashMap<>();
    	try {
    		int page = offset/limit;
    		Agent spv = getLoginedAgent(request);
    		Agent agent = null;
    		if(Validator.isNotNullOrEmpty(agentCode)) {
    			logger.error("[INFO] agentCode: {}", agentCode);
    			agent = agentRepository.findByCodeAndRowStatus(agentCode, true);
    		}else {
    			logger.error("[INFO] agentCode is null");
    		}
    		resultMap = agentService.getClientMap(spv, agent, new PageRequest(page, limit));
		} catch (Exception e) {
			logger.error("[FATAL]", e);
			logger.error(e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "/subordinate/clients", null);
		}
    	resultMap.put("offset", offset);
    	logger.error("[INFO] result : {}", resultMap.toString());
    	loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
    	logger.error("end getSubordinateClients .....");
    	return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }
	
    
    @PostMapping(value = "/commission/calculate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> calculateFee(HttpServletRequest request, @RequestParam Long trxId){
    	Map<String, Object> resultMap = new HashMap<>();
    	try {
    		if(!isExistingData(request.getHeader(X_ACCESS_KEY))){
    			resultMap.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
    			resultMap.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "access_key", null));
    			return new ResponseEntity<>(resultMap, HttpStatus.OK);
    		}
    		String accessKeyHeader = request.getHeader(X_ACCESS_KEY);
    		GlobalParameter gp = globalParameterRepository.findByCategoryAndName(ConstantUtil.GLOBAL_PARAM_ACCESS_KEY, ConstantUtil.GLOBAL_PARAM_ACCESS_KEY);
    		if(gp == null) {
    			logger.error("GlobalParameter is null with name: {}", ConstantUtil.GLOBAL_PARAM_ACCESS_KEY);
    			throw new ValidationException("invalid access");
    		}else if(!accessKeyHeader.equalsIgnoreCase(gp.getValue())) {
    			logger.error("accessKeyHeader is not equal ");
    			throw new ValidationException("invalid access");
    		}
    		UtTransactions utTransactions = utTransactionsRepository.findOne(trxId);
    		if(utTransactions != null) {
    			jmsPublisher.run(utTransactions);
    			resultMap.put(ConstantUtil.CODE.toLowerCase(), ConstantUtil.STATUS_SUCCESS);
    			resultMap.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
    			resultMap.put(ConstantUtil.DATA, utTransactions);
    		}else {
    			throw new ValidationException("utTransactions is null with trxId : " + trxId);
    		}
    	} catch (ValidationException ve) {
    		logger.error(ve.getMessage());
    		resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "/commission/calculate", ve.getMessage());
		} catch (Exception e) {
			logger.error("[FATAL]", e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "/commission/calculate", null);
		}
    	return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }
    
    
    @NeedLogin
    @GetMapping(value = "/commission/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getHistory(HttpServletRequest request, 
    		@RequestParam(required = false, name = ConstantUtil.X_AGENT_TOKEN) String xAgentToken,
    		@RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
    		@RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
    		@RequestParam(defaultValue = "0") int offset,
    		@RequestParam(defaultValue = "10") int limit){
    	Map<String, Object> resultMap = new HashMap<>();
    	try {
    		if(fromDate != null) {
    			fromDate = clearDate(fromDate, 0);
    		}
    		if(toDate != null) {
    			toDate = clearDate(toDate, 1);    			
    		}
    		
    		Agent agent = getLoginedAgent(request);
    		
    		if(agent != null) {
    			Map<String, Object> map = utTransactionsAgentFeeService.findAllHistoryByAgentAndPeriod(agent.getId(), fromDate, toDate, offset, limit);
    			resultMap.put(ConstantUtil.CODE.toLowerCase(), ConstantUtil.STATUS_SUCCESS);
    			resultMap.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
    			resultMap.put(ConstantUtil.DATA, map.get("list"));
    			resultMap.put("offset", offset);
    			resultMap.put("page", (offset/limit));
    			resultMap.put("totalPages", map.get("totalPages"));
    		}else {
    			throw new Exception("Agent is null");
    		}
			
		} catch (Exception e) {
			logger.error("[FATAL]", e);
			logger.error(e.getMessage(), e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "/commission/history", null);
		}
    	
    	return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    
    @NeedLogin
    @GetMapping(value = "/commission/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getHistory(
    		HttpServletRequest request, 
    		@RequestParam(required = false, name = ConstantUtil.X_AGENT_TOKEN) String xAgentToken,
    		@RequestParam(required = true) String agentCode,
    		@RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
    		@RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate){
    	Map<String, Object> resultMap = new HashMap<>();
    	
    	try {
    		if(fromDate == null && toDate == null) {
        		Calendar cal = Calendar.getInstance();
        		cal.setTime(new Date());
        		cal.set(Calendar.DATE, 1);
        		fromDate = cal.getTime();
        		toDate = new Date();
        	}
        	if(toDate == null) {
        		toDate = new Date();
        	}
        	fromDate = clearDate(fromDate, 0);
        	toDate = clearDate(toDate, 1);

        	Agent spv = getLoginedAgent(request);
    		Agent agent = agentRepository.findByCodeAndRowStatus(agentCode, true);

    		if(agent != null) {
    			if(agent.getId().equals(spv.getId())) {
    				// do nothing
    			}else if(agent.getSpv() == null || (!agent.getSpv().getId().equals(spv.getId()))) {
    				throw new ValidationException("Unauthorized to access this agent");
    			}
    			Map<String, Object> dataMap = new HashMap<>();
    			UtTransactionsAgentFee sum = utTransactionsAgentFeeRepository.getSummaryByAgentIdAndPeriod(agent.getId(), fromDate, toDate);
    			if(sum != null) {
    				dataMap.put("totalOrderAmount", sum.getOrderAmount());
    				dataMap.put("totalFeeAmount", sum.getFeeAmount());
    				dataMap.put("AvgFeePercentage", sum.getFeePercentage());
    				dataMap.put("agentId", sum.getAgent().getId());
    			}
    			resultMap.put(ConstantUtil.CODE.toLowerCase(), ConstantUtil.STATUS_SUCCESS);
    			resultMap.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
    			resultMap.put(ConstantUtil.DATA, dataMap);
    		}else {
    			throw new ValidationException("agent is null : "+ agentCode);
    		}

    	} catch (ValidationException ve) {
    		logger.error(ve.getMessage());
    		resultMap = errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, ve.getActualMessage(), null);
    	} catch (Exception e) {
    		logger.error("[FATAL]", e);
    		resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "/commission/summary", null);
    	}

    	return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }
    
    @PostMapping(value = "/failed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> failedHandlerPost(HttpServletRequest request){
    	return failedHandler(request);
    }
    
    @GetMapping(value = "/failed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> failedHandler(HttpServletRequest request){
    	Map data = new HashMap();
    	if(Validator.isNotNullOrEmpty(request.getAttribute(ConstantUtil.DATA_NOT_FOUND))) {
    		data.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
    		data.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, request.getAttribute(ConstantUtil.DATA_NOT_FOUND).toString(), null));
    	}else {
    		data = (Map) request.getAttribute("data");    		
    	}
    	return new ResponseEntity<Map<String, Object>>(data, HttpStatus.OK);
    }

	@PostMapping(value = "/v2/failed", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> newFailedHandlerPost(HttpServletRequest request){
		Map data = (Map) request.getAttribute("data");
		return new ResponseEntity<Map<String, Object>>(data, httpStatusCode(data, false));
	}
    		

    private Date clearDate(Date date, int additionDay) {
    	Calendar cal = Calendar.getInstance();
    	if(date == null && additionDay == 0) {
    		cal.setTime(new Date());
    		cal.set(Calendar.YEAR, 2000);
    	}else {
    		cal.setTime(date);    		
    	}
    	cal.set(Calendar.DATE, (cal.get(Calendar.DATE) + additionDay));
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	return cal.getTime();
    }
}
