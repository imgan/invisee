package com.nsi.controllers;

import com.nsi.dto.request.PackagePerformanceRequest;
import com.nsi.dto.response.BaseResponse;
import com.nsi.dto.response.PackagePerformanceResponse;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.AttachFile;
import com.nsi.domain.core.CustomerDocument;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.Score;
import com.nsi.domain.core.User;
import com.nsi.repositories.core.AgentRepository;
import com.nsi.repositories.core.AttachFileRepository;
import com.nsi.repositories.core.KycRepository;
import com.nsi.services.AgentService;
import com.nsi.services.PackageService;
import com.nsi.services.UtilService;
import com.nsi.util.ConstantUtil;
import com.nsi.util.ValidateUtil;

import java.io.IOException;

@RestController
@RequestMapping("/package")
public class PackageController extends BaseController {

    @Autowired
    PackageService packageService;
    @Autowired
    KycRepository kycRepository;
    @Autowired
    UtilService utilService;
    @Autowired
    AgentService agentService;
    @Autowired
    AgentRepository agentRepository;
    @Autowired
    AttachFileRepository attachFileRepository;
//	@RequestMapping(value="/list", method = RequestMethod.GET)
//	@ResponseBody
//	public ResponseEntity<Map> packageLists(@RequestParam("offset") Integer offset,@RequestParam("limit") Integer limit){
//		Map resultMap = packageService.getFundPackageList(offset, limit);
//		return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
//	}
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map> packageLists(HttpServletRequest request, @RequestBody Map map) {
        String version = request.getHeader("version");
        Integer offset = -1, limit = -1;

        try {
            if (map.get("offset") != null) {
                offset = Integer.parseInt(String.valueOf(map.get("offset")));
            }

            if (map.get("limit") != null) {
                limit = Integer.parseInt(String.valueOf(map.get("limit")));
            }

            Score score = null;
            Map resultMap = new HashMap<>();
            if (map.get("token") != null) {
                //TODO: Cek token
                Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR"));
                if (!tokenMap.get("code").equals(100)) {
                    User user = (User) tokenMap.get("user");
                    Kyc kyc = kycRepository.findByEmail(user.getUsername());
                    if (kyc != null) {
                        score = kyc.getRiskProfile();
                    }
                } else {
                    resultMap = tokenMap;

                    if("2".equals(version)){
                        resultMap = changeCodeIntToString(resultMap);
                    }

                    return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
                }
            }

            if (map.get("signature").equals("") || map.get("agent").equals("")) {
                resultMap.put("code", 50);
                resultMap.put("info", "Incomplete data agent or signature");

                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
            }

            Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
            if (!agentService.checkSignatureAgent(agent, map.get("signature").toString())) {
                resultMap.put("code", 12);
                resultMap.put("info", "Channel invalid");

                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
            }

            if (!utilService.checkAccessPermission(agent)) {
                resultMap.put("code", 12);
                resultMap.put("info", "invalid access, you dont have permission");
            }

            resultMap = packageService.getFundPackageListV2(offset, limit, score, String.valueOf(map.get("agent")));

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        } catch (Exception e) {
            // TODO: handle exception
        	logger.error("[FATAL]" ,e);
            Map resultMap = new HashMap<>();
            resultMap.put("code", 99);
            resultMap.put("info", "General error");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public ResponseEntity<Map> packageDetails(HttpServletRequest request, @RequestBody Map map) {
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();
        try {
            if (map.get("token") != null) {
                //TODO: Cek token
                Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")), request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR"));
                if (!tokenMap.get("code").equals(1)) {
                    resultMap = tokenMap;

                    if("2".equals(version)){
                        resultMap = changeCodeIntToString(resultMap);
                    }

                    return new ResponseEntity<>(resultMap, HttpStatus.OK);
                }

            }

            if (map.get("signature") == null || map.get("agent") == null) {
                resultMap.put("code", 10);
                resultMap.put("info", "Incomplete data agent or signature");

                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                return new ResponseEntity<>(resultMap, HttpStatus.OK);
            }

            logger.info("signature : "+map.get("signature"));
            Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
            if (!agentService.checkSignatureAgent(agent, map.get("signature").toString())) {
                resultMap.put("code", 12);
                resultMap.put("info", "Channel invalid");

                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
            }

            if (!utilService.checkAccessPermission(agent)) {
                resultMap.put("code", 12);
                resultMap.put("info", "invalid access, you dont have permission");

                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
            }

            resultMap = packageService.getPackageDetails(String.valueOf(map.get("code")));

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        } catch (Exception e) {
        	logger.error("[FATAL]" ,e);
            resultMap.put("code", 99);
            resultMap.put("info", "General error");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/fund_allocation", method = RequestMethod.POST)
    public ResponseEntity<Map> packageFundAllocation(HttpServletRequest request, @RequestBody Map map) {
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();
        if (map.get("signature") == null || map.get("agent") == null) {
            resultMap.put("code", 50);
            resultMap.put("info", "Incomplete data, data agent or signature must be send");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        if (map.get("signature").equals("") || map.get("agent").equals("")) {
            resultMap.put("code", 50);
            resultMap.put("info", "Incomplete data agent or signature");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
        if (!agentService.checkSignatureAgent(agent, map.get("signature").toString())) {
            resultMap.put("code", 12);
            resultMap.put("info", "Channel invalid");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        if (!utilService.checkAccessPermission(agent)) {
            resultMap.put("code", 12);
            resultMap.put("info", "invalid access, you dont have permission");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        resultMap = packageService.getPackageFundAllocation(String.valueOf(map.get("code")));

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/fee", method = RequestMethod.GET)
    public ResponseEntity<Map> packageTransactionFee(HttpServletRequest request, @RequestParam("code") String code) {
        String version = request.getHeader("version");
        Map resultMap = packageService.getPackageTransactionFee(code);

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/subscription_fee", method = RequestMethod.POST)
    public ResponseEntity<Map> packageSubscriptionFee(HttpServletRequest request, @RequestBody Map map) {
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();
        if (map.get("signature") == null || map.get("agent") == null) {
            resultMap.put("code", 50);
            resultMap.put("info", "Incomplete data, data agent or signature must be send");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        if (map.get("signature").equals("") || map.get("agent").equals("")) {
            resultMap.put("code", 50);
            resultMap.put("info", "Incomplete data agent or signature");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
        if (!agentService.checkSignatureAgent(agent, map.get("signature").toString())) {
            resultMap.put("code", 12);
            resultMap.put("info", "Channel invalid");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        if (!utilService.checkAccessPermission(agent)) {
            resultMap.put("code", 12);
            resultMap.put("info", "invalid access, you dont have permission");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }
        resultMap = packageService.getPackageSubscriptionFee(String.valueOf(map.get("code")));

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/redemption_fee", method = RequestMethod.POST)
    public ResponseEntity<Map> packageRedemptionFee(HttpServletRequest request, @RequestBody Map map) {
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();
        if (map.get("signature") == null || map.get("agent") == null) {
            resultMap.put("code", 50);
            resultMap.put("info", "Incomplete data, data agent or signature must be send");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        if (map.get("signature").equals("") || map.get("agent").equals("")) {
            resultMap.put("code", 50);
            resultMap.put("info", "Incomplete data agent or signature");
            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
        if (!agentService.checkSignatureAgent(agent, map.get("signature").toString())) {
            resultMap.put("code", 12);
            resultMap.put("info", "Channel invalid");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        if (!utilService.checkAccessPermission(agent)) {
            resultMap.put("code", 12);
            resultMap.put("info", "invalid access, you dont have permission");
        }

        resultMap = packageService.getPackageRedemptionFee(String.valueOf(map.get("code")));

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/payment_type", method = RequestMethod.POST)
    public ResponseEntity<Map> packagePaymentType(HttpServletRequest request, @RequestBody Map map) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        String version = request.getHeader("version");
        Map resultMap;
        try {
            resultMap = ValidateUtil.validateAPI("package/payment_type.json", map);
            if(resultMap == null) {
                resultMap = packageService.getPackagePaymentTypes(map);
            }
        } catch (IOException e) {
        	logger.error("[FATAL]" ,e);
            resultMap = errorResponse(99, "payment type", null);
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    @PostMapping(value = "/performance")
    public BaseResponse<PackagePerformanceResponse> performance(@RequestBody PackagePerformanceRequest packagePerformanceRequest) {
        return packageService.performance(packagePerformanceRequest);
    }
    
    @RequestMapping(value = "/document_download", method = RequestMethod.POST)
    public ResponseEntity<Map> documentDownload(HttpServletRequest request, @RequestBody Map map) throws Exception {
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();
        Map maps = new HashMap<>();
        String encodedString = null;
        //VALIDATION SIGNATURE & AGENT
	    if (map.get("signature").equals("") || map.get("agent").equals("")) {
            resultMap.put("code", 50);
            resultMap.put("info", "Incomplete data agent or signature");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
        //COMPARE IF AGENT WITH SIGNATURE TRUE
        if (agent == null || !agentService.checkSignatureAgent(agent, map.get("signature").toString())) {
            resultMap.put("code", 12);
            resultMap.put("info", "Channel invalid");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }
        //CHECK PERMISSION AGENT
        if (!utilService.checkAccessPermission(agent)) {
            resultMap.put("code", 12);
            resultMap.put("info", "invalid access, you dont have permission");
        }
        
		//VALIDATION & GET FILE LOCATION
		try {
		
			resultMap = ValidateUtil.validateAPI("package/documentDownload.json", map);
			resultMap = packageService.getDocumentDownload(String.valueOf(map.get("key")));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("[FATAL] " ,e);
			resultMap.put("code", 99);
            resultMap.put("info", "General error");
		}
		
        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }
        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
    }
}
