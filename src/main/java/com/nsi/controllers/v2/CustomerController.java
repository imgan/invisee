package com.nsi.controllers.v2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jms.core.JmsTemplate;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.nsi.controllers.BaseController;
import com.nsi.domain.core.User;
import com.nsi.services.AgentService;
import com.nsi.services.CustomerService;
import com.nsi.services.UtilService;
import com.nsi.util.ConstantUtil;
import com.nsi.util.ValidateUtil;

@RestController("customerV2")
@RequestMapping("/customer/v2/")
public class CustomerController extends BaseController {
    @Autowired
    AgentService agentService;
    @Autowired
    CustomerService customerService;
    @Autowired
	UtilService utilService;
    @Autowired
	JmsTemplate jmsTemplate;
    @SuppressWarnings("ALL")
    @RequestMapping(value = "/profile_register", method = RequestMethod.POST)
    public ResponseEntity<Map> profileRegister(HttpServletRequest request, @RequestBody Map map) {
        loggerHttp(request, ConstantUtil.REQUEST, map);
        Map resultMap;
        try {
            resultMap = ValidateUtil.validateAPI("customer/v2/profile_register.json", map);

            if (resultMap == null) {
                if (!isExistingDataAndStringValue(map.get("agent")) || !isExistingDataAndStringValue(map.get("signature"))) {
                    resultMap = errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "agent or signature", null);
                } else {
                    if (!agentService.checkSignatureAgent(map.get("agent").toString(), map.get("signature").toString())) {
                        resultMap = errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "Channel invalid", null);
                    } else {
                        resultMap = customerService.profileRegisterV2(map);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[FATAL]" ,e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "customer/v2/profile_register", e);
        }

        loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/profile_update", method = RequestMethod.POST)
	public ResponseEntity<Map> profileUpdate(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		Map resultMap;
		String version = request.getHeader("version");
		try {
			resultMap = ValidateUtil.validateAPI("customer/v2/profile_update.json", map);

			if (resultMap == null) {
				Map checkToken = utilService.checkToken((String) map.get("token"), getIpAddress(request));
				if (Integer.parseInt(checkToken.get("code").toString()) == 100) {
					resultMap = checkToken;
				} else {
					User user = (User) checkToken.get("user");
					if (user.getUserStatus().equalsIgnoreCase("ACT")) {
						resultMap = ValidateUtil.validateAPI("customer/v2/profile_update_act.json", map);
					}

					if (resultMap == null) {
						resultMap = customerService.profileUpdateV2(map, user);
					}
				}
			}
		} catch (IOException | NumberFormatException e) {
			logger.error("[FATAL]" ,e);
			resultMap = errorResponse(99, "General error", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}
}