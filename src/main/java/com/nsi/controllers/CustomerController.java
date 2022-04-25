package com.nsi.controllers;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.CustomerDocument;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import com.nsi.dto.request.PasswordChangeRequest;
import com.nsi.dto.request.PasswordForgotConfirmRequest;
import com.nsi.dto.request.PasswordForgotRequest;
import com.nsi.dto.response.BaseResponse;
import com.nsi.dto.response.CompletenessResponse;
import com.nsi.dto.response.PasswordChangeResponse;
import com.nsi.interceptor.NeedLogin;
import org.apache.commons.codec.binary.Base64;
import com.nsi.repositories.core.AnswerRepository;
import com.nsi.repositories.core.ChannelCustomerRepository;
import com.nsi.repositories.core.CustomerAnswerRepository;
import com.nsi.repositories.core.CustomerDocumentRepository;
import com.nsi.repositories.core.DocumentTypeRepository;
import com.nsi.repositories.core.GlobalParameterRepository;
import com.nsi.repositories.core.KycRepository;
import com.nsi.repositories.core.QuestionRepository;
import com.nsi.repositories.core.QuestionairesRepository;
import com.nsi.repositories.core.UserRepository;
import com.nsi.services.*;
import com.nsi.util.ConstantUtil;
import com.nsi.util.DateTimeUtil;
import com.nsi.util.ValidateUtil;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nsi.util.Validator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/customer")
public class CustomerController extends BaseController {
	@Autowired
	KycRepository kycRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	QuestionairesRepository questionairesRepository;
	@Autowired
	QuestionRepository questionRepository;
	@Autowired
	CustomerAnswerRepository customerAnswerRepository;
	@Autowired
	AnswerRepository answerRepository;
	@Autowired
	CustomerService customerService;
	@Autowired
	ChannelService channelService;
	@Autowired
	ChannelCustomerRepository channelCustomerRepository;
	@Autowired
	UtilService utilService;
	@Autowired
	AgentService agentService;
	@Autowired
	CustomerDocumentRepository customerDocumentRepository;
	@Autowired
	DocumentTypeRepository documentTypeRepository;
	@Autowired
	GlobalParameterRepository globalParameterRepository;
	@Autowired
	OtpService otpService;
	@Autowired
	SbnService sbnService;
	@Autowired
	AttachFileService attachFileService;

	@PostMapping(value = "/password/forgot")
	public BaseResponse passwordForgot(@RequestBody PasswordForgotRequest request) {
		return customerService.passwordForgot(request);
	}

	@PostMapping(value = "/password/forgot/confirm")
	public BaseResponse passwordForgotConfirm(@RequestBody PasswordForgotConfirmRequest request) {
		return customerService.passwordForgotConfirm(request);
	}

	@PostMapping(value = "/password/change")
	public PasswordChangeResponse passwordChange(HttpServletRequest httpServletRequest, @RequestBody
			PasswordChangeRequest request) {
		return customerService.passwordChange(request, httpServletRequest);
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Map> login(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		Map resultMap;
		String version = request.getHeader("version");
		try {
			resultMap = ValidateUtil.validateAPI("customer/login.json", map);
			if (resultMap == null) {
				if (map.get("username") == null || map.get("password") == null || 
						map.get("username") == "" || map.get("password") == "") {
					resultMap = customerService.loginByCustomerCif(map.get("customer_cif").toString(),
							map.get("signature").toString(), getIpAddress(request));
				} else {
					resultMap = customerService
							.loginByUsername(map.get("username").toString(),map.get("password").toString(), 
									getIpAddress(request));
				}
				if (resultMap.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
					Map mapData = (Map) resultMap.get(ConstantUtil.DATA);
					Kyc kyc = (Kyc) mapData.get(ConstantUtil.KYC);
					User user = kyc.getAccount();
					String token = (String) mapData.get(ConstantUtil.TOKEN);
					Map rejected = (Map) resultMap.get("rejected");

					Map mRisk = new LinkedHashMap();
					if (kyc.getRiskProfile() != null) {
						mRisk.put("code", kyc.getRiskProfile().getScoreCode());
						mRisk.put("value", kyc.getRiskProfile().getScoreName());
					}

					Map dataMap = new LinkedHashMap<>();
					dataMap.put("last_login", DateTimeUtil
							.convertDateToStringCustomized(user.getLastLogin(), DateTimeUtil.DATE_TIME_MCW));
					dataMap.put("token", token);
					dataMap.put("customer_risk_profile", mRisk);
					dataMap.put("customer_status", user.getUserStatus());
					dataMap.put("customer_status_before", user.getUserStatusSebelumnya());
					if (!(map.get("username") == null || map.get("password") == null || 
							map.get("username") == "" || map.get("password") == "")) {
						dataMap.put("signature_customer", resultMap.get("signature_customer"));
					}
					dataMap.put("sid", kyc.getSid());
					if (rejected != null) {
						dataMap.put("rejected", rejected);
					}

					resultMap = errorResponse(ConstantUtil.STATUS_SUCCESS, "login", dataMap);
				} else {
					resultMap = (Map) resultMap.get(ConstantUtil.DATA);
				}
			}
		} catch (IOException e) {
			logger.error("[FATAL]" ,e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "login", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<BaseResponse> logout(@RequestBody Map request,
			HttpServletRequest httpServletRequest) {
		return customerService.logout(request.get("token").toString(), httpServletRequest);
	}

	@RequestMapping(value = "/document_update", method = RequestMethod.POST)
	public ResponseEntity<Map> documentUpdate(HttpServletRequest request,
			@RequestParam("uploadfile") MultipartFile uploadfile, @RequestParam("token") String token,
			@RequestParam("document_type") String document_type) {
		Map resultMap;
		Map tokenMap = utilService.checkToken(token, getIpAddress(request));
		String version = request.getHeader("version");

		if (!tokenMap.get("code").equals(1)) {
			resultMap = tokenMap;

			if("2".equals(version)){
				resultMap = changeCodeIntToString(resultMap);
			}

			return new ResponseEntity<>(resultMap, HttpStatus.OK);
		}

		User user = (User) tokenMap.get("user");

		try {
			Map map = customerService.uploadDocument(user, uploadfile, document_type);
			if (map.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				Map mapData = (Map) map.get(ConstantUtil.DATA);
				Kyc kyc = (Kyc) mapData.get(ConstantUtil.KYC);

				List<CustomerDocument> ktps = customerDocumentRepository
						.findByDocumentTypeAndUserOrderByCreatedOnDesc("DocTyp01", user);
				List<CustomerDocument> npwps = customerDocumentRepository
						.findByDocumentTypeAndUserOrderByCreatedOnDesc("DocTyp05", user);

				String ktp = null;
				String ttd = null;

				if (ktps != null && !ktps.isEmpty()) {
					ktp = ktps.get(0).getFileKey();
				}
				if (npwps != null && !npwps.isEmpty()) {
					ttd = npwps.get(0).getFileKey();
				}

				//String type = (String) mapData.get(ConstantUtil.TYPE);
				Map riskProfileMap = new HashMap<>();
				riskProfileMap.put("code", kyc.getRiskProfile().getScoreCode());
				riskProfileMap.put("value", kyc.getRiskProfile().getScoreName());

				Map fileMap = new LinkedHashMap();
				fileMap.put("id_card_image", ktp);
				fileMap.put("signature_image", ttd);

				Map dataMap = new LinkedHashMap<>();
				dataMap.put("customer_document", fileMap);
				dataMap.put("customer_risk_profile", riskProfileMap);
				dataMap.put("customer_status", kyc.getAccount().getUserStatus());

				resultMap = errorResponse(ConstantUtil.STATUS_SUCCESS, "upload document", dataMap);
			} else {
				resultMap = (Map) map.get(ConstantUtil.DATA);
			}
		} catch (Exception e) {
			logger.error("[FATAL]" ,e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "upload document", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/profile_view", method = RequestMethod.POST)
	public ResponseEntity<Map> profileView(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		Map resultMap;
		String version = request.getHeader("version");
		try {
			if (!isExistingDataAndStringValue(map.get("token"))) {
				resultMap = errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "customer_cif", null);
			} else {
				Map checkToken = utilService.checkToken((String) map.get("token"), getIpAddress(request));
				if (Integer.parseInt(checkToken.get("code").toString()) != 1) {
					resultMap = checkToken;
				} else {
					User user = (User) checkToken.get("user");
					resultMap = customerService.profileView(user);
					if (resultMap.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
						resultMap = (Map) resultMap.get(ConstantUtil.DATA);
					}
				}
			}
			resultMap = errorResponse(ConstantUtil.STATUS_SUCCESS, "profile view", resultMap);
		} catch (NumberFormatException e) {
			logger.error("[FATAL]" ,e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "profile view", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/pre_register", method = RequestMethod.POST)
	public ResponseEntity<Map> preRegister(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		Map resultMap;
		String version = request.getHeader("version");
		try {
			resultMap = ValidateUtil.validateAPI("customer/pre_register.json", map);

			if (resultMap == null) {
				if (!agentService
						.checkSignatureAgent(map.get("agent").toString(), map.get("signature").toString())) {
					resultMap = errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "Channel invalid", null);
				} else {
					resultMap = customerService.preRegisterAndOrder(map);
				}
			}
		} catch (IOException e) {
			logger.error("[FATAL]" ,e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "profile register", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@GetMapping("/completeness")
	public BaseResponse<CompletenessResponse> completeness(HttpServletRequest httpServletRequest,
			@RequestHeader("Authorization") String authorization) {
		return customerService.completeness(httpServletRequest, authorization);
	}

	@RequestMapping(value = "/profile_register", method = RequestMethod.POST)
	public ResponseEntity<Map> profileRegister(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		Map resultMap;
		String version = request.getHeader("version");
		try {
			resultMap = ValidateUtil.validateAPI("profile_register.json", map);

			if (resultMap == null) {
				if (!isExistingDataAndStringValue(map.get("agent")) || !isExistingDataAndStringValue(map.get("signature"))) {
					resultMap = errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "agent or signature", null);
				} else {
					if (!agentService.checkSignatureAgent(map.get("agent").toString(), map.get("signature").toString())) {
						resultMap = errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "Channel invalid", null);
					} else {
						if(version != null && version.equals("2")){
							resultMap = customerService.profileRegisterVer2(map);
						}else{
							resultMap = customerService.profileRegister(map);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("[FATAL]" ,e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "profile register", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/profile_register_customer", method = RequestMethod.POST)
	public ResponseEntity<Map> profileRegisterCustomerAgent(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		Map resultMap;
		String version = request.getHeader("version");
		try {
			resultMap = ValidateUtil.validateAPI("profile_register_customer.json", map);

			if (resultMap == null) {
				if (!isExistingDataAndStringValue(map.get("token"))) {
					resultMap = errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "agent or token",null);
				} else {
					Map data = utilService.checkTokenAgent(map.get("token").toString(), getIpAddress(request));
					if ((int) (data.get("code")) != 1) {

						if("2".equals(version)){
							data = changeCodeIntToString(data);
						}

						return new ResponseEntity<>(data, HttpStatus.OK);
					} else {
						Agent agent = (Agent) data.get("agent");
						map.put("agent", agent.getCode());
						resultMap = customerService.profileRegisterCustomer(map);
					}
				}
			}
		} catch (IOException e) {
			logger.error("[FATAL]" ,e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "profile register", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
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
			resultMap = ValidateUtil.validateAPI("profile_update.json", map);

			if (resultMap == null) {
				Map checkToken = utilService.checkToken((String) map.get("token"), getIpAddress(request));
				if (Integer.parseInt(checkToken.get("code").toString()) == 100) {
					resultMap = checkToken;
				} else {
					User user = (User) checkToken.get("user");
					if (user.getUserStatus().equalsIgnoreCase("ACT")) {
						resultMap = ValidateUtil.validateAPI("profile_update_act.json", map);
					}

					if (resultMap == null) {
						resultMap = customerService.profileUpdate(map, user);
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

	@RequestMapping(value = "/profile_update_settlement", method = RequestMethod.POST)
	public ResponseEntity<Map> profileUpdateSettlement(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		Map resultMap;
		String version = request.getHeader("version");
		try {
			resultMap = ValidateUtil.validateAPI("customer/profile_update_settlement.json", map);

			if (resultMap == null) {
				Map checkToken = utilService.checkToken((String) map.get("token"), getIpAddress(request));
				if (Integer.parseInt(checkToken.get("code").toString()) != 1) {
					resultMap = checkToken;
				} else {
					User user = (User) checkToken.get("user");
					resultMap = customerService.profileUpdateSettlement(map, user);
				}
			}
		} catch (Exception e) {
			logger.error("[FATAL] :"+e.getMessage(), e);
			if(e.getMessage().contains("AVANTRADE")){
				String[] error = e.getMessage().split("#");
				resultMap = errorResponse(88, "Update Customer Failed", "Ava Response code :"+error[1]);
			}else{
				resultMap = errorResponse(99, "General error", null);
			}
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/business_status", method = RequestMethod.POST)
	public ResponseEntity<Map> businessStatus(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		String version = request.getHeader("version");
		Map resultMap;
		try {
			Map checkToken = utilService.checkToken((String) map.get("token"), getIpAddress(request));
			if (Integer.parseInt(checkToken.get("code").toString()) == 100) {
				resultMap = checkToken;
			} else {
				resultMap = customerService.businessStatus(map, (User) checkToken.get("user"));
			}
		} catch (NumberFormatException e) {
			logger.error("[FATAL]" ,e);
			resultMap = errorResponse(99, "General error", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	// TODO: Customer View Document
	@RequestMapping(value = "/document_view", method = RequestMethod.POST)
	public ResponseEntity<?> viewDocument(HttpServletRequest request, @RequestBody Map map, HttpServletResponse response) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		String version = request.getHeader("version");
		Map resultMap;
		// 1. SKIP CEK TOKEN
		if (map.get("key").equals("")) {
			resultMap = errorResponse(10, "Key can't null", null);

			if("2".equals(version)){
				resultMap = changeCodeIntToString(resultMap);
			}

			return new ResponseEntity<>(resultMap, HttpStatus.OK);
		}

		// TODO: Check token
		User user;
		Map tokenMap = utilService.checkToken((String) map.get("token"), getIpAddress(request));
		if (!tokenMap.get("code").equals(1)) {

			if("2".equals(version)){
				tokenMap = changeCodeIntToString(tokenMap);
			}

			return new ResponseEntity<>(tokenMap, HttpStatus.OK);
		} else {
			user = (User) tokenMap.get("user");
		}

		CustomerDocument doc = customerDocumentRepository.findByFileKeyAndUser(String.valueOf(map.get("key")), user);
		try {
			// TODO: Cek document dengan key yg dilemparkan user terdapat di db atau engga
			if (doc == null) {
				resultMap = errorResponse(50, "File Not Found", null);

				if("2".equals(version)){
					resultMap = changeCodeIntToString(resultMap);
				}

				return new ResponseEntity<>(resultMap, HttpStatus.OK);
			} else {
				Map dataImage = attachFileService.getFileFromAwsS3(doc.getFileLocation());
				if((int) dataImage.get("code") == 50){
					resultMap = new LinkedHashMap();
					resultMap.put("code", 50);
					resultMap.put("info", "File not found");
					return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
				}

				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", "Attachment;Filename=" + doc.getFileName());
				OutputStream outputStream = response.getOutputStream();
				byte[] bytes = Base64.decodeBase64(dataImage.get("data").toString());
				outputStream.write(bytes);
				outputStream.flush();
				outputStream.close();

				return new ResponseEntity(outputStream, HttpStatus.OK);
			}
		} catch (IOException e) {
			logger.error("[FATAL] " ,e);
			resultMap = errorResponse(90, "document_view", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	/*

    @RequestMapping(value = "/view_kyc", method = RequestMethod.POST)
    public ResponseEntity<Map> viewKyc(@RequestBody Map map, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        User user = null;
        Map tokenMap = utilService.checkToken(String.valueOf(map.get("token")),
                request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr()
                : request.getHeader("X-FORWARDED-FOR"));
        if (!tokenMap.get("code").equals(1)) {
            resultMap = tokenMap;
            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        } else {
            user = (User) tokenMap.get("user");
        }
        resultMap = customerService.viewKyc(user);
        return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
    }

    private Map deleteFileExisting(String key) {
        Map resultMap = new HashMap<>();
        CustomerDocument customerDocument = customerDocumentRepository.findByFileKey(key);
        if (customerDocument != null) {
            File file = new File(customerDocument.getFileLocation());
            if (!file.delete()) {
                resultMap.put("code", 1);
                resultMap.put("info", "Failed delete");
                return resultMap;
            }
            resultMap.put("code", 0);
            resultMap.put("info", "Success delete");
            return resultMap;
        }
        resultMap.put("code", 1);
        resultMap.put("info", "Failed delete");
        return resultMap;
    }

    @RequestMapping(value = "/pre_register", method = RequestMethod.POST)
    public ResponseEntity<Map> pre_register(@RequestBody Map map) {
        Map resultMap = new HashMap();
        if (String.valueOf(map.get("agent")) == "" || String.valueOf(map.get("agent")).equals("")) {
            resultMap.put("code", 10);
            resultMap.put("info", "incomplete data");
        }

        Map result = channelService.generateAgentSignature(String.valueOf(map.get("agent")),
                String.valueOf(map.get("signature")));
        if (result.get("code").equals(0)) {
            ChannelCustomer channelCustomer = channelCustomerRepository
                    .findByChannelCustomer(String.valueOf(map.get("customer")));
            if (channelCustomer == null) {
                channelCustomer = new ChannelCustomer();
                channelCustomer.setChannelCustomer(String.valueOf(map.get("customer")));
                channelCustomer.setCreatedBy(String.valueOf(map.get("agent")));
                channelCustomer.setCreatedOn(new Date());
                channelCustomer.setEmail(String.valueOf(map.get("email")));
                channelCustomer.setMobile(String.valueOf(map.get("mobile_number")));
                channelCustomer.setName(String.valueOf(map.get("full_name")));
                channelCustomerRepository.save(channelCustomer);

                resultMap.put("code", 0);
                resultMap.put("info", "Customer successfully pre-registered");
                return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
            }
            resultMap.put("code", 1);
            resultMap.put("info", "Customer already exist");
        } else {
            resultMap.put("code", result.get("code"));
            resultMap.put("info", result.get("info"));
        }
        return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
    }

	 */

	@RequestMapping(value = "/register_order", method = RequestMethod.POST)
	public ResponseEntity<Map> registerAndOrder(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		Map resultMap;
		String version = request.getHeader("version");
		try {
			resultMap = ValidateUtil.validateAPI("customer/register_order.json", map);

			if (resultMap == null) {
				if (!agentService.checkSignatureAgent(map.get("agent").toString(), map.get("signature").toString())) {
					resultMap = errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "Channel invalid", null);
				} else {
					String email = map.get("email").toString();
					String phone = map.get("phone_number").toString();
					String firstName = "Sahabat";
					String lastName = "Investasi";

					Kyc kyc = new Kyc();
					kyc.setFirstName(firstName);
					kyc.setLastName(lastName);
					kyc.setEmail(email);
					kyc.setMobileNumber(phone);

					Map connectToPartner = customerService.connectToPartner(map);
					String customerId;
					if (connectToPartner.get("id") != null) {
						customerId = connectToPartner.get("id").toString();
						map.put("customerId", customerId);
					} else {

						if("2".equals(version)){
							connectToPartner = changeCodeIntToString(connectToPartner);
						}

						return (ResponseEntity<Map>) connectToPartner;
					}

					Map result = checkToken(map, kyc);
					if (result != null) {

						if("2".equals(version)){
							result = changeCodeIntToString(result);
						}

						return new ResponseEntity<>(result, HttpStatus.OK);
					}

					map.put("firstName", firstName);
					map.put("lastName", lastName);

					resultMap = customerService.registerAndOrder(map);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "profile register", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/connectToPartner", method = RequestMethod.POST)
	public ResponseEntity<Map> connectToPartner(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		String version = request.getHeader("version");
		Map resultMap;
		try {
			resultMap = customerService.connectToPartner(map);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "connect to partner", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/login_partner", method = RequestMethod.POST)
	public ResponseEntity<Map> loginPartner(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		String version = request.getHeader("version");
		Map resultMap = null;
		try {
			resultMap = ValidateUtil.validateAPI("customer/loginPartner.json", map);
			if (resultMap == null) {
				if (map.get("memberId") != null || map.get("jsessionId") != null ) {
					resultMap = customerService.loginPartner(map.get("memberId").toString(),
							map.get("jsessionId").toString(), getIpAddress(request));
				}
				if (resultMap.get(ConstantUtil.CODE).equals(ConstantUtil.STATUS_SUCCESS)) {
					Map mapData = (Map) resultMap.get(ConstantUtil.DATA);
					Kyc kyc = (Kyc) mapData.get(ConstantUtil.KYC);
					User user = kyc.getAccount();
					String token = (String) mapData.get(ConstantUtil.TOKEN);
					Map rejected = (Map) resultMap.get("rejected");
					String signatureCustomer = (String) mapData.get("signature_customer");

					Map mRisk = new LinkedHashMap();
					if (kyc.getRiskProfile() != null) {
						mRisk.put("code", kyc.getRiskProfile().getScoreCode());
						mRisk.put("value", kyc.getRiskProfile().getScoreName());
					}

					Map dataMap = new LinkedHashMap<>();
					dataMap.put("last_login", DateTimeUtil
							.convertDateToStringCustomized(user.getLastLogin(), DateTimeUtil.DATE_TIME_MCW));
					dataMap.put("token", token);
					dataMap.put("signature_customer", signatureCustomer);
					dataMap.put("customer_risk_profile", mRisk);
					dataMap.put("customer_status", user.getUserStatus());
					dataMap.put("customer_status_before", user.getUserStatusSebelumnya());
					if (!(map.get("username") == null || map.get("password") == null ||
							map.get("username") == "" || map.get("password") == "")) {
						dataMap.put("signature_customer", resultMap.get("signature_customer"));
					}

					if (rejected != null) {
						dataMap.put("rejected", rejected);
					}

					resultMap = errorResponse(ConstantUtil.STATUS_SUCCESS, "login partner", dataMap);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "login partner", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	private Map checkToken(Map map, Kyc kyc) {
		if (map.get("type_otp") == null) {
			return errorResponse(10, "type_otp", null);
		}
		if (map.get("otp") == null) {
			return errorResponse(10, "otp", null);
		}
		if (map.get("stan") == null) {
			return errorResponse(10, "stan", null);
		}

		String channelOtp = map.get("type_otp").toString();
		String valueOtp = map.get("otp").toString();
		String stan = map.get("stan").toString();

		boolean val = otpService.validate(channelOtp, kyc, stan, valueOtp);
		if (!val) {
			return errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "token problem", "wrong token");
		} else {
			return null;
		}
	}

	@NeedLogin(NeedLogin.UserLogin.CUSTOMER)
	@PostMapping(value = "/validateCustomerSbn")
	public ResponseEntity<Map<String, Object>> validateCustomerSbn(HttpServletRequest request){
		Map map = (Map) request.getAttribute(ConstantUtil.REQ_BODY);
		loggerHttp(request, ConstantUtil.REQUEST, map);
		Map resultMap;
		User user = (User) request.getAttribute(ConstantUtil.LOGINED_USER);
		String version = request.getHeader("version");

		try{
			resultMap = sbnService.validateCustomerSbn(user);
		}catch (HttpStatusCodeException e){
			logger.error("[FATAL] :" + e.getMessage(), e);
			try{
				JSONParser parser = new JSONParser();
				resultMap = (JSONObject) parser.parse(e.getResponseBodyAsString());
			}catch(org.json.simple.parser.ParseException ex){
				resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "validateCustomerSbn", null);
			}
		}catch (Exception e){
			logger.error("[FATAL] :" + e.getMessage(), e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "validateCustomerSbn", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, httpStatusCode(resultMap, true));
	}

	@PostMapping(value = "/failed", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> failedHandlerPost(HttpServletRequest request, HttpServletResponse response){
		return failedHandler(request, response);
	}

	@GetMapping(value = "/failed", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> failedHandler(HttpServletRequest request, HttpServletResponse response){
		Map data;
		HttpStatus httpStatus = HttpStatus.OK;
		if(Validator.isNotNullOrEmpty(request.getAttribute(ConstantUtil.DATA_NOT_FOUND))) {
			data = errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, request.getAttribute(ConstantUtil.DATA_NOT_FOUND).toString(), null);
			httpStatus = HttpStatus.BAD_REQUEST;
		}else {
			data = (Map) request.getAttribute("data");
			if(response.getStatus() == 401){
				httpStatus = HttpStatus.UNAUTHORIZED;
			}
		}
		return new ResponseEntity<Map<String, Object>>(data, httpStatus);
	}

	@RequestMapping(value = "/resend_activation_code", method = RequestMethod.POST)
	public ResponseEntity<Map> resendActivationCode(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		String version = request.getHeader("version");
		Map resultMap;
		try {
			resultMap = ValidateUtil.validateAPI("check_token.json", map);
			if(resultMap == null){
				Map checkToken = utilService.checkToken((String) map.get("token"), getIpAddress(request));
				if (Integer.parseInt(checkToken.get("code").toString()) != 1) {
					resultMap = checkToken;

					if("2".equals(version)){
						resultMap = changeCodeIntToString(resultMap);
					}
				}else{
					User user = (User) checkToken.get("user");
					resultMap = customerService.resendActivationCode(user);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM.toString(), "resend activation code", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/activation_customer", method = RequestMethod.POST)
	public ResponseEntity<Map> activationCustomer(HttpServletRequest request, @RequestBody Map map) {
		loggerHttp(request, ConstantUtil.REQUEST, map);
		String version = request.getHeader("version");
		Map resultMap;
		try {
			resultMap = ValidateUtil.validateAPI("customer/activation_customer.json", map);
			if(resultMap == null){
				Map checkToken = utilService.checkToken((String) map.get("token"), getIpAddress(request));
				if (Integer.parseInt(checkToken.get("code").toString()) != 1) {
					resultMap = checkToken;
					if("2".equals(version)){
						resultMap = changeCodeIntToString(resultMap);
					}
				}else{
					User user = (User) checkToken.get("user");
					resultMap = customerService.activationCustomer(user, (String) map.get("activation_code"));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM.toString(), "activation customer", null);
		}

		if("2".equals(version)){
			resultMap = changeCodeIntToString(resultMap);
		}

		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/get_data_migration", method = RequestMethod.POST)
	public ResponseEntity<Map> getDataMigration(HttpServletRequest request, @RequestBody Map map){
		loggerHttp(request, ConstantUtil.REQUEST, map);
		Map resultMap;
		try{
			resultMap = ValidateUtil.validateAPI("customer/migration.json", map);
			if(resultMap == null){
				resultMap = customerService.getDataMigration(Integer.valueOf(map.get("offset").toString()),
					Integer.valueOf(map.get("limit").toString()),
					request.getHeader("x-api-key"));
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "data migration", null);
		}
		loggerHttp(request, ConstantUtil.RESPONSE, resultMap);
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}
	
	// TODO: Customer View Document
			@RequestMapping(value = "/document_download", method = RequestMethod.POST)
			public ResponseEntity<?> documentDownload(HttpServletRequest request, @RequestBody Map map,
					HttpServletResponse response) {
				loggerHttp(request, ConstantUtil.REQUEST, map);
				String version = request.getHeader("version");
				String encodedString = null;
				Map document = new HashMap();
				Map resultMap = new HashMap<>();
				// 1. SKIP CEK TOKEN
				if (map.get("key").equals("")) {
					resultMap = errorResponse(10, "Key can't null", null);

					if("2".equals(version)){
						resultMap = changeCodeIntToString(resultMap);
					}

					return new ResponseEntity<>(resultMap, HttpStatus.OK);
				}

				// TODO: Check File By Key

				CustomerDocument doc = customerDocumentRepository
						.findByFileKey(String.valueOf(map.get("key")));

				try {
					if (doc == null) {
						resultMap = errorResponse(50, "File Not Found", null);

						if("2".equals(version)){
							resultMap = changeCodeIntToString(resultMap);
						}

						return new ResponseEntity<>(resultMap, HttpStatus.OK);
					} else {
						
						File file = new File(doc.getFileLocation());
						FileInputStream fis = new FileInputStream(file);
						byte[] fileContent = new byte[(int) file.length()];
						fis = new FileInputStream(file);
						fis.read(fileContent);
						encodedString = Base64.encodeBase64String(fileContent);
						fis.close();
						
						document.put("content", encodedString);
						
						Map data = new HashMap();
						data.put("code", 0);
						data.put("data", document);
						data.put("info", "Successfuly downloaded");
						
						return new ResponseEntity(data, HttpStatus.OK);
					}
				} catch (IOException e) {
					logger.error("[FATAL] " ,e);
					resultMap = errorResponse(90, "document_download", e);
				}

				if("2".equals(version)){
					resultMap = changeCodeIntToString(resultMap);
				}

				return new ResponseEntity<>(resultMap, HttpStatus.OK);
			}    
}