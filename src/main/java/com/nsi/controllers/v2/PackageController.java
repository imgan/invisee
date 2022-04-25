package com.nsi.controllers.v2;

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

import com.nsi.controllers.BaseController;
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


@RestController("packageV2")
@RequestMapping("/package/v2/")
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

            resultMap = packageService.getFundPackageListV3(offset, limit, score, String.valueOf(map.get("agent")));

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
}