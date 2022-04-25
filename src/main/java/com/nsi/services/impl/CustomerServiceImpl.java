package com.nsi.services.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import com.nsi.services.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsi.domain.core.Agent;
import com.nsi.domain.core.AgentCredential;
import com.nsi.domain.core.Answer;
import com.nsi.domain.core.Bank;
import com.nsi.domain.core.Cities;
import com.nsi.domain.core.ContentOTP;
import com.nsi.domain.core.Countries;
import com.nsi.domain.core.CustomerAnswer;
import com.nsi.domain.core.CustomerDocument;
import com.nsi.domain.core.DocumentType;
import com.nsi.domain.core.GlobalParameter;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.LookupHeader;
import com.nsi.domain.core.LookupLine;
import com.nsi.domain.core.Question;
import com.nsi.domain.core.Questionaires;
import com.nsi.domain.core.RejectionHistory;
import com.nsi.domain.core.SbnSid;
import com.nsi.domain.core.SbnTransactions;
import com.nsi.domain.core.Score;
import com.nsi.domain.core.SettlementAccounts;
import com.nsi.domain.core.States;
import com.nsi.domain.core.User;
import com.nsi.dto.FileDto;
import com.nsi.dto.request.PasswordChangeRequest;
import com.nsi.dto.request.PasswordForgotConfirmRequest;
import com.nsi.dto.request.PasswordForgotRequest;
import com.nsi.dto.request.TokenValidationRequest;
import com.nsi.dto.response.BaseResponse;
import com.nsi.dto.response.CompletenessDetailResponse;
import com.nsi.dto.response.CompletenessResponse;
import com.nsi.dto.response.PasswordChangeResponse;
import com.nsi.dto.response.TokenValidationResponse;
import com.nsi.enumeration.CustomerEnum;
import com.nsi.repositories.core.AgentCredentialRepository;
import com.nsi.repositories.core.AgentRepository;
import com.nsi.repositories.core.AnswerRepository;
import com.nsi.repositories.core.ApiOtpParameterRepository;
import com.nsi.repositories.core.BankRepository;
import com.nsi.repositories.core.CitiesRepository;
import com.nsi.repositories.core.ContentOTPRepository;
import com.nsi.repositories.core.CountriesRepository;
import com.nsi.repositories.core.CustomerAnswerRepository;
import com.nsi.repositories.core.CustomerDocumentRepository;
import com.nsi.repositories.core.DocumentTypeRepository;
import com.nsi.repositories.core.GlobalParameterRepository;
import com.nsi.repositories.core.KonfigRepository;
import com.nsi.repositories.core.KycRepository;
import com.nsi.repositories.core.LookupHeaderRepository;
import com.nsi.repositories.core.LookupLineRepository;
import com.nsi.repositories.core.QuestionRepository;
import com.nsi.repositories.core.QuestionairesRepository;
import com.nsi.repositories.core.RejectionHistoryRepository;
import com.nsi.repositories.core.SbnAccountDetailRepository;
import com.nsi.repositories.core.SbnSidRepository;
import com.nsi.repositories.core.SbnTransactionsRepository;
import com.nsi.repositories.core.ScoreRepository;
import com.nsi.repositories.core.SettlementAccountsRepository;
import com.nsi.repositories.core.StatesRepository;
import com.nsi.repositories.core.UserRepository;
import com.nsi.util.ConstantUtil;
import com.nsi.util.DateTimeUtil;
import com.nsi.util.Generator;
import com.nsi.util.ValidateUtil;
import freemarker.template.TemplateException;

@Service
public class CustomerServiceImpl extends BaseService implements CustomerService {
	@Autowired
	KycRepository kycRepository;
	@Autowired
	SettlementAccountsRepository settlementAccountsRepository;
	@Autowired
	QuestionairesRepository questionairesRepository;
	@Autowired
	QuestionRepository questionRepository;
	@Autowired
	CustomerAnswerRepository customerAnswerRepository;
	@Autowired
	AnswerRepository answerRepository;
	@Autowired
	CustomerDocumentRepository customerDocumentRepository;
	@Autowired
	DocumentTypeRepository documentTypeRepository;
	@Autowired
	LookupHeaderRepository lookupHeaderRepository;
	@Autowired
	LookupLineRepository lookupLineRepository;
	@Autowired
	AgentRepository agentRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CountriesRepository countriesRepository;
	@Autowired
	StatesRepository statesRepository;
	@Autowired
	CitiesRepository citiesRepository;
	@Autowired
	BankRepository bankRepository;
	@Autowired
	ScoreRepository scoreRepository;
	@Autowired
	ChannelService channelService;
	@Autowired
	UtilService utilService;
	@Autowired
	AgentService agentService;
	@Autowired
	AgentCredentialRepository agentCredentialRepository;
	@Autowired
	GlobalParameterRepository globalParameterRepository;
	@Autowired
	EmailService emailService;
	@Autowired
	ViseepayService viseepayService;
	@Autowired
	TransactionService transactionService;
	@Autowired
	OtpService otpService;
	@Autowired
	Generator generator;
	@Autowired
	RejectionHistoryRepository rejectionHistoryRepository;
	@Autowired
	ContentOTPRepository contentOTPRepository;
	@Autowired
	KonfigRepository konfigRepository;
	@Autowired
	ApiOtpParameterRepository apiOtpParameterRepository;
	@Autowired
	AvantradeIntegrationService avantradeIntegrationService;
	@Autowired
	SbnSidRepository sbnSidRepository;
	@Autowired
	SendingEmailService sendingEmailService;
	@PersistenceContext(name = "core")
	EntityManager entityManager;
	@Autowired
	SbnTransactionsRepository sbnTransactionsRepository;
	@Autowired
	SbnAccountDetailRepository sbnAccountDetailRepository;
    @Autowired
	AttachFileService attachFileService;
	
	private Logger logger = Logger.getLogger(this.getClass());

	private String jsonKyc(Kyc kyc) {
		SettlementAccounts settlementAccounts = settlementAccountsRepository.findByKycs(kyc);
		Map<String, Object> maps = new HashMap<>();
		if (settlementAccounts != null) {
			maps.put("settlementAccountName", settlementAccounts.getSettlementAccountName());
		}
		if (settlementAccounts != null) {
			maps.put("settlementAccountNo", settlementAccounts.getSettlementAccountNo());
		}
		if (settlementAccounts != null) {
			maps.put("bankId", settlementAccounts.getBankId().getId());
		}
		maps.put("legalPhoneNumber", kyc.getLegalPhoneNumber());
		maps.put("officeCountry", kyc.getOfficeCountry());
		maps.put("citizenship", kyc.getCitizenship());
		maps.put("legalAddress", kyc.getLegalAddress());
		maps.put("homeAddress", kyc.getHomeAddress());
		maps.put("officeAddress", kyc.getOfficeAddress());
		maps.put("idType", kyc.getIdType());
		maps.put("homePhoneNumber", kyc.getHomePhoneNumber());
		maps.put("maritalStatus", kyc.getMaritalStatus());
		maps.put("birthPlace", kyc.getBirthPlace());
		maps.put("sourceOfIncome", kyc.getSourceOfIncome());
		maps.put("religion", kyc.getReligion());
		maps.put("legalCountry", kyc.getLegalCountry());
		maps.put("investmentExperience", kyc.getInvestmentExperience());
		maps.put("homeProvince", kyc.getHomeProvince());
		maps.put("motherMaidenName", kyc.getMotherMaidenName());
		maps.put("homeCity", kyc.getHomeCity());
		maps.put("investmentPurpose", "PDT");
		maps.put("homeAddress", kyc.getHomeAddress());
		maps.put("legalCity", kyc.getLegalCity());
		maps.put("gender", kyc.getGender());
		maps.put("birthDate", DateTimeUtil.convertDateToStringCustomized(kyc.getBirthDate(), "dd-MM-yyyy"));
		maps.put("firstName", kyc.getFirstName());
		maps.put("officeCity", kyc.getOfficeCity());
		maps.put("idNumber", kyc.getIdNumber());
		maps.put("middleName", kyc.getMiddleName());
		maps.put("homePostalCode", kyc.getHomePostalCode());
		maps.put("legalProvince", kyc.getLegalProvince());
		maps.put("idExpirationDate",
				DateTimeUtil.convertDateToStringCustomized(kyc.getIdExpirationDate(), "dd-MM-yyyy"));
		maps.put("lastName", kyc.getLastName());
		maps.put("occupation", kyc.getOccupation());
		maps.put("totalIncomePa", kyc.getTotalIncomePa());
		maps.put("officePhoneNumber", kyc.getOfficePhoneNumber());
		maps.put("officeProvince", kyc.getOfficeProvince());
		maps.put("homeCountry", kyc.getHomeCountry());
		maps.put("natureOfBusiness", kyc.getNatureOfBusiness());
		maps.put("totalAsset", kyc.getTotalAsset());
		maps.put("educationBackground", kyc.getEducationBackground());
		maps.put("nationality", kyc.getNationality());
		maps.put("officePostalCode", kyc.getOfficePostalCode());
		maps.put("legalPostalCode", kyc.getLegalPostalCode());
		maps.put("preferredMailingAddress", kyc.getPreferredMailingAddress());
		maps.put("officeAddress", kyc.getOfficeAddress());
		maps.put("salutation", kyc.getSalutation());

		JSONObject jSONObject = new JSONObject(maps);
		return jSONObject.toString();
	}

	private Map validateFatcaProfile(List<Map> maps, Questionaires questionairesFatca, Kyc kyc) {
		List<Question> listQuestionFatca;
		logger.info("questionnaire category : " + questionairesFatca.getQuestionnaireCategory());
		if (questionairesFatca.getQuestionnaireCategory() == 1) {
			logger.info("set list question fatca by risk profile");
			listQuestionFatca = questionRepository.findAllQuestionByQuestionairesWithQuery(questionairesFatca);
		} else {
			logger.info("set list question fatca by fatcha");
//      listQuestionFatca = questionRepository.findAllByQuestionairesOrderBySeqAsc(questionairesFatca);
			listQuestionFatca = questionRepository
					.findAllByQuestionairesAndParentqIdIsNullOrderBySeqAsc(questionairesFatca);
		}
		Map<String, String> mapQuestion = new HashMap<>();
		Long score = Long.valueOf("0");

		for (Question question : listQuestionFatca) {
			mapQuestion.put(question.getQuestionName(), "OK");
		}

		if (kyc.getId() != null) {
			List<CustomerAnswer> cas = customerAnswerRepository.findAllByQuestionWithQuery(kyc, listQuestionFatca);
			if (cas != null) {
				for (CustomerAnswer ca : cas) {
					mapQuestion.remove(ca.getQuestion().getQuestionName());
				}
			}
		}

		for (Map map : maps) {
			mapQuestion.remove(map.get("question").toString());
		}

		if (!mapQuestion.isEmpty()) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, questionairesFatca.getQuestionnaireName(), null);
		}

		mapQuestion.clear();

		List<CustomerAnswer> listFatcaAnswer = new ArrayList<>();

		for (Map map : maps) {
			Long questionId = null;
			for (Question q : listQuestionFatca) {
				if (q.getQuestionName().equals(map.get("question"))) {
					questionId = q.getId();
				}
			}
			Question question = questionRepository.findFirstByQuestionairesAndIdOrderByIdDesc(questionairesFatca,
					questionId);
			if (question == null) {
				return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "question " + map.get("question"), null);
			}

			mapQuestion.put(question.getId().toString(), "OK");

			List<String> answers = new ArrayList<>();
			try {
				answers = (List<String>) map.get("answers");
			} catch (Exception ignored) {
				answers.add((String) map.get("answers"));
			}
			for (String answer : answers) {
				Answer ans = answerRepository.findByAnswerNameAndQuestion(answer, question);
				if (ans == null) {
					return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "answers " + answer, null);
				}

				score += ans.getScore();
				CustomerAnswer ca = new CustomerAnswer();
				ca.setAnswer(ans);
				ca.setKyc(kyc);
				ca.setQuestion(question);
				ca.setVersion(0);
				ca.setCreatedDate(new Date());
				ca.setCreatedBy(kyc.getAccount().getUsername());

				listFatcaAnswer.add(ca);
			}
		}

		if (kyc.getId() != null) {
			for (String oldQuest : mapQuestion.keySet()) {
				Question question = questionRepository.findFirstByQuestionairesAndIdOrderByIdDesc(questionairesFatca,
						Long.parseLong(oldQuest));
				List<CustomerAnswer> oldQ = customerAnswerRepository.findAllByKycAndQuestionWithQuery(kyc, question);
				for (CustomerAnswer question1 : oldQ) {
					customerAnswerRepository.delete(question1);
				}
			}

			List<CustomerAnswer> cas = customerAnswerRepository.findAllByQuestionWithQuery(kyc, listQuestionFatca);
			for (CustomerAnswer ca : cas) {
				score += ca.getAnswer().getScore();
			}
		}

		Map datas = new HashMap();
		datas.put(ConstantUtil.QUESTION, listFatcaAnswer);
		datas.put(ConstantUtil.SCORE, score);
		datas.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);

		return datas;
	}

	private Map validateFieldProfile(Map map, Kyc kyc, Agent agent) {
		User user;
		SettlementAccounts settlementAccounts;
		Date birthDate = null;
		Date expirationDate = null;
		Countries nationality = null;
		Countries legalCountry = null;
		Countries homeCountry = null;
		LookupLine gender = null;
		LookupLine marital = null;
		LookupLine education = null;
		LookupLine religion = null;
		LookupLine statementType = null;
		LookupLine occupation = null;
		LookupLine businessNature = null;
		LookupLine sourceOfIncome = null;
		LookupLine totalIncome = null;
		LookupLine totalAset = null;
		LookupLine investmentPurpose = null;
		LookupLine investmentExperience = null;
		States legalProvince = null;
		States homeProvince = null;
		Cities homeCity = null;
		Cities legalCity = null;
		String otherInvestmentExperience = null;
		Bank bank = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false);

		if (kyc == null) {
			user = new User();
			user.setAgent(agent);
			user.setAccountExpired(false);
			user.setAccountLocked(false);
			user.setEnabled(true);
			user.setUserStatus("ACT");
			user.setUserStatusSebelumnya("REG");
			user.setApprovalStatus(false);
			user.setSecurityLevel("NOR");
			user.setCustomerKey(UUID.randomUUID().toString());
			user.setCreatedDate(new Date());
			user.setIsProcess(true);
			if (agent.getSbn()) {
				user.setIsSbnCustomer(true);
				user.setIsSbnCustomerProcess(false);
			}

			kyc = new Kyc();
			kyc.setPortalcif(utilService.generatePortalCIF());
			kyc.setCitizenship("DOM");
			kyc.setCreatedDate(user.getCreatedDate());
			kyc.setIdType("IDC");

			String emailKey = ((String) map.get("customer")).toLowerCase();
			if (org.springframework.util.StringUtils.containsWhitespace(emailKey)) {
				return errorResponse(11, "customer contains whitespace", null);
			}

			String password, passwordTmp;
			if (map.get("password") == null || map.get("password") == "") {
				emailKey += "@" + agent.getCode().toLowerCase() + "." + agent.getChannel().getCode().toLowerCase();
				password = emailKey;
				passwordTmp = emailKey;
			} else {
				passwordTmp = (String) map.get("password");
				password = DigestUtils.sha256Hex(passwordTmp + agent.getCode());
			}
			if (map.containsKey("emailUserKey")) {
				user.setEmail((String) map.get("emailUserKey"));
				user.setUsername(user.getEmail());
			} else {
				user.setEmail(emailKey);
				user.setUsername(emailKey);
			}
			user.setCreatedBy(emailKey);
			user.setPassword(password);
			user.setPasswordTemp(passwordTmp);
			user.setCreatedBy(emailKey);

			settlementAccounts = new SettlementAccounts();
			settlementAccounts.setCreatedDate(new Date());
			settlementAccounts.setCreatedBy(emailKey);

			kyc.setCreatedBy(emailKey);
			kyc.setCreatedDate(new Date());

			if (!agent.getEmailCustom()) {
				kyc.setFlagEmail(true);
			}

//            User findEmailKey = userRepository.findByEmail(emailKey);
//            if (findEmailKey != null) {
//                Map max = new HashMap();
//                max.put("customer", findEmailKey.getChannelCustomer());
//                max.put("", findEmailKey.getChannelCustomer());
//
//                return errorResponse(ConstantUtil.STATUS_EXISTING_DATA, "Email customer already exist", map);
//            }
		} else {
			settlementAccounts = settlementAccountsRepository.findByKycs(kyc);
			if (settlementAccounts == null) {
				settlementAccounts = new SettlementAccounts();
				settlementAccounts.setCreatedDate(new Date());
				settlementAccounts.setCreatedBy(kyc.getAccount().getUsername());
			}

			user = kyc.getAccount();
		}

		if (map.get("tgl_terbit") != null) {
			try {
				sdf.parse(map.get("tgl_terbit").toString().trim());
			} catch (ParseException e) {
				return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "tgl_terbit", null);
			}
		}

		if (map.get("kyc") != null) {
			Map kycs = (Map) map.get("kyc");
			if (!isExistingData(kycs.get("id_number").toString().trim())) {
				return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "ID Number", null);
			}

			if (!StringUtils.isNumeric(kycs.get("id_number").toString().trim())) {
				return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "ID Number", null);
			}

			if (isExistingData(kycs.get("income_source"))) {
				sourceOfIncome = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("SOURCE_OF_INCOME"), kycs.get("income_source").toString(),
						true);
				if (sourceOfIncome == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.income_source", null);
				}
			}

			if (isExistingData(kycs.get("annual_income"))) {
				totalIncome = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("ANNUAL_INCOME"), kycs.get("annual_income").toString(),
						true);
				if (totalIncome == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.annual_income", null);
				}
			}

			if (isExistingData(kycs.get("total_asset"))) {
				totalAset = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("TOTAL_ASSET"), kycs.get("total_asset").toString(), true);
				if (totalAset == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.total_asset", null);
				}
			}

			if (isExistingData(kycs.get("investment_purpose"))) {
				investmentPurpose = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("INVESTMENT_PURPOSE"),
						kycs.get("investment_purpose").toString(), true);
				if (investmentPurpose == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.investment_purpose", null);
				}
			}

			if (isExistingData(kycs.get("investment_experience"))) {
				investmentExperience = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("INVESTMENT_EXPERIENCE"),
						kycs.get("investment_experience").toString(), true);
				if (investmentExperience == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.investment_experience", null);
				}
				if (kycs.get("other_investment_experience") != null) {
					otherInvestmentExperience = kycs.get("other_investment_experience").toString();
				}
			}

			if (isExistingData(kycs.get("settlement_bank"))) {
				bank = bankRepository.findByBankCode(kycs.get("settlement_bank").toString());
				if (bank == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.settlement_bank", null);
				}
			}

			if (isExistingData(kycs.get("birth_date"))) {
				birthDate = DateTimeUtil.convertStringToDateCustomized(kycs.get("birth_date").toString(),
						DateTimeUtil.API_MCW);
			}

			if (isExistingData(kycs.get("gender"))) {
				gender = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("GENDER"), kycs.get("gender").toString(), true);
				if (gender == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.gender", null);
				}
			}

			if (isExistingData(kycs.get("nationality"))) {
				nationality = countriesRepository.findByAlpha3Code(kycs.get("nationality").toString());
				if (nationality == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.nationality", null);
				}
			}

			if (isExistingData(kycs.get("marital_status"))) {
				marital = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("MARITAL_STATUS"), kycs.get("marital_status").toString(),
						true);
				if (marital == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.marital_status", null);
				}
			}

			if (isExistingData(kycs.get("education_background"))) {
				education = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("EDUCATION_BACKGROUND"),
						kycs.get("education_background").toString(), true);
				if (education == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.education_background", null);
				}
			}

			if (isExistingData(kycs.get("religion"))) {
				religion = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("RELIGION"), kycs.get("religion").toString(), true);
				if (religion == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.religion", null);
				}
			}

			if (isExistingData(kycs.get("statement_type"))) {
				statementType = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("STATEMENT_TYPE"), kycs.get("statement_type").toString(),
						true);
				if (statementType == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.statement_type", null);
				}
			}

			if (isExistingData(kycs.get("occupation"))) {
				occupation = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("OCCUPATION"), kycs.get("occupation").toString(), true);
				if (occupation == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.occupation", null);
				}
			}

			if (isExistingData(kycs.get("business_nature"))) {
				businessNature = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("NATURE_OF_BUSINESS"),
						kycs.get("business_nature").toString(), true);
				if (businessNature == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.business_nature", null);
				}
			}

			if (isExistingData(kycs.get("id_expiration"))) {
				expirationDate = DateTimeUtil.convertStringToDateCustomized(kycs.get("id_expiration").toString(),
						DateTimeUtil.API_MCW);
			}

			if (kycs.get("legal") != null) {
				Map legals = (Map) kycs.get("legal");

				if (isExistingData(legals.get("country"))) {
					legalCountry = countriesRepository.findByAlpha3Code(legals.get("country").toString());
					if (legalCountry == null) {
						return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.legal.country", null);
					}
				}

				if (isExistingData(legals.get("province"))) {
					legalProvince = statesRepository.findByStateCode(legals.get("province").toString());
					if (legalProvince == null) {
						return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.legal.province", null);
					}
				}

				if (isExistingData(legals.get("city"))) {
					legalCity = citiesRepository.findByCityCode(legals.get("city").toString());
					if (legalCity == null) {
						return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.legal.city", null);
					}
				}
			}

			logger.info("cek kyc.mailing");

			// if (!isExistingData(kycs.get("mailing"))) {
			// return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.mailing",
			// null);
			// }
			if (kycs.get("mailing") != null) {
				Map mailings = (Map) kycs.get("mailing");

				if (isExistingData(mailings.get("country"))) {
					homeCountry = countriesRepository.findByAlpha3Code(mailings.get("country").toString());
					if (homeCountry == null) {
						return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.mailing.country", null);
					}
				}

				if (isExistingData(mailings.get("province"))) {
					homeProvince = statesRepository.findByStateCode(mailings.get("province").toString());
					if (homeProvince == null) {
						return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.mailing.province", null);
					}
				}

				if (isExistingData(mailings.get("city"))) {
					homeCity = citiesRepository.findByCityCode(mailings.get("city").toString());
					if (homeCity == null) {
						return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.mailing.city", null);
					}
				}
			}

			if (kycs.get("settlement_account_no") != null) {
				if (!StringUtils.isNumeric(kycs.get("settlement_account_no").toString().trim())) {
					return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "kyc.settlement_account_no", null);
				}
			}
		}

		if (map.get("email") != null) {
			kyc.setEmail(map.get("email").toString());
		}
		if (map.get("first_name") != null) {
			kyc.setFirstName(map.get("first_name").toString());
		}
		if (map.get("last_name") != null) {
			kyc.setLastName(map.get("last_name").toString());
		}
		if (map.get("customer") != null) {
			user.setChannelCustomer(map.get("customer").toString());
		}
		if (birthDate != null) {
			kyc.setBirthDate(birthDate);
		}
		if (map.get("phone_number") != null) {
			kyc.setMobileNumber(map.get("phone_number").toString());
		}

		if (map.get("no_kmiln") != null) {
			kyc.setNoKmiln(map.get("no_kmiln").toString().trim());
		}

		if (map.get("tgl_terbit") != null) {
			try {
				kyc.setIssueDateKmiln(sdf.parse(map.get("tgl_terbit").toString().trim()));
			} catch (ParseException e) {
				return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "tgl_terbit", null);
			}
		}

		if (map.get("kyc") != null) {
			Map kycs = (Map) map.get("kyc");

			if (gender != null) {
				kyc.setGender(gender.getCode());
			}
			if (nationality != null) {
				kyc.setNationality(nationality.getId().toString());
			}
			if (marital != null) {
				kyc.setMaritalStatus(marital.getCode());
			}

			System.out.println("BANK : " + bank);

			if (bank != null) {
				settlementAccounts.setBankId(bank);
			}
			if (kycs.get("settlement_account_no") != null) {
				settlementAccounts.setSettlementAccountNo(kycs.get("settlement_account_no").toString().trim());
			}
			if (kycs.get("settlement_account_name") != null) {
				settlementAccounts.setSettlementAccountName(kycs.get("settlement_account_name").toString());
			}
			if (sourceOfIncome != null) {
				kyc.setSourceOfIncome(sourceOfIncome.getCode());
			}
			if (totalIncome != null) {
				kyc.setTotalIncomePa(totalIncome.getCode());
			}
			if (totalAset != null) {
				kyc.setTotalAsset(totalAset.getCode());
			}
			if (investmentPurpose != null) {
				kyc.setInvestmentPurpose(investmentPurpose.getCode());
			}
			if (investmentExperience != null) {
				kyc.setInvestmentExperience(investmentExperience.getCode());
			}
			if (otherInvestmentExperience != null) {
				kyc.setOtherInvestmentExperience(otherInvestmentExperience);
			}
			if (kycs.get("birth_place") != null) {
				kyc.setBirthPlace(kycs.get("birth_place").toString());
			}
			if (kycs.get("mother_maiden_name") != null) {
				kyc.setMotherMaidenName(kycs.get("mother_maiden_name").toString());
			}
			if (education != null) {
				kyc.setEducationBackground(education.getCode());
			}
			if (religion != null) {
				kyc.setReligion(religion.getCode());
			}
			if (statementType != null) {
				kyc.setPreferredMailingAddress(statementType.getCode());
			}
			if (occupation != null) {
				kyc.setOccupation(occupation.getCode());
			}
			if (businessNature != null) {
				kyc.setNatureOfBusiness(businessNature.getCode());
			}
			if (kycs.get("id_number") != null) {
				kyc.setIdNumber(kycs.get("id_number").toString().trim());
			}
			if (expirationDate != null) {
				kyc.setIdExpirationDate(expirationDate);
			}
			if (kycs.get("legal") != null) {
				Map legals = (Map) kycs.get("legal");

				if (legalCountry != null) {
					kyc.setLegalCountry(legalCountry.getId().toString());
				}
				if (legalProvince != null) {
					kyc.setLegalProvince(legalProvince.getStateCode());
				}
				if (legalCity != null) {
					kyc.setLegalCity(legalCity.getId().toString());
				}
				if (legals.get("postal_code") != null) {
					kyc.setLegalPostalCode(legals.get("postal_code").toString());
				}
				if (legals.get("address") != null) {
					kyc.setLegalAddress(legals.get("address").toString());
				}
				if (legals.get("phone") != null) {
					kyc.setLegalPhoneNumber(legals.get("phone").toString());
				}
			}
			if (kycs.get("mailing") != null) {
				Map legals = (Map) kycs.get("mailing");

				if (homeCountry != null) {
					kyc.setHomeCountry(homeCountry.getId().toString());
					kyc.setOfficeCountry(homeCountry.getId().toString());
				}
				if (homeProvince != null) {
					kyc.setHomeProvince(homeProvince.getStateCode());
					kyc.setOfficeProvince(homeProvince.getStateCode());
				}
				if (homeCity != null) {
					kyc.setHomeCity(homeCity.getId().toString());
					kyc.setOfficeCity(homeCity.getId().toString());
				}
				if (legals.get("postal_code") != null) {
					kyc.setHomePostalCode(legals.get("postal_code").toString());
					kyc.setOfficePostalCode(legals.get("postal_code").toString());
				}
				if (legals.get("address") != null) {
					kyc.setHomeAddress(legals.get("address").toString());
					kyc.setOfficeAddress(legals.get("address").toString());
				}
				if (legals.get("phone") != null) {
					kyc.setHomePhoneNumber(legals.get("phone").toString());
					kyc.setOfficePhoneNumber(legals.get("phone").toString());
				}
			}
		}

		kyc.setAccount(user);
		settlementAccounts.setKycs(kyc);

		Map datas = new HashMap();
		datas.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
		datas.put(ConstantUtil.KYC, kyc);
		datas.put(ConstantUtil.SETTLEMENT, settlementAccounts);

		logger.info(datas);

		return datas;
	}

	private Kyc validateStatusUser(Kyc kyc) {
		User user = kyc.getAccount();
		List<String> docs = customerDocumentRepository.getDocValid(user, "DocTyp01", "DocTyp03");
		System.out.println("docs : " + docs.size());
		if (docs.size() >= 2 && !user.getUserStatus().equalsIgnoreCase("PEN")) {
			CompletenessDetailResponse checkKyc = this.checkKyc(kyc);
			logger.info("COMPLETENESS KYC : " + checkKyc);
			if (checkKyc.getCompleted()) {
				user.setUserStatusSebelumnya(user.getUserStatus());
				user.setUserStatus("PEN");
				// user.setApprovalStatus(false);
				user = userRepository.save(user);
				kyc.setAccount(user);
			}
		}
		return kyc;
	}

	@Override
	public BaseResponse passwordForgot(PasswordForgotRequest request) {
		BaseResponse response = new BaseResponse();
		// change find email from kyc table not user table
		Kyc kyc = kycRepository.findByEmailAndAccount_Agent_CodeIgnoreCase(request.getUsername(),
				request.getAgentCode().toLowerCase());
		if (kyc == null) {
			response.setCode("NOT_FOUND");
			response.setInfo("Resource not found: " + request.getUsername());
		} else {
			User user = kyc.getAccount();
			String resetCode = utilService.random(6);
			user.setResetCode(resetCode);
			user.setUpdatedBy(kyc.getEmail());
			user.setUpdatedDate(new Date());
			userRepository.save(user);

			List<Map> recipientList = new ArrayList<>();
			LinkedHashMap recipient = new LinkedHashMap();
			recipient.put("type", "to");
			recipient.put("email", kyc.getEmail());
			recipientList.add(recipient);

			String url = apiOtpParameterRepository.findByCode("URL_EMAIL_SEND").getValue();
			String session = apiOtpParameterRepository.findByCode("TIME_SESSION_DEFAULT").getValue();
			ContentOTP contentOTP = contentOTPRepository.findByTypeotp("OTP_RESET_PASSWORD_EMAIL");

			Map content = new LinkedHashMap();
			content.put("type", "html");
			content.put("text",
					contentOTP.getContent().replace("#CUSTOMER_NAME#", kyc.getFirstName() + " " + kyc.getLastName())
							.replace("#SESSION#", session).replace("#TOKEN#", resetCode)
							.replace("#IMAGELINK#", konfigRepository.findByKey("images_email").getValue()));
			content.put("subject", contentOTP.getSubject());

			Map requestEmail = new LinkedHashMap();
			requestEmail.put("channel", "INVISEE");
			requestEmail.put("recipient", recipientList);
			requestEmail.put("content", content);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity entity = new HttpEntity(requestEmail, headers);

			ResponseEntity<LinkedHashMap> responseEntity = new RestTemplate().exchange(url, HttpMethod.POST, entity,
					LinkedHashMap.class);
			if (Integer.parseInt(responseEntity.getBody().get("code").toString()) != 0) {
				response.setCode("INTERNAL_SERVER_ERROR");
				response.setInfo(responseEntity.getBody().get("info").toString());
			} else {
				response.setCode("SUCCEED");
				response.setInfo("Kode untuk reset password sudah dikirimkan ke email Anda.");
			}
		}
		response.setServerTime(new Date());
		return response;
	}

	@Override
	public BaseResponse passwordForgotConfirm(PasswordForgotConfirmRequest request) {
		Date now = new Date();
		BaseResponse response = new BaseResponse();

		Kyc kyc = kycRepository.findByEmailAndAccount_Agent_CodeIgnoreCase(request.getUsername(),
				request.getAgentCode().toLowerCase());
		if (kyc == null) {
			response.setCode("NOT_FOUND");
			response.setInfo("Resource not found: " + request.getUsername());
		} else {
			User user = userRepository.findById(kyc.getAccount().getId());

			Integer session = Integer.parseInt(apiOtpParameterRepository.findByCode("TIME_SESSION_DEFAULT").getValue());
			if (now.getTime() - user.getUpdatedDate().getTime() > session * 60 * 1000) {
				response.setCode("BAD_REQUEST");
				response.setInfo("Reset kode Anda telah kadaluwarsa! Silahkan melakukan request kembali.");
			} else {
				if (!user.getResetCode().equals(request.getResetCode())) {
					response.setCode("BAD_REQUEST");
					response.setInfo("Reset code Anda salah!");
				} else {
					if (!request.getPassword().equals(request.getPasswordConfirm())) {
						response.setCode("BAD_REQUEST");
						response.setInfo("Password and konfirm password tidak sama!");
					} else {
						user.setPasswordTemp(request.getPassword());
						user.setPassword(DigestUtils.sha256Hex(request.getPassword() + request.getAgentCode()));
						user.setResetCode(null);
						user.setUpdatedBy(user.getEmail());
						user.setUpdatedDate(new Date());
						userRepository.save(user);

						response.setCode("SUCCEED");
						response.setInfo("Selamat! Password Anda berhasil di-reset.");
					}
				}
			}
		}
		response.setServerTime(new Date());
		return response;
	}

	@Override
	public PasswordChangeResponse passwordChange(PasswordChangeRequest request, HttpServletRequest httpServletRequest) {
		String code = null;
		String info = null;

		User user = (User) utilService.checkToken(request.getToken(), getIpAddress(httpServletRequest)).get("user");

		if (user == null) {
			code = "FAILED";
			info = "Akses token tidak ditemukan!";
		}

		String passwordHashedRequest = DigestUtils.sha256Hex(request.getPasswordOld() + user.getAgent().getCode());
		String passowrdHashedDatabase = DigestUtils.sha256Hex(user.getPasswordTemp() + user.getAgent().getCode());

		if (code == null && passwordHashedRequest.compareToIgnoreCase(passowrdHashedDatabase) != 0) {
			code = "FAILED";
			info = "Password lama Anda tidak sama!";
		} else {
			if (user.getPasswordTemp().compareTo(request.getPassword()) == 0) {
				code = "FAILED";
				info = "Password baru Anda tidak boleh sama!";
			}
		}

		if (code == null && request.getPassword().compareTo(request.getPasswordConfirm()) != 0) {
			code = "FAILED";
			info = "Konfirmasi password Anda tidak sama!";
		}

		if (code == null) {
			String newPasswordHashed = DigestUtils.sha256Hex(request.getPassword() + user.getAgent().getCode());
			user.setPassword(newPasswordHashed);
			user.setPasswordTemp(request.getPassword());
			user.setUpdatedBy(user.getUsername());
			user.setUpdatedDate(new Date());
			userRepository.save(user);

			code = "SUCCESS";
			info = "Password Anda telah berubah!";
		}

		PasswordChangeResponse response = new PasswordChangeResponse();
		response.setCode(code);
		response.setInfo(info);
		return response;
	}

	@Override
	public Map loginByCustomerCif(String customerCif, String signature, String ip) {
		Map result = new HashMap();

		if (!isExistingDataAndStringValue(signature) || !isExistingDataAndStringValue(customerCif)) {
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, null, null));
			return result;
		}

		Kyc kyc = kycRepository.findByPortalcif(customerCif);
		if (kyc == null) {
			logger.error("kyc from '" + customerCif + "' not found");
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, null, null));
			return result;
		}

		User user = kyc.getAccount();
		if (!agentService.checkSignatureCustomer(user, signature)) {
			logger.error("agentService from customerCif : '" + customerCif + "' and '" + signature + "' not found");
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "login", null));
			return result;
		}

		String token = user.generateNewToken(ip);

		user.setRecordLogin(new Date());
		user.setLastLogin(user.getRecordLogin());
		user = userRepository.save(user);
		kyc.setAccount(user);

		Map data = new HashMap();
		data.put(ConstantUtil.KYC, kyc);
		data.put(ConstantUtil.TOKEN, token);

		result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
		result.put(ConstantUtil.DATA, data);

		RejectionHistory rejectionHistory = rejectionHistoryRepository
				.findFirstByRejectedUserIdOrderByCreatedOnDesc(user);
		String reason = "";
		String datetime = "";
		if (rejectionHistory != null) {
			reason = rejectionHistory.getNote();
			datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(rejectionHistory.getCreatedOn());
		}
		Map rejected = new LinkedHashMap();
		rejected.put("reason", reason);
		rejected.put("datetime", datetime);
		result.put("rejected", rejected);

		logger.info("result : " + result);

		return result;
	}

	@Override
	public Map loginByUsername(String username, String password, String ip) {
		Map result = new HashMap();

		if (!isExistingDataAndStringValue(username) || !isExistingDataAndStringValue(password)) {
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, null, null));
			return result;
		}

		List<User> userList = userRepository.findAllByUsernameIsLike('%' + username + '%');
		User user = null;

		for (User x : userList) {
			String passwordHashed = DigestUtils.sha256Hex(x.getPasswordTemp() + x.getAgent().getCode());
			if (passwordHashed.compareToIgnoreCase(password) == 0) {
				user = x;
				break;
			}
		}

		if (user == null) {
			logger.error("User with username & password: '" + username + "' and '" + password + "' not found");
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "login", null));
			return result;
		}

		Kyc kyc = kycRepository.findByAccount(user);
		if (kyc == null) {
			logger.error("kyc with username '" + username + "' not found");
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, null, null));
			return result;
		} else {
			if (kyc.getReferralCode() == null || kyc.getReferralCode().trim().equals("")) {
				String firstName, middleName, lastName;
				try {
					firstName = Character.toString(kyc.getFirstName().charAt(0));
				} catch (Exception ignored) {
					firstName = "";
				}
				try {
					middleName = Character.toString(kyc.getMiddleName().charAt(0));
				} catch (Exception ignored) {
					middleName = "";
				}
				try {
					lastName = Character.toString(kyc.getLastName().charAt(0));
				} catch (Exception ignored) {
					lastName = "";
				}
				String referralCode = "@" + firstName + middleName + lastName + generator.hexaDecimal(kyc.getId());
				kyc.setReferralCode(referralCode);
				kycRepository.save(kyc);
			}
		}

		String token = user.generateNewToken(ip);

		user.setRecordLogin(new Date());
		user.setLastLogin(user.getRecordLogin());
		user = userRepository.save(user);
		kyc.setAccount(user);

		Map data = new HashMap();
		data.put(ConstantUtil.KYC, kyc);
		data.put(ConstantUtil.TOKEN, token);

		result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
		result.put(ConstantUtil.DATA, data);
		result.put("signature_customer",
				DigestUtils.sha384Hex(DigestUtils.sha256Hex(user.getAgent().getCode()) + user.getCustomerKey()));

		RejectionHistory rejectionHistory = rejectionHistoryRepository
				.findFirstByRejectedUserIdOrderByCreatedOnDesc(user);
		Map rejected = new LinkedHashMap();
		String reason = "";
		String datetime = "";
		if (rejectionHistory != null) {
			rejected.put("reason", rejectionHistory.getNote());
			rejected.put("datetime",
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(rejectionHistory.getCreatedOn()));
		}
		result.put("rejected", rejected);

		logger.info("result : " + result);

		return result;
	}

  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Map uploadDocument(User user, MultipartFile uploadfile, String documentType) throws Exception {
    Map mapData = uploadDocumentTransactional(user, uploadfile, documentType);
    Kyc kyc = kycRepository.findByAccount(user);

		if (kyc.getAccount().getUserStatus().equalsIgnoreCase("PEN")
				&& kyc.getAccount().getUserStatusSebelumnya().equalsIgnoreCase("ACT")) {
			emailService.sendOpenRekening(kyc);
		}
		return mapData;
	}

	@Transactional
	Map uploadDocumentTransactional(User user, MultipartFile uploadfile, String documentType) throws Exception {
		Map result = new HashMap();

		if (!isExistingData(user) || !isExistingData(uploadfile) || !isExistingData(documentType)) {
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, null, null));
			return result;
		}

		logger.info("check file image");
		if (!ValidateUtil.checkImageFile(uploadfile.getInputStream())) {
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA,
					errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "file upload wrong format or corupt", null));
			return result;
		}

		Kyc kyc = kycRepository.findByAccount(user);
		if (kyc == null) {
			logger.error("kyc from userId : '" + user.getId() + "' not found");
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, null, null));
			return result;
		}

		DocumentType docType = documentTypeRepository.findByCodeAndRowStatus(documentType, true);
		if (docType == null) {
			logger.error("docType from type : '" + documentType + "' not found");
			result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "type document", null));
			return result;
		}

        GlobalParameter globalPath = globalParameterRepository.findByName(ConstantUtil.GLOBAL_PARAM_CUSTOMER_FILE_PATH);
            if (globalPath == null) {
            result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
            result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, null, null));
            return result;
        }

        String filename = uploadfile.getOriginalFilename();
        Long fileSize = uploadfile.getSize();

        String fileNameToDb = kyc.getPortalcif() + "_" + System.currentTimeMillis() + "_" + documentType;
        String tmpFilePath = System.getProperty("user.dir") + "/" +fileNameToDb;
        String filepath = Paths.get(globalPath.getValue(), fileNameToDb).toString();

        String contentType = uploadfile.getContentType();
        Integer version = 0;
        CustomerDocument doc = null;

		List<CustomerDocument> actExist = customerDocumentRepository
				.findTop1ByDocumentTypeAndUserOrderByCreatedOnDesc(docType.getCode(), user);
		if (actExist != null && !actExist.isEmpty()) {
			doc = actExist.get(0);
			if (doc.getRowStatus()) {
				version = doc.getVersion() + 1;
				doc = null;
			}
		}

        if (doc == null) {
            doc = new CustomerDocument();
            doc.setFileKey(UUID.randomUUID().toString());
            doc.setDocumentType(docType.getCode());
            doc.setSourceType(CustomerEnum._CUSTOMER.getName());
            if (user.getUserStatusSebelumnya().equals("ACT") || user.getUserStatusSebelumnya().equals("REG")) {
                doc.setRowStatus(false);
            } else {
                doc.setRowStatus(true);
            }
        }

        doc.setFileName(filename);
        doc.setUser(user);
        doc.setFileLocation(filepath);

        uploadfile.transferTo(new File(tmpFilePath));
        attachFileService.uploadToAwsS3(tmpFilePath, doc.getFileLocation());

        doc.setFileType(contentType);
        doc.setFileSize(fileSize);
        doc.setCreatedBy(user.getUsername());
        doc.setCreatedOn(new Date());
        doc.setVersion(version);
        doc.setEndedOn(DateTimeUtil.convertStringToDateCustomized("9999-12-31", DateTimeUtil.API_MCW));
        doc = customerDocumentRepository.saveAndFlush(doc);

        File temp = new File(tmpFilePath);
        if(temp.isFile()){
            temp.delete();
        }

        kyc = validateStatusUser(kyc);

        Map data = new HashMap();
        data.put(ConstantUtil.KYC, kyc);
        data.put(ConstantUtil.DOCUMENT, doc);
        data.put(ConstantUtil.TYPE, docType.getDescription());

		result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
		result.put(ConstantUtil.DATA, data);

		return result;
	}

	@Override
	public Map profileView(User user) {
		Kyc kyc = kycRepository.findByAccount(user);
		System.out.println("KYC LEWAT");

		String nationality = "";
		if (kyc.getNationality() != null) {
			Countries nat = countriesRepository.findById(Long.valueOf(kyc.getNationality()));
			if (nat != null) {
				nationality = nat.getAlpha3Code();
			}
		}
		System.out.println("nationality LEWAT");

		String legalCountry = "";
		if (kyc.getLegalCountry() != null) {
			Countries leg = countriesRepository.findById(Long.valueOf(kyc.getLegalCountry()));
			if (leg != null) {
				legalCountry = leg.getAlpha3Code();
			}
		}
		System.out.println("legalCountry LEWAT");

		String homeCountry = "";
		if (kyc.getHomeCountry() != null) {
			Countries home = countriesRepository.findById(Long.valueOf(kyc.getHomeCountry()));
			if (home != null) {
				homeCountry = home.getAlpha3Code();
			}
		}
		System.out.println("homeCountry LEWAT");

		String legalCity = "";
		if (kyc.getLegalCity() != null) {
			Cities legalcity = citiesRepository.findById(Long.valueOf(kyc.getLegalCity()));
			if (legalcity != null) {
				legalCity = legalcity.getCityCode();
			}
		}
		System.out.println("legalCity LEWAT");

		String homeCity = "";
		if (kyc.getHomeCity() != null) {
			Cities homecity = citiesRepository.findById(Long.valueOf(kyc.getHomeCity()));
			if (homecity != null) {
				homeCity = homecity.getCityCode();
			}
		}

		System.out.println("homeCity LEWAT");

		SettlementAccounts account = settlementAccountsRepository.findByKycs(kyc);

		System.out.println("LEWAT");

		Map dataGeneral = new HashMap();
		dataGeneral.put("first_name", kyc.getFirstName());
		dataGeneral.put("last_name", kyc.getLastName());
		dataGeneral.put("phone_number", kyc.getMobileNumber());
		dataGeneral.put("no_kmiln", kyc.getNoKmiln());
		if (kyc.getIssueDateKmiln() != null) {
			dataGeneral.put("tgl_terbit",
					DateTimeUtil.convertDateToStringCustomized(kyc.getIssueDateKmiln(), DateTimeUtil.API_MCW));
		}

		Map dataLegal = new HashMap();
		dataLegal.put("country", legalCountry);
		dataLegal.put("province", kyc.getLegalProvince());
		dataLegal.put("city", legalCity);
		dataLegal.put("postal_code", kyc.getLegalPostalCode());
		dataLegal.put("address", kyc.getLegalAddress());
		dataLegal.put("phone", kyc.getLegalPhoneNumber());

		Map dataMailing = new HashMap();
		dataMailing.put("country", homeCountry);
		dataMailing.put("province", kyc.getHomeProvince());
		dataMailing.put("city", homeCity);
		dataMailing.put("postal_code", kyc.getHomePostalCode());
		dataMailing.put("address", kyc.getHomeAddress());
		dataMailing.put("phone", kyc.getHomePhoneNumber());

		Map dataKyc = new HashMap();
		dataKyc.put("birth_date", DateTimeUtil.convertDateToStringCustomized(kyc.getBirthDate(), DateTimeUtil.API_MCW));
		dataKyc.put("birth_place", kyc.getBirthPlace());
		dataKyc.put("gender", kyc.getGender());

		dataKyc.put("email", kyc.getEmail());
		dataKyc.put("nationality", nationality);
		dataKyc.put("marital_status", kyc.getMaritalStatus());
		dataKyc.put("mother_maiden_name", kyc.getMotherMaidenName());
		dataKyc.put("annual_income", kyc.getTotalIncomePa());
		dataKyc.put("education_background", kyc.getEducationBackground());
		dataKyc.put("religion", kyc.getReligion());
		dataKyc.put("statement_type", kyc.getPreferredMailingAddress());
		dataKyc.put("occupation", kyc.getOccupation());
		dataKyc.put("business_nature", kyc.getNatureOfBusiness());
		dataKyc.put("id_number", kyc.getIdNumber());
		dataKyc.put("id_expiration",
				DateTimeUtil.convertDateToStringCustomized(kyc.getIdExpirationDate(), DateTimeUtil.DATE_TIME_MCW));
		dataKyc.put("legal", dataLegal);
		dataKyc.put("mailing", dataMailing);
		dataKyc.put("income_source", kyc.getSourceOfIncome());
		dataKyc.put("total_asset", kyc.getTotalAsset());
		dataKyc.put("investment_purpose", kyc.getInvestmentPurpose());
		dataKyc.put("investment_experience", kyc.getInvestmentExperience());

		if (account != null) {
			dataKyc.put("settlement_bank", account.getBankId().getBankCode());
			dataKyc.put("settlement_account_name", account.getSettlementAccountName());
			dataKyc.put("settlement_account_no", account.getSettlementAccountNo());
		}

		if ("IE04".equals(kyc.getInvestmentExperience())) {
			dataKyc.put("other_investment_experience", kyc.getOtherInvestmentExperience());
		}

		dataKyc.put("referralCode", kyc.getReferralCode());
		if (kyc.getReferralCus() != null) {
			dataKyc.put("referrerName", kyc.getReferralName());
			dataKyc.put("referrerEmail", kyc.getReferralCus().getEmail());
			dataKyc.put("referrerCode", kyc.getReferralCus().getReferralCode());
		}

		List listFatca = new ArrayList();
		Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));
		if (questionairesFatca != null) {
			List<Question> listQuestionFatca = questionRepository
					.findAllByQuestionairesOrderBySeqAsc(questionairesFatca);

			for (Question question : listQuestionFatca) {
				List listAnswers = new ArrayList();
				List<CustomerAnswer> listFatcaAnswer = customerAnswerRepository
						.findAllByKycAndQuestionOrderByCreatedDateAsc(kyc, question);
				for (CustomerAnswer customerAnswer : listFatcaAnswer) {
					listAnswers.add(customerAnswer.getAnswer().getAnswerName());
				}

				if (!listAnswers.isEmpty()) {
					Map data = new HashMap();
					data.put("question", question.getQuestionName());
					data.put("answer", listAnswers);
					listFatca.add(data);
				}
			}
		}

		List listRisk = new ArrayList();
		Questionaires questionairesRisk = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
		if (questionairesRisk != null) {
			List<Question> listQuestionRisk = questionRepository
					.findAllQuestionByQuestionairesWithQuery(questionairesRisk);

			for (Question question : listQuestionRisk) {
				List listAnswers = new ArrayList();
				List<CustomerAnswer> listFatcaAnswer = customerAnswerRepository
						.findAllByKycAndQuestionOrderByCreatedDateAsc(kyc, question);
				for (CustomerAnswer customerAnswer : listFatcaAnswer) {
					listAnswers.add(customerAnswer.getAnswer().getAnswerName());
				}

				if (!listAnswers.isEmpty()) {
					Map data = new HashMap();
					data.put("question", question.getQuestionName());
					data.put("answer", listAnswers);
					listRisk.add(data);
				}
			}
		}

		Map dataCustomer = new HashMap();
		dataCustomer.put("general", dataGeneral);
		dataCustomer.put("kyc", dataKyc);
		dataCustomer.put("fatca", listFatca);
		dataCustomer.put("risk_profile", listRisk);

		List<CustomerDocument> custDocKTPs = customerDocumentRepository
				.findTop1ByDocumentTypeAndUserOrderByCreatedOnDesc("DocTyp01", user);
		List<CustomerDocument> custDocTTDs = customerDocumentRepository
				.findTop1ByDocumentTypeAndUserOrderByCreatedOnDesc("DocTyp05", user);

		String ktp = null;
		String ttd = null;

		if (custDocKTPs != null && !custDocKTPs.isEmpty()) {
			ktp = custDocKTPs.get(0).getFileKey();
		}
		if (custDocTTDs != null && !custDocTTDs.isEmpty()) {
			ttd = custDocTTDs.get(0).getFileKey();
		}

		Map cusDoc = new HashMap();
		cusDoc.put("id_card_image", ktp);
		cusDoc.put("signature_image", ttd);

		Map dataScore = new HashMap();
		if (kyc.getRiskProfile() != null) {
			dataScore.put("code", kyc.getRiskProfile().getScoreCode());
			dataScore.put("value", kyc.getRiskProfile().getScoreName());
		}

		Map dataProfile = new HashMap();
		dataProfile.put("customer_id", kyc.getPortalcif());
		dataProfile.put("customer_status", user.getUserStatus());
		dataProfile.put("customer_status_before", user.getUserStatusSebelumnya());
		dataProfile.put("customer_document", cusDoc);
		if (kyc.getRiskProfile() != null) {
			dataProfile.put("customer_risk_profile", dataScore);
		}
		dataProfile.put("customer_data", dataCustomer);

		Map data = new HashMap();
		data.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
		data.put(ConstantUtil.DATA, dataProfile);

		return data;
	}

	@Override
	public Map preRegisterAndOrder(Map map) {
		try {

			Map kyc = (Map) map.get("kyc");
			Map photo = (Map) kyc.get("photo");
			Map midCard = (Map) photo.get("id_card");
			Map mselfie = (Map) photo.get("selfie");
			Map order = (Map) map.get("order");

			String referral = null;
			if (kyc.get("referralCode") != null && !kyc.get("referralCode").toString().trim().equals("")) {
				Map isReferralExistRequest = new LinkedHashMap();
				isReferralExistRequest.put("referralCode", kyc.get("referralCode"));
				isReferralExistRequest.put("agentCode", map.get("agent").toString());
				if (!this.isReferralExist(isReferralExistRequest)) {
					return errorResponse(13, "kode referral tidak di temukan", null);
				} else {
					referral = kyc.get("referralCode").toString();
				}
			}

			Agent agent = agentRepository.findByCodeAndRowStatus(map.get("agent").toString(), true);

			String customerId = kyc.get("customer").toString();
			if (org.springframework.util.StringUtils.containsWhitespace(customerId)) {
				return errorResponse(11, "kyc.customer contains whitespace", null);
			}

			String firstName = kyc.get("first_name").toString();

			String middleName = "";
			if (kyc.get("middle_name") != null) {
				middleName = kyc.get("middle_name").toString();
			}

			String lastName = null;
			if (kyc.get("last_name") != null) {
				lastName = kyc.get("last_name").toString();
			}
			String idCard = kyc.get("id_card").toString();
			String email = kyc.get("email").toString();
			String phone = kyc.get("phone_number").toString();
			String channelOtp = map.get("type_otp").toString();
			String valueOtp = map.get("otp").toString();
			String stan = map.get("stan").toString();

			User user = userRepository.findByAgentChannelAndChannelCustomerWithQuery(agent, customerId);
			if (user != null) {
				return errorResponse(13, "customerId : " + customerId, null);
			}

      List<Kyc> valids = kycRepository.findAllByAccountAgentAndEmail(agent, email);
      for (Kyc valid : valids) {
        if (valid != null) {
          return errorResponse(13, "invalid " + email + " is already exists", null);
        }
      }

	List<Kyc> validasis = kycRepository.findAllByAccountAgentAndMobileNumber(agent, phone);
	for (Kyc validasi : validasis) {
        if (validasi != null) {
          return errorResponse(13, "invalid " + phone + " is already exists", null);
        }
    }

			byte[] content1 = Base64.decodeBase64(midCard.get("content").toString());
			byte[] content2 = Base64.decodeBase64(mselfie.get("content").toString());

			String ext1 = midCard.get("extention").toString();
			String ext2 = mselfie.get("extention").toString();

			Kyc kycx = new Kyc();
			kycx.setFirstName(firstName);
			kycx.setMiddleName(middleName);
			kycx.setLastName(lastName);

			kycx.setEmail(email);
			kycx.setMobileNumber(phone);

            boolean val = otpService.validate(channelOtp, kycx, stan, valueOtp);
            if (!val) {
                return errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "pre_register", "wrong token");
            }

			List<FileDto> fileDtos = new ArrayList<>();

			FileDto fileDto1 = new FileDto();
			fileDto1.setContent(content1);
			fileDto1.setDocumentType("DocTyp01");
			fileDto1.setExtention(ext1);
			fileDtos.add(fileDto1);

			FileDto fileDto2 = new FileDto();
			fileDto2.setContent(content2);
			fileDto2.setDocumentType("DocTyp05");
			fileDto2.setExtention(ext2);
			fileDtos.add(fileDto2);

      //bypass untuk validasi approve officer, karena tidak membutuhkan foto ttd
      FileDto fileDto3 = new FileDto();
      fileDto3.setContent(Base64.decodeBase64("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77W3W09bzxup0Wylbkj9laUz1UM5quaJt1uI2qmx/RkruduOT3999fS6byH2N6lpamqPojr3pt6b4j9r8b+o/L7lZuwj01k+4fb9ySZk2Elr1zhi7fh8bPjh1Psod/nI1+LtEVI7Qw42fQbl+mekRvvVTW/iualieKq2je6XSqeGc0nG/QNraq/X1OwRo+95r129lOll4Rauf+lM9VB5xh/YaTmSbu2VOWdt5Oc166/Zyak39a4U/w59O6D4S80XsLXhXBatW7Xim0/50LKJqwdUa3e3L5Z5lU4f3AdfE7eaXd4fIZWvjvf5kn2emflxypm+9E4Olv6lr6XfQRn55kzX2C4vUSbuea/P0+J6uCxJbU12fFY+Qn7KtINtdF9kX86Z/mERbd/Un6fPp/jHdM+u7ZCGY+rmZOSYTSbuUQHV2vG6fdn819foWX34/stvf+7/fEOzy37465+/+ar/6+N8+vSp/9MXXzRP+93///Off/76a/P/f//731988csvv3QfbA+2n765/BEAIkufsD29wQECBPf2JPj625/7oDvmNJkdAF6z62tTX32VPet++vHrN3/xshQc6L/6/5/aNz/96+b3sv71k9QOAIf59OHH77/+8ssv37z48suvv//Qf7bx8deX19je/lFqhwOEeMUdAHigTx++/1P9e99P7z7+68/dC2fJG2W83g7HCPGKOwDwKO1bYLLUnn7/+5dfLz/I9cWHf778PJrX2+EggjsAMOrD9y9vXO+8ff/xu/6PreeQfs3tT+/+KrfDMQR3AGDEh+/zX+z29O6PX/z9Jcg/vX1/eUvMpx//9vLrZH64vHMG2J/3uAMAVenviSk9vX33j58uGT15mHe3w5G84g4AVCW/J6aT/puB/xqm9ubTUjscSXAHAKre/G74a5gzeWrvf70McBTBHQCo+uoP32XJ/eVXyLQ+/fj9c2p/evteaoc78B53AGBU8T73JqP/8Mf//PNvf3n+/ZDNB/7xk38CEe5CcAcApnz69OHvf/rbz79c43vj6entdz/89Q/ffCWzw90I7gAAEID3uAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACC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HSAW73EHeG2K97k3Gf2HP/7nn397+Z3uzQf+8dM3QjtAMII7wCv06dOHv//pbz//co3vjaent9/98Nc/fPOVzA4QkeAOAAABeI87AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAwOl98cX/D/qOo5QnGTVwAAAAAElFTkSuQmCC"));
      fileDto3.setDocumentType("DocTyp03");
      fileDto3.setExtention("png");
      fileDtos.add(fileDto3);

      String password;
      String passwordConfirm;
      try {
        password = map.get("password").toString();
        passwordConfirm = map.get("passwordConfirm").toString();
      } catch (Exception ignored) {
        password = null;
        passwordConfirm = null;
      }

			if (password != null && passwordConfirm != null) {
				if (password.compareTo(passwordConfirm) != 0) {
					return errorResponse(88, "pre_register", "Konfirmasi password tidak sama");
				}
			} else if ((password != null && passwordConfirm == null) || (password == null && passwordConfirm != null)) {
				return errorResponse(88, "pre_register", "Konfirmasi password tidak sama");
			}

      Map result = miniRegisterAndOrder(agent, customerId, firstName, middleName, lastName, idCard,
          email, phone, fileDtos, order, channelOtp, password, referral);
      return result;
    } catch (Exception e) {
      logger.error(e);
      return errorResponse(88, "pre_register", null);
    }
  }

	@Override
	public Map preRegister(Map map) {
		try {
			Agent agent = agentRepository.findByCodeAndRowStatus(map.get("agent").toString(), true);
			Map kyc = (Map) map.get("kyc");
			Map photo = (Map) kyc.get("photo");
			Map midCard = (Map) photo.get("id_card");
			Map mselfie = (Map) photo.get("selfie");

			String customerId = kyc.get("customer").toString();
			String firstName = kyc.get("first_name").toString();
			String middleName = kyc.get("middle_name").toString();
			String lastName = kyc.get("last_name").toString();
			String idCard = kyc.get("id_card").toString();
			String email = kyc.get("email").toString();
			String phone = kyc.get("phone_number").toString();

			byte[] content1 = Base64.decodeBase64(midCard.get("content").toString());
			byte[] content2 = Base64.decodeBase64(mselfie.get("content").toString());

			String ext1 = midCard.get("extention").toString();
			String ext2 = mselfie.get("extention").toString();

			List<FileDto> fileDtos = new ArrayList<>();

			FileDto fileDto1 = new FileDto();
			fileDto1.setContent(content1);
			fileDto1.setDocumentType("DocTyp01");
			fileDto1.setExtention(ext1);
			fileDtos.add(fileDto1);

			FileDto fileDto2 = new FileDto();
			fileDto2.setContent(content2);
			fileDto2.setDocumentType("DocTyp03");
			fileDto2.setExtention(ext2);
			fileDtos.add(fileDto2);

			Kyc kycx = miniRegister(agent, customerId, firstName, middleName, lastName, idCard, email, phone, fileDtos,
					null, map.get("password").toString());
			User user = kycx.getAccount();

			System.out.println("user : " + user);
			System.out.println("kyc : " + kycx);

			Map dataScore = new HashMap();
			dataScore.put("code", kycx.getRiskProfile().getScoreCode());
			dataScore.put("value", kycx.getRiskProfile().getScoreName());

			Map data = new HashMap();
			data.put("customer_key", user.getCustomerKey());
			data.put("customer_cif", kycx.getPortalcif());
			data.put("channel_customer", user.getChannelCustomer());
			data.put("customer_status", user.getUserStatus());
			data.put("customer_risk_profile", dataScore);

			return errorResponse(0, "pre_register", data);
		} catch (Exception e) {
			logger.error(e);
			return errorResponse(88, "pre_register", null);
		}

	}

	@Override
	@Transactional
	public Map profileRegister(Map map) {
		Agent agent = agentRepository.findByCodeAndRowStatus(map.get("agent").toString(), true);
		if (agent == null) {
			return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "agent", null);
		}

		if (!isExistingData(map.get("risk_profile"))) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "risk_profile", null);
		}

		if (!isExistingData(map.get("fatca"))) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "fatca", null);
		}

		Kyc kyc = null;
		Score riskProfile;
		Integer status;

		String password;
		String passwordConfirm;
		try {
			password = map.get("password").toString();
			passwordConfirm = map.get("passwordConfirm").toString();
		} catch (Exception ignored) {
			password = null;
			passwordConfirm = null;
		}

		if (password != null && passwordConfirm != null) {
			if (password.compareTo(passwordConfirm) != 0) {
				return errorResponse(88, "profile_register", "Konfirmasi password tidak sama");
			}
		} else if ((password != null && passwordConfirm == null) || (password == null && passwordConfirm != null)) {
			return errorResponse(88, "profile_register", "Konfirmasi password tidak sama");
		}

		String emailKey = ((String) map.get("customer")).toLowerCase();
		if (password == null) {
			emailKey += "@" + agent.getCode().toLowerCase() + "." + agent.getChannel().getCode().toLowerCase();
		}
		User user = userRepository.findByEmail(emailKey);
		if (user == null) {
			Long score;
			SettlementAccounts accounts;

			Map fields = validateFieldProfile(map, kyc, agent);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				kyc = (Kyc) fields.get(ConstantUtil.KYC);
				accounts = (SettlementAccounts) fields.get(ConstantUtil.SETTLEMENT);
			} else {
				return fields;
			}

			logger.info("kyc : " + kyc);
			logger.info("accounts : " + accounts);

			List<CustomerAnswer> customerFatcas;
			List<CustomerAnswer> customerRisk;

			Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));
			List<Map> lists = (List<Map>) map.get("fatca");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerFatcas = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
			} else {
				return fields;
			}

			logger.info("customerFatcas done.");

			questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
			lists = (List<Map>) map.get("risk_profile");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerRisk = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
				score = (Long) fields.get(ConstantUtil.SCORE);
			} else {
				return fields;
			}

			logger.info("customerRisk done.");

			riskProfile = scoreRepository.getScore(score, new Date());

			user = kyc.getAccount();
			user = userRepository.save(user);
			logger.info("save user success");

			kyc.setRiskProfile(riskProfile);
			kyc = kycRepository.save(kyc);
			logger.info("save user kyc success");

			accounts.setKycs(kyc);
			settlementAccountsRepository.save(accounts);
			logger.info("save settlement account success");

			for (CustomerAnswer customerAnswer : customerRisk) {
				customerAnswer.setKyc(kyc);
				customerAnswerRepository.save(customerAnswer);
			}
			logger.info("save customer risk success");

			for (CustomerAnswer customerAnswer : customerFatcas) {
				customerAnswer.setKyc(kyc);
				customerAnswerRepository.save(customerAnswer);
			}
			logger.info("save customer fatcas success");

			SbnSid sbnSid = new SbnSid();
			sbnSid.setCreatedBy(kyc.getAccount().getUsername());
			sbnSid.setCreatedDate(new Date());
			sbnSid.setKyc(kyc);
			sbnSidRepository.saveAndFlush(sbnSid);

			status = ConstantUtil.STATUS_SUCCESS;
		} else {
			kyc = kycRepository.findByAccount(user);
			riskProfile = kyc.getRiskProfile();
			status = ConstantUtil.STATUS_EXISTING_DATA;
		}

		Map dataScore = new HashMap();
		dataScore.put("code", riskProfile.getScoreCode());
		dataScore.put("value", riskProfile.getScoreName());

		Map data = new HashMap();
		data.put("customer_key", user.getCustomerKey());
		data.put("customer_cif", kyc.getPortalcif());
		data.put("channel_customer", user.getChannelCustomer());
		data.put("customer_status", user.getUserStatus());
		data.put("customer_risk_profile", dataScore);

		return errorResponse(status, "profile_register", data);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map profileRegisterVer2(Map map) throws MessagingException, IOException, TemplateException {
		Agent agent = agentRepository.findByCodeAndRowStatus(map.get("agent").toString(), true);
		if (agent == null) {
			return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "agent", null);
		}

		if (!isExistingData(map.get("risk_profile"))) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "risk_profile", null);
		}

		if (!isExistingData(map.get("fatca"))) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "fatca", null);
		}

		Kyc kyc = null;
		Score riskProfile;
		String status;

		String password = null;
		String passwordConfirm = null;
		if (map.get("password") != null) {
			password = map.get("password").toString();
		}

		if (map.get("passwordConfirm") != null) {
			passwordConfirm = map.get("passwordConfirm").toString();
		}

		if (password != null && passwordConfirm != null) {
			if (password.compareTo(passwordConfirm) != 0) {
				return errorResponse(88, "profile_register", "Konfirmasi password tidak sama");
			}
		} else if ((password != null && passwordConfirm == null) || (password == null && passwordConfirm != null)) {
			return errorResponse(88, "profile_register", "Konfirmasi password tidak sama");
		}

		String emailKey = ((String) map.get("customer")).toLowerCase();
		if (password == null) {
			emailKey += "@" + agent.getCode().toLowerCase() + "." + agent.getChannel().getCode().toLowerCase();
		}
		User user = userRepository.findByEmail(emailKey);
		if (user == null) {
			Long score;
			SettlementAccounts accounts;

			Map fields = validateFieldProfile(map, kyc, agent);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				kyc = (Kyc) fields.get(ConstantUtil.KYC);
				accounts = (SettlementAccounts) fields.get(ConstantUtil.SETTLEMENT);
			} else {
				return fields;
			}

			List<CustomerAnswer> customerFatcas;
			List<CustomerAnswer> customerRisk;

			Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));
			List<Map> lists = (List<Map>) map.get("fatca");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerFatcas = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
			} else {
				return fields;
			}

			questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
			lists = (List<Map>) map.get("risk_profile");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerRisk = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
				score = (Long) fields.get(ConstantUtil.SCORE);
			} else {
				return fields;
			}

			riskProfile = scoreRepository.getScore(score, new Date());

			user = kyc.getAccount();
			user.setUserStatus("REG");
			user.setUserStatusSebelumnya(null);
			user.setResetCode(generateCodeActivation());

			user = userRepository.saveAndFlush(user);

			kyc.setRiskProfile(riskProfile);
			kyc = kycRepository.saveAndFlush(kyc);

			accounts.setKycs(kyc);
			settlementAccountsRepository.saveAndFlush(accounts);

			for (CustomerAnswer customerAnswer : customerRisk) {
				customerAnswer.setKyc(kyc);
				customerAnswerRepository.saveAndFlush(customerAnswer);
			}

			for (CustomerAnswer customerAnswer : customerFatcas) {
				customerAnswer.setKyc(kyc);
				customerAnswerRepository.saveAndFlush(customerAnswer);
			}

			status = ConstantUtil.STATUS_SUCCESS.toString();
			sendingEmailService.sendActivationEmail(kyc);
		} else {
			kyc = kycRepository.findByAccount(user);
			riskProfile = kyc.getRiskProfile();
			status = ConstantUtil.STATUS_EXISTING_DATA.toString();
		}

		Map dataScore = new HashMap();
		dataScore.put("code", riskProfile.getScoreCode());
		dataScore.put("value", riskProfile.getScoreName());

		Map data = new HashMap();
		data.put("customer_key", user.getCustomerKey());
		data.put("customer_cif", kyc.getPortalcif());
		data.put("channel_customer", user.getChannelCustomer());
		data.put("customer_status", user.getUserStatus());
		data.put("customer_risk_profile", dataScore);

		return errorResponse(status, "profile_register", data);
	}

	@SuppressWarnings("ALL")
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map profileRegisterV2(Map map) throws Exception {
		Map customerDocument = new LinkedHashMap();
		Agent agent = agentRepository.findByCodeAndRowStatus(map.get("agent").toString(), true);
		if (agent == null) {
			return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "agent", null);
		}

		List<Map> listFile = (List<Map>) map.get("file_upload");
		Boolean ktp = false;
		Boolean ttd = false;
		Boolean selfie = false;
		List<FileDto> fileDtos = new ArrayList<>();
		for (Map fileUpload : listFile) {
			if (fileUpload.get("content") == null || fileUpload.get("document_type") == null
					|| fileUpload.get("extention") == null) {
				return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "file_upload", null);
			}

			if (!(Boolean) ValidateUtil.checkBase64(fileUpload.get("content").toString())) {
				return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "content file_upload", null);
			}

			byte[] content = Base64.decodeBase64(fileUpload.get("content").toString());
			String type = fileUpload.get("document_type").toString();
			String ext = fileUpload.get("extention").toString();

			if (type.equals("DocTyp01")) {
				ktp = true;
			} else if (type.equals("DocTyp03")) {
				ttd = true;
			} else if (type.equals("DocTyp05")) {
				selfie = true;
			}

			FileDto fileDto = new FileDto();
			fileDto.setContent(content);
			fileDto.setDocumentType(type);
			fileDto.setExtention(ext);
			fileDtos.add(fileDto);
		}

		if (!ktp) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "file_upload ktp", null);
		}

		if (!ttd) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "file_upload ttd", null);
		}

		if (!selfie) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "file_upload selfie", null);
		}

		Kyc kyc = null;
		Score riskProfile;
		Integer status;

		String emailKey = ((String) map.get("customer")).toLowerCase();
		emailKey += "@" + agent.getCode().toLowerCase() + "." + agent.getChannel().getCode().toLowerCase();

		User user = userRepository.findByEmail(emailKey);
		if (user == null) {
			Long score;
			SettlementAccounts accounts;

			Map fields = validateFieldProfile(map, kyc, agent);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				kyc = (Kyc) fields.get(ConstantUtil.KYC);
				accounts = (SettlementAccounts) fields.get(ConstantUtil.SETTLEMENT);
			} else {
				return fields;
			}

			logger.info("kyc : " + kyc);
			logger.info("accounts : " + accounts);

			List<CustomerAnswer> customerFatcas;
			List<CustomerAnswer> customerRisk;

			Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));
			List<Map> lists = (List<Map>) map.get("fatca");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerFatcas = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
			} else {
				return fields;
			}

			logger.info("customerFatcas done.");

			questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
			lists = (List<Map>) map.get("risk_profile");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerRisk = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
				score = (Long) fields.get(ConstantUtil.SCORE);
			} else {
				return fields;
			}

			logger.info("customerRisk done.");

			riskProfile = scoreRepository.getScore(score, new Date());

			user = kyc.getAccount();
			user.setUserStatus(ConstantUtil.USER_STATUS_PENDING);
			user.setUserStatusSebelumnya(ConstantUtil.USER_STATUS_ACTIVATED);
			user = userRepository.save(user);
			logger.info("save user success");

			kyc.setRiskProfile(riskProfile);
			kyc = kycRepository.save(kyc);
			logger.info("save user kyc success");

			accounts.setKycs(kyc);
			settlementAccountsRepository.save(accounts);
			logger.info("save settlement account success");

			for (CustomerAnswer customerAnswer : customerRisk) {
				customerAnswer.setKyc(kyc);
				customerAnswerRepository.save(customerAnswer);
			}
			logger.info("save customer risk success");

			for (CustomerAnswer customerAnswer : customerFatcas) {
				customerAnswer.setKyc(kyc);
				customerAnswerRepository.save(customerAnswer);
			}
			logger.info("save customer fatcas success");

			Map dataUpload = uploadDocument(kyc, fileDtos);
			if ((int) dataUpload.get("code") != 0) {
				throw new Exception("Failed upload customer documents");
			}

			dataUpload = (Map) dataUpload.get("data");
			for (Map customerDoc : (List<Map>) dataUpload.get(ConstantUtil.DOCUMENT)) {
				if ("DocTyp01".equals(customerDoc.get("type").toString())) {
					customerDocument.put("id_card_image", customerDoc.get("key"));
				} else if ("DocTyp03".equals(customerDoc.get("type").toString())) {
					customerDocument.put("signature_image", customerDoc.get("key"));
				} else if ("DocTyp05".equals(customerDoc.get("type").toString())) {
					customerDocument.put("selfie_image", customerDoc.get("key"));
				}
			}

			SbnSid sbnSid = new SbnSid();
			sbnSid.setCreatedBy(kyc.getAccount().getUsername());
			sbnSid.setCreatedDate(new Date());
			sbnSid.setKyc(kyc);
			sbnSidRepository.saveAndFlush(sbnSid);

			status = ConstantUtil.STATUS_SUCCESS;
		} else {
			kyc = kycRepository.findByAccount(user);
			riskProfile = kyc.getRiskProfile();
			status = ConstantUtil.STATUS_EXISTING_DATA;
		}

		Map dataScore = new HashMap();
		dataScore.put("code", riskProfile.getScoreCode());
		dataScore.put("value", riskProfile.getScoreName());

		Map data = new HashMap();
		data.put("customer_key", user.getCustomerKey());
		data.put("customer_cif", kyc.getPortalcif());
		data.put("channel_customer", user.getChannelCustomer());
		data.put("customer_status", user.getUserStatus());
		data.put("customer_risk_profile", dataScore);
		data.put("customer_document", customerDocument);

		return errorResponse(status, "profile_register and upload_document", data);
	}

	@Override
	@Transactional
	public Map profileRegisterCustomer(Map map) {
		Agent agent = agentRepository.findByCodeAndRowStatus(map.get("agent").toString(), true);
		if (agent == null) {
			return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "agent", null);
		}

		if (!isExistingData(map.get("risk_profile"))) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "risk_profile", null);
		}

		if (!isExistingData(map.get("fatca"))) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "fatca", null);
		}

		Kyc kyc = null;
		Score riskProfile;
		Integer status;

		String password;
		String passwordConfirm;
		try {
			password = map.get("password").toString();
			passwordConfirm = map.get("passwordConfirm").toString();
		} catch (Exception ignored) {
			password = null;
			passwordConfirm = null;
		}

		if (password != null && passwordConfirm != null) {
			if (password.compareTo(passwordConfirm) != 0) {
				return errorResponse(88, "profile_register", "Konfirmasi password tidak sama");
			}
		} else if ((password != null && passwordConfirm == null) || (password == null && passwordConfirm != null)) {
			return errorResponse(88, "profile_register", "Konfirmasi password tidak sama");
		}

		String emailKey = ((String) map.get("customer")).toLowerCase();
		if (password == null) {
			emailKey += "@" + agent.getCode().toLowerCase() + "." + agent.getChannel().getCode().toLowerCase();
		}
		String emailUserKey = emailKey;
		// uuid@agentCode.channelCode
		boolean insert = false; // false -> update
		Kyc kycUser = kycRepository.findByEmail(emailKey);
		User user = null;
		if (kycUser == null) {
			insert = true;
		} else {
			user = kycUser.getAccount();
			if (user == null) {
				insert = true;
			} else if (user.getAgent().getId().equals(agent.getId()) && user.getUserStatus().equalsIgnoreCase("ACT")
					&& user.getUserStatusSebelumnya().equalsIgnoreCase("REG")) {
				return errorResponse(88, "customer_profile_register",
						"Update tidak dapat dilakukan untuk user dengan status active");
			} else if (user.getAgent().getId().equals(agent.getId()) && user.getUserStatus().equalsIgnoreCase("PEN")) {
				return errorResponse(88, "profile_register",
						"Update tidak dapat dilakukan untuk user dengan status pending");
			} else if (user.getAgent().getId().equals(agent.getId()) && !user.getUserStatus().equalsIgnoreCase("PEN")) {
				insert = false;
			} else if (!user.getAgent().getId().equals(agent.getId())
					&& !user.getAgent().getChannel().getId().equals(agent.getChannel().getId())) {
				insert = true;
				emailUserKey = UUID.randomUUID().toString().replace("-", "") + "@" + agent.getCode().toLowerCase() + "."
						+ agent.getChannel().getCode().toLowerCase();
				if (emailUserKey.length() > 50) {
					emailUserKey = emailUserKey.substring(emailUserKey.length() - 50);
				}
			} else if (!user.getAgent().getId().equals(agent.getId())
					&& user.getAgent().getChannel().getId().equals(agent.getChannel().getId())) {
				return errorResponse(88, "profile_register", "Client ini sudah terdaftar di agent lain");
			} else {
				return errorResponse(88, "profile_register", "Tedapat masalah, silahkan hubungi customer service");
			}
		}
		map.put("emailUserKey", emailUserKey);
		/*
		 * (user.getAgent() == agentLogin && user.status != PEN) -> update
		 */
		String tokenUser = "";
		if (insert) { // insert
			Long score;
			SettlementAccounts accounts;

			Map fields = validateFieldProfile(map, kyc, agent);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				kyc = (Kyc) fields.get(ConstantUtil.KYC);
				accounts = (SettlementAccounts) fields.get(ConstantUtil.SETTLEMENT);
			} else {
				return fields;
			}

			logger.info("kyc : " + kyc);
			logger.info("accounts : " + accounts);

			List<CustomerAnswer> customerFatcas;
			List<CustomerAnswer> customerRisk;

			Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));
			List<Map> lists = (List<Map>) map.get("fatca");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerFatcas = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
			} else {
				return fields;
			}

			logger.info("customerFatcas done.");

			questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
			lists = (List<Map>) map.get("risk_profile");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerRisk = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
				score = (Long) fields.get(ConstantUtil.SCORE);
			} else {
				return fields;
			}

			logger.info("customerRisk done.");

			riskProfile = scoreRepository.getScore(score, new Date());

			user = kyc.getAccount();

			tokenUser = user.generateNewToken("INVISEE");
			user = userRepository.save(user);
			logger.info("save user success");

			kyc.setRiskProfile(riskProfile);
			kyc.setFlagEmail(true);
			kyc = kycRepository.save(kyc);
			logger.info("save user kyc success");

			accounts.setKycs(kyc);
			settlementAccountsRepository.save(accounts);
			logger.info("save settlement account success");

			for (CustomerAnswer customerAnswer : customerRisk) {
				customerAnswer.setKyc(kyc);
				customerAnswerRepository.save(customerAnswer);
			}
			logger.info("save customer risk success");

			for (CustomerAnswer customerAnswer : customerFatcas) {
				customerAnswer.setKyc(kyc);
				customerAnswerRepository.save(customerAnswer);
			}
			logger.info("save customer fatcas success");

			status = ConstantUtil.STATUS_SUCCESS;
		} else { // update
			kyc = kycRepository.findByAccount(user);
			SettlementAccounts accounts;
			Long score;

			Map fields = validateFieldProfile(map, kyc, agent);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				kyc = (Kyc) fields.get(ConstantUtil.KYC);
				accounts = (SettlementAccounts) fields.get(ConstantUtil.SETTLEMENT);
			} else {
				return fields;
			}

			logger.info("kyc : " + kyc);
			logger.info("accounts : " + accounts);

			List<CustomerAnswer> customerFatcas;
			List<CustomerAnswer> customerRisk;

			Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));
			List<Map> lists = (List<Map>) map.get("fatca");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerFatcas = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
			} else {
				return fields;
			}

			logger.info("customerFatcas done.");

			questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
			lists = (List<Map>) map.get("risk_profile");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerRisk = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
				score = (Long) fields.get(ConstantUtil.SCORE);
			} else {
				return fields;
			}

			logger.info("customerRisk done.");
			riskProfile = scoreRepository.getScore(score, new Date());

			kyc.setRiskProfile(riskProfile);
			kyc = kycRepository.save(kyc);
			logger.info("save user kyc success");

			settlementAccountsRepository.save(accounts);
			logger.info("save settlement account success");

			tokenUser = user.generateNewToken("INVISEE");
			if (user.getUserStatus().equalsIgnoreCase("VER")) {
				user.setUserStatus("PEN");
				user.setUserStatusSebelumnya("VER");
				user.setUpdatedDate(new Date());
				user.setUpdatedBy(agent.getCode());
			}
			user = userRepository.save(user);

			for (CustomerAnswer customerAnswer : customerRisk) {
				customerAnswer.setKyc(kyc);
				customerAnswerRepository.save(customerAnswer);
			}
			logger.info("save customer risk success");

			for (CustomerAnswer customerAnswer : customerFatcas) {
				customerAnswer.setKyc(kyc);
				customerAnswerRepository.save(customerAnswer);
			}
			logger.info("save customer fatcas success");

			// riskProfile = kyc.getRiskProfile();
			status = ConstantUtil.STATUS_SUCCESS;
		}

		Map dataScore = new HashMap();
		dataScore.put("code", riskProfile.getScoreCode());
		dataScore.put("value", riskProfile.getScoreName());

		Map data = new HashMap();
		data.put("customer_key", user.getCustomerKey());
		data.put("customer_cif", kyc.getPortalcif());
		data.put("channel_customer", user.getChannelCustomer());
		data.put("customer_status", user.getUserStatus());
		data.put("customer_risk_profile", dataScore);
		data.put("token", tokenUser);
		data.put("email", kyc.getEmail());
		data.put("name", kyc.getFirstName());
		data.put("phone_number", kyc.getMobileNumber());

		return errorResponse(status, "profile_register_customer", data);
	}

	@Override
	public Map profileUpdate(Map map, User user) {
//        logger.info("profile update" + Thread.currentThread().getStackTrace()[2].getLineNumber());
		Map mapData = profileUpdateTransactional(map, user);
		if (mapData.get("code").equals(0)) {
			Kyc kyc = kycRepository.findByAccount(user);
			logger.info("profile " + mapData.get("code"));
			if (kyc.getAccount().getUserStatus().equalsIgnoreCase("PEN")
					&& kyc.getAccount().getUserStatusSebelumnya().equalsIgnoreCase("ACT")) {
				emailService.sendOpenRekening(kyc);
			}
		}
		return mapData;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map profileUpdateSettlement(Map map, User user) throws Exception {
		Kyc kyc = kycRepository.findByAccount(user);
		SettlementAccounts settlementAccounts = settlementAccountsRepository.findByKycs(kyc);
		Map dataKyc = (Map) map.get("kyc");
		Boolean updateKycToAvantrade = false;
		if (isExistingData(dataKyc.get("email"))) {
			if (!kyc.getEmail().equals(dataKyc.get("email").toString().trim())) {
				Kyc isExistKyc = kycRepository.findByEmailAndAccount_Agent_CodeIgnoreCase(
						dataKyc.get("email").toString().trim(), kyc.getAccount().getAgent().getCode());
				if (isExistKyc != null) {
					return errorResponse(14, "profile_update_settlement", "email is already registered");
				}

				updateKycToAvantrade = true;
				kyc.setEmail(dataKyc.get("email").toString().trim());
				kycRepository.saveAndFlush(kyc);
			}
		}

		if (isExistingData(dataKyc.get("settlement_bank"))) {
			Bank bank = bankRepository.findByBankCode(dataKyc.get("settlement_bank").toString());
			if (bank == null) {
				return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "profile_update_settlement", null);
			}

			updateKycToAvantrade = true;
			settlementAccounts.setBankId(bank);
		}

		if (isExistingData(dataKyc.get("settlement_account_name"))) {
			if (settlementAccounts == null) {
				return errorResponse(50, "profile_update_settlement", "settlement account not found");
			}

			updateKycToAvantrade = true;
			settlementAccounts.setSettlementAccountName(dataKyc.get("settlement_account_name").toString().trim());
		}

		if (isExistingData(dataKyc.get("settlement_account_no"))) {
			if (settlementAccounts == null) {
				return errorResponse(50, "profile_update_settlement", "settlement account not found");
			}

			updateKycToAvantrade = true;
			settlementAccounts.setSettlementAccountNo(dataKyc.get("settlement_account_no").toString().trim());
		}

		if (isExistingData(dataKyc.get("settlement_bank")) || isExistingData(dataKyc.get("settlement_account_name"))
				|| isExistingData(dataKyc.get("settlement_account_no"))) {
			settlementAccountsRepository.saveAndFlush(settlementAccounts);
		}

		if (updateKycToAvantrade) {
			avantradeIntegrationService.updateCustomer(kyc);
		}

		Map data = new LinkedHashMap();
		data.put("email", kyc.getEmail());
		data.put("settlement_bank", settlementAccounts.getBankId().getBankCode());
		data.put("settlement_account_name", settlementAccounts.getSettlementAccountName());
		data.put("settlement_account_no", settlementAccounts.getSettlementAccountNo());
		data.put("customer_status", user.getUserStatus());
		data.put("customer_cif", kyc.getPortalcif());

		return errorResponse(ConstantUtil.STATUS_SUCCESS, "profile_update_settlement", data);
	}

	@Transactional
	Map profileUpdateTransactional(Map map, User user) {
		logger.info("##profileUpdateTransactional " + Thread.currentThread().getStackTrace()[2].getLineNumber());
		if (user.getUserStatus().equalsIgnoreCase("PEN") && user.getUserStatusSebelumnya().equalsIgnoreCase("ACT")) {
			return errorResponse(12, "profile_update", "User Pending Status");
		}

		Kyc kyc = kycRepository.findByAccount(user);

		// {"settlementAccountName":"SILVESTER KEVIN DEWANGGA
		// KURNIAWAN","legalPhoneNumber":"00-000-00000","officeCountry":"653","citizenship":"DOM","homePhoneNumber":"00-000-00000","maritalStatus":"SGL","birthPlace":"Kab.
		// Semarang","sourceOfIncome":"REV","religion":"CATH","legalCountry":"653","investmentExperience":"IE05","homeProvince":"ID-JT","motherMaidenName":"CAECILIA
		// ENDANG
		// SUSIATI","homeCity":"1975","investmentPurpose":"PDT","homeAddress":"Lingkungan
		// Sidorejo, RT 002/RW 010 Kel/Desa Bergaslor, Kecamatan
		// Bergas","legalCity":"1975","gender":"ML","birthDate":"21-12-1991","firstName":"SILVESTER","officeCity":"1652","idNumber":"3322132112910000","middleName":"","homePostalCode":"50552","legalProvince":"ID-JT","idExpirationDate":"01-01-2020","lastName":"KURNIAWAN","occupation":"9","settlementAccountNo":"2220476572","totalIncomePa":"INC1","officePhoneNumber":"62-21-3523626","officeProvince":"ID-JK","homeCountry":"653","natureOfBusiness":"4","totalAsset":"TA01","bankId":1,"educationBackground":"BCH","nationality":"653","officePostalCode":"10120","legalPostalCode":"50552","preferredMailingAddress":"2","officeAddress":"Batu
		// Tulis 3","salutation":""}
		String oldKyc = null;
		if (user.getUserStatus().equalsIgnoreCase("VER")) {
			oldKyc = jsonKyc(kyc);
		}

		SettlementAccounts accounts;

		Map fields = validateFieldProfile(map, kyc, user.getAgent());
		if (fields.get(ConstantUtil.STATUS) != null
				&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
			kyc = (Kyc) fields.get(ConstantUtil.KYC);
			accounts = (SettlementAccounts) fields.get(ConstantUtil.SETTLEMENT);
		} else {
			return fields;
		}

		if (isExistingData(map.get("email"))) {
			User findEmail = userRepository.findByEmailAndAgent(map.get("email").toString(), user.getAgent());
			logger.info("email" + findEmail);
			if (findEmail != null) {
				return errorResponse(ConstantUtil.STATUS_EXISTING_DATA, "email existing", null);
			}
		}
		logger.info("kyc : " + kyc);
		logger.info("jsonkyc : " + oldKyc);
		logger.info("accounts : " + accounts);

		List<CustomerAnswer> customerFatcas = new ArrayList<>();
		List<CustomerAnswer> customerRisk = new ArrayList<>();
		Long score;
		List<Map> lists;

		Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));

		if (isExistingData(map.get("fatca"))) {
			lists = (List<Map>) map.get("fatca");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerFatcas = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
			} else {
				return fields;
			}
		}

		if (isExistingData(map.get("risk_profile"))) {
			questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
			lists = (List<Map>) map.get("risk_profile");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerRisk = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
				score = (Long) fields.get(ConstantUtil.SCORE);
			} else {
				return fields;
			}

			Score riskProfile = scoreRepository.getScore(score, new Date());
			kyc.setRiskProfile(riskProfile);
		}

		/*
		 * if (this.checkDoc(kyc).getCompleted()) { user.setUserStatus("PEN");
		 * user.setUserStatusSebelumnya("ACT"); user = userRepository.save(user);
		 * logger.info("save user success"); } else { logger.info("CUSTOMER DENGAN CIF "
		 * + kyc.getPortalcif().toUpperCase() + ", DOKUMENTASI BELUM LENGKAP"); }
		 */

		if (this.checkDoc(kyc).getCompleted()) {
			if (user.getUserStatus().equalsIgnoreCase("VER")) {
				user.setUserStatus("PEN");
				user.setUserStatusSebelumnya("VER");
			} else if (user.getUserStatus().equalsIgnoreCase("PEN")) {
				user.setUserStatus("PEN");
				user.setUserStatusSebelumnya("VER");
			} else {
				user.setUserStatus("PEN");
				user.setUserStatusSebelumnya("ACT");
			}
		} else {
			logger.info("CUSTOMER DENGAN CIF " + kyc.getPortalcif().toUpperCase() + ", DOKUMENTASI BELUM LENGKAP");
		}
		user.setUpdatedDate(new Date());
		user.setUpdatedBy(user.getChannelCustomer() + "@" + user.getAgent().getCode() + ".com");

		// user.setApprovalStatus(true);
		userRepository.save(user);

		kyc.setAccount(user);
		if (oldKyc != null) {
			kyc.setOldValueKyc(oldKyc);
		}

		if (map.get("referralCode") != null) {
			Kyc kycReferral = kycRepository.findByReferralCodeAndAccount_Agent(map.get("referralCode").toString(),
					kyc.getAccount().getAgent());
			if (kycReferral != null) {
				String referralName;
				if (kycReferral.getMiddleName() == null || kycReferral.getMiddleName().trim().equalsIgnoreCase("")) {
					referralName = kycReferral.getFirstName() + " " + kycReferral.getLastName();
				} else {
					referralName = kycReferral.getFirstName() + " " + kycReferral.getMiddleName() + " "
							+ kycReferral.getLastName();
				}

				kyc.setReferral("CUS");
				kyc.setReferralName(referralName);
				kyc.setReferralCus(kycReferral);
			} else {
				return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "referral code", null);
			}
		}

		kyc = kycRepository.save(kyc);
		logger.info("save user kyc success");

		accounts.setKycs(kyc);
		settlementAccountsRepository.save(accounts);
		logger.info("save settlement account success");

		for (CustomerAnswer customerAnswer : customerRisk) {
			customerAnswer.setKyc(kyc);
			customerAnswerRepository.save(customerAnswer);
		}
		logger.info("save customer risk success");

		for (CustomerAnswer customerAnswer : customerFatcas) {
			customerAnswer.setKyc(kyc);
			customerAnswerRepository.save(customerAnswer);
		}
		logger.info("save customer fatcas success");

		CustomerDocument custDocKTP = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user, true,
				"DocTyp01");
		CustomerDocument custDocTTD = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user, true,
				"DocTyp03");

		String ttd = null;
		String ktp = null;
		if (custDocKTP != null) {
			ktp = custDocKTP.getFileKey();
		}
		if (custDocTTD != null) {
			ttd = custDocTTD.getFileKey();
		}

		kyc = validateStatusUser(kyc);
		user = kyc.getAccount();

		Score riskProfile = kyc.getRiskProfile();

		Map dataScore = new HashMap();
		dataScore.put("code", riskProfile.getScoreCode());
		dataScore.put("value", riskProfile.getScoreName());

		Map documents = new HashMap();
		documents.put("id_card_image", ktp);
		documents.put("signature_image", ttd);

		Map data = new HashMap();
		data.put("customer_key", user.getCustomerKey());
		data.put("customer_id", kyc.getPortalcif());
		data.put("customer_status", user.getUserStatus());
		data.put("customer_document", documents);
		data.put("customer_risk_profile", dataScore);

		return errorResponse(ConstantUtil.STATUS_SUCCESS, "profile_update", data);
	}

	@Override
	public Map login(Map map, String ip) {
		Map result = new HashMap();

		Kyc kyc = kycRepository.findByPortalcif(map.get("customer_cif").toString());
		if (kyc == null) {
			result.put("code", 14);
			result.put("info", "Invalid request: Invalid customerCIF");
			return result;
		}
		if (map.get("signature") == null || "".equals(map.get("signature").toString())) {
			result.put("code", 10);
			result.put("info", "Incomplete data : signature");
			return result;
		}

		// customer id lama
		// if (map.get("customer") == null || "".equals(map.get("customer").toString()))
		// {
		// result.put("code", 10);
		// result.put("info", "Incomplete data : customer");
		// return result;
		// }
		if (map.get("customer_cif") == null || "".equals(map.get("customer_cif").toString())) {
			result.put("code", 10);
			result.put("info", "Incomplete data : customer_CIF");
			return result;
		}

		User user = kyc.getAccount();
		if (!agentService.checkSignatureCustomer(user, map.get("signature").toString())) {
			result.put("code", 12);
			result.put("info", "Channel tidak valid");
			return result;
		}

		// User user =
		// userRepository.findByChannelCustomer(map.get("customer").toString());
		// if (!agentService.checkSignatureAgent(user.getAgent(),
		// map.get("signature").toString())) {
		// result.put("code", 12);
		// result.put("info", "Channel tidak valid");
		// return result;
		// }
		if (user == null) {
			result.put("code", 12);
			result.put("info", "Invalid access");
			return result;
		} else {
			String visibleToken = user.generateNewToken(ip);
			if (user.getRecordLogin() == null) {
				user.setRecordLogin(new Date());
				user.setLastLogin(user.getRecordLogin());
			} else {
				user.setLastLogin(user.getRecordLogin());
				user.setRecordLogin(new Date());
			}
			userRepository.save(user);

			Map mapRiskProflie = new HashMap();
			// Kyc kyc = kycRepository.findByAccount(user);
			mapRiskProflie.put("code", kyc.getRiskProfile().getScoreCode());
			mapRiskProflie.put("value", kyc.getRiskProfile().getScoreName());

			result.put("code", 0);
			result.put("info", "Customer successfully logged in");

			Map dataMap = new HashMap<>();
			dataMap.put("last_login", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,S'Z'").format(user.getLastLogin()));
			dataMap.put("token", visibleToken);
			dataMap.put("customer_risk_profile", mapRiskProflie);
			dataMap.put("customer_status", user.getUserStatus());

			result.put("data", dataMap);
			return result;
		}
	}

	@Override
	public Map viewKyc(User user) {
		Map resultMap = new HashMap<>();
		Kyc kyc = kycRepository.findByAccount(user);
		if (kyc != null) {
			Map map = new HashMap<>();
			map.put("kyc_progress", this.completenessKyc(kyc));
			map.put("email", kyc.getEmail());
			map.put("first_name", kyc.getFirstName());
			map.put("middle_name", kyc.getMiddleName());
			map.put("last_name", kyc.getLastName());
			map.put("birth_date", kyc.getBirthDate());
			map.put("birth_place", kyc.getBirthPlace());
			map.put("occupation", kyc.getOccupation());
			map.put("business_type", kyc.getNatureOfBusiness());
			map.put("home_country", kyc.getHomeCountry());
			map.put("home_province", kyc.getHomeProvince());
			map.put("home_city", kyc.getHomeCity());
			map.put("home_postal", kyc.getHomePostalCode());
			map.put("home_address", kyc.getHomeAddress());
			map.put("home_phone", kyc.getHomePhoneNumber());
			map.put("legal_country", kyc.getLegalCountry());
			map.put("legal_province", kyc.getLegalProvince());
			map.put("legal_city", kyc.getLegalCity());
			map.put("legal_postal", kyc.getLegalPostalCode());
			map.put("legal_address", kyc.getLegalAddress());
			map.put("legal_phone", kyc.getLegalPhoneNumber());
			map.put("income_source", kyc.getSourceOfIncome());
			map.put("annual_income", kyc.getTotalIncomePa());
			map.put("total_asset", kyc.getTotalAsset());
			map.put("investment_purpose", kyc.getInvestmentPurpose());
			map.put("investment_experience", kyc.getInvestmentExperience());
			map.put("other_investment_ experience", kyc.getOtherInvestmentExperience());
			map.put("pep_name", kyc.getPepName());
			map.put("pep_position", kyc.getPepPosition());
			map.put("pep_pubic_function", kyc.getPepPublicFunction());
			map.put("pep_country", kyc.getPepCountry() == null ? null : kyc.getPepCountry().getCountryName());
			map.put("pep_started_on", kyc.getPepYearOfService());
			map.put("pep_relationship", kyc.getPepRelationship());
			map.put("pep_other_relationship", kyc.getPepOther());
			map.put("gender", kyc.getGender());
			map.put("citizenship", kyc.getCitizenship());
			map.put("nationality", kyc.getNationality());
			map.put("marital_status", kyc.getMaritalStatus());
			map.put("mother_maiden_name", kyc.getMotherMaidenName());
			map.put("education_background", kyc.getEducationBackground());
			map.put("religion", kyc.getReligion());
			map.put("beneficiary_name", kyc.getBeneficiaryName());
			map.put("beneficiary_relationship", kyc.getBeneficiaryRelationship());
			map.put("id_type", kyc.getIdType());
			map.put("id_number", kyc.getIdNumber());
			map.put("id_expiration_date", kyc.getIdExpirationDate());
			map.put("tax_id", kyc.getTaxId());
			map.put("tax_registration_date", kyc.getTaxIdRegisDate());
			SettlementAccounts accounts = settlementAccountsRepository.findByKycs(kyc);
			if (accounts != null) {
				map.put("settlement_bank", accounts.getBankId().getBankName());
				map.put("settlement_bank_branch", accounts.getBranchId().getBranchName());
				map.put("settlement_account_name", accounts.getSettlementAccountName());
				map.put("settlement_account_no", accounts.getSettlementAccountNo());
				map.put("settlement_mail_type", kyc.getPreferredMailingAddress());
			} else {
				map.put("settlement_bank", null);
				map.put("settlement_bank_branch", null);
				map.put("settlement_account_name", null);
				map.put("settlement_account_no", null);
				map.put("settlement_mail_type", null);
			}
			resultMap.put("code", 0);
			resultMap.put("info", "Data " + kyc.getFirstName() + " is loaded");
			resultMap.put("data", map);
		} else {
			resultMap.put("code", 50);
			resultMap.put("info", "Invalid Account");
			resultMap.put("data", kyc);
		}
		return resultMap;
	}

	@Override
	public Map viewFatca(User user) {
		Map resultMap = new HashMap<>();
		Kyc kyc = kycRepository.findByAccount(user);

		Questionaires fatcaDefault = questionairesRepository.findByQuestionnaireName("FATCA Default");
		List<Question> questions = questionRepository.findAllByQuestionairesOrderBySeqAsc(fatcaDefault);

		Map mapData = new HashMap<>();
		mapData.put("fatca_progress", this.completenessFatca(kyc));

		List<Map> maps = new ArrayList<>();
		for (Question q : questions) {
			List<CustomerAnswer> customerAnswers = customerAnswerRepository.findAllByKycAndQuestionWithQuery(kyc, q);
			Map map = new HashMap<>();
			map.put("code", q.getQuestionName());
			map.put("value", q.getQuestionText());
			List<Answer> answers = answerRepository.findAllByQuestionOrderByAnswerNameAsc(q);
			List<Map> mapOptions = new ArrayList<>();
			for (Answer a : answers) {
				Map mapCa = new HashMap<>();
				mapCa.put("code", a.getAnswerName());
				mapCa.put("value", a.getAnswerText());
				mapOptions.add(mapCa);
			}
			map.put("option", mapOptions);

			List<Map> mapsCa = new ArrayList<>();
			for (CustomerAnswer ca : customerAnswers) {
				Map mapCa = new HashMap<>();
				mapCa.put("code", ca.getAnswer().getAnswerName());
				mapCa.put("value", ca.getAnswer().getAnswerText());
				mapsCa.add(mapCa);
			}

			Map mapX = new HashMap<>();
			mapX.put("question", map);
			mapX.put("answers", mapsCa);
			maps.add(mapX);
		}
		mapData.put("fatca", maps);

		resultMap.put("code", 0);
		resultMap.put("info", "Customer FATCA data loaded");
		resultMap.put("data", mapData);
		return resultMap;
	}

	@Override
	public Double completenessFatca(Kyc kyc) {
		Questionaires fatcaDefault = questionairesRepository.findByQuestionnaireCategory(2L);
		Long fatcaQuestionCount = questionRepository.countByQuestionaires(fatcaDefault);
		List<Question> questions = questionRepository.findAllByQuestionairesOrderBySeqAsc(fatcaDefault);
		Integer answerFatca = customerAnswerRepository.findByQuestionWithQuery(kyc, questions);
		Double fatcaProgress = 100.0 * (new Double(answerFatca) / new Double(fatcaQuestionCount));
		return fatcaProgress;
	}

	@Override
	public Double completenessRiskProfile(Kyc kyc) {
		Questionaires riskProfileDefault = questionairesRepository.findByQuestionnaireCategory(1L);
		Long riskProfileCount = questionRepository.countByQuestionaires(riskProfileDefault);
		List<Question> questions = questionRepository.findAllQuestionByQuestionairesWithQuery(riskProfileDefault);
		Integer answerRiskProfile = customerAnswerRepository.findByQuestionWithQuery(kyc, questions);
		Double riskProfileProgress = 100.0 * (new Double(answerRiskProfile) / new Double(riskProfileCount));
		return riskProfileProgress;
	}

	@Override
	public Map viewRiskProfile(User user) {
		Map resultMap = new HashMap();
		Kyc kyc = kycRepository.findByEmail(user.getEmail());
		if (kyc == null) {
			resultMap.put("code", 10);
			resultMap.put("info", "Invalid or unregister e-mail , please check your format or Sign Up now");
		}

		Questionaires questionaires = questionairesRepository.findByQuestionnaireCategory(1L);
		List<Question> questions = questionRepository.findAllQuestionByQuestionairesWithQuery(questionaires);

		Map dataMap = new HashMap<>();
		dataMap.put("risk_profile_progress", this.completenessRiskProfile(kyc));

		List<Map> questionMaps = new ArrayList<>();

		if (!questions.isEmpty()) {
			for (Question q : questions) {
				List<CustomerAnswer> customerAnswers = customerAnswerRepository.findAllByKycAndQuestionWithQuery(kyc,
						q);
				List<Answer> answers = answerRepository.findAllByQuestionOrderByAnswerNameAsc(q);
				Map map = new HashMap<>();
				map.put("code", q.getQuestionName());
				map.put("value", q.getQuestionText());
				List<Map> options = new ArrayList<>();
				for (Answer answer : answers) {
					Map answerMap = new HashMap<>();
					answerMap.put("code", answer.getAnswerName());
					answerMap.put("value", answer.getAnswerText());
					options.add(answerMap);
				}
				map.put("option", options);
				List<String> cusAnswers = new ArrayList<>();
				for (CustomerAnswer ca : customerAnswers) {
					cusAnswers.add(ca.getAnswer().getAnswerName());
				}
				map.put("answers", cusAnswers);
				questionMaps.add(map);
			}
		}
		dataMap.put("risk_profile", questionMaps);
		resultMap.put("code", 0);
		resultMap.put("info", "Customer risk profile data loaded");
		resultMap.put("data", dataMap);
		return resultMap;
	}

	@Override
	public Double completenessKyc(Kyc kyc) {
		Double nilai = 1.0;
		Double total = 0.0;
		Double totalMax = 40.0;

		if (kyc.getFirstName() != null && !kyc.getFirstName().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getLastName() != null && !kyc.getLastName().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getBirthDate() != null && !kyc.getBirthDate().toString().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getBirthPlace() != null && !kyc.getBirthPlace().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getOccupation() != null && !kyc.getOccupation().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getNatureOfBusiness() != null && !kyc.getNatureOfBusiness().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getHomeCountry() != null && !kyc.getHomeCountry().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getHomeProvince() != null && !kyc.getHomeProvince().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getHomeCity() != null && !kyc.getHomeCity().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getHomeAddress() != null && !kyc.getHomeAddress().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getHomePostalCode() != null && !kyc.getHomePostalCode().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getHomePhoneNumber() != null && !kyc.getHomePhoneNumber().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getLegalCountry() != null && !kyc.getLegalCountry().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getLegalProvince() != null && !kyc.getLegalProvince().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getLegalCity() != null && !kyc.getLegalCity().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getLegalAddress() != null && !kyc.getLegalAddress().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getLegalPostalCode() != null && !kyc.getLegalPostalCode().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getLegalPhoneNumber() != null && !kyc.getLegalPhoneNumber().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getGender() != null && !kyc.getGender().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getCitizenship() != null && !kyc.getCitizenship().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getMaritalStatus() != null && !kyc.getMaritalStatus().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getMotherMaidenName() != null && !kyc.getMotherMaidenName().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getEducationBackground() != null && !kyc.getEducationBackground().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getReligion() != null && !kyc.getReligion().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getInvestmentPurpose() != null && !kyc.getInvestmentPurpose().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getSourceOfIncome() != null && !kyc.getSourceOfIncome().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getTotalIncomePa() != null && !kyc.getTotalIncomePa().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getPreferredMailingAddress() != null && !kyc.getPreferredMailingAddress().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getIdNumber() != null && !kyc.getIdNumber().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getIdType() != null && !kyc.getIdType().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getIdExpirationDate() != null) {
			total += nilai;
		}

		if (kyc.getNationality() != null && !kyc.getNationality().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getTotalAsset() != null && !kyc.getTotalAsset().trim().isEmpty()) {
			total += nilai;
		}

		if (kyc.getInvestmentExperience() != null && !kyc.getInvestmentExperience().trim().isEmpty()) {
			total += nilai;

			LookupHeader header = lookupHeaderRepository.findByCategory("INVESTMENT_EXPERIENCE");
			LookupLine line = lookupLineRepository.findByCategoryAndCode(header, "IE04");
			if (kyc.getInvestmentExperience() == line.getCode()
					|| kyc.getInvestmentExperience().equals(line.getCode())) {
				totalMax += nilai;
				if (kyc.getOtherInvestmentExperience() != null
						&& !kyc.getOtherInvestmentExperience().trim().isEmpty()) {
					total += nilai;
				}
			}
		}

		if (("ACT".equals(kyc.getAccount().getUserStatus()) && "REG".equals(kyc.getAccount().getUserStatusSebelumnya()))
				|| ("ACT".equals(kyc.getAccount().getUserStatus())
						&& "PEN".equals(kyc.getAccount().getUserStatusSebelumnya()))) {
			List<CustomerDocument> docKTP = customerDocumentRepository.findByDocumentTypeWithQuery(kyc.getAccount(),
					"KTP");
			if (docKTP.size() > 0) {
				total += nilai;
			}
			List<CustomerDocument> docTTD = customerDocumentRepository.findByDocumentTypeWithQuery(kyc.getAccount(),
					"TTD");
			if (docTTD.size() > 0) {
				total += nilai;
			}
		} else if ("PEN".equals(kyc.getAccount().getUserStatus())
				&& "ACT".equals(kyc.getAccount().getUserStatusSebelumnya())) {
			List<CustomerDocument> docKTP = customerDocumentRepository.findByDocumentTypeWithQuery(kyc.getAccount(),
					"KTP");
			if (docKTP.size() > 0) {
				total += nilai;
			}
			List<CustomerDocument> docTTD = customerDocumentRepository.findByDocumentTypeWithQuery(kyc.getAccount(),
					"TTD");
			if (docTTD.size() > 0) {
				total += nilai;
			}
		} else if ("VER".equals(kyc.getAccount().getUserStatus())
				&& "PEN".equals(kyc.getAccount().getUserStatusSebelumnya())) {
			String[] codes = { "KTP", "TTD" };
			List<DocumentType> listDocType = documentTypeRepository.findAllByRowStatusAndCodeIn(true, codes);
			for (DocumentType documentType : listDocType) {
				CustomerDocument document = customerDocumentRepository
						.findByUserAndRowStatusAndDocumentType(kyc.getAccount(), true, documentType.getCode());
				if (document != null) {
					total += nilai;
				}
			}

		} else if ("PEN".equals(kyc.getAccount().getUserStatus())
				&& "VER".equals(kyc.getAccount().getUserStatusSebelumnya())) {
			String[] codes = { "KTP", "TTD" };
			List<DocumentType> listDocType = documentTypeRepository.findAllByRowStatusAndCodeIn(true, codes);
			for (DocumentType documentType : listDocType) {
				CustomerDocument document = customerDocumentRepository
						.findByUserAndRowStatusAndDocumentType(kyc.getAccount(), true, documentType.getCode());
				if (document != null) {
					total += nilai;
				}
			}

		}

		LookupHeader header = lookupHeaderRepository.findByCategory("PEP_RELATIONSHIP");
		LookupLine line = lookupLineRepository.findByCategoryAndCode(header, "PR12");
		if (kyc.getPepRelationship() != null) {
			if (kyc.getPepRelationship() == line.getCode() || kyc.getPepRelationship().equals(line.getCode())) {
				totalMax += nilai;
				if (kyc.getPepOther() != null && !kyc.getPepOther().trim().isEmpty()) {
					total += nilai;
				}
			}
		}

		if (kyc.getId() != null) {
			if (settlementAccountsRepository.countByKycs(kyc) > 0) {
				SettlementAccounts settlementAccounts = settlementAccountsRepository.findByKycs(kyc);
				if (settlementAccounts.getSettlementAccountNo() != null
						&& !settlementAccounts.getSettlementAccountNo().trim().isEmpty()) {
					total += nilai;
				}
				if (settlementAccounts.getSettlementAccountName() != null
						&& !settlementAccounts.getSettlementAccountName().trim().isEmpty()) {
					total += nilai;
				}
				if (settlementAccounts.getBankId() != null && settlementAccounts.getBankId().getBankName() != null
						&& !settlementAccounts.getBankId().getBankName().trim().isEmpty()) {
					total += nilai;
				}
				if (settlementAccounts.getBranchId() != null && settlementAccounts.getBranchId().getBranchName() != null
						&& !settlementAccounts.getBranchId().getBranchName().trim().isEmpty()) {
					total += nilai;
				}
			}
		}

		Double sum = (total / totalMax) * 100;
		return sum;
	}

	@Override
	public Map updateFatca(Map map, User user) {
		Questionaires questionaires = questionairesRepository.findByQuestionnaireName("FATCA Default");
		Kyc kyc = kycRepository.findByAccount(user);

		// TODO: Cek FATCA already Exist and move to oldFatca in KYC Table
		JSONArray listOldValueFatca = new JSONArray();
		List<Question> fatcaCustomerAnswers = customerAnswerRepository
				.findAllQuestionByUserAndQuestionariesWithQuery(user, questionaires);
		if (!fatcaCustomerAnswers.isEmpty()) {
			for (Question q : fatcaCustomerAnswers) {
				List<CustomerAnswer> answers = customerAnswerRepository.findAllByUserAndQuestionWithQuery(user, q);
				List<Long> answerCode = new ArrayList<>();
				for (CustomerAnswer answer : answers) {
					answerCode.add(answer.getAnswer().getId());
				}
				JSONObject oldValueFatca = new JSONObject();
				oldValueFatca.put("questionId", q.getId());
				oldValueFatca.put("answerId", answerCode);
				listOldValueFatca.put(oldValueFatca);
			}

			kyc.setOldValueFatca(listOldValueFatca.toString());
			kycRepository.save(kyc);
		}

		// TODO : Delete Existing Customer Answer Fatca
		try {
			customerAnswerRepository.deleteByKycAndQuestionaries(kyc, questionaires);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Map> maps = (List<Map>) map.get("fatca");
		for (Map newMap : maps) {
			Question question = questionRepository.findFirstByQuestionairesAndQuestionNameOrderByIdDesc(questionaires,
					(String) newMap.get("question"));
			if (question != null) {
				List<String> strings = (List<String>) newMap.get("answers");
				if (!strings.isEmpty()) {
					for (String str : strings) {
						Answer answer = answerRepository.findByAnswerNameAndQuestion(str, question);
						CustomerAnswer customerAnswer = new CustomerAnswer();
						customerAnswer.setAnswer(answer);
						customerAnswer.setQuestion(question);
						customerAnswer.setCreatedBy(user.getUsername());
						customerAnswer.setCreatedDate(new Date());
						customerAnswer.setKyc(kyc);
						customerAnswer.setVersion(0);
						customerAnswerRepository.save(customerAnswer);
					}
				}

			}
		}

		Double pcgFatca = this.completenessFatca(kyc);
		Map pcgMap = new HashMap<>();
		pcgMap.put("fatca_progress", pcgFatca);

		Map resultMap = new HashMap<>();
		resultMap.put("code", 0);
		resultMap.put("info", "FATCA successfully updated");
		resultMap.put("data", pcgMap);
		return resultMap;
	}

	@Override
	public Map updateRiskProfile(Map map, User user) {
		Questionaires questionaires = questionairesRepository.findByQuestionnaireName("Portal Risk Questionnaire");
		Kyc kyc = kycRepository.findByAccount(user);

		// TODO: Cek Risk Profile already Exist and move to oldFatca in KYC
		// Table
		JSONArray listOldValueRiskProfile = new JSONArray();
		List<Question> riskProfileCustomerAnswers = customerAnswerRepository
				.findAllQuestionByUserAndQuestionariesWithQuery(user, questionaires);

		if (!riskProfileCustomerAnswers.isEmpty()) {
			for (Question q : riskProfileCustomerAnswers) {
				List<CustomerAnswer> answers = customerAnswerRepository.findAllByUserAndQuestionWithQuery(user, q);
				List<Long> answerCode = new ArrayList<>();
				for (CustomerAnswer answer : answers) {
					answerCode.add(answer.getAnswer().getId());
				}
				JSONObject oldValuerisk = new JSONObject();
				oldValuerisk.put("questionId", q.getId());
				oldValuerisk.put("answerId", answerCode);
				listOldValueRiskProfile.put(oldValuerisk);
			}

			kyc.setOldValueRiskProfile(listOldValueRiskProfile.toString());
			kycRepository.save(kyc);
		}

		// TODO : Delete Existing Customer Answer Fatca
		try {
			customerAnswerRepository.deleteByKycAndQuestionaries(kyc, questionaires);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Map> maps = (List<Map>) map.get("risk_profile");
		for (Map newMap : maps) {
			Question question = questionRepository.findFirstByQuestionairesAndQuestionNameOrderByIdDesc(questionaires,
					(String) newMap.get("question"));
			if (question != null) {
				List<String> strings = (List<String>) newMap.get("answers");
				if (!strings.isEmpty()) {
					for (String str : strings) {
						Answer answer = answerRepository.findByAnswerNameAndQuestion(str, question);
						CustomerAnswer customerAnswer = new CustomerAnswer();
						customerAnswer.setAnswer(answer);
						customerAnswer.setQuestion(question);
						customerAnswer.setCreatedBy(user.getUsername());
						customerAnswer.setCreatedDate(new Date());
						customerAnswer.setKyc(kyc);
						customerAnswer.setVersion(0);
						customerAnswerRepository.save(customerAnswer);
					}
				}

			}
		}

		Double pcgRisk = this.completenessRiskProfile(kyc);
		Map pcgMap = new HashMap<>();
		pcgMap.put("risk_profile_progress", pcgRisk);

		Map resultMap = new HashMap<>();
		resultMap.put("code", 0);
		resultMap.put("info", "Risk Profile successfully updated");
		resultMap.put("data", pcgMap);
		return resultMap;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map profileView(Map map, User user) {
		Map result = new HashMap();
		try {
			Kyc kyc = kycRepository.findByAccount(user);
			if (kyc == null) {
				result.put("code", 50);
				result.put("info", "Data not found : customer");
				return result;
			}

			Map dataGeneral = new HashMap();
			dataGeneral.put("first_name", kyc.getFirstName());
			dataGeneral.put("last_name", kyc.getLastName());
			dataGeneral.put("phone_number", kyc.getMobileNumber());

			Map dataKyc = new HashMap();
			dataKyc.put("birth_date", new SimpleDateFormat("yyyy-MM-dd").format(kyc.getBirthDate()));
			dataKyc.put("birth_place", kyc.getBirthPlace());
			dataKyc.put("gender", kyc.getGender());
			dataKyc.put("nationality",
					countriesRepository.findById(Long.valueOf(kyc.getNationality())).getAlpha3Code());
			dataKyc.put("marital_status", kyc.getMaritalStatus());
			dataKyc.put("mother_maiden_name", kyc.getMotherMaidenName());
			dataKyc.put("annual_income", kyc.getTotalIncomePa());
			dataKyc.put("education_background", kyc.getEducationBackground());
			dataKyc.put("religion", kyc.getReligion());
			dataKyc.put("statement_type", kyc.getPreferredMailingAddress());
			dataKyc.put("occupation", kyc.getOccupation());
			dataKyc.put("business_nature", kyc.getNatureOfBusiness());
			dataKyc.put("id_number", kyc.getIdNumber());
			dataKyc.put("id_expiration",
					new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,S'Z'").format(kyc.getIdExpirationDate()));

			Map dataLegal = new HashMap();
			dataLegal.put("country", countriesRepository.findById(Long.valueOf(kyc.getLegalCountry())).getAlpha3Code());
			dataLegal.put("province", kyc.getLegalProvince());
			dataLegal.put("city", citiesRepository.findById(Long.valueOf(kyc.getLegalCity())).getCityCode());
			dataLegal.put("postal_code", kyc.getLegalPostalCode());
			dataLegal.put("address", kyc.getLegalAddress());
			dataLegal.put("phone", kyc.getLegalPhoneNumber());
			dataKyc.put("legal", dataLegal);

			Map dataMailing = new HashMap();
			dataMailing.put("country",
					countriesRepository.findById(Long.valueOf(kyc.getHomeCountry())).getAlpha3Code());
			dataMailing.put("province", kyc.getHomeProvince());
			dataMailing.put("city", citiesRepository.findById(Long.valueOf(kyc.getHomeCity())).getCityCode());
			dataMailing.put("postal_code", kyc.getHomePostalCode());
			dataMailing.put("address", kyc.getHomeAddress());
			dataMailing.put("phone", kyc.getHomePhoneNumber());
			dataKyc.put("mailing", dataMailing);

			dataKyc.put("income_source", kyc.getSourceOfIncome());
			dataKyc.put("total_asset", kyc.getTotalAsset());
			dataKyc.put("investment_purpose", kyc.getInvestmentPurpose());
			dataKyc.put("investment_experience", kyc.getInvestmentExperience());

			if ("IE04".equals(kyc.getInvestmentExperience())) {
				dataKyc.put("other_investment_ experience", kyc.getOtherInvestmentExperience());
			}

			SettlementAccounts account = settlementAccountsRepository.findByKycs(kyc);
			dataKyc.put("settlement_bank", account.getBankId().getBankCode());
			dataKyc.put("settlement_account_name", account.getSettlementAccountName());
			dataKyc.put("settlement_account_no", account.getSettlementAccountNo());

			List listFatca = new ArrayList();
			Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));
			List<Question> listQuestionFatca = questionRepository
					.findAllByQuestionairesOrderBySeqAsc(questionairesFatca);

			for (int i = 0; i < listQuestionFatca.size(); i++) {
				Map data = new HashMap();
				Question question = listQuestionFatca.get(i);
				List<CustomerAnswer> listFatcaAnswer = customerAnswerRepository
						.findAllByKycAndQuestionOrderByCreatedDateAsc(kyc, question);
				List listAnswers = new ArrayList();
				for (int y = 0; y < listFatcaAnswer.size(); y++) {
					listAnswers.add(listFatcaAnswer.get(y).getAnswer().getAnswerName());
				}
				data.put("question", question.getQuestionName());
				data.put("answer", listAnswers);
				listFatca.add(data);
			}

			List listRisk = new ArrayList();
			Questionaires questionairesRisk = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
			List<Question> listQuestionRisk = questionRepository
					.findAllQuestionByQuestionairesWithQuery(questionairesRisk);

			for (int i = 0; i < listQuestionRisk.size(); i++) {
				Map data = new HashMap();
				Question question = listQuestionRisk.get(i);
				List<CustomerAnswer> listRiskAnswer = customerAnswerRepository
						.findAllByKycAndQuestionOrderByCreatedDateAsc(kyc, question);
				List listAnswers = new ArrayList();
				for (int y = 0; y < listRiskAnswer.size(); y++) {
					listAnswers.add(listRiskAnswer.get(y).getAnswer().getAnswerName());
				}
				data.put("question", question.getQuestionName());
				data.put("answer", listAnswers);
				listRisk.add(data);
			}

			Map dataCustomer = new HashMap();
			dataCustomer.put("general", dataGeneral);
			dataCustomer.put("kyc", dataKyc);
			dataCustomer.put("fatca", listFatca);
			dataCustomer.put("risk_profile", listRisk);

			String cus = kyc.getAccount().getUserStatus(); // current user status
			String pus = kyc.getAccount().getUserStatusSebelumnya(); // previous user status

			// doc type KTP
			CustomerDocument custDocKTP = null;
			// doc type TTD
			CustomerDocument custDocTTD = null;
			if (cus.equals("VER") && pus.equals("PEN")) {
				custDocKTP = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user, true, "DocTyp01");
				custDocTTD = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user, true, "DocTyp03");
			} else {
				try {
					custDocKTP = customerDocumentRepository
							.findTop1ByDocumentTypeAndUserOrderByCreatedOnDesc("DocTyp01", user).get(0);
					custDocTTD = customerDocumentRepository
							.findTop1ByDocumentTypeAndUserOrderByCreatedOnDesc("DocTyp03", user).get(0);
				} catch (Exception e) {
					System.out.println("error custDoc== " + e);
				}
			}

			Map cusDoc = new HashMap();
			cusDoc.put("id_card_image", custDocKTP == null ? null : custDocKTP.getFileKey());
			cusDoc.put("signature_image", custDocTTD == null ? null : custDocTTD.getFileKey());

			Map dataScore = new HashMap();
			dataScore.put("code", kyc.getRiskProfile().getScoreCode());
			dataScore.put("value", kyc.getRiskProfile().getScoreName());

			Map dataProfile = new HashMap();
			dataProfile.put("customer_id", kyc.getPortalcif());
			dataProfile.put("customer_status", kyc.getAccount().getUserStatus());
			dataProfile.put("customer_document", cusDoc);
			dataProfile.put("customer_risk_profile", dataScore);
			dataProfile.put("customer_data", dataCustomer);

			result.put("code", 0);
			result.put("info", "Customer profile successfully loaded");
			result.put("data", dataProfile);
			return result;
		} catch (DataIntegrityViolationException de) {
			de.printStackTrace();
			result.put("code", 99);
			result.put("info", "General error");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("code", 99);
			result.put("info", "General error");
			return result;
		}
	}

	@Override
	public Map loginOfficer(Map map, String ip) {
		Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
		if (agent != null) {
			AgentCredential agentCredential = agentCredentialRepository.findByAgent(agent);
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(String.valueOf(map.get("password")));
			if (agentCredential != null) {
				if (hashedPassword == agentCredential.getPassword()
						|| agentCredential.getPassword().equals(hashedPassword)) {
					System.out.println("masuk");
				}
			}
		}

		return null;
	}

	@Override
	public Map businessStatus(Map map, User user) {
		return businessStatus(user, "business_status");
	}

	@Override
	public BaseResponse<CompletenessResponse> completeness(HttpServletRequest httpServletRequest,
			String authorization) {
		TokenValidationRequest tokenValidationRequest = new TokenValidationRequest();
		tokenValidationRequest.setAuthorization(authorization);
		tokenValidationRequest.setIpAddress(httpServletRequest.getRemoteAddr());
		TokenValidationResponse tokenValidationResponse = utilService.tokenValidation(tokenValidationRequest);

		BaseResponse<CompletenessResponse> response = new BaseResponse<>();
		if (tokenValidationResponse.getAllowed()) {
			CompletenessResponse completenessResponse = new CompletenessResponse();

			try {
				Kyc kyc = kycRepository.findByAccount(tokenValidationResponse.getUser());

				Double completeness;
				Double calculatedData = 0.00;
				Double totalCalculatedData = 0.00;
				List<String> incompleteData = new ArrayList<>();

				CompletenessDetailResponse checkKyc = this.checkKyc(kyc);

				CompletenessDetailResponse checkDoc = this.checkDoc(kyc);

				calculatedData = checkKyc.getCalculatedData() + checkDoc.getCalculatedData();
				totalCalculatedData = checkKyc.getTotalCalculatedData() + checkDoc.getTotalCalculatedData();
				incompleteData.addAll(checkKyc.getIncompleteData());
				incompleteData.addAll(checkDoc.getIncompleteData());

				completeness = calculatedData / totalCalculatedData * 100;

				completenessResponse.setCompleteness(completeness);
				completenessResponse.setCalculatedData(calculatedData.intValue());
				completenessResponse.setTotalCalculatedData(totalCalculatedData.intValue());
				completenessResponse.setIncompleteData(incompleteData);

				response.setCode("SUCCESS");
				response.setInfo("Inquiry succeed");
				response.setServerTime(new Date());
				response.setData(completenessResponse);
			} catch (Exception e) {
				response.setCode("INTERNAL_SERVER_ERROR");
				response.setInfo(e.toString());
				response.setServerTime(new Date());
			}
		} else {
			response.setCode("ACCESS_DENIED");
			response.setInfo(tokenValidationResponse.getMessage());
			response.setServerTime(new Date());
		}
		return response;
	}

	@Override
	public boolean isReferralExist(Map request) {
		return kycRepository.findAllByReferralCodeAndAccount_Agent_Code(request.get("referralCode").toString(),
				request.get("agentCode").toString()).size() > 0;
	}

	@Override
	public ResponseEntity<BaseResponse> logout(String token, HttpServletRequest httpServletRequest) {
		BaseResponse response = new BaseResponse();

		User user = (User) utilService.checkToken(token, getIpAddress(httpServletRequest)).get("user");

		if (user == null) {
			response.setCode("NOT_FOUND");
			response.setInfo("Resource not found: " + token);
		} else {
			user.setToken(null);
			userRepository.save(user);

			response.setCode("SUCCEED");
			response.setInfo("Logout successful.");
		}
		response.setServerTime(new Date());
		return ResponseEntity.ok(response);
	}

	private CompletenessDetailResponse checkKyc(Kyc kyc) {
		CompletenessDetailResponse response = new CompletenessDetailResponse();

		Double calculatedData = 0.00;
		Double totalCalculatedData = 0.00;
		List<String> incompleteData = new ArrayList<>();

		// TODO: check customer data personal information (kyc)
		// <---------- CHECKED CUSTOMER DATA PERSONAL INFORMATION START ---------->
		// informasi pribadi
		totalCalculatedData += 1;
		if (kyc.getFirstName() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Nama Depan' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getBirthPlace() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Tempat Lahir' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getBirthDate() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Tanggal Lahir' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getGender() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Jenis Kelamin' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getNationality() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Kebangsaan' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getMaritalStatus() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Status Pernikahan' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getMotherMaidenName() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Nama Ibu Kandung' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getEducationBackground() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Latar Belakang Pendidikan' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getReligion() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Agama' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getPreferredMailingAddress() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Pengiriman Laporan Akun' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getOccupation() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Pekerjaan' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getNatureOfBusiness() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Bidang Usaha' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getIdNumber() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Nomor KTP' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getIdExpirationDate() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Tanggal Berlaku KTP' tidak ditemukan");
		}

		// alamat sesuai ktp
		totalCalculatedData += 1;
		if (kyc.getLegalAddress() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Alamat' di KTP tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getLegalCountry() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Negara' di KTP tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getLegalProvince() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Provinsi' di KTP tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getLegalCity() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Kota' di KTP tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getLegalPostalCode() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Kode Pos' di KTP tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getLegalPhoneNumber() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Nomor Telepon' di KTP tidak ditemukan");
		}

		// alamat surat menyurat
		totalCalculatedData += 1;
		if (kyc.getHomeAddress() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Alamat' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getHomeCountry() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Negara' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getHomeProvince() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Provinsi' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getHomeCity() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Kota' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getHomePostalCode() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Kode Pos' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getHomePhoneNumber() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Nomor Telepon' tidak ditemukan");
		}

		// profil investasi
		totalCalculatedData += 1;
		if (kyc.getSourceOfIncome() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Sumber Dana' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getTotalIncomePa() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Total Pendapatan Per Tahun' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getTotalAsset() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Total Aset' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getInvestmentPurpose() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Tujuan Ber-investasi' tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (kyc.getInvestmentExperience() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Pengalaman Ber-investasi' tidak ditemukan");
		}

		// data rekening bank nasabah
		SettlementAccounts settlementAccounts = settlementAccountsRepository.findByKycs(kyc);

		totalCalculatedData += 1;
		if (settlementAccounts.getBankId() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Bank' untuk Rekening Nasabah tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (settlementAccounts.getSettlementAccountNo() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Nomor Rekening' untuk Rekening Nasabah tidak ditemukan");
		}

		totalCalculatedData += 1;
		if (settlementAccounts.getSettlementAccountName() != null) {
			calculatedData += 1;
		} else {
			incompleteData.add("Data 'Nama Pemilik Rekening' untuk Rekening Nasabah tidak ditemukan");
		}

		// lainnya
		if (kyc.getReferral() != null || kyc.getReferralName() != null) {
			totalCalculatedData += 1;
			if (kyc.getReferralName() != null) {
				calculatedData += 1;
			} else {
				incompleteData.add("Data 'Referensi' tidak ditemukan");
			}

			totalCalculatedData += 1;
			if (kyc.getReferralName() != null) {
				calculatedData += 1;
			} else {
				incompleteData.add("Data 'Pemberi Referensi' tidak ditemukan");
			}
		}

		if (kyc.getBeneficiaryName() != null || kyc.getBeneficiaryRelationship() != null) {
			totalCalculatedData += 1;
			if (kyc.getBeneficiaryName() != null) {
				calculatedData += 1;
			} else {
				incompleteData.add("Data 'Nama Ahli Waris' tidak ditemukan");
			}

			totalCalculatedData += 1;
			if (kyc.getBeneficiaryRelationship() != null) {
				calculatedData += 1;
			} else {
				incompleteData.add("Data 'Hubungan Dengan Ahli Waris' tidak ditemukan");
			}
		}

		CustomerDocument document = customerDocumentRepository.findByRowStatusIsTrueAndDocumentTypeAndUser("DocTyp02",
				kyc.getAccount());
		totalCalculatedData += 1;
		if (document != null) {
			calculatedData += 1;
			if (kyc.getTaxId() != null || kyc.getTaxIdRegisDate() != null) {
				totalCalculatedData += 1;
				if (kyc.getTaxId() != null) {
					calculatedData += 1;
				} else {
					incompleteData.add("Data 'Nomor NPWP' tidak ditemukan");
				}

				totalCalculatedData += 1;
				if (kyc.getTaxIdRegisDate() != null) {
					calculatedData += 1;
				} else {
					incompleteData.add("Data 'Tanggal Pembuatan NPWP' tidak ditemukan");
				}
			}
		} else {
			totalCalculatedData -= 1;
		}

		// deklarasi pep
		if (kyc.getPepName() != null || kyc.getPepPosition() != null || kyc.getPepPublicFunction() != null
				|| kyc.getPepCountry() != null || kyc.getPepYearOfService() != null
				|| kyc.getPepRelationship() != null) {

			totalCalculatedData += 1;
			if (kyc.getPepName() != null) {
				calculatedData += 1;
			} else {
				incompleteData.add("Data 'Nama' untuk Deklarasi PEP tidak ditemukan");
			}

			totalCalculatedData += 1;
			if (kyc.getPepPosition() != null) {
				calculatedData += 1;
			} else {
				incompleteData.add("Data 'Posisi / Jabatan' untuk Deklarasi PEP tidak ditemukan");
			}

			totalCalculatedData += 1;
			if (kyc.getPepPublicFunction() != null) {
				calculatedData += 1;
			} else {
				incompleteData.add("Data 'Fungsi Umum' untuk Deklarasi PEP tidak ditemukan");
			}

			totalCalculatedData += 1;
			if (kyc.getPepCountry() != null) {
				calculatedData += 1;
			} else {
				incompleteData.add("Data 'Negara' untuk Deklarasi PEP tidak ditemukan");
			}

			totalCalculatedData += 1;
			if (kyc.getPepYearOfService() != null) {
				calculatedData += 1;
			} else {
				incompleteData.add("Data 'Lama Bekerja' untuk Deklarasi PEP tidak ditemukan");
			}

			totalCalculatedData += 1;
			if (kyc.getPepRelationship() != null) {
				calculatedData += 1;
			} else {
				incompleteData.add("Data 'Hubungan' untuk Deklarasi PEP tidak ditemukan");
			}
		}
		// <---------- CHECKED CUSTOMER DATA PERSONAL INFORMATION END ---------->

		response.setCompleted(calculatedData / totalCalculatedData * 100 == 100);
		response.setCalculatedData(calculatedData);
		response.setTotalCalculatedData(totalCalculatedData);
		response.setIncompleteData(incompleteData);

		return response;
	}

	private CompletenessDetailResponse checkDoc(Kyc kyc) {
		CompletenessDetailResponse response = new CompletenessDetailResponse();

		Double calculatedData = 0.00;
		Double totalCalculatedData = 0.00;
		List<String> incompleteData = new ArrayList<>();

		// TODO: check customer documents
		// <---------- CHECKED CUSTOMER DOCUMENTS START ---------->
		List<String> documentTypeList = new ArrayList<>();
		documentTypeList.add("DocTyp01");
		documentTypeList.add("DocTyp03");
		documentTypeList.add("DocTyp05");

		List<CustomerDocument> customerDocumentList = customerDocumentRepository
				.findAllByRowStatusIsTrueAndUserAndDocumentTypeIn(kyc.getAccount(), documentTypeList);
		for (String x : documentTypeList) {
			totalCalculatedData += 1;

			Boolean isContains = false;
			for (CustomerDocument customerDocument : customerDocumentList) {
				if (customerDocument.getDocumentType().equals(x)) {
					isContains = true;
					break;
				} else {
					isContains = false;
				}
			}

			if (isContains) {
				calculatedData += 1;
			} else {
				DocumentType documentType = documentTypeRepository.findByCodeAndRowStatus(x, true);
				incompleteData.add("Dokumentasi " + documentType.getDescription() + " tidak ditemukan");
			}
		}
		// <---------- CHECKED CUSTOMER DOCUMENTS END ---------->

		response.setCompleted(calculatedData / totalCalculatedData * 100 == 100);
		response.setCalculatedData(calculatedData);
		response.setTotalCalculatedData(totalCalculatedData);
		response.setIncompleteData(incompleteData);

		return response;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	Map miniRegisterAndOrder(Agent agent, String customerId, String firstName, String middleName, String lastName,
			String idCard, String email, String phone, List<FileDto> fileDtos, Map order, String channelOtp,
			String password, String referral) throws Exception {
		Kyc kyc = miniRegister(agent, customerId, firstName, middleName, lastName, idCard, email, phone, fileDtos,
				channelOtp, password);

		if (referral != null) {
			Kyc kycReferral = kycRepository.findByReferralCodeAndAccount_Agent(referral, kyc.getAccount().getAgent());
			if (kycReferral != null) {
				String referralName;
				if (kycReferral.getMiddleName() == null || kycReferral.getMiddleName().trim().equalsIgnoreCase("")) {
					referralName = kycReferral.getFirstName() + " " + kycReferral.getLastName();
				} else {
					referralName = kycReferral.getFirstName() + " " + kycReferral.getMiddleName() + " "
							+ kycReferral.getLastName();
				}

				kyc.setReferral("CUS");
				kyc.setReferralName(referralName);
				kyc.setReferralCus(kycReferral);
				kycRepository.save(kyc);
			} else {
				return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "referral code", null);
			}
		}

		List<Map> lMaps = new ArrayList<>();
		lMaps.add(order);
		logger.info("kyc : " + kyc);
		Map mTrx = transactionService.subscribeOrderByTransfer(lMaps, kyc);
		logger.info("subscribe : " + mTrx);
		if (!mTrx.get("code").equals(0) || ((List) mTrx.get("data")).isEmpty()) {
			throw new Exception(mTrx.toString());
		}

		Map dataScore = new HashMap();
		dataScore.put("code", kyc.getRiskProfile().getScoreCode());
		dataScore.put("value", kyc.getRiskProfile().getScoreName());

		Map data = new HashMap();
		data.put("customer_key", kyc.getAccount().getCustomerKey());
		data.put("customer_cif", kyc.getPortalcif());
		data.put("channel_customer", kyc.getAccount().getChannelCustomer());
		data.put("customer_status", kyc.getAccount().getUserStatus());
		data.put("customer_risk_profile", dataScore);
		data.put("order", mTrx.get("data"));

		return errorResponse(0, "preregister_and_order", data);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	Kyc miniRegister(Agent agent, String customerId, String firstName, String middleName, String lastName,
			String idCard, String email, String phone, List<FileDto> fileDtos, String channelOtp, String password)
			throws Exception {

		String emailKey = customerId.toLowerCase().concat("." + agent.getCode().toLowerCase());
		String passwordTmp;
		if (password == null || password.equals("")) {
			emailKey += "@" + agent.getCode().toLowerCase() + "." + agent.getChannel().getCode().toLowerCase();
			password = emailKey;
			passwordTmp = emailKey;
		} else {
			passwordTmp = password;
			password = DigestUtils.sha256Hex(passwordTmp + agent.getCode());
		}

		Date now = new Date();
		Score score = scoreRepository.getScore(Long.valueOf("10"), now);
		score.getId();

		User user = new User();
		user.setAgent(agent);
		user.setAccountExpired(false);
		user.setAccountLocked(false);
		user.setEnabled(true);
		user.setUserStatus("ACT");
		user.setUserStatusSebelumnya("REG");
		user.setApprovalStatus(false);
		user.setSecurityLevel("NOR");
		user.setCustomerKey(UUID.randomUUID().toString());
		user.setCreatedDate(now);
		user.setIsProcess(true);
		user.setEmail(emailKey);
		user.setUsername(emailKey);
		user.setCreatedBy(emailKey);
		user.setPassword(password);
		user.setPasswordTemp(passwordTmp);
		user.setCreatedBy(emailKey);
		user.setChannelCustomer(customerId);

		logger.info("try to save user");
		user = userRepository.save(user);
		logger.info("save user success with id " + user.getId());

		Kyc kyc = new Kyc();
		kyc.setPortalcif(utilService.generatePortalCIF());
		kyc.setCitizenship("DOM");
		kyc.setCreatedDate(user.getCreatedDate());
		kyc.setIdType("IDC");
		kyc.setFirstName(firstName);
		kyc.setMiddleName(middleName);
		kyc.setLastName(lastName);
		kyc.setEmail(email);
		kyc.setIdNumber(idCard);
		kyc.setCreatedDate(now);
		kyc.setCreatedBy(emailKey);
		kyc.setAccount(user);
		kyc.setMobileNumber(phone);
		kyc.setRiskProfile(score);
		kyc.setFlagEmail(channelOtp.equalsIgnoreCase("email"));
		kyc.setFlagPhoneNumber(channelOtp.equalsIgnoreCase("sms"));

		logger.info("try to save kyc");
		kyc = kycRepository.save(kyc);
		logger.info("save kyc success with id " + kyc.getId());

		logger.info("try to upload documents for kyc id " + kyc.getId());
		uploadDocument(kyc, fileDtos);
		logger.info("upload documents success for kyc id " + kyc.getId());

		logger.info("try to create account viseepay for kyc id " + kyc.getId());
		user = viseepayService.createAccount(kyc, user.getPasswordTemp());
		logger.info("create account viseepay success for kyc id " + kyc.getId());

		logger.info("try to update data user id " + user.getId());
		user = userRepository.save(user);
		logger.info("try to update data user sucess id " + user.getId());

		kyc.setAccount(user);

		return kyc;
	}

	private Map uploadDocument(Kyc kyc, List<FileDto> fileDtos) throws Exception {
		Map result = new HashMap();
		User user = kyc.getAccount();
		List<CustomerDocument> customerDocuments = new ArrayList<>();

    GlobalParameter globalPath = globalParameterRepository.findByName(ConstantUtil.GLOBAL_PARAM_CUSTOMER_FILE_PATH);
    if (globalPath == null) {
      return errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, null, null);
    }
    for (FileDto fileDto : fileDtos) {
      String extention = fileDto.getExtention();
      String documentType = fileDto.getDocumentType();
      byte[] contents = fileDto.getContent();

			String contentType = null;
			if (extention.equalsIgnoreCase("jpg")) {
				contentType = "image/jpg";
			} else if (extention.equalsIgnoreCase("jpeg")) {
				contentType = "image/jpeg";
			} else if (extention.equalsIgnoreCase("png")) {
				contentType = "image/png";
			}

      if (contentType == null) {
        result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
        result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "type document", "extention not allowed"));
        return result;
      }

      DocumentType docType = documentTypeRepository.findByCodeAndRowStatus(documentType, true);
      if (docType == null) {
        return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "type document", null);
      }

      String fileName = kyc.getPortalcif() + "_" + System.currentTimeMillis() + "_" + documentType + "." + extention;
      String tmpFilePath = System.getProperty("user.dir") + "/" +fileName;
      String filepath = Paths.get(globalPath.getValue(), fileName).toString();

      Integer version = 0;
      CustomerDocument doc = new CustomerDocument();
      doc.setFileKey(UUID.randomUUID().toString());
      doc.setDocumentType(docType.getCode());
      doc.setSourceType(CustomerEnum._CUSTOMER.getName());
      doc.setRowStatus(false);
      doc.setFileName(fileName);
      doc.setUser(user);
      doc.setFileLocation(filepath);

      File file = new File(tmpFilePath);
      FileUtils.writeByteArrayToFile(file, contents);
      attachFileService.uploadToAwsS3(tmpFilePath, doc.getFileLocation());

      doc.setFileType(contentType);
      doc.setFileSize(file.length() / 1024);
      doc.setCreatedBy(user.getUsername());
      doc.setCreatedOn(new Date());
      doc.setVersion(version);
      doc.setEndedOn(DateTimeUtil.convertStringToDateCustomized("9999-12-31", DateTimeUtil.API_MCW));

      customerDocuments.add(doc);

      if(file.isFile()){
        file.delete();
      }
    }

    for (CustomerDocument customerDocument : customerDocuments) {
      customerDocumentRepository.saveAndFlush(customerDocument);
    }

		List<Map> lMaps = new ArrayList<>();

		for (CustomerDocument fileDto : customerDocuments) {
			Map fileMap = new HashMap();
			fileMap.put("key", fileDto.getFileKey());
			fileMap.put("type", fileDto.getDocumentType());
			lMaps.add(fileMap);
		}

		Map data = new HashMap();
		data.put(ConstantUtil.DOCUMENT, lMaps);

		return errorResponse(ConstantUtil.STATUS_SUCCESS, "type document", data);
	}

	public Map businessStatus(User user, String type) {
		Map result = new HashMap();
		try {
			Kyc kyc = kycRepository.findByAccount(user);
			if (kyc == null) {
				result.put("code", 50);
				result.put("info", "Data not found : customer");
				return result;
			}

			Map data = new HashMap();

			// doc type KTP
			CustomerDocument custDocKTP = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user, true,
					"DocTyp01");
			// doc type TTD
			CustomerDocument custDocTTD = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user, true,
					"DocTyp03");
			// doc type selfie
			CustomerDocument custDocSelfie = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user,
					true, "DocTyp05");

			Map cusDoc = new HashMap();
			cusDoc.put("id_card_image", custDocKTP == null ? "" : custDocKTP.getFileKey());
			cusDoc.put("signature_image", custDocTTD == null ? "" : custDocTTD.getFileKey());
			cusDoc.put("selfie_image", custDocSelfie == null ? "" : custDocSelfie.getFileKey());

			Map dataScore = new HashMap();
			dataScore.put("code", kyc.getRiskProfile().getScoreCode());
			dataScore.put("value", kyc.getRiskProfile().getScoreName());

			data.put("customer_status", kyc.getAccount().getUserStatus());
			data.put("customer_document", cusDoc);
			data.put("customer_risk_profile", dataScore);

			result.put("code", 0);
			result.put("info", type);
			result.put("data", data);
			return result;
		} catch (Exception e) {
			result.put("code", 99);
			result.put("info", "General error");
			return result;
		}
	}

	@Override
	public Map registerAndOrder(Map map) {
		try {
			Agent agent = agentRepository.findByCodeAndRowStatus(map.get("agent").toString(), true);
			if (agent == null) {
				return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "agent", null);
			}

			SettlementAccounts settlementAccounts;
			Bank bank = null;
			LookupLine occupation = null;
			LookupLine sourceOfIncome = null;
			LookupLine totalIncome = null;
			LookupLine investmentPurpose = null;
			LookupLine education = null;

			Map mKyc = (Map) map.get("kyc");
			Map photo = (Map) mKyc.get("photo");
			Map midCard = (Map) photo.get("id_card");
			Map mselfie = (Map) photo.get("selfie");
			Map order = (Map) map.get("order");

			String email = map.get("email").toString();
			String phone = map.get("phone_number").toString();
			String channelOtp = map.get("type_otp").toString();
			String firstName = map.get("firstName").toString();
			String lastName = map.get("lastName").toString();
			String customerId = map.get("customerId").toString();

			User chekUser = userRepository.findByAgentChannelAndChannelCustomerWithQuery(agent, customerId);
			if (chekUser != null) {
				return errorResponse(13, "customerId : " + customerId, null);
			}

			String emailKey = customerId + "@" + agent.getCode().toLowerCase() + "."
					+ agent.getChannel().getCode().toLowerCase();

			List<Kyc> validEmail = kycRepository.findAllByAccountAgentAndEmail(agent, email);
			for (Kyc valid : validEmail) {
				if (valid != null) {
					System.out.println("valid.getEmail() !null: " + valid.getEmail());
					return errorResponse(13, "invalid " + email + " is already exists", null);
				} else {
					System.out.println("valid is null : " + valid.getEmail());
				}
			}

			List<Kyc> validPhone = kycRepository.findAllByAccountAgentAndMobileNumber(agent, phone);
			for (Kyc validasi : validPhone) {
				if (validasi != null) {
					System.out.println("validasi.getMobileNumber() : " + validasi.getMobileNumber());
					return errorResponse(13, "invalid " + phone + " is already exists", null);
				} else {
					System.out.println("validasi is null : " + validasi.getMobileNumber());
				}
			}

			if (isExistingData(mKyc.get("income_source"))) {
				sourceOfIncome = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("SOURCE_OF_INCOME"), mKyc.get("income_source").toString(),
						true);
				if (sourceOfIncome == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.income_source", null);
				}
			}

			if (isExistingData(mKyc.get("annual_income"))) {
				totalIncome = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("ANNUAL_INCOME"), mKyc.get("annual_income").toString(),
						true);
				if (totalIncome == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.annual_income", null);
				}
			}

			if (isExistingData(mKyc.get("investment_purpose"))) {
				investmentPurpose = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("INVESTMENT_PURPOSE"),
						mKyc.get("investment_purpose").toString(), true);
				if (investmentPurpose == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.investment_purpose", null);
				}
			}

			if (isExistingData(mKyc.get("settlement_bank"))) {
				bank = bankRepository.findByBankCode(mKyc.get("settlement_bank").toString());
				if (bank == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.settlement_bank", null);
				}
			}

			if (isExistingData(mKyc.get("education_background"))) {
				education = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("EDUCATION_BACKGROUND"),
						mKyc.get("education_background").toString(), true);
				if (education == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.education_background", null);
				}
			}

			if (isExistingData(mKyc.get("occupation"))) {
				occupation = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
						lookupHeaderRepository.findByCategory("OCCUPATION"), mKyc.get("occupation").toString(), true);
				if (occupation == null) {
					return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.occupation", null);
				}
			}

			User newUser = new User();
			newUser.setAgent(agent);
			newUser.setAccountExpired(false);
			newUser.setAccountLocked(false);
			newUser.setEnabled(true);
			newUser.setUserStatus("PEN");
			newUser.setUserStatusSebelumnya("ACT");
			newUser.setApprovalStatus(false);
			newUser.setSecurityLevel("NOR");
			newUser.setCustomerKey(UUID.randomUUID().toString());
			newUser.setCreatedDate(new Date());
			newUser.setIsProcess(true);
			newUser.setEmail(emailKey);
			newUser.setUsername(emailKey);
			newUser.setCreatedBy(emailKey);
			newUser.setCreatedBy(emailKey);
			newUser.setPassword(emailKey);
			newUser.setPasswordTemp(emailKey);
			newUser.setChannelCustomer(customerId);

			settlementAccounts = new SettlementAccounts();
			settlementAccounts.setCreatedDate(new Date());
			settlementAccounts.setCreatedBy(emailKey);

			Kyc kyc = new Kyc();
			kyc.setFirstName(firstName);
			kyc.setLastName(lastName);
			kyc.setEmail(email);
			kyc.setMobileNumber(phone);
			kyc.setPortalcif(utilService.generatePortalCIF());
			kyc.setCitizenship("DOM");
			kyc.setCreatedDate(newUser.getCreatedDate());
			kyc.setIdType("IDC");
			kyc.setBirthDate(DateTimeUtil.convertStringToDateCustomized("1999-12-31", DateTimeUtil.API_MCW));
			kyc.setBirthPlace("Jakarta");
			kyc.setGender("ML");
			kyc.setNationality("653");
			kyc.setMaritalStatus("SGL");
			kyc.setMotherMaidenName("IBU");
			kyc.setReligion("Others");
			kyc.setNatureOfBusiness("7");
			kyc.setIdNumber("99999999999999999");
			kyc.setIdExpirationDate(DateTimeUtil.convertStringToDateCustomized("2100-01-01", DateTimeUtil.API_MCW));
			kyc.setLegalCountry("653");
			kyc.setLegalProvince("ID-JK");
			kyc.setLegalCity("1654");
			kyc.setLegalPostalCode("14240");
			kyc.setLegalAddress("Jalan Kirana Avenue Blok G3 No. 1-2");
			kyc.setLegalPhoneNumber("62-21-22455763");
			kyc.setHomeCountry("653");
			kyc.setHomeProvince("ID-JK");
			kyc.setHomeCity("1654");
			kyc.setHomePostalCode("14240");
			kyc.setHomeAddress("Jalan Kirana Avenue Blok G3 No. 1-2");
			kyc.setHomePhoneNumber("62-21-22455763");
			kyc.setTotalAsset("TA03");
			kyc.setInvestmentExperience("IE05");
			kyc.setFlagEmail(channelOtp.equalsIgnoreCase("email"));
			kyc.setFlagPhoneNumber(channelOtp.equalsIgnoreCase("sms"));
			kyc.setPreferredMailingAddress("1");
			kyc.setSalutation("MR");
			kyc.setNeedHelp(true);
			kyc.setRequestFillDate(new Date()); // new SimpleDateFormat("yyyy-MM-dd")

			if (bank != null) {
				settlementAccounts.setBankId(bank);
			}
			if (mKyc.get("settlement_account_no") != null) {
				settlementAccounts.setSettlementAccountNo(mKyc.get("settlement_account_no").toString());
			}
			if (mKyc.get("settlement_account_name") != null) {
				settlementAccounts.setSettlementAccountName(mKyc.get("settlement_account_name").toString());
			}
			if (sourceOfIncome != null) {
				kyc.setSourceOfIncome(sourceOfIncome.getCode());
			}
			if (totalIncome != null) {
				kyc.setTotalIncomePa(totalIncome.getCode());
			}
			if (investmentPurpose != null) {
				kyc.setInvestmentPurpose(investmentPurpose.getCode());
			}
			if (education != null) {
				kyc.setEducationBackground(education.getCode());
			}
			if (occupation != null) {
				kyc.setOccupation(occupation.getCode());
			}

			kyc.setAccount(newUser);
			settlementAccounts.setKycs(kyc);
			settlementAccountsRepository.save(settlementAccounts);

			// Validate fatca and risk profile
			List<CustomerAnswer> customerFatcas;
			List<CustomerAnswer> customerRisk;
			Long score;
			Resource resource = new ClassPathResource("patern/customer/customeFnR.json");
			File file = resource.getFile();
			String string = FileUtils.readFileToString(file, Charset.defaultCharset());
			ObjectMapper mapper = new ObjectMapper();
			Map customFnR = mapper.readValue(string, Map.class);

			Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));
			List<Map> lists = (List<Map>) customFnR.get("fatca");
			Map fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerFatcas = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
			} else {
				return fields;
			}

			logger.info("customerFatcas done.");

			questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
			lists = (List<Map>) customFnR.get("risk_profile");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerRisk = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
				score = (Long) fields.get(ConstantUtil.SCORE);
			} else {
				return fields;
			}

			logger.info("customerRisk done.");
			Score riskProfile = scoreRepository.getScore(score, new Date());
			kyc.setRiskProfile(riskProfile);
			kyc = kycRepository.save(kyc);
			logger.info("save user kyc success");

			for (CustomerAnswer customerAnswer : customerRisk) {
				customerAnswer.setKyc(kyc);
				customerAnswerRepository.save(customerAnswer);
			}
			logger.info("save customer risk success");

			for (CustomerAnswer customerAnswer : customerFatcas) {
				customerAnswer.setKyc(kyc);
				customerAnswerRepository.save(customerAnswer);
			}
			logger.info("save customer fatcas success");

			// validate image
			byte[] content1 = Base64.decodeBase64(midCard.get("content").toString());
			byte[] content2 = Base64.decodeBase64(mselfie.get("content").toString());

			String ext1 = midCard.get("extention").toString();
			String ext2 = mselfie.get("extention").toString();

			List<FileDto> fileDtos = new ArrayList<>();

			FileDto fileDto1 = new FileDto();
			fileDto1.setContent(content1);
			fileDto1.setDocumentType("DocTyp01");
			fileDto1.setExtention(ext1);
			fileDtos.add(fileDto1);

			FileDto fileDto2 = new FileDto();
			fileDto2.setContent(content2);
			fileDto2.setDocumentType("DocTyp05");
			fileDto2.setExtention(ext2);
			fileDtos.add(fileDto2);

			// bypass untuk validasi approve officer, karena tidak membutuhkan foto ttd
			FileDto fileDto3 = new FileDto();
			fileDto3.setContent(Base64.decodeBase64(
					"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77W3W09bzxup0Wylbkj9laUz1UM5quaJt1uI2qmx/RkruduOT3999fS6byH2N6lpamqPojr3pt6b4j9r8b+o/L7lZuwj01k+4fb9ySZk2Elr1zhi7fh8bPjh1Psod/nI1+LtEVI7Qw42fQbl+mekRvvVTW/iualieKq2je6XSqeGc0nG/QNraq/X1OwRo+95r129lOll4Rauf+lM9VB5xh/YaTmSbu2VOWdt5Oc166/Zyak39a4U/w59O6D4S80XsLXhXBatW7Xim0/50LKJqwdUa3e3L5Z5lU4f3AdfE7eaXd4fIZWvjvf5kn2emflxypm+9E4Olv6lr6XfQRn55kzX2C4vUSbuea/P0+J6uCxJbU12fFY+Qn7KtINtdF9kX86Z/mERbd/Un6fPp/jHdM+u7ZCGY+rmZOSYTSbuUQHV2vG6fdn819foWX34/stvf+7/fEOzy37465+/+ar/6+N8+vSp/9MXXzRP+93///Off/76a/P/f//731988csvv3QfbA+2n765/BEAIkufsD29wQECBPf2JPj625/7oDvmNJkdAF6z62tTX32VPet++vHrN3/xshQc6L/6/5/aNz/96+b3sv71k9QOAIf59OHH77/+8ssv37z48suvv//Qf7bx8deX19je/lFqhwOEeMUdAHigTx++/1P9e99P7z7+68/dC2fJG2W83g7HCPGKOwDwKO1bYLLUnn7/+5dfLz/I9cWHf778PJrX2+EggjsAMOrD9y9vXO+8ff/xu/6PreeQfs3tT+/+KrfDMQR3AGDEh+/zX+z29O6PX/z9Jcg/vX1/eUvMpx//9vLrZH64vHMG2J/3uAMAVenviSk9vX33j58uGT15mHe3w5G84g4AVCW/J6aT/puB/xqm9ubTUjscSXAHAKre/G74a5gzeWrvf70McBTBHQCo+uoP32XJ/eVXyLQ+/fj9c2p/evteaoc78B53AGBU8T73JqP/8Mf//PNvf3n+/ZDNB/7xk38CEe5CcAcApnz69OHvf/rbz79c43vj6entdz/89Q/ffCWzw90I7gAAEID3uAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAATw5W+//db/kdfg06dP/Z9aX331Vf8nAIBX5rOLPV5xfw0+ffrw4/dff9l6k2k/8vXX339IqxoAIK7POfYI7rFdSvfNm2//8vMv7d+f3r57//Hjb7/99vHj+7dP3UN++eXnb998+fWPwjtAAJ96/V+BK7HHW2Xi+vTh+z99eync1tO79//4w5vuj8/fKvr049dv/pI84OO//uytMwDnlR/bF09Pb3/4x0/fOL75zIk9La+4B9UWZ1K+b9++/eJ/vu2/U/Tmy/4rza/+/MPby+dbv/zlT152BzixT//7P0Vqb333V6mdz53Y0xPcQ/r045+Sl2Senp5+/vnnX9LT/pe//P1D+/83v7t84+jil18/9n8C4HSquf3tD75XyudO7HkhuEf04e/pd1Lf/vDDd82Xno2ntFw7H38dPgkAcEa13P72/U/f9H+Ez5XYcyW4h/f0uzff/Pmn1l+/6z/UePvH9qj/8M+fL3+9aB7a/wmAk6nk9qd3fxXbIfOZxx7BPbznbwR9un5B+vTuY/sSzacf/5YX8Hd/eH3fb/3wfff7oBpf//gKfv/TKxsOrBZiL/S//iXTf2qFSm6P8C6ZftyZ/lN8VvrFz/Sf2td5Yk8/ykz/qQP9xrE+5vqPbvTxXfbdoaf2+0Uvf353uUnxmK6uu0+8KoOZeB96kK9sOOyrP0Se9R99pU65Fz5+fP/uXfvd+eJwTaw/Zz++T36o7qJp7Hwrfegk3Ek3rVf9R1nmvpVwithzjuIX3A/QrezUura/dbR/7Gofm1v07fWe2qe253YHTwGnP0lXK7dqNw/95wJ68HCaJ7H377ujqTib3r7vHxFWyKHd5TApY8xZts9ptnazClOLkFpSTG1B3ggBmeaBb9/tsd6rHDQJd3OfrXRbu+jDQyjSAfu4SnhY7DlZ8Z86uPdPIq3+I4v1l8/XX7dOt7j96l2Ogba95nQui61x4OrWX7jpP7mzftpa/Ud28Px81nf+Zu8HX2W3sg39UKccTrdkTY69PIeUTyIDo33u2lmqv/Yg3S12GFqpa3eJ/rp1jj5MbsWYruT6u+4n2NbuprvowSU7N5+pdW3OSizIAOPueLodMAnd9liiv26do7fSMsNn587sY+iBDtkOe2n61t/1xQ5zetIh3zO4twNNN9DIpNZmqtWeVP1DZqoe+tNWr3R2CDx1y3rx/PlBXw5Z4OyIejH/VvdYo35iGm246nURq2mz0uitRbm1zMsLZ5F+LI1+KK3HDKfvR96TS1caTW9afTPLjFTQih3WWrHL+oHdb2ilV3OYlEdE9yzU3uLy2coJMn8cl162+iVqxdzazRAG89Dc7Xq7Wv6aHk79cH56e1mAYQroVr6dv9HKbtbuqPF3DpiExqvZSuuMBPfD77vJMZWwl/rO2jijpx7ynsG93xHtWfPs8tRaPbNrI6zPf2LRtKw4H9Yt9nCBU/1zS7nKe6/w2Nw1tdY/otWv0APXaOTYqule8bv1zFxb5ea58GU4+fh3d57hLCv3Nud22sVvteHhoi+QpLmxJV21w8abG/WAoZVWDfVkh0m+dbuXrfvPlIadmDOU17K1K0dcG/n6z15U62G8P4MJ7SYgbXLYYN7a6Lk7sYxbHDAJvdewlTYYGX5z31z/8Mc7rhJ2MLYttp0Npx5yZ7fgPmc3tk+l7XNovSond9mLfWZmvLfLQ0W689u40P/x4qW58nzYaYXbFHJ9Jivcfm4YOnqN5j25z56dSnOHn7yp8wxn556ktTK/VuudmH993SmGNi7CYdIcE0lb5cFQMxjVzZvsvEyV5u6wtYdhrxqMa2Md613xtty2veEDh0VUm6isPhJ7lHHigEmYJ8JW2mpkCcc0Pe+ekR+T5B9WCdMWxZ6FTjrkwhFvlRmpzMlRDa7pZr9VTuMukzO+eRY2nzXUrG9+8Dw3lh0i7dg2FVb73dPxsu00m336FidZo/pBPfvy4SgWLt/OTjKckcVtzH5aSkayoA/18e/5XPiwoU0Y79PC9rOG9jtM8u05//Apx7VsNCfZC0sMzrGxm1bHVi3AYu5HE8VguKOjrc/rfrNzwCTMN1z1ZwvHlzW031baYDJrNn28jq97Ru2e4vvPXW3MpMs8tBIGdok9t5xryFP2D+4rDpbykvJ8yz+/w/SM9LGzpPnieGg+UrTcFFL+TZfFlfWyi5uSHe90ryncNkr3l447yxrV+zHVjcTw4mZy+889xkmGU+9Ga3aDSWHP7H6rfuM9F+VhQ5sw3qdlYz/kMMlf7V14+uSxZtl81WflxFt7+JQ92tva2Gr9y3Lh5NDLmZ589EjB7TFDB0zCEiMj6yxp+5CttFL3stb1Tk/16Dm63pU3bVx6e1B3nz26Eo6IPTc8eMjL7B7cB0dQZ8E51O2gdt06/WOGO3GTeicvprqaG+lUbatdvt11o+F2vO0bcdv3nA++t1d3+T7a0m+knWaNqh1ZeenxT+03nWQ4tVOlM7u0k94s6UZ1/PP30wwPG9qE6qh7jztMGsXLR7f60u/m5EGDkS2YseqszLt+cOnxW7vynD3e2crQanObPuzG3A9HvPDxnfnFVnfEJCxTH9fF/NazVq4D2LKVVslv+PxiefUIuzW44S9BbBs8quP3rYT2zLlL7Jn0+OJfZufgPvLEOjoDjXwSmq+Eixm81Gf6qKnWZqnMe2Jm89lQZ1yTPv6pqbn2q8lLpVanbESz+7v3n2+okvOsUXUd5lw5GMLxT+0znGU4Y+U9+2xJGljSkWph7XugPWpoE8a6dDHzJtnU7dKxYjGmJmiQaJ4fPBja/J5VZ2XO5Q/Y2rW+js9XrcorXUwbzRtrT+9O//c181yd3vm7oOKQSVioPqpnM9vffyutUM/snWUTnajN+eWJd1/7VkL6+YfGnilnKP6F9g3u9Z03OajpzXrRzGG+/tvWrzbxiXlrkPZ7VofmDLQzVdQ71Md51qjW7JrrttbDTk4znJHlmt1ucv2Seqtuq53X5kFDm/CYw+SGolNTTdb7310xmOz5Xaut05yry+v2mIxpS6u28vjao9OBvNTA4HXTy2cGk3W7aEaqbm1NHzQJC40M6tljttJy+Te6ymhdHeTcdattq9WLXrV7JVS7XHNw7Bm3+5DvYs/gXp2BG5OeXTO6ck3xJ4/bOk03amlW82kbs8pqMDldobZffV5+WV3+5WR9Knco4BOtUa0rty8bvNh//z1Td5rh1Jd4dvGkly+qt9q22nl1HjW0CQ85TCaVJTXR4th8dv0ejGz+YtbavX31A7Z2dQKm7ltZ7tr8pg97aa5ybfu5wYfnlEClrca64jlqEpaqj+nFrHJI29ijTwsN3p3WPa+3+gdUB7mgo4vXapH9K2HQ4mNiz7izFP9SOwb36r67VVPpvDVlPrpw+eM21Wm1n6nb67CiN8ldZ1xR7+P2AjnRGqWPfnZrgOU1GythT+cZzkh9by+eabUJ2Ht9HjS0CSM9urrdt3TiNk/YkpJK+/6Uh+b2m9rlyObP8nn2wrRaP6dvPFzu+sOzx/VDb+72ov9Me3HZ5LyBDzvSWDVnx03CQtUhpe68lRarzuRV16HaY+ZvrNZ+K186ohKSR8zoY70Els3PIqcp/sV2C+7Lp+Aive7l4emrPZf3OKYP27aQ2Q2rLx/f7PWKziwr4Opkbq+QarMPWqNaX6YvKq844O19651nOLWeNGbW6Wq1224u2MKDhjYh7dH9DpMxg/mZbO+6f7s+ps9I7U+x9H98tqBrtWWavry84i5bu9bN6eWqXDEyrvKRl1970f+ijOdPDaa9NW/PVPu+ZI2eHTkJy6TtPn4rLTfYMUNNnyqPmrfiV8uXbJZDKiEZ7oweVruww9DGHDLkO9kruNfKduaMZ5e230W57trmvOubSB+0baqyWho+QbVu3GBFX9IFn3FJtVebC7jW6qPWqLIDluyY439ibZkTDadaOxv3zAyV224u2NKDhjYh6dH9DpMRg7tPN3d9ePe4l7+2b8otm1q0kqfa2m1a7vR/vxpMV2vy3pWBjT++fN9PoZ3ly8P6D/RmznSlKxPX9lNw/0lYIunKw7fSctX+lpo1X7g3qqqLP2/EfR3cqxLSh8zoX7UPy+cn1w/43MW/3D7BvVpK84c0/EVNT9kPYuc32LaSyWp1DdVWb/oOZQszLCzg6nRuLuBTrdFw2icuKbq+dSYOcKLh1Ar6+Cmr3HX/ez5maBOSDnXdqHVwun9lC2sNd/eNrZ1cUDyyLM+lL38PJ2FiZIfthfy8GoxhOF+N6bsPL7nR2/YNz4NLmlPz/TVElHM1c/xze3+CSZjrPFtphepEDjS9WrQ3Rq1Zt0dUQvqQ+8eeQMW/wi7BvTYH+w4pLfj5YbNiUEvV9Zu6xYrzIb3HnGuWH1q3nWuNFhxgRccHG/AMTjScaj3vu9AVlbvuf8/HDG1C2qG7HSY1w/q7tQGza64PbrNm/8HW4tDeOMVeyDpRuf+wk43pOVszx7eUbc5tsdb94bVBJqFzmq20SjEtL/dPP95+sDKoNfNXnZuplrL+3a0S0m7OWZJaL1Yv5WOGfD97BPfKHKye77qlJTAqbaif9doumFiQZLCzly25Zk7vaz3aOqHnWqPZW6D8If1de7ybMw2nstDHz1ulYg+450OGNuExh8nQcF5mzUp6WZPQi++oFd9Om23YmbvvhXwRai2vqKTBJTv0uKyWuTVQ6355bZhJ6JxlK61U9LXrQPY1cD9Pwylf19fa0o2uxcMqIXnMjdY6tQWfc13Fw4Z8P9uD+8INtkZ6i22zlUz+Sx8rAxi9SfrY2YO83vRpTu9rE7qxRk62RsPu1DqTP2rNi393cqbhDPuyuXhmGB6DB9zzMUOb8JDDZGA497MbK15fb7W/qm1dYr8Yjv/ueyGfkMrs1wrp1pwNZnmHyiv7MXfZKis+6E2YSbhIWn7pQ6WHo/dLHzt3GvdU9PUp+ena5Cvg4YjW9bW6dmOT87BKuD7m3rHnYUO+o83BfTCeA0aU3mPTtkwaStqpLOPIXdJHburIlP1n9GRrNJzvwQXlt+wfukduONVwKkt9XKU+G970iBE+ZGgTkv4k3bjzYVKZlEfOyQn2QjEjw9morNDNXgymeYdJLvsxs8lK94e9DzMJF0nLSZuVPo7cMX3kbp1aaPCva5U/A7bfBNYWb2z5wlTCoM3b3RgRZshbbAzutSnYfUTpnG1qPGkoW6dKzVTvk4x2ZU3NUJnSbTc72xoN+lMOL3/AiV9q75xqOHdZ69Jw+xyxOx4ytAlnOEwq9zpi6mc7w14o5mQwH2vqaDDPOxRe2Y+ZTc4rryiTcJG0nPV03liz0Ty0/G8YTPrazlbmpVFvLUolVPqx0/ScdsibbAvulQraf0DpTdauZWd8fw/HUbvR+PU72rGAL063RmWHssfnwz97aG+dajh3WezCsGIP2R2PGNqE8cNg2NHafIxfP9tw4g+a+tnOsBcGs5JXSWXSbs7Z5LDWKhqdV8uV3lcvjDIJnaQ3RaN320p3sdcMVlavVa+gIJWwph9jggx5my3BvTIDI9WzRTpnm+Yr6W7ZTmUkw4FMXL+jeWfVbOdbo/E9kH8TPUJob5xqOMPiOWCxC8MKO2R3PGJoE05wmFRm5KFTcpq9MJiYZIYri3Nz/seHtUHR6KyFW7LVQkxCZ2IrVDp6yFa6i71msDIprdEKClEJg05uajbEkLfZENzXzMBy6Zxtan5yfw/HMnzMtScHrtucjixwvjUa2QPFO18fnEDmO9VwKqt9+I2H9zzklo8Y2oSkO8OCH/Z1+JjNh0llQtY2tZcT7YXilk1fLu83XlNG5TW7zHLeaNpk/8/FDP/BmGHfb3T99JPQSVoeNjrs6vAxm7fSfew1g5XVa002F2w7tLY2G6P411sf3MtjunFzBpZL77JpvpLJr7UzHE05mLucDzsX8PnWqBxg253Bj/W0DujnAc41nNtFPPQx/UfYl3dzWLDHjPQBQ5vw+MNkeItDz6U5Tra1839/ZcztORtWeGPrVOeNtq1VupvN06Abs7pw6knoJC3X2rvDVrqTYiRrO1tdiRnNBdoOrT3W8vzFv8Ha4F4bzAEHclrt25q/tb8HJ0TxsPucD7d6scgJ16jsUvNl8MjeevTGmOVcwxmud205Pn78+L5NtMN/1XFNdQwK9oACazxiaBNuHQa3tvGt62+p7exH75hzbu2mIqaevduX4RrdK9yd9m/tr7Rvpb/Ur7CxmqrrN5DcpCyoZVN4zkm4ePRWupdyyVdO3kjlzB37SSvh1iJvcebi32BlcK/UzxH7Jl3QbTN1c38PR5Td8D7nw64FfMI1Gjl2qo6c552cbDjD6ukOpTbJTp5BL5ZvsfKWR43yAUObcO3NyHgPPkyqdffg/XL2rd09f/f332xrNQ3KueblJsV3/Tf8hMCpJqHz6K10L+UwVk7eSOEsb+3c2+GQtTxf8W+xLrhXTukD5jq7y7aZmrG/J0+I+5wPgy5sGfQJ16jSpQlHzvQuTjackUN9WpN5375914Tg4XtqZyhvedQYHzC0CY8+TKqzse183CzC1h5U6+Vrvkb/gduazLxDNY1MVrOE/St9Td32dyl/QGDzzJ1mEjqP3kp3U0z7yt1aLt7FyqGfphIGK3zYWXaaIW+3KrhXDp4D9k06yxubvzY10dBgV1wfO+v6zXYt4DOu0WCGm4vetj8yUvlE48i53sO5hlNZ8F5/NjWnVJsIXr4z2F+3QTnOow7cBwxtwnXUEys6KIHrY2ddP6FaXY/eK5VOnWxrl0U0qwtF7/cp8JFyHjT+8X32wGY+N0/aeSahc215oiODCro+dtb1Z1DM+7oprNfNypGfeDvsWGCZkxX/NmuC+2AnHTKe9C4bm5+3v4f74vm2867frJzXLaM+4xqVfXrqfsy7s+uZdCfnGs7djr+rPet1ygOGNuE66sccJuWsdw4qrfaLoO79nteXpOp3OtdeqCm7MauGimHtVHfVFSxaL39/5nU+tzjRJLSuLT9mK91NMYR1U1gvm5XLceLtsFOzAycr/o1WBPdKAe0/nnSWN+7KpKnpfpYr+3zj64APPR/2LOAzrtHkHhjMfefM5/HJhrNn9cxztzvef2jjHn6YVCtraVOXQN57fnfGy7eNG327Q/U7nWwvVEz2cMyqi27KWm1mvf9To/lyp/u5jWS+nvb8nfcnmoTGw7fS/eRzuGoKi2W4WD3uE1VCObD9Cix3oiHvYXlwr5zDu48nu8fG1mefD8MaumyM60cPPR/Ked0y7DOuUdGn8opKlxu793o3JxtOeb/jJ67YLMftjfsPbVzSl1vdOOgwGTTbWjQl9dIc06T4NtO3kXL0PUhFi2Vv6je86zLmszZz6m8Ma6Ws1bbN8m3sjXbS3+3zInviRJPQSBq+1ehBW+lu8jlc09vtuz5z1u3Q2K/Acucq/u0WB/dyohu7jyed5K2bMmnrZj+HY2suuTZw6PlQ3HvTvc64RnmfalfkW6t3pr2SOtlwyhU/tFQ7xfCOu+H9hzYuGfSDDpNhq41FbV1baF9abzP5y48GDBPkrHrN+1TrzIO39u0O1hRTvVd307m4Zy2fahLOsJXuZt3MJ2rbZ8Ogz1QJRatHLeWZhryPxcF9WER7T/bCk619xun1H0ktOR8GK9V4e/0NQkcV1UU+r9vudbo1mrdzhrPfuH2qX/UfuYOzDadY8RXL3d+z039oSnHDA8+0uw6tf1Sr/0gq6cqjDpNiNi7WNlYYtj1rWR++F/qHjD4mH9fsUl152Q1pqzutW6efg/NMQt+fVv+RVNLujEYP2Up3k83h8t7mS9B5mpqzftLPUwlT8lbXLmU/4hhD3sfS4F45gfcdUDpZE+vY/k7O2lsx85JOezurm/lS5Q5duH0K+OIsa5TKxjd6TW36xx7cvj44LIA93xI64WTDKW40c00uup3UX5iY/h0W+Q3n3K8/WscP1xH3GFqgw6Ta5j67e9j0vHaz6+66Fwb/Lmutwfw0nD9VeYf3meKiN7s0eq5JeG3PyzuYtz9G5AvXyacyEXA75K0unZyQQ97J9uC+fLYnpHM11vB1sbofth88OV9nOGltZi8r++TZoQu3a42cYI1K2fgmLqrNf+Xh2cPaKsj+ZYXjt9jJhlPcZu6itBcWeyc32lBertP3G27Q6a8JckcPLdphks98b4+Cr3R2XrMP2gsjk1v2OX/YgolafeG0dLa2N3qqSXiNz8s7yLo+c7i95gvY/roXYw2cqhJmy8e3sNWYQ97LqYJ7uo7VZtN3YSYPyDr18vH0o7M7OdwqvUMXbt8aeewa1WSzOn3VsPODCaktd+1jhznZcIp7zB39dS+1r2XW3uE8Vor5Jpkq19rwOzNr/Mih/b+Ih0m1yfXNvags1LxWH7IXai1dFD3IerdkmvI77DDBF2l/ZpfRiBNNwmt9Xt5BfQ7mGA557PITVcIiG1qNOuTdnCe4Zy3X5mn8AelnXj6RrtiCaR85IQ5duJ1r5IFrVJdddrMvw+7n1+St1b4ve+xqnW44ecnOG/v1mrQzg+KvNVYMaPx+lZFf3Zy3zoFD+//6/zeydtNev3wivXpeLzqDm14saKFUndN5czml0tF5jebVe+uSSu+za/LWRvZC8qD218Vnr8jnU5uOauEcZROyYb1y6fi2LdqJJiFbtOxR6WdePpE2umBis75cLWjhMdZN//C19tFr426HrHCWtBp3yPvZ/sOpC+djxK1pGixy8qLaywGfvAty9bTnN3q2zyBHrC7gMQ9aozHZ+OZ0pbIG6WWD4ZUOXa3TDSe/fta6XC8p2i76UrtzPprxvuXT1H73/P3H9/VX5kYdOLSrptlAh0m1ySV9qqhNzbxO5os845JK99PLRhfp2dPb/9v/6XnQaYvZPKRtLZ3yxeOaJ+3SljW7tvPoScjXs7n1K3pe3sOa+R8MNZm/0nkqYbFB6cwUeMg7Whzc8+luzZ/yMVmbtSItbtreMV2UZma7b4n3jy4+uXDW84Yvjl24LfVV9ZA1GpVdOXd8w1VIr0zyX8X2wU4523DyxZ419lqJdx0qCqc2urm3uz6uaSX/t9t7t7t64NCu2mbzq859mNQGsqnJ+szMmu3s0rmdGM5JeuX0Xvi/11jYX5O0lnU5vcussaRWjeu2tNkNrV6befQkZA+53CJf3dDPyztYvOLFjDZXTT3XnqcSVki7NL/R0EPe0eLgPiyt5bOSmTFF6RI0bt0vf/jS3g3Ht3WEN6wr4EkPWKNRa6+tvDF55Op8vXeawjFnG06+1LOWeVgdrfbS2ztn7u1eHnfpf+2WNyfvwKFd3Wr29pRMqfVnaRulVXM5qj5jK2Z7QRdW74Xy5fZrO02+6S7oJdetmZr0tuuntpRO14ZWr5179CSkj2jcqpn84bceXTpiKx1t0YoPXt64+UP856mEFdY1GnrIe1oe3Cs7aPX+yU/wsQlKJ7ExPY9F71Z0rbhdY/X45jikRO6+RuPS8S3txPUXFTwrX4AoMsDxe+xkw8kXel6HhsVxuXJGW9lDpnr3PE3PrQw31c3OHje0xPQMF5fN60NmOO4VjZQqk7my2bF5mdfavffC9X6Ty5YOatW0ZLOyw3r1tkzX1XkmoSjDkFvpWEmXpxer8pu3bn9T+zyVsEK6mtOVkwo95F2tCO6VLTR/5hP5Vh5vIn/c9DwWPVvVr8Hw1rUyU3qzPSvkvms0bvP4hoda+w3Y7l97zD4x56jb7mzDyZZ5doeKSHS5VbrWIys9+zy7PrAd2mDErZvFdNDQ/k///4updosttO4YKBpZ2UqpbLWxouFKKxfz2kovv8deSFZxtINZ2l97IqQD22e9WmkRrpquznkmIR1PK+hWOlDS4Ym1KvfA3Cey0NshbXP+Zgh+AuxpVXAf7Nmlo2uqNW1gulTLm40v2dwH3lC0c+jCrSvgOe66RqN2G1/TneaEq/ymie6pfvG/67PS2YaT9mdjh5LVHmtowXGWdazXjOx6k5vFeMzQrm+SvhjtxtkPk8oEL2t7eD68mNdQ2oO77IWsx5U7Zk/ZK8+rVnqf3dZrr1bPMwlZTxqjg5r9wBuKdvZbmoMkG6TW13aliolpy73/7G3nqYTlVp4dkYe8s3XBvTF84pg3T80pnT933p6YYsNWVzpbss6CeijlY9vQ0E31Am5/83QzTS8jakqw/8wyd1yjEenSHTmPd3K24dw6yObKxzW21mk53a6IfEM2JZz+Spnb1x81tKzdRq3pGIdJOZLW3OabEfZXdIoAMauVfGL7Dx6rOM/aLwTfN8G+OyyzIWx7sk1vs9/IklY3de88k/B6ttIhkunJlqJ7di0mZVlk752nEpaqNzkj9sQd8t5WB/fhvm1MfpEziIPTD0+Ud8pXZdju1nXL7retqaGmzJo6u8h/A2nlNaeL9bVzvzWqSm/fD6IZfLLHTrorRpxtONnqrlynZgDXFU+PyrZOM8XNGv0jbxuU1c2JOm5oWcuNyIfJMBbNmax8BtoNnj8fzqritJH+guP3QvG8XdX+Zuf+4eukI3ter8vQig8uUpmvlc4zCXkhlRMTayvtLulsu8Wa2RhOR/upDSsVaTu0zxd96mmu7S/tLIo9gU+AfW0I7o3hTxi1Lu9TbFeqW6rka6irpQVbLlizOF3TtUXfPq3J3XZeo1uF137HuJm/VjvCdg77K9e63xp1rXXaJltJo+2w+j9eHfHkvpt+LI1+NOcYTtKh2sJetN1rdHV0qaRWN5hG++fmaaR4jSJNfHPOx8T1Zu0rIK3+Pt17IPoHXY3N03PfGscNrfFaDpNOZX83szOycfOk32zwy8OyCal1sp/dRjtTrcfshTIq5kaHvUg6GW2dDe44MrR+glL9Zxppx7dOzZkm4VVtpS3a5e42Rqc8gUrNztuaLltn3g6ZskxK3ZHeP4M005fvnUyYIR9sW3Bvtc8FU3NZaCaiWZf+2kXqCTS3NGuOSKpj3zV6mau2HpoSnajQPd1jjW7tzd5lg16+w9VfeUrnHM70sbXW4LibOfhVxp5x7zS03ms4TK6azFAbzuWM6Q6Z4vW+fGjZ1FfW51R7Ybhyl7N0r1tO1WE3bdUbLanetr8Xz1/mNpY9E5xpEl7XVlpl1upfVmi3JeqdczsU9o09IYZ8tC+b//o+bfLp04f//fs//+ff//7ll1/6D71ozvMvfv/77373xz/8+Zuv+o+t9enDj3//2/8M79Is3Xc//HV7+6/ZoWv04fsvv/25/cOlpd//7nd//O//fvPmTffJL7746qtYK3PO4Xz68fs3f2m71ZR7u1j//Yc3b1668unTp/Z/zZn4xRf/+c8/f/31i3//+9/NByqr3WjH9cXvv/tu8YJ3t/n48X+bO9RLaUITo//xU/12jxjaqztMPn348L//bHd4bWaaaRnZ4Z9+/PrNX54f3ySin77p/3zxyrb2DclkJOP9w5tvJof54fuvv/25VozzNV8x/evPJ5nKxZPweT8vv+yQTnf+NCfQ73//xe/++Md2pwTeIuu2Q2ghhrxXcAc+U81XhB8//uef//y1i9PDzNgcf79vnr7/8IoP+9DS3DHM7WzQf8nZ6b7ubFy+9Pzi8sXny24x8cBMgjvAq3fNkMULgOkL7uIjwMn9V/9/AF6dTx9+/P7rL7/88s2LL7/8+vsP/WcbH399+RbJ2z9K7QDnJrgDvEqfPnz/9Ztv/zJ49/UvP//tx+fX3z/88+XtuXI7wOkJ7gCvT/sWmOwnJi8/NHfxy6+XN1zL7QCxCO4Ar82H71/euN55+/7jd/0fW88h/Zrbn979VW4HOD3BHeB1+fB98vvpGk/v/vjF31+C/NPb95cfQf30499efp3MD2f5XYQATPBbZQBek/T3xJSe3r77x0+XjJ48zG+TAQjCK+4Ar0nye2I66b+G+q9ham8+LbUDBCG4A7wmb343/o92d/LUfpp/sBOAmwR3gNfkqz98lyX3l18h0/r04/fPqf3p7XupHSAW73EHeG2K97k3Gf2HP/7nn397+Z3uzQf+8dM3QjtAMII7wCv06dOHv//pbz//co3vjaent9/98Nc/fPOVzA4QkeAOAAABeI87AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAQACCOwAABCC4AwBAAII7AAAEILgDAEAAgjsAAAQguAMAwOl98cX/D/qOo5QnGTVwAAAAAElFTkSuQmCC"));
			fileDto3.setDocumentType("DocTyp03");
			fileDto3.setExtention("png");
			fileDtos.add(fileDto3);

			// ------ try to save
			logger.info("try to save user");
			newUser = userRepository.save(newUser);
			logger.info("save user success with id " + newUser.getId());

			logger.info("try to save kyc");
			kyc = kycRepository.save(kyc);
			logger.info("save kyc success with id " + kyc.getId());

			logger.info("try to upload documents for kyc id " + kyc.getId());
			uploadDocument(kyc, fileDtos);
			logger.info("upload documents success for kyc id " + kyc.getId());

			/*
			 * logger.info("try to create account viseepay for kyc id " + kyc.getId());
			 * newUser = viseepayService.createAccount(kyc, newUser.getPasswordTemp());
			 * logger.info("create account viseepay success for kyc id " + kyc.getId());
			 */

			logger.info("try to update data user id " + newUser.getId());
			newUser = userRepository.save(newUser);
			logger.info("try to update data user sucess id " + newUser.getId());

			logger.info("try to update data kyc");
			kyc.setAccount(newUser);
			kyc = kycRepository.save(kyc);
			logger.info("try to update data kyc success with id " + kyc.getId());

			// ------ Add transaction
			List<Map> lMaps = new ArrayList<>();
			lMaps.add(order);
			logger.info("kyc : " + kyc);
			Map mTrx;
			if (order.get("payment").toString().equalsIgnoreCase("finpay")) {
				mTrx = transactionService.subscribeOrderByFinpay(lMaps, kyc);
			} else {
				mTrx = transactionService.subscribeOrderByTransfer(lMaps, kyc);
			}
			logger.info("subscribe : " + mTrx);
			if (!mTrx.get("code").equals(0) || ((List) mTrx.get("data")).isEmpty()) {
				throw new Exception(mTrx.toString());
			}

			// ------ Response
			Map dataScore = new HashMap();
			dataScore.put("code", kyc.getRiskProfile().getScoreCode());
			dataScore.put("value", kyc.getRiskProfile().getScoreName());

			Map data = new HashMap();
			data.put("customer_key", kyc.getAccount().getCustomerKey());
			data.put("customer_cif", kyc.getPortalcif());
			data.put("channel_customer", kyc.getAccount().getChannelCustomer());
			data.put("customer_status", kyc.getAccount().getUserStatus());
			data.put("customer_risk_profile", dataScore);
			data.put("order", mTrx.get("data"));
			return errorResponse(0, "register_and_order", data);
		} catch (Exception e) {

			return null;
		}
	}

	@Override
	public Map loginPartner(String memberId, String jsessionId, String ip) {

		Map result = new HashMap();
		Kyc kyc;
		User user;

		if (!isExistingDataAndStringValue(memberId) || !isExistingDataAndStringValue(jsessionId)) {
			result.put(ConstantUtil.CODE, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, null, null));
			return result;
		}
		Map map = new HashMap();
		map.put("memberId", memberId);
		map.put("jsessionId", jsessionId);
		Map connectToPartner = connectToPartner(map);

		if (connectToPartner.size() > 3) {
			user = userRepository.findByChannelCustomer(connectToPartner.get("id").toString());
			if (user == null) {
				return errorResponse(50, "user", null);
			}
		} else {
			return connectToPartner;
		}

		kyc = kycRepository.findByAccount(user);
		if (kyc == null) {
			logger.error("kyc from '" + user + "' not found");
			result.put(ConstantUtil.CODE, ConstantUtil.STATUS_ERROR);
			result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, null, null));
			return result;
		}

		String token = user.generateNewToken(ip);
		user.setRecordLogin(new Date());
		user.setLastLogin(user.getRecordLogin());
		user = userRepository.save(user);
		kyc.setAccount(user);

		Map data = new HashMap();
		data.put(ConstantUtil.KYC, kyc);
		data.put(ConstantUtil.TOKEN, token);
		data.put("signature_customer",
				DigestUtils.sha384Hex(DigestUtils.sha256Hex(user.getAgent().getCode()) + user.getCustomerKey()));
		result.put("code", ConstantUtil.STATUS_SUCCESS);
		result.put(ConstantUtil.DATA, data);

		RejectionHistory rejectionHistory = rejectionHistoryRepository
				.findFirstByRejectedUserIdOrderByCreatedOnDesc(user);
		String reason = "";
		String datetime = "";
		if (rejectionHistory != null) {
			reason = rejectionHistory.getNote();
			datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(rejectionHistory.getCreatedOn());
		}
		Map rejected = new LinkedHashMap();
		rejected.put("reason", reason);
		rejected.put("datetime", datetime);
		result.put("rejected", rejected);

		logger.info("result : " + result);

		return result;
	}

	public String encryptData(String memberId, String jsessionId) {
		Map<String, Object> header = new HashMap();
		header.put("alg", "HS256");
		header.put("typ", "JWT");

		String client_key = globalParameterRepository.findByCategory("BLANJA_CLIENT_KEY").getValue();
//      String client_key = "4a89c743d76ae5bec511d227d5397602";
		String token = null;

		Date now = new Date();
		try {
			Algorithm algorithm = Algorithm.HMAC256(client_key);
			token = JWT.create().withHeader(header).withClaim("iat", now.getTime() / 1000)
					.withClaim("exp", now.getTime() / 1000 + 300).withClaim("memberId", memberId)
					.withClaim("jsessionId", jsessionId).sign(algorithm);
		} catch (JWTCreationException exception) {
			return exception.getMessage();
		}
		return token;
	}

	public Map dencryptData(String token) {
		{
			String base_url = globalParameterRepository.findByCategory("GLOBAL_URL_BLANJA").getValue();
			String url = base_url + "/api/v1/member/detail/user-detail";
			DecodedJWT jwt = null;

			Map<String, Object> header = new HashMap();
			header.put("alg", "HS256");
			header.put("typ", "JWT");

			Long exp = (new Date().getTime() + 300);
			String memberId = "testbel3";
			String jsessionId = "BB8C6A258DB154C11AB094FD6B284347";
			String client_id = "blanjareksa";
			String client_key = "Ure5QxJYNvIq70iQNUTzymp89_Vzapy5A7BANsZL";

			Map<String, Object> payload = new HashMap();
			payload.put("iat", new Date().getTime());
			payload.put("exp", exp);
			payload.put("memberId", memberId);
			payload.put("jsessionId", jsessionId);

//            Date now = new Date();
			try {
				Algorithm algorithm = Algorithm.HMAC256(client_key);
				JWTVerifier verifier = JWT.require(algorithm).build(); // Reusable verifier instance
				jwt = verifier.verify(token);
			} catch (JWTVerificationException exception) {
				// Invalid signature/claims
			}
			return errorResponse(0, "preregister_and_order", jwt);
		}
	}

	public Map connectToPartner(Map map) {
		String base_url = globalParameterRepository.findByCategory("BLANJA_BASE_URL").getValue();
		String client_id = globalParameterRepository.findByCategory("BLANJA_CLIENT_ID").getValue();
//      String base_url = "http://cbtapi.blanja.com";
//      String client_id = "blanjainvest";
		String jwt_token = encryptData(map.get("memberId").toString(), map.get("jsessionId").toString());
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("X-AUTH-TOKEN", jwt_token);
			headers.add("X-AUTH-CLIENT", client_id);

			HttpEntity entity = new HttpEntity(headers);
			logger.info("--- entity --- " + entity);
			String respon = restTemplate.postForObject(base_url + "/api/v1/member/detail/user-detail", entity,
					String.class);
			JSONParser parser = new JSONParser();
			org.json.simple.JSONObject jres = (org.json.simple.JSONObject) parser.parse(respon);
			Map response = (Map) jres;
			logger.info("--response -- " + jres);
			return response;
		} catch (Exception e) {
			return errorResponse(12, "Blanja - " + e.getMessage(), null);
		}
	}

	String generateCodeActivation() {
		String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		Integer length = 6;
		String randomString = RandomStringUtils.random(length, charset);
		return randomString;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map resendActivationCode(User user) throws MessagingException, IOException, TemplateException {
		if (!user.getUserStatus().equals("REG")) {
			return errorResponse("12", "Account alreadry activated", null);
		}

		user.setResetCode(generateCodeActivation());
		user.setUpdatedDate(new Date());
		user.setUpdatedBy(user.getUsername());
		userRepository.saveAndFlush(user);
		sendingEmailService.sendActivationEmail(kycRepository.findByAccount(user));

		return errorResponse(ConstantUtil.STATUS_SUCCESS.toString(), "Resend activation code", null);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map activationCustomer(User user, String activationCode) {
		if (!user.getUserStatus().equals("REG")) {
			return errorResponse("12", "Account alreadry activated", null);
		}

		if (!activationCode.equals(user.getResetCode())) {
			return errorResponse("14", "activation code", null);
		}

		user.setUserStatusSebelumnya(user.getUserStatus());
		user.setUserStatus("ACT");
		user.setUpdatedDate(new Date());
		user.setUpdatedBy(user.getUsername());
		userRepository.saveAndFlush(user);

		return errorResponse(ConstantUtil.STATUS_SUCCESS.toString(), "Activation customer", null);
	}

	public Map getDataMigration(Integer offset, Integer limit, String key) {
		if (key == null || !key.equals("5bac7bd2-d2a6-4cd4-be02-8307be1ec417")) {
			return errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "api-key", null);
		}

		if (offset < 0) {
			return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "offset", null);
		}

		if (limit <= 0) {
			return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "limit", null);
		}

		String sqlCount = "SELECT " + "count(kyc.customer_id) " + "FROM _user usr "
				+ "JOIN kyc kyc ON (kyc.account_id=usr.id) "
				+ "LEFT JOIN countries country ON (CAST(kyc.nationality AS INTEGER)=country.country_code) "
				+ "LEFT JOIN countries home ON (CAST(kyc.home_country AS INTEGER)=home.country_code) "
				+ "LEFT JOIN countries legal ON (CAST(kyc.legal_country AS INTEGER)=legal.country_code) "
				+ "LEFT JOIN settlement_accounts st ON (st.kycs_id=kyc.customer_id) "
				+ "LEFT JOIN bank bank ON (st.bank_id_id=bank.bank_id) "
				+ "LEFT JOIN score score ON (kyc.risk_profile_id=score.score_id) "
				+ "LEFT JOIN (SELECT cd.user_id, cd.file_key FROM customer_document cd WHERE cd.document_type='DocTyp01' AND cd.row_status=TRUE) ktp ON (ktp.user_id=kyc.account_id) "
				+ "LEFT JOIN (SELECT cd.user_id, cd.file_key FROM customer_document cd WHERE cd.document_type='DocTyp02' AND cd.row_status=TRUE) npwp ON (npwp.user_id=kyc.account_id) "
				+ "LEFT JOIN (SELECT cd.user_id, cd.file_key FROM customer_document cd WHERE cd.document_type='DocTyp05' AND cd.row_status=TRUE) selfie ON (selfie.user_id=kyc.account_id) "
				+ "LEFT JOIN (SELECT cd.user_id, cd.file_key FROM customer_document cd WHERE cd.document_type='DocTyp04' AND cd.row_status=TRUE) rekening ON (rekening.user_id=kyc.account_id) "
				+ "LEFT JOIN (SELECT cd.user_id, cd.file_key FROM customer_document cd WHERE cd.document_type='DocTyp03' AND cd.row_status=TRUE) ttd ON (ttd.user_id=kyc.account_id) "
				+ "WHERE usr.migration=TRUE ";

		String sql = "SELECT " + "usr.channel_customer, " + "kyc.first_name, " + "kyc.middle_name, " + "kyc.last_name, "
				+ "kyc.mobile_number, " + "kyc.email, " + "kyc.birth_date, " + "kyc.birth_place, " + "kyc.gender, "
				+ "country.alpha3code AS nationality, " + "kyc.marital_status, " + "kyc.mother_maiden_name, "
				+ "kyc.education_background, " + "kyc.religion, " + "kyc.preferred_mailing_address, "
				+ "kyc.occupation, " + "kyc.nature_of_business, " + "kyc.id_number, " + "kyc.id_expiration_date, "
				+ "home.alpha3code AS home_country, " + "kyc.home_province, " + "kyc.home_city, "
				+ "kyc.home_postal_code, " + "kyc.home_address, " + "kyc.home_phone_number, "
				+ "legal.alpha3code AS legal_country, " + "kyc.legal_province, " + "kyc.legal_city, "
				+ "kyc.legal_postal_code, " + "kyc.legal_address, " + "kyc.legal_phone_number, "
				+ "kyc.source_of_income, " + "kyc.total_income_pa, " + "kyc.total_asset, " + "kyc.investment_purpose, "
				+ "kyc.investment_experience, " + "kyc.other_investment_experience, " + "bank.bank_code, "
				+ "st.settlement_account_name, " + "st.settlement_account_no, " + "score.score_code, "
				+ "usr.customer_key, " + "kyc.portalcif, " + "ktp.file_key AS ktp, " + "npwp.file_key AS npwp, "
				+ "selfie.file_key AS selfie, " + "rekening.file_key AS rekening, " + "ttd.file_key AS ttd, "
				+ "usr.user_status, " + "usr.created_date, " + "usr.created_by, " + "usr.updated_date, "
				+ "usr.updated_by, " + "kyc.sid " + "FROM _user usr " + "JOIN kyc kyc ON (kyc.account_id=usr.id) "
				+ "LEFT JOIN countries country ON (CAST(kyc.nationality AS INTEGER)=country.country_code) "
				+ "LEFT JOIN countries home ON (CAST(kyc.home_country AS INTEGER)=home.country_code) "
				+ "LEFT JOIN countries legal ON (CAST(kyc.legal_country AS INTEGER)=legal.country_code) "
				+ "LEFT JOIN settlement_accounts st ON (st.kycs_id=kyc.customer_id) "
				+ "LEFT JOIN bank bank ON (st.bank_id_id=bank.bank_id) "
				+ "LEFT JOIN score score ON (kyc.risk_profile_id=score.score_id) "
				+ "LEFT JOIN (SELECT cd.user_id, cd.file_key FROM customer_document cd WHERE cd.document_type='DocTyp01' AND cd.row_status=TRUE) ktp ON (ktp.user_id=kyc.account_id) "
				+ "LEFT JOIN (SELECT cd.user_id, cd.file_key FROM customer_document cd WHERE cd.document_type='DocTyp02' AND cd.row_status=TRUE) npwp ON (npwp.user_id=kyc.account_id) "
				+ "LEFT JOIN (SELECT cd.user_id, cd.file_key FROM customer_document cd WHERE cd.document_type='DocTyp05' AND cd.row_status=TRUE) selfie ON (selfie.user_id=kyc.account_id) "
				+ "LEFT JOIN (SELECT cd.user_id, cd.file_key FROM customer_document cd WHERE cd.document_type='DocTyp04' AND cd.row_status=TRUE) rekening ON (rekening.user_id=kyc.account_id) "
				+ "LEFT JOIN (SELECT cd.user_id, cd.file_key FROM customer_document cd WHERE cd.document_type='DocTyp03' AND cd.row_status=TRUE) ttd ON (ttd.user_id=kyc.account_id) "
				+ "WHERE usr.migration=TRUE " + "ORDER BY usr.created_date ASC OFFSET :offset LIMIT :limit";

		BigInteger totalItem = (BigInteger) entityManager.createNativeQuery(sqlCount).getSingleResult();
		Query query = entityManager.createNativeQuery(sql).setParameter("offset", offset).setParameter("limit", limit);
		List<Object[]> listData = query.getResultList();
		if (listData.size() <= 0) {
			return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "Data not found", null);
		}

		List listResult = new ArrayList();
		for (Object[] data : listData) {
			Map dataResult = new LinkedHashMap();
			dataResult.put("customer", data[0]);
			dataResult.put("first_name", data[1]);
			dataResult.put("middle_name", data[2]);
			dataResult.put("last_name", data[3]);
			dataResult.put("phone_number", data[4]);
			dataResult.put("email", data[5]);
			dataResult.put("birth_date", data[6]);
			dataResult.put("birth_place", data[7]);
			dataResult.put("gender", data[8]);
			dataResult.put("nationality", data[9]);
			dataResult.put("marital_status", data[10]);
			dataResult.put("mother_name", data[11]);
			dataResult.put("education", data[12]);
			dataResult.put("religion", data[13]);
			dataResult.put("statement_type", data[14]);
			dataResult.put("occupation", data[15]);
			dataResult.put("business_nature", data[16]);
			dataResult.put("id_number", data[17]);
			dataResult.put("id_expiration", data[18]);
			dataResult.put("legal_country", data[19]);
			dataResult.put("legal_province", data[20]);
			dataResult.put("legal_city", data[21]);
			dataResult.put("legal_postal_code", data[22]);
			dataResult.put("legal_address", data[23]);
			dataResult.put("legal_phone", data[24]);
			dataResult.put("mailing_country", data[25]);
			dataResult.put("mailing_province", data[26]);
			dataResult.put("mailing_city", data[27]);
			dataResult.put("mailing_postal_code", data[28]);
			dataResult.put("mailing_address", data[29]);
			dataResult.put("mailing_phone", data[30]);
			dataResult.put("income_source", data[31]);
			dataResult.put("annual_income", data[32]);
			dataResult.put("total_asset", data[33]);
			dataResult.put("investment_purpose", data[34]);
			dataResult.put("investment_experience", data[35]);
			dataResult.put("other_investment_exp", data[36]);
			dataResult.put("settlement_bank", data[37]);
			dataResult.put("bank_account_name", data[38]);
			dataResult.put("bank_account_no", data[39]);
			dataResult.put("customer_risk_profile", data[40]);
			dataResult.put("customer_key", data[41]);
			dataResult.put("customer_cif", data[42]);
			dataResult.put("ktp", data[43]);
			dataResult.put("npwp", data[44]);
			dataResult.put("selfie", data[45]);
			dataResult.put("rekening", data[46]);
			dataResult.put("ttd", data[47]);
			dataResult.put("status", data[48]);
			dataResult.put("created_at", data[49]);
			dataResult.put("created_by", data[50]);
			dataResult.put("updated_at", data[51]);
			dataResult.put("updated_by", data[52]);
			dataResult.put("sid", data[53]);
			dataResult.put("sbn", getDataSbnTransaction((String) data[42]));
			listResult.add(dataResult);
		}

		Boolean before = offset != 0;
		Boolean after = listResult.size() + offset < totalItem.longValue();

		Map result = new LinkedHashMap();
		result.put(ConstantUtil.CODE, ConstantUtil.STATUS_SUCCESS);
		result.put(ConstantUtil.INFO.toLowerCase(), ConstantUtil.SUCCESS);
		result.put(ConstantUtil.DATA.toLowerCase(), listResult);
		result.put("totalItems", totalItem);
		result.put("itemsPerPage", listResult.size());
		result.put("before", before);
		result.put("after", after);
		return result;
	}

	Map getDataSbnTransaction(String portalCif) {
		List listTrx = new ArrayList();
		Kyc kyc = kycRepository.findByPortalcif(portalCif);
		SbnSid sbnSid = sbnSidRepository.findByKyc(kyc);
		if (sbnSid != null) {
			List<SbnTransactions> listDataTrx = sbnTransactionsRepository.findAllBySbnSidOrderByCreatedDateDesc(sbnSid);
			for (SbnTransactions trx : listDataTrx) {
				Map data = new LinkedHashMap();
				data.put("sbn_trx_amount", trx.getTrxAmount());
				data.put("sisa_kepemilikan", trx.getSisaKepemilikan());
				data.put("id_seri", trx.getIdSeri());
				data.put("redeemable_amount", trx.getRedeemableAmount());
				data.put("kode_pemesanan", trx.getKodePemesanan());
				listTrx.add(data);
			}
		}

		Map result = new LinkedHashMap();
		result.put("sid", sbnSid == null ? null : sbnSid.getSid());
		result.put("sid_name", sbnSid == null ? null : sbnSid.getSidName());
		result.put("id_partisipan_subregistry",
				sbnSid == null ? null : sbnAccountDetailRepository.getAllPartisipanSubregistry(sbnSid.getId()));
		result.put("transactions", listTrx);

		return result;
	}

	@Override
	public Map profileUpdateV2(Map map, User user) {
		Map mapData = profileUpdateTransactionalV2(map, user);
		logger.info("profile " + mapData.get("code"));
		if (mapData.get("code").equals(0)) {
			Kyc kyc = kycRepository.findByAccount(user);
			logger.info("profile " + mapData.get("code"));
			if (kyc.getAccount().getUserStatus().equalsIgnoreCase("PEN")
					&& kyc.getAccount().getUserStatusSebelumnya().equalsIgnoreCase("ACT")) {
				emailService.sendOpenRekening(kyc);
			}
		}
		return mapData;
	}

	@Transactional
	Map profileUpdateTransactionalV2(Map map, User user) {
		logger.info("##profileUpdateTransactional " + Thread.currentThread().getStackTrace()[2].getLineNumber());
		if (user.getUserStatus().equalsIgnoreCase("PEN") && user.getUserStatusSebelumnya().equalsIgnoreCase("ACT")) {
			return errorResponse(12, "profile_update", "User Pending Status");
		}

		Kyc kyc = kycRepository.findByAccount(user);
		Map customerDocument = new LinkedHashMap();
		// {"settlementAccountName":"SILVESTER KEVIN DEWANGGA
		// KURNIAWAN","legalPhoneNumber":"00-000-00000","officeCountry":"653","citizenship":"DOM","homePhoneNumber":"00-000-00000","maritalStatus":"SGL","birthPlace":"Kab.
		// Semarang","sourceOfIncome":"REV","religion":"CATH","legalCountry":"653","investmentExperience":"IE05","homeProvince":"ID-JT","motherMaidenName":"CAECILIA
		// ENDANG
		// SUSIATI","homeCity":"1975","investmentPurpose":"PDT","homeAddress":"Lingkungan
		// Sidorejo, RT 002/RW 010 Kel/Desa Bergaslor, Kecamatan
		// Bergas","legalCity":"1975","gender":"ML","birthDate":"21-12-1991","firstName":"SILVESTER","officeCity":"1652","idNumber":"3322132112910000","middleName":"","homePostalCode":"50552","legalProvince":"ID-JT","idExpirationDate":"01-01-2020","lastName":"KURNIAWAN","occupation":"9","settlementAccountNo":"2220476572","totalIncomePa":"INC1","officePhoneNumber":"62-21-3523626","officeProvince":"ID-JK","homeCountry":"653","natureOfBusiness":"4","totalAsset":"TA01","bankId":1,"educationBackground":"BCH","nationality":"653","officePostalCode":"10120","legalPostalCode":"50552","preferredMailingAddress":"2","officeAddress":"Batu
		// Tulis 3","salutation":""}
		String oldKyc = null;
		if (user.getUserStatus().equalsIgnoreCase("VER")) {
			oldKyc = jsonKyc(kyc);
		}

		SettlementAccounts accounts;

		Map fields = validateFieldProfile(map, kyc, user.getAgent());
		if (fields.get(ConstantUtil.STATUS) != null
				&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
			kyc = (Kyc) fields.get(ConstantUtil.KYC);
			accounts = (SettlementAccounts) fields.get(ConstantUtil.SETTLEMENT);
		} else {
			return fields;
		}

		if (isExistingData(map.get("email"))) {
			User findEmail = userRepository.findByEmailAndAgent(map.get("email").toString(), user.getAgent());
			logger.info("email" + findEmail);
			if (findEmail != null) {
				return errorResponse(ConstantUtil.STATUS_EXISTING_DATA, "email existing", null);
			}
		}
		logger.info("kyc : " + kyc);
		logger.info("jsonkyc : " + oldKyc);
		logger.info("accounts : " + accounts);

		List<CustomerAnswer> customerFatcas = new ArrayList<>();
		List<CustomerAnswer> customerRisk = new ArrayList<>();
		Long score;
		List<Map> lists;

		Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));

		if (isExistingData(map.get("fatca"))) {
			lists = (List<Map>) map.get("fatca");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerFatcas = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
			} else {
				return fields;
			}
		}

		if (isExistingData(map.get("risk_profile"))) {
			questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
			lists = (List<Map>) map.get("risk_profile");
			fields = validateFatcaProfile(lists, questionairesFatca, kyc);
			if (fields.get(ConstantUtil.STATUS) != null
					&& fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
				customerRisk = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
				score = (Long) fields.get(ConstantUtil.SCORE);
			} else {
				return fields;
			}

			Score riskProfile = scoreRepository.getScore(score, new Date());
			kyc.setRiskProfile(riskProfile);
		}

		if (this.checkDoc(kyc).getCompleted()) {
			if (user.getUserStatus().equalsIgnoreCase("VER")) {
				user.setUserStatus("PEN");
				user.setUserStatusSebelumnya("VER");
			} else if (user.getUserStatus().equalsIgnoreCase("PEN")) {
				user.setUserStatus("PEN");
				user.setUserStatusSebelumnya("VER");
			} else {
				user.setUserStatus("PEN");
				user.setUserStatusSebelumnya("ACT");
			}
		} else {
			logger.info("CUSTOMER DENGAN CIF " + kyc.getPortalcif().toUpperCase() + ", DOKUMENTASI BELUM LENGKAP");
		}
		user.setUpdatedDate(new Date());
		user.setUpdatedBy(user.getChannelCustomer() + "@" + user.getAgent().getCode() + ".com");

		// user.setApprovalStatus(true);
		userRepository.save(user);

		kyc.setAccount(user);
		if (oldKyc != null) {
			kyc.setOldValueKyc(oldKyc);
		}

		if (map.get("referralCode") != null) {
			Kyc kycReferral = kycRepository.findByReferralCodeAndAccount_Agent(map.get("referralCode").toString(),
					kyc.getAccount().getAgent());
			if (kycReferral != null) {
				String referralName;
				if (kycReferral.getMiddleName() == null || kycReferral.getMiddleName().trim().equalsIgnoreCase("")) {
					referralName = kycReferral.getFirstName() + " " + kycReferral.getLastName();
				} else {
					referralName = kycReferral.getFirstName() + " " + kycReferral.getMiddleName() + " "
							+ kycReferral.getLastName();
				}

				kyc.setReferral("CUS");
				kyc.setReferralName(referralName);
				kyc.setReferralCus(kycReferral);
			} else {
				return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "referral code", null);
			}
		}

		kyc = kycRepository.save(kyc);
		logger.info("save user kyc success");

		accounts.setKycs(kyc);
		settlementAccountsRepository.save(accounts);
		logger.info("save settlement account success");

		for (CustomerAnswer customerAnswer : customerRisk) {
			customerAnswer.setKyc(kyc);
			customerAnswerRepository.save(customerAnswer);
		}
		logger.info("save customer risk success");

		for (CustomerAnswer customerAnswer : customerFatcas) {
			customerAnswer.setKyc(kyc);
			customerAnswerRepository.save(customerAnswer);
		}
		logger.info("save customer fatcas success");

		List<Map> listFile = (List<Map>) map.get("file_upload");
		Boolean ktp = false;
		Boolean ttd = false;
		Boolean selfie = false;
		List<FileDto> fileDtos = new ArrayList<>();
		for (Map fileUpload : listFile) {
			if (fileUpload.get("content") == null || fileUpload.get("document_type") == null
					|| fileUpload.get("extention") == null) {
				return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "file_upload", null);
			}

			if (!(Boolean) ValidateUtil.checkBase64(fileUpload.get("content").toString())) {
				return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "content file_upload", null);
			}

			byte[] content = Base64.decodeBase64(fileUpload.get("content").toString());
			String type = fileUpload.get("document_type").toString();
			String ext = fileUpload.get("extention").toString();

			if (type.equals("DocTyp01")) {
				ktp = true;
			} else if (type.equals("DocTyp03")) {
				ttd = true;
			} else if (type.equals("DocTyp05")) {
				selfie = true;
			}

			FileDto fileDto = new FileDto();
			fileDto.setContent(content);
			fileDto.setDocumentType(type);
			fileDto.setExtention(ext);
			fileDtos.add(fileDto);
		}
		if (!ktp) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "file_upload ktp", null);
		}

		if (!ttd) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "file_upload ttd", null);
		}

		if (!selfie) {
			return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "file_upload selfie", null);
		}

		Map dataUpload = null;
		try {
			dataUpload = uploadDocument(kyc, fileDtos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("save iamge faield " + e);
		}

		dataUpload = (Map) dataUpload.get("data");
		for (Map customerDoc : (List<Map>) dataUpload.get(ConstantUtil.DOCUMENT)) {
			if ("DocTyp01".equals(customerDoc.get("type").toString())) {
				customerDocument.put("id_card_image", customerDoc.get("key"));
			} else if ("DocTyp03".equals(customerDoc.get("type").toString())) {
				customerDocument.put("signature_image", customerDoc.get("key"));
			} else if ("DocTyp05".equals(customerDoc.get("type").toString())) {
				customerDocument.put("selfie_image", customerDoc.get("key"));
			}
		}

		kyc = validateStatusUser(kyc);
		user = kyc.getAccount();

		Score riskProfile = kyc.getRiskProfile();

		Map dataScore = new HashMap();
		dataScore.put("code", riskProfile.getScoreCode());
		dataScore.put("value", riskProfile.getScoreName());

		Map data = new HashMap();
		data.put("customer_key", user.getCustomerKey());
		data.put("customer_id", kyc.getPortalcif());
		data.put("customer_status", user.getUserStatus());
		data.put("customer_document", customerDocument);
		data.put("customer_risk_profile", dataScore);

		return errorResponse(ConstantUtil.STATUS_SUCCESS, "profile & document updated", data);
	}
}