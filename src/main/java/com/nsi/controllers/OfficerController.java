package com.nsi.controllers;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nsi.domain.core.User;
import com.nsi.repositories.core.UserRepository;
import com.nsi.services.OfficerService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/officer")
public class OfficerController extends BaseController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	OfficerService officerService;

	@RequestMapping(value="/detail_channel", method = RequestMethod.POST)
	public ResponseEntity<Map> detailChannel(HttpServletRequest request, @RequestBody Map map, @RequestParam("token") String token){
		//TODO : SKIP TOKEN
		String version = request.getHeader("version");
		Map resultMap = new HashMap();
		User user = userRepository.findByEmail("invisee.adm1@mailinator.com");
		if(!user.getRoleCode().equals("1") || user.getRoleCode() != "1"){
			resultMap.put("code", 12);
			resultMap.put("info", "Invalid access");
		}
		
		resultMap = officerService.detailChannel(user, map);

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
	}
	
	@RequestMapping(value="/monitor_customer", method = RequestMethod.POST)
	public ResponseEntity<Map> monitorCustomer(HttpServletRequest request, @RequestParam("token") String token){
		//TODO : SKIP TOKEN
		String version = request.getHeader("version");
		Map resultMap = new HashMap();
		User user = userRepository.findByEmail("invisee.adm1@mailinator.com"); //Officer
		if(!user.getRoleCode().equals("1") || user.getRoleCode() != "1"){
			resultMap.put("code", 12);
			resultMap.put("info", "Invalid access");
		}
		
		resultMap = officerService.monitorCustomer(user);

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
	}
	
	@RequestMapping(value="/list_channel", method = RequestMethod.POST)
	public ResponseEntity<Map> listChannel(HttpServletRequest request, @RequestBody Map map,@RequestParam("token") String token){
		//TODO : SKIP TOKEN
		String version = request.getHeader("version");
		Map resultMap = new HashMap();
		User user = userRepository.findByEmail("invisee.adm1@mailinator.com"); //Officer
		if(!user.getRoleCode().equals("1") || user.getRoleCode() != "1"){
			resultMap.put("code", 12);
			resultMap.put("info", "Invalid access");
		}
		
		resultMap = officerService.listChannel(user, (Map) map.get("filter"));

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
	}
}
