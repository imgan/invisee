package com.nsi.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.User;
import com.nsi.repositories.core.AgentEmailRepository;
import com.nsi.repositories.core.AgentRepository;
import com.nsi.repositories.core.ChannelCommissionRepository;
import com.nsi.repositories.core.ChannelRepository;
import com.nsi.repositories.core.GroupsRepository;
import com.nsi.repositories.core.UserRepository;
import com.nsi.services.ChannelService;
import com.nsi.services.UtilService;

@RestController
@RequestMapping("/channel")
public class ChannelController extends BaseController{
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	AgentRepository agentRepository;
	@Autowired
	ChannelRepository channelRepository;
	@Autowired
	ChannelCommissionRepository channelCommissionRepository;
	@Autowired
	AgentEmailRepository agentEmailRepository;
	@Autowired
	ChannelService channelService;
	@Autowired
	GroupsRepository groupsRepository;
	@Autowired
	UtilService utilService;

	@RequestMapping(value="/view_channel", method = RequestMethod.POST)
	public ResponseEntity<Map> viewChannel(HttpServletRequest request,@RequestBody Map map){
		//TODO : SKIP TOKEN Super Admin’s active token
		String version = request.getHeader("version");
		Map resultMap = new HashMap();
		User user = null;
		Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), request.getHeader("X-FORWARDED-FOR") == null
				? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR"));
		if(!tokenMap.get("code").equals(1)){
			resultMap = tokenMap;

			if("2".equals(version)){
				resultMap = changeCodeIntToString(resultMap);
			}

			return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
		}else{
			user = (User) tokenMap.get("user");
		}

		//Cek permission Super Admin (Agent)
		if(!utilService.checkAccessPermissionSuperAdmin(user.getAgent())){
			resultMap.put("code", 12);
			resultMap.put("info", "Invalid access, you dont have permission");

			if("2".equals(version)){
				resultMap = changeCodeIntToString(resultMap);
			}

			return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
		}
		
		Map dataMap = channelService.viewChannel(user.getAgent());		
		
		resultMap.put("code", 0);
		resultMap.put("info", "Channel info successfully loaded");
		resultMap.put("data", dataMap);

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
	}
	
	@RequestMapping(value="/update_channel", method = RequestMethod.POST)
	public ResponseEntity<Map> updateChannel(HttpServletRequest request,@RequestBody Map map){
		//TODO : SKIP TOKEN
		String version = request.getHeader("version");
		Map resultMap = new HashMap();
		User user = userRepository.findByEmail("invisee.adm1@mailinator.com");
		if(!user.getRoleCode().equals("1") || user.getRoleCode() != "1"){
			resultMap.put("code", 12);
			resultMap.put("info", "Invalid access");
		}
		resultMap = channelService.updateChannel(map, user);

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
	}
	
	@RequestMapping(value="/detail_customer", method = RequestMethod.POST)
	public ResponseEntity<Map> detailCustomer(HttpServletRequest request, @RequestBody Map map){
		//TODO : SKIP TOKEN AGENT
		String version = request.getHeader("version");
		Map resultMap = channelService.detailCustomer(map);

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
	}
	
	@RequestMapping(value="/list_customer", method = RequestMethod.POST)
	public ResponseEntity<Map> listCustomer(HttpServletRequest request, @RequestBody Map map){
		//TODO : SKIP TOKEN AGENT
		String version = request.getHeader("version");
		Agent agent = agentRepository.findByCodeAndRowStatus("INVPTL_AGENT_1", true); //Hardcode  INVPTL_AGENT_1
		Map resultMap = channelService.getListCustomer((Map) map.get("filter"), agent);

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
	}
}
