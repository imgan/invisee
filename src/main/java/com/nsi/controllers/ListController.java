package com.nsi.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nsi.domain.core.Agent;
import com.nsi.repositories.core.AgentRepository;
import com.nsi.repositories.core.UserRepository;
import com.nsi.services.AgentService;
import com.nsi.services.CustomerService;
import com.nsi.services.ListService;
import com.nsi.util.ValidateUtil;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/list")
public class ListController extends BaseController {

    @Autowired
    ListService listService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerService customerService;
    @Autowired
    AgentRepository agentRepository;
    @Autowired
    AgentService agentService;

    private ResponseEntity<Map> listLookupLine(HttpServletRequest request, Map map, String lookupHeader) {
        String version = request.getHeader("version");
        Map resultMap;
        try {
            resultMap = ValidateUtil.validateAPI("list/lockup_line.json", map);
            if(resultMap == null) {
                resultMap = listService.getListLookupLine(map, lookupHeader);
                if (resultMap.get("code").equals(0)) {
                    resultMap.put("info", lookupHeader.toLowerCase().replaceAll("_", " ") + " list successfully loaded");
                }
            }
        } catch (IOException e) {
        	logger.error("[FATAL]" ,e);
            resultMap = errorResponse(99, lookupHeader.toLowerCase().replaceAll("_", " ") + " list", null);
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    } 
    
    @RequestMapping(value = "/gender", method = RequestMethod.POST)
    public ResponseEntity<Map> listGender(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "GENDER");
    }
    
    @RequestMapping(value = "/denom", method = RequestMethod.POST)
    public ResponseEntity<Map> listDenom(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "DENOMINATION");
    }

    @RequestMapping(value = "/marital_status", method = RequestMethod.POST)
    public ResponseEntity<Map> listMaritalStatus(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "MARITAL_STATUS");
    }

    @RequestMapping(value = "/education", method = RequestMethod.POST)
    public ResponseEntity<Map> listEducation(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "EDUCATION_BACKGROUND");
    }

    @RequestMapping(value = "/religion", method = RequestMethod.POST)
    public ResponseEntity<Map> listReligion(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "RELIGION");
    }

    @RequestMapping(value = "/statement_type", method = RequestMethod.POST)
    public ResponseEntity<Map> listStatementType(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "STATEMENT_TYPE");
    }

    @RequestMapping(value = "/occupation", method = RequestMethod.POST)
    public ResponseEntity<Map> listOccupation(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "OCCUPATION");
    }

    @RequestMapping(value = "/business_nature", method = RequestMethod.POST)
    public ResponseEntity<Map> listBusinessNature(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "NATURE_OF_BUSINESS");
    }
    
    @RequestMapping(value = "/income_source", method = RequestMethod.POST)
    public ResponseEntity<Map> listIncomeSource(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "SOURCE_OF_INCOME");
    }
    
    @RequestMapping(value = "/annual_income", method = RequestMethod.POST)
    public ResponseEntity<Map> listAnnualIncome(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "ANNUAL_INCOME");
    }

    @RequestMapping(value = "/total_asset", method = RequestMethod.POST)
    public ResponseEntity<Map> listTotalAsset(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "TOTAL_ASSET");
    }

    @RequestMapping(value = "/investment_purpose", method = RequestMethod.POST)
    public ResponseEntity<Map> listInvestmentPurpose(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "INVESTMENT_PURPOSE");
    }

    @RequestMapping(value = "/investment_experience", method = RequestMethod.POST)
    public ResponseEntity<Map> listInvestmentExperience(HttpServletRequest request, @RequestBody Map map) {
        return listLookupLine(request, map, "INVESTMENT_EXPERIENCE");
    }

    @RequestMapping(value = "/country", method = RequestMethod.POST)
    public ResponseEntity<Map> listCountries(HttpServletRequest request, @RequestBody Map map) {
        String version = request.getHeader("version");
        Map resultMap = listService.getListCountries(map);
        if (resultMap.get("code").equals(0)) {
            resultMap.put("info", "Country list successfully loaded");
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/province", method = RequestMethod.POST)
    public ResponseEntity<Map> listProvince(HttpServletRequest request, @RequestBody Map map) {
        String version = request.getHeader("version");
        Map resultMap = listService.getListProvinces(map, version);
        if (resultMap.get("code").equals(0)) {
            resultMap.put("info", "Province list successfully loaded");
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/city", method = RequestMethod.POST)
    public ResponseEntity<Map> listCity(HttpServletRequest request, @RequestBody Map map) {
        String version = request.getHeader("version");
        Map resultMap = listService.getListCity(map);
        if (resultMap.get("code").equals(0)) {
            resultMap.put("info", "City list successfully loaded");
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/bank", method = RequestMethod.POST)
    public ResponseEntity<Map> listBank(HttpServletRequest request, @RequestBody Map map) {
        String version = request.getHeader("version");
        Map resultMap = listService.getListBank(map);
        if (resultMap.get("code").equals(0)) {
            resultMap.put("info", "Bank list successfully loaded");
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/fatca", method = RequestMethod.POST)
    public ResponseEntity<Map> viewFatca(HttpServletRequest request, @RequestBody Map map) {
        // TODO: Cek mandatory request
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();
        if (!this.checkMandatoryField(map)) {
            resultMap.put("code", 10);
            resultMap.put("info", "Incomplete data request");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }

        //TODO: Cek Signature Agent
        Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
        Boolean checkSignature = agentService.checkSignatureAgent(agent, String.valueOf(map.get("signature")));
        if (!checkSignature) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid access");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        }
        resultMap = listService.getListFatca();

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    //TODO: Check Mandatory Field
    private Boolean checkMandatoryField(Map map) {
        if (map.get("signature").equals("") || map.get("agent").equals("")) {
            return false;
        }
        return true;
    }

    @RequestMapping(value = "/risk_profile", method = RequestMethod.POST)
    public ResponseEntity<Map> viewRiskProfile(HttpServletRequest request, @RequestBody Map map) {
        // TODO: Cek mandatory request
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();
        if (!this.checkMandatoryField(map)) {
            resultMap.put("code", 10);
            resultMap.put("info", "Incomplete data request");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        //TODO: Cek Signature Agent
        Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
        Boolean checkSignature = agentService.checkSignatureAgent(agent, String.valueOf(map.get("signature")));
        if (!checkSignature) {
            resultMap.put("code", 12);
            resultMap.put("info", "Invalid access");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        resultMap = listService.getListRiskProfile();

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
    }
}