//package com.nsi.services.impl;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import com.nsi.domain.core.Agent;
//import com.nsi.domain.core.AgentCredential;
//import com.nsi.domain.core.Answer;
//import com.nsi.domain.core.Bank;
//import com.nsi.domain.core.Cities;
//import com.nsi.domain.core.Countries;
//import com.nsi.domain.core.CustomerAnswer;
//import com.nsi.domain.core.CustomerDocument;
//import com.nsi.domain.core.DocumentType;
//import com.nsi.domain.core.GlobalParameter;
//import com.nsi.domain.core.Kyc;
//import com.nsi.domain.core.LookupHeader;
//import com.nsi.domain.core.LookupLine;
//import com.nsi.domain.core.Question;
//import com.nsi.domain.core.Questionaires;
//import com.nsi.domain.core.Score;
//import com.nsi.domain.core.SettlementAccounts;
//import com.nsi.domain.core.States;
//import com.nsi.domain.core.User;
//import com.nsi.enumeration.CustomerEnum;
//import com.nsi.repositories.core.AgentCredentialRepository;
//import com.nsi.repositories.core.AgentRepository;
//import com.nsi.repositories.core.AnswerRepository;
//import com.nsi.repositories.core.BankRepository;
//import com.nsi.repositories.core.CitiesRepository;
//import com.nsi.repositories.core.CountriesRepository;
//import com.nsi.repositories.core.CustomerAnswerRepository;
//import com.nsi.repositories.core.CustomerDocumentRepository;
//import com.nsi.repositories.core.DocumentTypeRepository;
//import com.nsi.repositories.core.GlobalParameterRepository;
//import com.nsi.repositories.core.KycRepository;
//import com.nsi.repositories.core.LookupHeaderRepository;
//import com.nsi.repositories.core.LookupLineRepository;
//import com.nsi.repositories.core.QuestionRepository;
//import com.nsi.repositories.core.QuestionairesRepository;
//import com.nsi.repositories.core.ScoreRepository;
//import com.nsi.repositories.core.SettlementAccountsRepository;
//import com.nsi.repositories.core.StatesRepository;
//import com.nsi.repositories.core.UserRepository;
//import com.nsi.services.AgentService;
//import com.nsi.services.ChannelService;
//import com.nsi.services.CustomerService;
//import com.nsi.services.UtilService;
//import com.nsi.util.ConstantUtil;
//import com.nsi.util.DateTimeUtil;
//import com.nsi.util.ValidateUtil;
//import java.io.File;
//import java.nio.file.Paths;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.multipart.MultipartFile;
//
//public class CustomerServiceImpl1 extends BaseService implements CustomerService {
//
//    @Autowired
//    KycRepository kycRepository;
//    @Autowired
//    SettlementAccountsRepository settlementAccountsRepository;
//    @Autowired
//    QuestionairesRepository questionairesRepository;
//    @Autowired
//    QuestionRepository questionRepository;
//    @Autowired
//    CustomerAnswerRepository customerAnswerRepository;
//    @Autowired
//    AnswerRepository answerRepository;
//    @Autowired
//    CustomerDocumentRepository customerDocumentRepository;
//    @Autowired
//    DocumentTypeRepository documentTypeRepository;
//    @Autowired
//    LookupHeaderRepository lookupHeaderRepository;
//    @Autowired
//    LookupLineRepository lookupLineRepository;
//    @Autowired
//    AgentRepository agentRepository;
//    @Autowired
//    UserRepository userRepository;
//    @Autowired
//    CountriesRepository countriesRepository;
//    @Autowired
//    StatesRepository statesRepository;
//    @Autowired
//    CitiesRepository citiesRepository;
//    @Autowired
//    BankRepository bankRepository;
//    @Autowired
//    ScoreRepository scoreRepository;
//    @Autowired
//    ChannelService channelService;
//    @Autowired
//    UtilService utilService;
//    @Autowired
//    AgentService agentService;
//    @Autowired
//    AgentCredentialRepository agentCredentialRepository;
//    @Autowired
//    GlobalParameterRepository globalParameterRepository;
//
//    private Map validateFatcaProfile(List<Map> maps, Questionaires questionairesFatca, Kyc kyc) {
//        List<Question> listQuestionFatca = questionRepository.findAllByQuestionairesOrderBySeqAsc(questionairesFatca);
//        Long score = Long.valueOf("0");
//        List<String> quests = new ArrayList<>();
//        for (Map map : maps) {
//            System.out.println("question1 : " + map.get("question").toString());
//            quests.add(map.get("question").toString());
//        }
//
//        Integer ques = 0;
//        for (Question question : listQuestionFatca) {
//            System.out.println("question2 : " + question.getQuestionName());
//
//            if (quests.contains(question.getQuestionName())) {
//                ques += 1;
//            }
//        }
//
//        if (listQuestionFatca.size() < ques) {
//            return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, questionairesFatca.getQuestionnaireName(), null);
//        }
//
//        List<CustomerAnswer> listFatcaAnswer = new ArrayList<>();
//
//        for (Map map : maps) {
//            Question question = questionRepository.findFirstByQuestionairesAndQuestionNameOrderByIdDesc(questionairesFatca, map.get("question").toString());
//            if (question == null) {
//                return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "question " + map.get("question"), null);
//            }
//            List<String> answers = (List<String>) map.get("answers");
//            for (String answer : answers) {
//                Answer ans = answerRepository.findByAnswerNameAndQuestion(answer, question);
//                if (ans == null) {
//                    return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "answers " + answer, null);
//                }
//
//                CustomerAnswer ca = new CustomerAnswer();
//                ca.setAnswer(ans);
//                ca.setKyc(kyc);
//                ca.setQuestion(question);
//                ca.setVersion(0);
//                ca.setCreatedDate(new Date());
//                ca.setCreatedBy(kyc.getAccount().getUsername());
//
//                listFatcaAnswer.add(ca);
//                score += ans.getScore();
//            }
//        }
//
//        Map datas = new HashMap();
//        datas.put(ConstantUtil.QUESTION, listFatcaAnswer);
//        datas.put(ConstantUtil.SCORE, score);
//        datas.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
//
//        return datas;
//    }
//
//    private Map validateFieldProfile(Map map, Kyc kyc, Agent agent) {
//        boolean isInsert = false;
//        User user;
//        SettlementAccounts settlementAccounts;
//        Date birthDate = null;
//        Date expirationDate = null;
//        Countries nationality = null;
//        Countries legalCountry = null;
//        Countries homeCountry = null;
//        LookupLine gender = null;
//        LookupLine marital = null;
//        LookupLine education = null;
//        LookupLine religion = null;
//        LookupLine statementType = null;
//        LookupLine occupation = null;
//        LookupLine businessNature = null;
//        LookupLine sourceOfIncome = null;
//        LookupLine totalIncome = null;
//        LookupLine totalAset = null;
//        LookupLine investmentPurpose = null;
//        LookupLine investmentExperience = null;
//        States legalProvince = null;
//        States homeProvince = null;
//        Cities homeCity = null;
//        Cities legalCity = null;
//        String otherInvestmentExperience = null;
//        Bank bank = null;
//
//        if (kyc == null) {
//            isInsert = true;
//
//            user = new User();
//            user.setAgent(agent);
//            user.setAccountExpired(false);
//            user.setAccountLocked(false);
//            user.setEnabled(true);
//            user.setUserStatus("ACT");
//            user.setUserStatusSebelumnya("REG");
//            user.setApprovalStatus(false);
//            user.setSecurityLevel("NOR");
//            user.setCustomerKey(UUID.randomUUID().toString());
//            user.setCreatedDate(new Date());
//            user.setIsProcess(true);
//
//            kyc = new Kyc();
//            kyc.setPortalcif(utilService.generatePortalCIF());
//            kyc.setCitizenship("DOM");
//            kyc.setVersion(Long.valueOf("0"));
//            kyc.setCreatedDate(user.getCreatedDate());
//            kyc.setIdType("IDC");
//
//            settlementAccounts = new SettlementAccounts();
//            settlementAccounts.setCreatedDate(new Date());
//        } else {
//            settlementAccounts = settlementAccountsRepository.findByKycs(kyc);
//            user = kyc.getAccount();
//        }
//
//        if (!isExistingData(map.get("customer"))) {
//            return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "customer", null);
//        }
//
//        if (!isExistingData(map.get("agent"))) {
//            return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "agent", null);
//        }
//
//        if (!isExistingData(map.get("signature"))) {
//            return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "signature", null);
//        }
//
//        if (!isExistingData(map.get("first_name")) && isInsert) {
//            return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "first_name", null);
//        }
//
//        if (!isExistingData(map.get("last_name")) && isInsert) {
//            return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "last_name", null);
//        }
//
//        if (!isExistingData(map.get("phone_number")) && isInsert) {
//            return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "phone_number", null);
//        } else if (isExistingData(map.get("phone_number"))) {
//            if (!ValidateUtil.phoneNumber(map.get("phone_number").toString())) {
//                return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "phone_number", null);
//            }
//        }
//
//        if (!isExistingData(map.get("email")) && isInsert) {
//            return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "email", null);
//        } else if (isExistingData(map.get("email"))) {
//            if (!ValidateUtil.email(map.get("email").toString())) {
//                return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "email", null);
//            }
//        }
//
//        logger.info("cek kyc");
//
//        if (!isExistingData(map.get("kyc")) && isInsert) {
//            return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc", null);
//        }
//
//        if (map.get("kyc") != null) {
//            Map kycs = (Map) map.get("kyc");
//
//            if (!isExistingData(kycs.get("income_source")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.income_source", null);
//            } else if (isExistingData(kycs.get("income_source"))) {
//                sourceOfIncome = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("SOURCE_OF_INCOME"), kycs.get("income_source").toString(), true);
//                if (sourceOfIncome == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.income_source", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("annual_income")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.annual_income", null);
//            } else if (isExistingData(kycs.get("annual_income"))) {
//                totalIncome = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("ANNUAL_INCOME"), kycs.get("annual_income").toString(), true);
//                if (totalIncome == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.annual_income", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("total_asset")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.total_asset", null);
//            } else if (isExistingData(kycs.get("total_asset"))) {
//                totalAset = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("TOTAL_ASSET"), kycs.get("total_asset").toString(), true);
//                if (totalAset == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.total_asset", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("investment_purpose")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.investment_purpose", null);
//            } else if (isExistingData(kycs.get("investment_purpose"))) {
//                investmentPurpose = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("INVESTMENT_PURPOSE"), kycs.get("investment_purpose").toString(), true);
//                if (investmentPurpose == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.investment_purpose", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("investment_experience")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.investment_experience", null);
//            } else if (isExistingData(kycs.get("investment_experience"))) {
//                investmentExperience = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("INVESTMENT_EXPERIENCE"), kycs.get("investment_experience").toString(), true);
//                if (investmentExperience == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.investment_experience", null);
//                }
//
//                if (investmentExperience != null && investmentExperience.getCode().equalsIgnoreCase("IE04")) {
//                    if (!isExistingData(kycs.get("other_investment_experience"))) {
//                        return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.other_investment_experience", null);
//                    }
//                    otherInvestmentExperience = kycs.get("other_investment_experience").toString();
//                }
//            }
//
//            if (!isExistingData(kycs.get("settlement_bank")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.settlement_bank", null);
//            } else if (isExistingData(kycs.get("settlement_bank"))) {
//                bank = bankRepository.findByBankCode(kycs.get("settlement_bank").toString());
//            }
//
//            if (!isExistingData(kycs.get("settlement_account_name")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.settlement_account_name", null);
//            }
//
//            if (!isExistingData(kycs.get("settlement_account_no")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.settlement_account_no", null);
//            }
//
//            if (!isExistingData(kycs.get("birth_date")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.birth_date", null);
//            } else if (isExistingData(kycs.get("birth_date"))) {
//                birthDate = DateTimeUtil.convertStringToDateCustomized(kycs.get("birth_date").toString(), DateTimeUtil.API_MCW);
//                if (birthDate == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "kyc.birth_date", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("birth_place")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.birth_place", null);
//            }
//
//            if (!isExistingData(kycs.get("gender")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.gender", null);
//            } else if (isExistingData(kycs.get("gender"))) {
//                gender = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("GENDER"), kycs.get("gender").toString(), true);
//                if (gender == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.gender", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("nationality")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.nationality", null);
//            } else if (isExistingData(kycs.get("nationality"))) {
//                nationality = countriesRepository.findByAlpha3Code(kycs.get("nationality").toString());
//                if (nationality == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.nationality", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("marital_status")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.marital_status", null);
//            } else if (isExistingData(kycs.get("marital_status"))) {
//                marital = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("MARITAL_STATUS"), kycs.get("marital_status").toString(), true);
//                if (marital == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.marital_status", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("mother_maiden_name")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.mother_maiden_name", null);
//            }
//
//            if (!isExistingData(kycs.get("education_background")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.education_background", null);
//            } else if (isExistingData(kycs.get("education_background"))) {
//                education = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("EDUCATION_BACKGROUND"), kycs.get("education_background").toString(), true);
//                if (education == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.education_background", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("religion")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.religion", null);
//            } else if (isExistingData(kycs.get("religion"))) {
//                religion = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("RELIGION"), kycs.get("religion").toString(), true);
//                if (religion == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.religion", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("statement_type")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.statement_type", null);
//            } else if (isExistingData(kycs.get("statement_type"))) {
//                statementType = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("STATEMENT_TYPE"), kycs.get("statement_type").toString(), true);
//                if (statementType == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.statement_type", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("occupation")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.occupation", null);
//            } else if (isExistingData(kycs.get("occupation"))) {
//                occupation = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("OCCUPATION"), kycs.get("occupation").toString(), true);
//                if (occupation == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.occupation", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("business_nature")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.business_nature", null);
//            } else if (isExistingData(kycs.get("business_nature"))) {
//                businessNature = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(lookupHeaderRepository.findByCategory("NATURE_OF_BUSINESS"), kycs.get("business_nature").toString(), true);
//                if (businessNature == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.business_nature", null);
//                }
//            }
//
//            if (!isExistingData(kycs.get("id_number")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.id_number", null);
//            }
//
//            if (!isExistingData(kycs.get("id_expiration")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.id_expiration", null);
//            } else if (isExistingData(kycs.get("id_expiration"))) {
//                expirationDate = DateTimeUtil.convertStringToDateCustomized(kycs.get("id_expiration").toString(), DateTimeUtil.API_MCW);
//                if (expirationDate == null && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "kyc.id_expiration", null);
//                }
//            }
//
//            logger.info("cek kyc.legal");
//
//            if (!isExistingData(kycs.get("legal")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.legal", null);
//            }
//
//            if (kycs.get("legal") != null) {
//                Map legals = (Map) kycs.get("legal");
//
//                if (!isExistingData(legals.get("country")) && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.legal.country", null);
//                } else if (isExistingData(legals.get("country"))) {
//                    legalCountry = countriesRepository.findByAlpha3Code(legals.get("country").toString());
//                    if (legalCountry == null && isInsert) {
//                        return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.legal.country", null);
//                    }
//                }
//
//                if (!isExistingData(legals.get("province")) && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.legal.province", null);
//                } else if (isExistingData(legals.get("province"))) {
//                    legalProvince = statesRepository.findByStateCode(legals.get("province").toString());
//                    if (legalProvince == null && isInsert) {
//                        return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.legal.province", null);
//                    }
//                }
//
//                if (!isExistingData(legals.get("city")) && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.legal.city", null);
//                } else if (isExistingData(legals.get("city"))) {
//                    legalCity = citiesRepository.findByCityCode(legals.get("city").toString());
//                    if (legalCity == null && isInsert) {
//                        return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.legal.city", null);
//                    }
//                }
//
//                if (!isExistingData(legals.get("postal_code")) && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.legal.postal_code", null);
//                }
//
//                if (!isExistingData(legals.get("address")) && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.legal.address", null);
//                }
//
//                if (!isExistingData(legals.get("phone")) && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.legal.phone", null);
//                } else if (isExistingData(legals.get("phone"))) {
//                    if (!ValidateUtil.fixlineNumber(legals.get("phone").toString())) {
//                        return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "kyc.legal.phone", null);
//                    }
//                }
//            }
//
//            logger.info("cek kyc.mailing");
//
//            if (!isExistingData(kycs.get("mailing")) && isInsert) {
//                return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.mailing", null);
//            }
//
//            if (kycs.get("mailing") == null) {
//                Map mailings = (Map) kycs.get("mailing");
//
//                if (!isExistingData(mailings.get("country")) && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.mailing.country", null);
//                } else if (isExistingData(mailings.get("country"))) {
//                    homeCountry = countriesRepository.findByAlpha3Code(mailings.get("country").toString());
//                    if (homeCountry == null && isInsert) {
//                        return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.mailing.country", null);
//                    }
//                }
//
//                if (!isExistingData(mailings.get("province")) && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.mailing.province", null);
//                } else if (isExistingData(mailings.get("province"))) {
//                    homeProvince = statesRepository.findByStateCode(mailings.get("province").toString());
//                    if (homeProvince == null && isInsert) {
//                        return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.mailing.province", null);
//                    }
//                }
//
//                if (!isExistingData(mailings.get("city")) && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.mailing.city", null);
//                } else if (isExistingData(mailings.get("city"))) {
//                    homeCity = citiesRepository.findByCityCode(mailings.get("city").toString());
//                    if (homeCity == null && isInsert) {
//                        return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "kyc.mailing.city", null);
//                    }
//                }
//
//                if (!isExistingData(mailings.get("postal_code")) && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.mailing.postal_code", null);
//                }
//
//                if (!isExistingData(mailings.get("address")) && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.mailing.address", null);
//                }
//
//                if (!isExistingData(mailings.get("phone")) && isInsert) {
//                    return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "kyc.mailing.phone", null);
//                } else if (isExistingData(mailings.get("phone"))) {
//                    if (!ValidateUtil.fixlineNumber(mailings.get("phone").toString())) {
//                        return errorResponse(ConstantUtil.STATUS_INVALID_FORMAT, "kyc.mailing.phone", null);
//                    }
//                }
//            }
//
//        }
//
//        logger.info("validasi field ok");
//
//        if (isInsert) {
//            String emailKey = map.get("customer").toString().toLowerCase() + "@" + agent.getCode().toLowerCase() + "." + agent.getChannel().getCode().toLowerCase();
//            String email = map.get("email").toString();
//
//            User findEmailKey = userRepository.findByEmail(emailKey);
//            if (findEmailKey != null) {
//                return errorResponse(ConstantUtil.STATUS_EXISTING_DATA, "Customer already exist", null);
//            }
//
////            User findEmail = userRepository.findByUserEmailAndAgent(email, agent);
////            if (findEmail != null) {
////                return errorResponse(ConstantUtil.STATUS_EXISTING_DATA, "email", null);
////            }
//
//            user.setEmail(emailKey);
//            user.setUsername(emailKey);
//            user.setCreatedBy(emailKey);
//            user.setPassword(emailKey);
//            user.setPasswordTemp(emailKey);
//            user.setCreatedBy(emailKey);
//
////            user.setUserEmail(email);
//            kyc.setEmail(email);
//        }
//
//        if (map.get("first_name") != null) {
//            kyc.setFirstName(map.get("first_name").toString());
//        }
//        if (map.get("last_name") != null) {
//            kyc.setLastName(map.get("last_name").toString());
//        }
//        if (birthDate != null) {
//            kyc.setBirthDate(birthDate);
//        }
//        if (map.get("phone_number") != null) {
//            kyc.setMobileNumber(map.get("phone_number").toString());
//        }
//
//        if (map.get("kyc") != null) {
//            Map kycs = (Map) map.get("kyc");
//
//            if (gender != null) {
//                kyc.setGender(gender.getCode());
//            }
//            if (nationality != null) {
//                kyc.setNationality(nationality.getAlpha3Code());
//            }
//            if (marital != null) {
//                kyc.setMaritalStatus(marital.getCode());
//            }
//            if (bank != null) {
//                settlementAccounts.setBankId(bank);
//            }
//            if (kycs.get("settlement_account_no") != null) {
//                settlementAccounts.setSettlementAccountNo(kycs.get("settlement_account_no").toString());
//            }
//            if (kycs.get("settlement_account_name") != null) {
//                settlementAccounts.setSettlementAccountName(kycs.get("settlement_account_name").toString());
//            }
//            if (sourceOfIncome != null) {
//                kyc.setSourceOfIncome(sourceOfIncome.getCode());
//            }
//            if (totalIncome != null) {
//                kyc.setTotalIncomePa(totalIncome.getCode());
//            }
//            if (totalAset != null) {
//                kyc.setTotalAsset(totalAset.getCode());
//            }
//            if (investmentPurpose != null) {
//                kyc.setInvestmentPurpose(investmentPurpose.getCode());
//            }
//            if (investmentExperience != null) {
//                kyc.setInvestmentExperience(investmentExperience.getCode());
//            }
//            if (otherInvestmentExperience != null) {
//                kyc.setOtherInvestmentExperience(otherInvestmentExperience);
//            }
//            if (kycs.get("birth_place") != null) {
//                kyc.setBirthPlace(kycs.get("birth_place").toString());
//            }
//            if (kycs.get("mother_maiden_name") != null) {
//                kyc.setMotherMaidenName(kycs.get("mother_maiden_name").toString());
//            }
//            if (education != null) {
//                kyc.setEducationBackground(education.getCode());
//            }
//            if (religion != null) {
//                kyc.setReligion(religion.getCode());
//            }
//            if (statementType != null) {
//                kyc.setPreferredMailingAddress(statementType.getCode());
//            }
//            if (occupation != null) {
//                kyc.setOccupation(occupation.getCode());
//            }
//            if (businessNature != null) {
//                kyc.setNatureOfBusiness(businessNature.getCode());
//            }
//            if (kycs.get("id_number") != null) {
//                kyc.setIdNumber(kycs.get("id_number").toString());
//            }
//            if (expirationDate != null) {
//                kyc.setIdExpirationDate(expirationDate);
//            }
//            if (kycs.get("legal") != null) {
//                Map legals = (Map) kycs.get("legal");
//
//                if (legalCountry != null) {
//                    kyc.setLegalCountry(legalCountry.getId().toString());
//                }
//                if (legalProvince != null) {
//                    kyc.setLegalProvince(legalProvince.getStateCode());
//                }
//                if (legalCity != null) {
//                    kyc.setLegalCity(legalCity.getId().toString());
//                }
//                if (legals.get("postal_code") != null) {
//                    kyc.setLegalPostalCode(legals.get("postal_code").toString());
//                }
//                if (legals.get("address") != null) {
//                    kyc.setLegalAddress(legals.get("address").toString());
//                }
//                if (legals.get("phone") != null) {
//                    kyc.setLegalPhoneNumber(legals.get("phone").toString());
//                }
//            }
//            if (kycs.get("mailing") != null) {
//                Map legals = (Map) kycs.get("mailing");
//
//                if (homeCountry != null) {
//                    kyc.setHomeCountry(homeCountry.getId().toString());
//                    kyc.setOfficeCountry(homeCountry.getId().toString());
//                }
//                if (homeProvince != null) {
//                    kyc.setHomeProvince(homeProvince.getStateCode());
//                    kyc.setOfficeProvince(homeProvince.getStateCode());
//                }
//                if (homeCity != null) {
//                    kyc.setHomeCity(homeCity.getId().toString());
//                    kyc.setOfficeCity(homeCity.getId().toString());
//                }
//                if (legals.get("postal_code") != null) {
//                    kyc.setHomePostalCode(legals.get("postal_code").toString());
//                    kyc.setOfficePostalCode(legals.get("postal_code").toString());
//                }
//                if (legals.get("address") != null) {
//                    kyc.setLegalAddress(legals.get("address").toString());
//                    kyc.setOfficeAddress(legals.get("address").toString());
//                }
//                if (legals.get("phone") != null) {
//                    kyc.setLegalPhoneNumber(legals.get("phone").toString());
//                    kyc.setOfficePhoneNumber(legals.get("phone").toString());
//                }
//            }
//        }
//
//        kyc.setAccount(user);
//        settlementAccounts.setKycs(kyc);
//
//        Map datas = new HashMap();
//        datas.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
//        datas.put(ConstantUtil.KYC, kyc);
//        datas.put(ConstantUtil.SETTLEMENT, settlementAccounts);
//
//        logger.info(datas);
//
//        return datas;
//    }
//
//    private Kyc validateStatusUser(Kyc kyc) {
//        User user = kyc.getAccount();
//        System.out.println("user : " + user);
//        List<String> docs = customerDocumentRepository.getDocValid(user, "DocTyp01", "DocTyp03");
//        System.out.println("docs : " + docs);
//        if (docs != null && docs.size() >= 2 && !user.getUserStatus().equalsIgnoreCase("PEN")) {
//            user.setUserStatusSebelumnya(user.getUserStatus());
//            user.setUserStatus("PEN");
//            user = userRepository.save(user);
//            kyc.setAccount(user);
//        }
//        return kyc;
//    }
//
//    @Override
//    public Map login(String customerCif, String signature, String ip) {
//        Map result = new HashMap();
//
//        if (!isExistingDataAndStringValue(signature) || !isExistingDataAndStringValue(customerCif)) {
//            result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
//            result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, null, null));
//            return result;
//        }
//
//        Kyc kyc = kycRepository.findByPortalcif(customerCif);
//        if (kyc == null) {
//            logger.error("kyc from '" + customerCif + "' not found");
//            result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
//            result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, null, null));
//            return result;
//        }
//
//        User user = kyc.getAccount();
//        if (!agentService.checkSignatureCustomer(user, signature)) {
//            logger.error("agentService from customerCif : '" + customerCif + "' and '" + signature + "' not found");
//            result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
//            result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "login", null));
//            return result;
//        }
//
//        String token = user.generateNewToken(ip);
//
//        user.setRecordLogin(new Date());
//        user.setLastLogin(user.getRecordLogin());
//        user = userRepository.save(user);
//        kyc.setAccount(user);
//
//        Map data = new HashMap();
//        data.put(ConstantUtil.KYC, kyc);
//        data.put(ConstantUtil.TOKEN, token);
//
//        result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
//        result.put(ConstantUtil.DATA, data);
//
//        logger.info("result : " + result);
//
//        return result;
//    }
//
//    @Override
//    @Transactional
//    public Map uploadDocument(User user, MultipartFile uploadfile, String documentType) throws Exception {
//        Map result = new HashMap();
//
//        if (!isExistingData(user) || !isExistingData(uploadfile) || !isExistingData(documentType)) {
//            result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
//            result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, null, null));
//            return result;
//        }
//
//        Kyc kyc = kycRepository.findByAccount(user);
//        if (kyc == null) {
//            logger.error("kyc from userId : '" + user.getId() + "' not found");
//            result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
//            result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, null, null));
//            return result;
//        }
//
//        DocumentType docType = documentTypeRepository.findByCodeAndRowStatus(documentType, true);
//        if (docType == null) {
//            logger.error("docType from type : '" + documentType + "' not found");
//            result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
//            result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "type document", null));
//            return result;
//        }
//
//        GlobalParameter globalPath = globalParameterRepository.findByName(ConstantUtil.GLOBAL_PARAM_CUSTOMER_FILE_PATH);
//        if (globalPath == null) {
//            logger.error("GlobalParameter CUSTOMER_FILE_PATH not found");
//            result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_ERROR);
//            result.put(ConstantUtil.DATA, errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, null, null));
//            return result;
//        }
//
//        String directory = globalPath.getValue();
//        File dir = new File(directory);
//        if (!dir.exists()) {
//            System.out.println("isDirectoryCreated : " + dir.mkdirs());
//        }
//
//        String filename = uploadfile.getOriginalFilename();
//        Long fileSize = uploadfile.getSize();
//        String filepath = Paths.get(directory, System.currentTimeMillis() + "_" + filename).toString();
//        String contentType = uploadfile.getContentType();
//        Integer version = 0;
//        CustomerDocument doc = null;
//
//        List<CustomerDocument> actExist = customerDocumentRepository.findTop1ByDocumentTypeAndUserOrderByCreatedOnDesc(docType.getCode(), user);
//        if (actExist != null && !actExist.isEmpty()) {
//            doc = actExist.get(0);
//            if (doc.getRowStatus()) {
//                version = doc.getVersion() + 1;
//                doc = null;
//            }
//        }
//
//        if (doc == null) {
//            doc = new CustomerDocument();
//            doc.setFileKey(UUID.randomUUID().toString());
//            doc.setDocumentType(docType.getCode());
//            doc.setSourceType(CustomerEnum._CUSTOMER.getName());
//            doc.setRowStatus(false);
//        } else {
//            File file = new File(doc.getFileLocation());
//            if (file.exists()) {
//                System.out.println("delete file : " + file.delete());
//            }
//        }
//
//        doc.setFileName(filename);
//        doc.setUser(user);
//        doc.setFileLocation(filepath);
//        doc.setFileType(contentType);
//        doc.setFileSize(fileSize);
//        doc.setCreatedBy(user.getUsername());
//        doc.setCreatedOn(new Date());
//        doc.setVersion(version);
//        doc.setEndedOn(DateTimeUtil.convertStringToDateCustomized("9999-12-31", DateTimeUtil.API_MCW));
//
//        doc = customerDocumentRepository.save(doc);
//
//        System.out.println("doc.getFileLocation() : " + doc.getFileLocation());
//
//        uploadfile.transferTo(new File(doc.getFileLocation()));
//
//        System.out.println("doc.getFileLocation() : " + doc.getFileLocation());
//
//        kyc = validateStatusUser(kyc);
//
//        System.out.println("kyc : " + kyc);
//
//        Map data = new HashMap();
//        data.put(ConstantUtil.KYC, kyc);
//        data.put(ConstantUtil.DOCUMENT, doc);
//        data.put(ConstantUtil.TYPE, docType.getDescription());
//
//        result.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
//        result.put(ConstantUtil.DATA, data);
//
//        return result;
//    }
//
//    @Override
//    public Map profileView(User user) {
//        Kyc kyc = kycRepository.findByAccount(user);
//        Countries nationality = countriesRepository.findById(Long.valueOf(kyc.getNationality()));
//        Countries legalCountry = countriesRepository.findById(Long.valueOf(kyc.getLegalCountry()));
//        Countries homeCountry = countriesRepository.findById(Long.valueOf(kyc.getHomeCountry()));
//        Cities legalCity = citiesRepository.findById(Long.valueOf(kyc.getLegalCity()));
//        Cities homeCity = citiesRepository.findById(Long.valueOf(kyc.getHomeCity()));
//        SettlementAccounts account = settlementAccountsRepository.findByKycs(kyc);
//
//        Map dataGeneral = new HashMap();
//        dataGeneral.put("first_name", kyc.getFirstName());
//        dataGeneral.put("last_name", kyc.getLastName());
//        dataGeneral.put("phone_number", kyc.getMobileNumber());
//
//        Map dataLegal = new HashMap();
//        dataLegal.put("country", legalCountry.getAlpha3Code());
//        dataLegal.put("province", kyc.getLegalProvince());
//        dataLegal.put("city", legalCity.getCityCode());
//        dataLegal.put("postal_code", kyc.getLegalPostalCode());
//        dataLegal.put("address", kyc.getLegalAddress());
//        dataLegal.put("phone", kyc.getLegalPhoneNumber());
//
//        Map dataMailing = new HashMap();
//        dataMailing.put("country", homeCountry.getAlpha3Code());
//        dataMailing.put("province", kyc.getHomeProvince());
//        dataMailing.put("city", homeCity.getCityCode());
//        dataMailing.put("postal_code", kyc.getHomePostalCode());
//        dataMailing.put("address", kyc.getHomeAddress());
//        dataMailing.put("phone", kyc.getHomePhoneNumber());
//
//        Map dataKyc = new HashMap();
//        dataKyc.put("birth_date", DateTimeUtil.convertDateToStringCustomized(kyc.getBirthDate(), DateTimeUtil.API_MCW));
//        dataKyc.put("birth_place", kyc.getBirthPlace());
//        dataKyc.put("gender", kyc.getGender());
//        dataKyc.put("email", kyc.getEmail());
//        dataKyc.put("nationality", nationality.getAlpha3Code());
//        dataKyc.put("marital_status", kyc.getMaritalStatus());
//        dataKyc.put("mother_maiden_name", kyc.getMotherMaidenName());
//        dataKyc.put("annual_income", kyc.getTotalIncomePa());
//        dataKyc.put("education_background", kyc.getEducationBackground());
//        dataKyc.put("religion", kyc.getReligion());
//        dataKyc.put("statement_type", kyc.getPreferredMailingAddress());
//        dataKyc.put("occupation", kyc.getOccupation());
//        dataKyc.put("business_nature", kyc.getNatureOfBusiness());
//        dataKyc.put("id_number", kyc.getIdNumber());
//        dataKyc.put("id_expiration", DateTimeUtil.convertDateToStringCustomized(kyc.getIdExpirationDate(), DateTimeUtil.DATE_TIME_MCW));
//        dataKyc.put("legal", dataLegal);
//        dataKyc.put("mailing", dataMailing);
//        dataKyc.put("income_source", kyc.getSourceOfIncome());
//        dataKyc.put("total_asset", kyc.getTotalAsset());
//        dataKyc.put("investment_purpose", kyc.getInvestmentPurpose());
//        dataKyc.put("investment_experience", kyc.getInvestmentExperience());
//        dataKyc.put("settlement_bank", account.getBankId().getBankCode());
//        dataKyc.put("settlement_account_name", account.getSettlementAccountName());
//        dataKyc.put("settlement_account_no", account.getSettlementAccountNo());
//
//        if ("IE04".equals(kyc.getInvestmentExperience())) {
//            dataKyc.put("other_investment_experience", kyc.getOtherInvestmentExperience());
//        }
//
//        List listFatca = new ArrayList();
//        Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));
//        List<Question> listQuestionFatca = questionRepository.findAllByQuestionairesOrderBySeqAsc(questionairesFatca);
//
//        for (Question question : listQuestionFatca) {
//            List listAnswers = new ArrayList();
//            List<CustomerAnswer> listFatcaAnswer = customerAnswerRepository.findAllByKycAndQuestionOrderByCreatedDateAsc(kyc, question);
//            for (CustomerAnswer customerAnswer : listFatcaAnswer) {
//                listAnswers.add(customerAnswer.getAnswer().getAnswerName());
//            }
//
//            if (!listAnswers.isEmpty()) {
//                Map data = new HashMap();
//                data.put("question", question.getQuestionName());
//                data.put("answer", listAnswers);
//                listFatca.add(data);
//            }
//        }
//
//        List listRisk = new ArrayList();
//        Questionaires questionairesRisk = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
//        List<Question> listQuestionRisk = questionRepository.findAllQuestionByQuestionairesWithQuery(questionairesRisk);
//
//        for (Question question : listQuestionRisk) {
//            List listAnswers = new ArrayList();
//            List<CustomerAnswer> listFatcaAnswer = customerAnswerRepository.findAllByKycAndQuestionOrderByCreatedDateAsc(kyc, question);
//            for (CustomerAnswer customerAnswer : listFatcaAnswer) {
//                listAnswers.add(customerAnswer.getAnswer().getAnswerName());
//            }
//
//            if (!listAnswers.isEmpty()) {
//                Map data = new HashMap();
//                data.put("question", question.getQuestionName());
//                data.put("answer", listAnswers);
//                listRisk.add(data);
//            }
//        }
//
//        Map dataCustomer = new HashMap();
//        dataCustomer.put("general", dataGeneral);
//        dataCustomer.put("kyc", dataKyc);
//        dataCustomer.put("fatca", listFatca);
//        dataCustomer.put("risk_profile", listRisk);
//
//        List<CustomerDocument> custDocKTPs = customerDocumentRepository.findTop1ByDocumentTypeAndUserOrderByCreatedOnDesc("DocTyp01", user);
//        List<CustomerDocument> custDocTTDs = customerDocumentRepository.findTop1ByDocumentTypeAndUserOrderByCreatedOnDesc("DocTyp03", user);
//
//        Map cusDoc = new HashMap();
//        if (custDocKTPs != null && !custDocKTPs.isEmpty()) {
//            cusDoc.put("id_card_image", custDocKTPs.get(0).getFileKey());
//        }
//        if (custDocTTDs != null && !custDocTTDs.isEmpty()) {
//            cusDoc.put("signature_image", custDocTTDs.get(0).getFileKey());
//        }
//
//        Map dataScore = new HashMap();
//        dataScore.put("code", kyc.getRiskProfile().getScoreCode());
//        dataScore.put("value", kyc.getRiskProfile().getScoreName());
//
//        Map dataProfile = new HashMap();
//        dataProfile.put("customer_id", kyc.getPortalcif());
//        dataProfile.put("customer_status", user.getUserStatus());
//        dataProfile.put("customer_document", cusDoc);
//        dataProfile.put("customer_risk_profile", dataScore);
//        dataProfile.put("customer_data", dataCustomer);
//
//        Map data = new HashMap();
//        data.put(ConstantUtil.STATUS, ConstantUtil.STATUS_SUCCESS);
//        data.put(ConstantUtil.DATA, dataProfile);
//
//        return data;
//    }
//
//    @Override
//    @Transactional
//    public Map profileRegister(Map map) {
//        Map result = new HashMap();
//
//        if (!isExistingDataAndStringValue(map.get("customer"))) {
//            return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "customer", null);
//        }
//
//        if (!isExistingDataAndStringValue(map.get("agent"))) {
//            return errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "agent", null);
//        }
//
//        Agent agent = agentRepository.findByCodeAndRowStatus(map.get("agent").toString(), true);
//        if (agent == null) {
//            return errorResponse(ConstantUtil.STATUS_DATA_NOT_FOUND, "agent", null);
//        }
//
//        logger.info("cek awal lolos");
//
//        Kyc kyc = null;
//        SettlementAccounts accounts = null;
//
//        Map fields = validateFieldProfile(map, kyc, agent);
//        if (fields.get(ConstantUtil.STATUS) != null && fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
//            kyc = (Kyc) fields.get(ConstantUtil.KYC);
//            accounts = (SettlementAccounts) fields.get(ConstantUtil.SETTLEMENT);
//        } else {
//            return fields;
//        }
//
//        logger.info("kyc : " + kyc);
//        logger.info("accounts : " + accounts);
//
//        List<CustomerAnswer> customerFatcas;
//        List<CustomerAnswer> customerRisk;
//        Long score;
//
//        Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));
//        List<Map> lists = (List<Map>) map.get("fatca");
//        fields = validateFatcaProfile(lists, questionairesFatca, kyc);
//        if (fields.get(ConstantUtil.STATUS) != null && fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
//            customerFatcas = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
//        } else {
//            return fields;
//        }
//
//        questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
//        lists = (List<Map>) map.get("risk_profile");
//        fields = validateFatcaProfile(lists, questionairesFatca, kyc);
//        if (fields.get(ConstantUtil.STATUS) != null && fields.get(ConstantUtil.STATUS).equals(ConstantUtil.STATUS_SUCCESS)) {
//            customerRisk = (List<CustomerAnswer>) fields.get(ConstantUtil.QUESTION);
//            score = (Long) fields.get(ConstantUtil.SCORE);
//        } else {
//            return fields;
//        }
//
//        Score riskProfile = scoreRepository.getScore(score, new Date());
//
//        User user = kyc.getAccount();
//        user = userRepository.save(user);
//        logger.info("save user success");
//
//        kyc.setRiskProfile(riskProfile);
//        kyc.setAccount(user);
//        kyc = kycRepository.save(kyc);
//        logger.info("save user kyc success");
//
//        accounts.setKycs(kyc);
//        settlementAccountsRepository.save(accounts);
//        logger.info("save settlement account success");
//
//        for (CustomerAnswer customerAnswer : customerRisk) {
//            customerAnswer.setKyc(kyc);
//            customerAnswerRepository.save(customerAnswer);
//        }
//        logger.info("save customer risk success");
//
//        for (CustomerAnswer customerAnswer : customerFatcas) {
//            customerAnswer.setKyc(kyc);
//            customerAnswerRepository.save(customerAnswer);
//        }
//        logger.info("save customer fatcas success");
//
//        Map dataScore = new HashMap();
//        dataScore.put("code", riskProfile.getScoreCode());
//        dataScore.put("value", riskProfile.getScoreName());
//
//        Map data = new HashMap();
//        data.put("customer_key", user.getCustomerKey());
//        data.put("customer_cif", kyc.getPortalcif());
//        data.put("customer_status", user.getUserStatus());
//        data.put("customer_risk_profile", dataScore);
//
//        return errorResponse(ConstantUtil.STATUS_SUCCESS, "Success", data);
//    }
//
//    @Override
//    public Map login(Map map, String ip) {
//        Map result = new HashMap();
//
//        Kyc kyc = kycRepository.findByPortalcif(map.get("customer_cif").toString());
//        if (kyc == null) {
//            result.put("code", 14);
//            result.put("info", "Invalid request: Invalid customerCIF");
//            return result;
//        }
//        if (map.get("signature") == null || "".equals(map.get("signature").toString())) {
//            result.put("code", 10);
//            result.put("info", "Incomplete data : signature");
//            return result;
//        }
//
//        // customer id lama
//        // if (map.get("customer") == null || "".equals(map.get("customer").toString()))
//        // {
//        // result.put("code", 10);
//        // result.put("info", "Incomplete data : customer");
//        // return result;
//        // }
//        if (map.get("customer_cif") == null || "".equals(map.get("customer_cif").toString())) {
//            result.put("code", 10);
//            result.put("info", "Incomplete data : customer_CIF");
//            return result;
//        }
//
//        User user = kyc.getAccount();
//        if (!agentService.checkSignatureCustomer(user, map.get("signature").toString())) {
//            result.put("code", 12);
//            result.put("info", "Channel tidak valid");
//            return result;
//        }
//
//        // User user =
//        // userRepository.findByChannelCustomer(map.get("customer").toString());
//        // if (!agentService.checkSignatureAgent(user.getAgent(),
//        // map.get("signature").toString())) {
//        // result.put("code", 12);
//        // result.put("info", "Channel tidak valid");
//        // return result;
//        // }
//        if (user == null) {
//            result.put("code", 12);
//            result.put("info", "Invalid access");
//            return result;
//        } else {
//            String visibleToken = user.generateNewToken(ip);
//            if (user.getRecordLogin() == null) {
//                user.setRecordLogin(new Date());
//                user.setLastLogin(user.getRecordLogin());
//            } else {
//                user.setLastLogin(user.getRecordLogin());
//                user.setRecordLogin(new Date());
//            }
//            userRepository.save(user);
//
//            Map mapRiskProflie = new HashMap();
//            // Kyc kyc = kycRepository.findByAccount(user);
//            mapRiskProflie.put("code", kyc.getRiskProfile().getScoreCode());
//            mapRiskProflie.put("value", kyc.getRiskProfile().getScoreName());
//
//            result.put("code", 0);
//            result.put("info", "Customer successfully logged in");
//
//            Map dataMap = new HashMap<>();
//            dataMap.put("last_login", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,S'Z'").format(user.getLastLogin()));
//            dataMap.put("token", visibleToken);
//            dataMap.put("customer_risk_profile", mapRiskProflie);
//            dataMap.put("customer_status", user.getUserStatus());
//
//            result.put("data", dataMap);
//            return result;
//        }
//    }
//
//    @Override
//    public Map viewKyc(User user) {
//        Map resultMap = new HashMap<>();
//        Kyc kyc = kycRepository.findByAccount(user);
//        if (kyc != null) {
//            Map map = new HashMap<>();
//            map.put("kyc_progress", this.completenessKyc(kyc));
//            map.put("email", kyc.getEmail());
//            map.put("first_name", kyc.getFirstName());
//            map.put("middle_name", kyc.getMiddleName());
//            map.put("last_name", kyc.getLastName());
//            map.put("birth_date", kyc.getBirthDate());
//            map.put("birth_place", kyc.getBirthPlace());
//            map.put("occupation", kyc.getOccupation());
//            map.put("business_type", kyc.getNatureOfBusiness());
//            map.put("home_country", kyc.getHomeCountry());
//            map.put("home_province", kyc.getHomeProvince());
//            map.put("home_city", kyc.getHomeCity());
//            map.put("home_postal", kyc.getHomePostalCode());
//            map.put("home_address", kyc.getHomeAddress());
//            map.put("home_phone", kyc.getHomePhoneNumber());
//            map.put("legal_country", kyc.getLegalCountry());
//            map.put("legal_province", kyc.getLegalProvince());
//            map.put("legal_city", kyc.getLegalCity());
//            map.put("legal_postal", kyc.getLegalPostalCode());
//            map.put("legal_address", kyc.getLegalAddress());
//            map.put("legal_phone", kyc.getLegalPhoneNumber());
//            map.put("income_source", kyc.getSourceOfIncome());
//            map.put("annual_income", kyc.getTotalIncomePa());
//            map.put("total_asset", kyc.getTotalAsset());
//            map.put("investment_purpose", kyc.getInvestmentPurpose());
//            map.put("investment_experience", kyc.getInvestmentExperience());
//            map.put("other_investment_ experience", kyc.getOtherInvestmentExperience());
//            map.put("pep_name", kyc.getPepName());
//            map.put("pep_position", kyc.getPepPosition());
//            map.put("pep_pubic_function", kyc.getPepPublicFunction());
//            map.put("pep_country", kyc.getPepCountry() == null ? null : kyc.getPepCountry().getCountryName());
//            map.put("pep_started_on", kyc.getPepYearOfService());
//            map.put("pep_relationship", kyc.getPepRelationship());
//            map.put("pep_other_relationship", kyc.getPepOther());
//            map.put("gender", kyc.getGender());
//            map.put("citizenship", kyc.getCitizenship());
//            map.put("nationality", kyc.getNationality());
//            map.put("marital_status", kyc.getMaritalStatus());
//            map.put("mother_maiden_name", kyc.getMotherMaidenName());
//            map.put("education_background", kyc.getEducationBackground());
//            map.put("religion", kyc.getReligion());
//            map.put("beneficiary_name", kyc.getBeneficiaryName());
//            map.put("beneficiary_relationship", kyc.getBeneficiaryRelationship());
//            map.put("id_type", kyc.getIdType());
//            map.put("id_number", kyc.getIdNumber());
//            map.put("id_expiration_date", kyc.getIdExpirationDate());
//            map.put("tax_id", kyc.getTaxId());
//            map.put("tax_registration_date", kyc.getTaxIdRegisDate());
//            SettlementAccounts accounts = settlementAccountsRepository.findByKycs(kyc);
//            if (accounts != null) {
//                map.put("settlement_bank", accounts.getBankId().getBankName());
//                map.put("settlement_bank_branch", accounts.getBranchId().getBranchName());
//                map.put("settlement_account_name", accounts.getSettlementAccountName());
//                map.put("settlement_account_no", accounts.getSettlementAccountNo());
//                map.put("settlement_mail_type", kyc.getPreferredMailingAddress());
//            } else {
//                map.put("settlement_bank", null);
//                map.put("settlement_bank_branch", null);
//                map.put("settlement_account_name", null);
//                map.put("settlement_account_no", null);
//                map.put("settlement_mail_type", null);
//            }
//            resultMap.put("code", 0);
//            resultMap.put("info", "Data " + kyc.getFirstName() + " is loaded");
//            resultMap.put("data", map);
//        } else {
//            resultMap.put("code", 50);
//            resultMap.put("info", "Invalid Account");
//            resultMap.put("data", kyc);
//        }
//        return resultMap;
//    }
//
//    @Override
//    public Map viewFatca(User user) {
//        Map resultMap = new HashMap<>();
//        Kyc kyc = kycRepository.findByAccount(user);
//
//        Questionaires fatcaDefault = questionairesRepository.findByQuestionnaireName("FATCA Default");
//        List<Question> questions = questionRepository.findAllByQuestionairesOrderBySeqAsc(fatcaDefault);
//
//        Map mapData = new HashMap<>();
//        mapData.put("fatca_progress", this.completenessFatca(kyc));
//
//        List<Map> maps = new ArrayList<>();
//        for (Question q : questions) {
//            List<CustomerAnswer> customerAnswers = customerAnswerRepository.findAllByKycAndQuestionWithQuery(kyc, q);
//            Map map = new HashMap<>();
//            map.put("code", q.getQuestionName());
//            map.put("value", q.getQuestionText());
//            List<Answer> answers = answerRepository.findAllByQuestionOrderByAnswerNameAsc(q);
//            List<Map> mapOptions = new ArrayList<>();
//            for (Answer a : answers) {
//                Map mapCa = new HashMap<>();
//                mapCa.put("code", a.getAnswerName());
//                mapCa.put("value", a.getAnswerText());
//                mapOptions.add(mapCa);
//            }
//            map.put("option", mapOptions);
//
//            List<Map> mapsCa = new ArrayList<>();
//            for (CustomerAnswer ca : customerAnswers) {
//                Map mapCa = new HashMap<>();
//                mapCa.put("code", ca.getAnswer().getAnswerName());
//                mapCa.put("value", ca.getAnswer().getAnswerText());
//                mapsCa.add(mapCa);
//            }
//
//            Map mapX = new HashMap<>();
//            mapX.put("question", map);
//            mapX.put("answers", mapsCa);
//            maps.add(mapX);
//        }
//        mapData.put("fatca", maps);
//
//        resultMap.put("code", 0);
//        resultMap.put("info", "Customer FATCA data loaded");
//        resultMap.put("data", mapData);
//        return resultMap;
//    }
//
//    @Override
//    public Double completenessFatca(Kyc kyc) {
//        Questionaires fatcaDefault = questionairesRepository.findByQuestionnaireCategory(2L);
//        Long fatcaQuestionCount = questionRepository.countByQuestionaires(fatcaDefault);
//        List<Question> questions = questionRepository.findAllByQuestionairesOrderBySeqAsc(fatcaDefault);
//        Integer answerFatca = customerAnswerRepository.findByQuestionWithQuery(kyc, questions);
//        Double fatcaProgress = 100.0 * (new Double(answerFatca) / new Double(fatcaQuestionCount));
//        return fatcaProgress;
//    }
//
//    @Override
//    public Double completenessRiskProfile(Kyc kyc) {
//        Questionaires riskProfileDefault = questionairesRepository.findByQuestionnaireCategory(1L);
//        Long riskProfileCount = questionRepository.countByQuestionaires(riskProfileDefault);
//        List<Question> questions = questionRepository.findAllQuestionByQuestionairesWithQuery(riskProfileDefault);
//        Integer answerRiskProfile = customerAnswerRepository.findByQuestionWithQuery(kyc, questions);
//        Double riskProfileProgress = 100.0 * (new Double(answerRiskProfile) / new Double(riskProfileCount));
//        return riskProfileProgress;
//    }
//
//    @Override
//    public Map viewRiskProfile(User user) {
//        Map resultMap = new HashMap();
//        Kyc kyc = kycRepository.findByEmail(user.getEmail());
//        if (kyc == null) {
//            resultMap.put("code", 10);
//            resultMap.put("info", "Invalid or unregister e-mail , please check your format or Sign Up now");
//        }
//
//        Questionaires questionaires = questionairesRepository.findByQuestionnaireCategory(1L);
//        List<Question> questions = questionRepository.findAllQuestionByQuestionairesWithQuery(questionaires);
//
//        Map dataMap = new HashMap<>();
//        dataMap.put("risk_profile_progress", this.completenessRiskProfile(kyc));
//
//        List<Map> questionMaps = new ArrayList<>();
//
//        if (!questions.isEmpty()) {
//            for (Question q : questions) {
//                List<CustomerAnswer> customerAnswers = customerAnswerRepository.findAllByKycAndQuestionWithQuery(kyc,
//                        q);
//                List<Answer> answers = answerRepository.findAllByQuestionOrderByAnswerNameAsc(q);
//                Map map = new HashMap<>();
//                map.put("code", q.getQuestionName());
//                map.put("value", q.getQuestionText());
//                List<Map> options = new ArrayList<>();
//                for (Answer answer : answers) {
//                    Map answerMap = new HashMap<>();
//                    answerMap.put("code", answer.getAnswerName());
//                    answerMap.put("value", answer.getAnswerText());
//                    options.add(answerMap);
//                }
//                map.put("option", options);
//                List<String> cusAnswers = new ArrayList<>();
//                for (CustomerAnswer ca : customerAnswers) {
//                    cusAnswers.add(ca.getAnswer().getAnswerName());
//                }
//                map.put("answers", cusAnswers);
//                questionMaps.add(map);
//            }
//        }
//        dataMap.put("risk_profile", questionMaps);
//        resultMap.put("code", 0);
//        resultMap.put("info", "Customer risk profile data loaded");
//        resultMap.put("data", dataMap);
//        return resultMap;
//    }
//
//    @Override
//    public Double completenessKyc(Kyc kyc) {
//        Double nilai = 1.0;
//        Double total = 0.0;
//        Double totalMax = 40.0;
//
//        if (kyc.getFirstName() != null && !kyc.getFirstName().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getLastName() != null && !kyc.getLastName().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getBirthDate() != null && !kyc.getBirthDate().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getBirthPlace() != null && !kyc.getBirthPlace().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getOccupation() != null && !kyc.getOccupation().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getNatureOfBusiness() != null && !kyc.getNatureOfBusiness().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getHomeCountry() != null && !kyc.getHomeCountry().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getHomeProvince() != null && !kyc.getHomeProvince().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getHomeCity() != null && !kyc.getHomeCity().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getHomeAddress() != null && !kyc.getHomeAddress().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getHomePostalCode() != null && !kyc.getHomePostalCode().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getHomePhoneNumber() != null && !kyc.getHomePhoneNumber().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getLegalCountry() != null && !kyc.getLegalCountry().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getLegalProvince() != null && !kyc.getLegalProvince().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getLegalCity() != null && !kyc.getLegalCity().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getLegalAddress() != null && !kyc.getLegalAddress().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getLegalPostalCode() != null && !kyc.getLegalPostalCode().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getLegalPhoneNumber() != null && !kyc.getLegalPhoneNumber().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getGender() != null && !kyc.getGender().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getCitizenship() != null && !kyc.getCitizenship().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getMaritalStatus() != null && !kyc.getMaritalStatus().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getMotherMaidenName() != null && !kyc.getMotherMaidenName().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getEducationBackground() != null && !kyc.getEducationBackground().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getReligion() != null && !kyc.getReligion().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getInvestmentPurpose() != null && !kyc.getInvestmentPurpose().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getSourceOfIncome() != null && !kyc.getSourceOfIncome().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getTotalIncomePa() != null && !kyc.getTotalIncomePa().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getPreferredMailingAddress() != null && !kyc.getPreferredMailingAddress().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getIdNumber() != null && !kyc.getIdNumber().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getIdType() != null && !kyc.getIdType().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getIdExpirationDate() != null) {
//            total += nilai;
//        }
//
//        if (kyc.getNationality() != null && !kyc.getNationality().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getTotalAsset() != null && !kyc.getTotalAsset().toString().trim().isEmpty()) {
//            total += nilai;
//        }
//
//        if (kyc.getInvestmentExperience() != null && !kyc.getInvestmentExperience().toString().trim().isEmpty()) {
//            total += nilai;
//
//            LookupHeader header = lookupHeaderRepository.findByCategory("INVESTMENT_EXPERIENCE");
//            LookupLine line = lookupLineRepository.findByCategoryAndCode(header, "IE04");
//            if (kyc.getInvestmentExperience() == line.getCode()
//                    || kyc.getInvestmentExperience().equals(line.getCode())) {
//                totalMax += nilai;
//                if (kyc.getOtherInvestmentExperience() != null
//                        && !kyc.getOtherInvestmentExperience().toString().trim().isEmpty()) {
//                    total += nilai;
//                }
//            }
//        }
//
//        if (("ACT".equals(kyc.getAccount().getUserStatus()) && "REG".equals(kyc.getAccount().getUserStatusSebelumnya()))
//                || ("ACT".equals(kyc.getAccount().getUserStatus())
//                && "PEN".equals(kyc.getAccount().getUserStatusSebelumnya()))) {
//            List<CustomerDocument> docKTP = customerDocumentRepository.findByDocumentTypeWithQuery(kyc.getAccount(),
//                    "KTP");
//            if (docKTP.size() > 0) {
//                total += nilai;
//            }
//            List<CustomerDocument> docTTD = customerDocumentRepository.findByDocumentTypeWithQuery(kyc.getAccount(),
//                    "TTD");
//            if (docTTD.size() > 0) {
//                total += nilai;
//            }
//        } else if ("PEN".equals(kyc.getAccount().getUserStatus())
//                && "ACT".equals(kyc.getAccount().getUserStatusSebelumnya())) {
//            List<CustomerDocument> docKTP = customerDocumentRepository.findByDocumentTypeWithQuery(kyc.getAccount(),
//                    "KTP");
//            if (docKTP.size() > 0) {
//                total += nilai;
//            }
//            List<CustomerDocument> docTTD = customerDocumentRepository.findByDocumentTypeWithQuery(kyc.getAccount(),
//                    "TTD");
//            if (docTTD.size() > 0) {
//                total += nilai;
//            }
//        } else if ("VER".equals(kyc.getAccount().getUserStatus())
//                && "PEN".equals(kyc.getAccount().getUserStatusSebelumnya())) {
//            String[] codes = {"KTP", "TTD"};
//            List<DocumentType> listDocType = documentTypeRepository.findAllByRowStatusAndCodeIn(true, codes);
//            for (DocumentType documentType : listDocType) {
//                CustomerDocument document = customerDocumentRepository
//                        .findByUserAndRowStatusAndDocumentType(kyc.getAccount(), true, documentType.getCode());
//                if (document != null) {
//                    total += nilai;
//                }
//            }
//
//        } else if ("PEN".equals(kyc.getAccount().getUserStatus())
//                && "VER".equals(kyc.getAccount().getUserStatusSebelumnya())) {
//            String[] codes = {"KTP", "TTD"};
//            List<DocumentType> listDocType = documentTypeRepository.findAllByRowStatusAndCodeIn(true, codes);
//            for (DocumentType documentType : listDocType) {
//                CustomerDocument document = customerDocumentRepository
//                        .findByUserAndRowStatusAndDocumentType(kyc.getAccount(), true, documentType.getCode());
//                if (document != null) {
//                    total += nilai;
//                }
//            }
//
//        }
//
//        LookupHeader header = lookupHeaderRepository.findByCategory("PEP_RELATIONSHIP");
//        LookupLine line = lookupLineRepository.findByCategoryAndCode(header, "PR12");
//        if (kyc.getPepRelationship() != null) {
//            if (kyc.getPepRelationship() == line.getCode() || kyc.getPepRelationship().equals(line.getCode())) {
//                totalMax += nilai;
//                if (kyc.getPepOther() != null && !kyc.getPepOther().toString().trim().isEmpty()) {
//                    total += nilai;
//                }
//            }
//        }
//
//        if (kyc.getId() != null) {
//            if (settlementAccountsRepository.countByKycs(kyc) > 0) {
//                SettlementAccounts settlementAccounts = settlementAccountsRepository.findByKycs(kyc);
//                if (settlementAccounts.getSettlementAccountNo() != null
//                        && !settlementAccounts.getSettlementAccountNo().toString().trim().isEmpty()) {
//                    total += nilai;
//                }
//                if (settlementAccounts.getSettlementAccountName() != null
//                        && !settlementAccounts.getSettlementAccountName().toString().trim().isEmpty()) {
//                    total += nilai;
//                }
//                if (settlementAccounts.getBankId() != null && settlementAccounts.getBankId().getBankName() != null
//                        && !settlementAccounts.getBankId().getBankName().toString().trim().isEmpty()) {
//                    total += nilai;
//                }
//                if (settlementAccounts.getBranchId() != null && settlementAccounts.getBranchId().getBranchName() != null
//                        && !settlementAccounts.getBranchId().getBranchName().toString().trim().isEmpty()) {
//                    total += nilai;
//                }
//            }
//        }
//
//        Double sum = (total / totalMax) * 100;
//        return sum;
//    }
//
//    @Override
//    public Map updateFatca(Map map, User user) {
//        Questionaires questionaires = questionairesRepository.findByQuestionnaireName("FATCA Default");
//        Kyc kyc = kycRepository.findByAccount(user);
//
//        // TODO: Cek FATCA already Exist and move to oldFatca in KYC Table
//        JSONArray listOldValueFatca = new JSONArray();
//        List<Question> fatcaCustomerAnswers = customerAnswerRepository
//                .findAllQuestionByUserAndQuestionariesWithQuery(user, questionaires);
//        if (!fatcaCustomerAnswers.isEmpty()) {
//            for (Question q : fatcaCustomerAnswers) {
//                List<CustomerAnswer> answers = customerAnswerRepository.findAllByUserAndQuestionWithQuery(user, q);
//                List<Long> answerCode = new ArrayList<>();
//                for (CustomerAnswer answer : answers) {
//                    answerCode.add(answer.getAnswer().getId());
//                }
//                JSONObject oldValueFatca = new JSONObject();
//                oldValueFatca.put("questionId", q.getId());
//                oldValueFatca.put("answerId", answerCode);
//                listOldValueFatca.put(oldValueFatca);
//            }
//
//            kyc.setOldValueFatca(listOldValueFatca.toString());
//            kycRepository.save(kyc);
//        }
//
//        // TODO : Delete Existing Customer Answer Fatca
//        try {
//            customerAnswerRepository.deleteByKycAndQuestionaries(kyc, questionaires);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        List<Map> maps = (List<Map>) map.get("fatca");
//        for (Map newMap : maps) {
//            Question question = questionRepository.findFirstByQuestionairesAndQuestionNameOrderByIdDesc(questionaires,
//                    (String) newMap.get("question"));
//            if (question != null) {
//                List<String> strings = (List<String>) newMap.get("answers");
//                if (!strings.isEmpty()) {
//                    for (String str : strings) {
//                        Answer answer = answerRepository.findByAnswerNameAndQuestion(str, question);
//                        CustomerAnswer customerAnswer = new CustomerAnswer();
//                        customerAnswer.setAnswer(answer);
//                        customerAnswer.setQuestion(question);
//                        customerAnswer.setCreatedBy(user.getUsername());
//                        customerAnswer.setCreatedDate(new Date());
//                        customerAnswer.setKyc(kyc);
//                        customerAnswer.setVersion(0);
//                        customerAnswerRepository.save(customerAnswer);
//                    }
//                }
//
//            }
//        }
//
//        Double pcgFatca = this.completenessFatca(kyc);
//        Map pcgMap = new HashMap<>();
//        pcgMap.put("fatca_progress", pcgFatca);
//
//        Map resultMap = new HashMap<>();
//        resultMap.put("code", 0);
//        resultMap.put("info", "FATCA successfully updated");
//        resultMap.put("data", pcgMap);
//        return resultMap;
//    }
//
//    @Override
//    public Map updateRiskProfile(Map map, User user) {
//        Questionaires questionaires = questionairesRepository.findByQuestionnaireName("Portal Risk Questionnaire");
//        Kyc kyc = kycRepository.findByAccount(user);
//
//        // TODO: Cek Risk Profile already Exist and move to oldFatca in KYC
//        // Table
//        JSONArray listOldValueRiskProfile = new JSONArray();
//        List<Question> riskProfileCustomerAnswers = customerAnswerRepository
//                .findAllQuestionByUserAndQuestionariesWithQuery(user, questionaires);
//
//        if (!riskProfileCustomerAnswers.isEmpty()) {
//            for (Question q : riskProfileCustomerAnswers) {
//                List<CustomerAnswer> answers = customerAnswerRepository.findAllByUserAndQuestionWithQuery(user, q);
//                List<Long> answerCode = new ArrayList<>();
//                for (CustomerAnswer answer : answers) {
//                    answerCode.add(answer.getAnswer().getId());
//                }
//                JSONObject oldValuerisk = new JSONObject();
//                oldValuerisk.put("questionId", q.getId());
//                oldValuerisk.put("answerId", answerCode);
//                listOldValueRiskProfile.put(oldValuerisk);
//            }
//
//            kyc.setOldValueRiskProfile(listOldValueRiskProfile.toString());
//            kycRepository.save(kyc);
//        }
//
//        // TODO : Delete Existing Customer Answer Fatca
//        try {
//            customerAnswerRepository.deleteByKycAndQuestionaries(kyc, questionaires);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        List<Map> maps = (List<Map>) map.get("risk_profile");
//        for (Map newMap : maps) {
//            Question question = questionRepository.findFirstByQuestionairesAndQuestionNameOrderByIdDesc(questionaires,
//                    (String) newMap.get("question"));
//            if (question != null) {
//                List<String> strings = (List<String>) newMap.get("answers");
//                if (!strings.isEmpty()) {
//                    for (String str : strings) {
//                        Answer answer = answerRepository.findByAnswerNameAndQuestion(str, question);
//                        CustomerAnswer customerAnswer = new CustomerAnswer();
//                        customerAnswer.setAnswer(answer);
//                        customerAnswer.setQuestion(question);
//                        customerAnswer.setCreatedBy(user.getUsername());
//                        customerAnswer.setCreatedDate(new Date());
//                        customerAnswer.setKyc(kyc);
//                        customerAnswer.setVersion(0);
//                        customerAnswerRepository.save(customerAnswer);
//                    }
//                }
//
//            }
//        }
//
//        Double pcgRisk = this.completenessRiskProfile(kyc);
//        Map pcgMap = new HashMap<>();
//        pcgMap.put("risk_profile_progress", pcgRisk);
//
//        Map resultMap = new HashMap<>();
//        resultMap.put("code", 0);
//        resultMap.put("info", "Risk Profile successfully updated");
//        resultMap.put("data", pcgMap);
//        return resultMap;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Map profileUpdate(Map map, User user) {
//        Map result = new HashMap();
//        try {
//            Kyc kyc = kycRepository.findByAccount(user);
//            if (kyc == null) {
//                result.put("code", 50);
//                result.put("info", "Data not found : customer");
//                return result;
//            }
//
//            if (map.get("first_name") != null && !"".equals(String.valueOf(map.get("first_name")))) {
//                kyc.setFirstName(map.get("first_name").toString());
//            }
//
//            if (map.get("last_name") != null && !"".equals(String.valueOf(map.get("last_name")))) {
//                kyc.setLastName(map.get("last_name").toString());
//            }
//
////            if (!isExistingData(map.get("email"))) {
////                String email = map.get("email").toString();
////
////                User findEmail = userRepository.findByUserEmailAndAgent(email, user.getAgent());
////                if (findEmail != null) {
////                    return errorResponse(ConstantUtil.STATUS_EXISTING_DATA, "email", null);
////                }
////
////                kyc.setEmail(email);
////            }
//
//            if (map.get("phone_number") != null && !"".equals(String.valueOf(map.get("phone_number")))) {
//                String[] phoneNumber = map.get("phone_number").toString().split("-");
//                if (phoneNumber.length != 2) {
//                    result.put("code", 11);
//                    result.put("info", "Invalid data format : phone number");
//                    return result;
//                }
//
//                if (phoneNumber[0].length() < 1) {
//                    result.put("code", 11);
//                    result.put("info", "Invalid data format : Country code phone number min length 1");
//                    return result;
//                }
//
//                if (phoneNumber[0].length() > 3) {
//                    result.put("code", 11);
//                    result.put("info", "Invalid data format : Country code phone number max length 3");
//                    return result;
//                }
//
//                if (phoneNumber[1].length() < 1) {
//                    result.put("code", 11);
//                    result.put("info", "Invalid data format : Phone number min length 1");
//                    return result;
//                }
//
//                if (phoneNumber[1].length() > 12) {
//                    result.put("code", 11);
//                    result.put("info", "Invalid data format : Phone number max length 12");
//                    return result;
//                }
//
//                kyc.setMobileNumber(map.get("phone_number").toString());
//            }
//
//            Map dataKyc = (Map) map.get("kyc");
//            if (dataKyc != null) {
//                if (dataKyc.get("birth_date") != null && !"".equals(String.valueOf(dataKyc.get("birth_date")))) {
//                    Date birthDate;
//                    try {
//                        birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(dataKyc.get("birth_date").toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        result.put("code", 11);
//                        result.put("info", "Invalid data format : Birth date");
//                        return result;
//                    }
//                    kyc.setBirthDate(birthDate);
//                }
//
//                if (dataKyc.get("birth_place") != null && !"".equals(String.valueOf(dataKyc.get("birth_place")))) {
//                    kyc.setBirthPlace(dataKyc.get("birth_place").toString());
//                }
//
//                if (dataKyc.get("gender") != null && !"".equals(String.valueOf(dataKyc.get("gender")))) {
//                    LookupLine gender = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
//                            lookupHeaderRepository.findByCategory("GENDER"), dataKyc.get("gender").toString(), true);
//                    if (gender == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : gender");
//                        return result;
//                    }
//                    kyc.setGender(gender.getCode());
//                }
//
//                if (dataKyc.get("nationality") != null && !"".equals(String.valueOf(dataKyc.get("nationality")))) {
//                    Countries nationality = countriesRepository.findByAlpha3Code(dataKyc.get("nationality").toString());
//                    if (nationality == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : nationality");
//                        return result;
//                    }
//                    kyc.setNationality(nationality.getId().toString());
//                }
//
//                if (dataKyc.get("marital_status") != null
//                        && !"".equals(String.valueOf(dataKyc.get("marital_status")))) {
//
//                    LookupLine marital = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
//                            lookupHeaderRepository.findByCategory("MARITAL_STATUS"),
//                            dataKyc.get("marital_status").toString(), true);
//                    if (marital == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : Marital status");
//                        return result;
//                    }
//                    kyc.setMaritalStatus(marital.getCode());
//                }
//
//                if (dataKyc.get("mother_maiden_name") != null
//                        && !"".equals(String.valueOf(dataKyc.get("mother_maiden_name")))) {
//
//                    kyc.setMotherMaidenName(dataKyc.get("mother_maiden_name").toString());
//                }
//
//                if (dataKyc.get("education_background") != null
//                        && !"".equals(String.valueOf(dataKyc.get("education_background")))) {
//
//                    LookupLine education = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
//                            lookupHeaderRepository.findByCategory("EDUCATION_BACKGROUND"),
//                            dataKyc.get("education_background").toString(), true);
//                    if (education == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : Education background");
//                        return result;
//                    }
//                    kyc.setEducationBackground(education.getCode());
//                }
//
//                if (dataKyc.get("religion") != null && !"".equals(String.valueOf(dataKyc.get("religion")))) {
//                    LookupLine religion = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
//                            lookupHeaderRepository.findByCategory("RELIGION"), dataKyc.get("religion").toString(),
//                            true);
//                    if (religion == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : Religion");
//                        return result;
//                    }
//                    kyc.setReligion(religion.getCode());
//                }
//
//                if (dataKyc.get("statement_type") != null
//                        && !"".equals(String.valueOf(dataKyc.get("statement_type")))) {
//
//                    LookupLine statementType = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
//                            lookupHeaderRepository.findByCategory("STATEMENT_TYPE"),
//                            dataKyc.get("statement_type").toString(), true);
//                    if (statementType == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : Statement type");
//                        return result;
//                    }
//                    kyc.setPreferredMailingAddress(statementType.getCode());
//                }
//
//                if (dataKyc.get("occupation") != null && !"".equals(String.valueOf(dataKyc.get("occupation")))) {
//                    LookupLine occupation = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
//                            lookupHeaderRepository.findByCategory("OCCUPATION"), dataKyc.get("occupation").toString(),
//                            true);
//                    if (occupation == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : Occupation");
//                        return result;
//                    }
//                    kyc.setOccupation(occupation.getCode());
//                }
//
//                if (dataKyc.get("business_nature") != null
//                        && !"".equals(String.valueOf(dataKyc.get("business_nature")))) {
//                    LookupLine businessNature = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
//                            lookupHeaderRepository.findByCategory("NATURE_OF_BUSINESS"),
//                            dataKyc.get("business_nature").toString(), true);
//                    if (businessNature == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : Business nature");
//                        return result;
//                    }
//                    kyc.setNatureOfBusiness(businessNature.getCode());
//
//                }
//
//                if (dataKyc.get("id_number") != null && !"".equals(String.valueOf(dataKyc.get("id_number")))) {
//                    kyc.setIdNumber(dataKyc.get("id_number").toString());
//                }
//
//                if (dataKyc.get("id_expiration") != null && !"".equals(String.valueOf(dataKyc.get("id_expiration")))) {
//                    Date expirationDate;
//                    try {
//                        expirationDate = new SimpleDateFormat("yyyy-MM-dd")
//                                .parse(dataKyc.get("id_expiration").toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        result.put("code", 11);
//                        result.put("info", "Invalid data format : id expiration date");
//                        return result;
//                    }
//                    kyc.setIdExpirationDate(expirationDate);
//                }
//
//                Map dataLegal = (Map) dataKyc.get("legal");
//                if (dataLegal != null) {
//                    if (dataLegal.get("country") != null && !"".equals(String.valueOf(dataLegal.get("country")))) {
//                        Countries legalCountry = countriesRepository
//                                .findByAlpha3Code(dataLegal.get("country").toString());
//                        if (legalCountry == null) {
//                            result.put("code", 50);
//                            result.put("info", "Data not found : Legal country");
//                            return result;
//                        }
//                        kyc.setLegalCountry(legalCountry.getId().toString());
//                    }
//
//                    if (dataLegal.get("province") != null && !"".equals(String.valueOf(dataLegal.get("province")))) {
//                        States legalProvince = statesRepository.findByStateCode(dataLegal.get("province").toString());
//                        if (legalProvince == null) {
//                            result.put("code", 50);
//                            result.put("info", "Data not found : Legal province");
//                            return result;
//                        }
//                        kyc.setLegalProvince(legalProvince.getStateCode());
//                    }
//
//                    if (dataLegal.get("city") != null && !"".equals(String.valueOf(dataLegal.get("city")))) {
//                        Cities legalCity = citiesRepository.findByCityCode(dataLegal.get("city").toString());
//                        if (legalCity == null) {
//                            result.put("code", 50);
//                            result.put("info", "Data not found : Legal city");
//                            return result;
//                        }
//                        kyc.setLegalCity(legalCity.getId().toString());
//                    }
//
//                    if (dataLegal.get("postal_code") != null
//                            && !"".equals(String.valueOf(dataLegal.get("postal_code")))) {
//                        kyc.setLegalPostalCode(dataLegal.get("postal_code").toString());
//                    }
//
//                    if (dataLegal.get("address") != null && !"".equals(String.valueOf(dataLegal.get("address")))) {
//                        kyc.setLegalAddress(dataLegal.get("address").toString());
//                    }
//
//                    if (dataLegal.get("phone") != null && !"".equals(String.valueOf(dataLegal.get("phone")))) {
//                        String[] legalPhone = dataLegal.get("phone").toString().split("-");
//                        if (legalPhone.length != 3) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : legal phone");
//                            return result;
//                        }
//
//                        if (legalPhone[0].length() < 1) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : Country code legal phone number min length 1");
//                            return result;
//                        }
//
//                        if (legalPhone[0].length() > 3) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : Country code legal phone number max length 3");
//                            return result;
//                        }
//
//                        if (legalPhone[1].length() < 1) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : Area code legal phone number min length 1");
//                            return result;
//                        }
//
//                        if (legalPhone[1].length() > 5) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : Area code legal phone number max length 5");
//                            return result;
//                        }
//
//                        if (legalPhone[2].length() < 1) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : Legal phone number min length 1");
//                            return result;
//                        }
//
//                        if (legalPhone[2].length() > 12) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : Legal phone number max length 12");
//                            return result;
//                        }
//
//                        kyc.setLegalPhoneNumber(dataLegal.get("phone").toString());
//                    }
//                }
//
//                Map dataMailing = (Map) dataKyc.get("mailing");
//                if (dataMailing != null) {
//                    if (dataMailing.get("country") != null && "".equals(String.valueOf(dataMailing.get("country")))) {
//                        Countries homeCountry = countriesRepository
//                                .findByAlpha3Code(dataMailing.get("country").toString());
//                        if (homeCountry == null) {
//                            result.put("code", 50);
//                            result.put("info", "Data not found : Home country");
//                            return result;
//                        }
//                        kyc.setHomeCountry(homeCountry.getId().toString());
//                        kyc.setOfficeCountry(homeCountry.getId().toString());
//                    }
//
//                    if (dataMailing.get("province") != null
//                            && !"".equals(String.valueOf(dataMailing.get("province")))) {
//                        States homeProvince = statesRepository.findByStateCode(dataMailing.get("province").toString());
//                        if (homeProvince == null) {
//                            result.put("code", 50);
//                            result.put("info", "Data not found : Home province");
//                            return result;
//                        }
//                        kyc.setHomeProvince(homeProvince.getStateCode());
//                        kyc.setOfficeProvince(homeProvince.getStateCode());
//                    }
//
//                    if (dataMailing.get("city") != null && !"".equals(String.valueOf(dataMailing.get("city")))) {
//                        Cities homeCity = citiesRepository.findByCityCode(dataMailing.get("city").toString());
//                        if (homeCity == null) {
//                            result.put("code", 50);
//                            result.put("info", "Data not found : Home city");
//                            return result;
//                        }
//                        kyc.setHomeCity(homeCity.getId().toString());
//                        kyc.setOfficeCity(homeCity.getId().toString());
//                    }
//
//                    if (dataMailing.get("postal_code") != null
//                            && !"".equals(String.valueOf(dataMailing.get("postal_code")))) {
//                        kyc.setHomePostalCode(dataMailing.get("postal_code").toString());
//                        kyc.setOfficePostalCode(dataMailing.get("postal_code").toString());
//                    }
//
//                    if (dataMailing.get("address") != null && !"".equals(String.valueOf(dataMailing.get("address")))) {
//                        kyc.setHomeAddress(dataMailing.get("address").toString());
//                        kyc.setOfficeAddress(dataMailing.get("address").toString());
//                    }
//
//                    if (dataMailing.get("phone") != null && !"".equals(String.valueOf(dataMailing.get("phone")))) {
//                        String[] mailingPhone = dataMailing.get("phone").toString().split("-");
//                        if (mailingPhone.length != 3) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : mailing phone");
//                            return result;
//                        }
//
//                        if (mailingPhone[0].length() < 1) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : Country code mailing phone number min length 1");
//                            return result;
//                        }
//
//                        if (mailingPhone[0].length() > 3) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : Country code mailing phone number max length 3");
//                            return result;
//                        }
//
//                        if (mailingPhone[1].length() < 1) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : Area code mailing phone number min length 1");
//                            return result;
//                        }
//
//                        if (mailingPhone[1].length() > 5) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : Area code mailing phone number max length 5");
//                            return result;
//                        }
//
//                        if (mailingPhone[2].length() < 1) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : Mailing phone number min length 1");
//                            return result;
//                        }
//
//                        if (mailingPhone[2].length() > 12) {
//                            result.put("code", 11);
//                            result.put("info", "Invalid data format : Mailing phone number max length 12");
//                            return result;
//                        }
//
//                        kyc.setHomePhoneNumber(dataMailing.get("phone").toString());
//                        kyc.setOfficePhoneNumber(dataMailing.get("phone").toString());
//                    }
//                }
//
//                if (dataKyc.get("income_source") != null && !"".equals(String.valueOf(dataKyc.get("income_source")))) {
//                    LookupLine sourceOfIncome = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
//                            lookupHeaderRepository.findByCategory("SOURCE_OF_INCOME"),
//                            dataKyc.get("income_source").toString(), true);
//                    if (sourceOfIncome == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : Source of income");
//                        return result;
//                    }
//                    kyc.setSourceOfIncome(sourceOfIncome.getCode());
//                }
//
//                if (dataKyc.get("annual_income") != null && !"".equals(String.valueOf(dataKyc.get("annual_income")))) {
//                    LookupLine totalIncome = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
//                            lookupHeaderRepository.findByCategory("ANNUAL_INCOME"),
//                            dataKyc.get("annual_income").toString(), true);
//                    if (totalIncome == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : Annual income");
//                        return result;
//                    }
//                    kyc.setTotalIncomePa(totalIncome.getCode());
//                }
//
//                if (dataKyc.get("total_asset") != null && !"".equals(String.valueOf(dataKyc.get("total_asset")))) {
//                    LookupLine totalAset = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
//                            lookupHeaderRepository.findByCategory("TOTAL_ASSET"), dataKyc.get("total_asset").toString(),
//                            true);
//                    if (totalAset == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : Total aset");
//                        return result;
//                    }
//                    kyc.setTotalAsset(totalAset.getCode());
//                }
//
//                if (dataKyc.get("investment_purpose") != null
//                        && !"".equals(String.valueOf(dataKyc.get("investment_purpose")))) {
//
//                    LookupLine investmentPurpose = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
//                            lookupHeaderRepository.findByCategory("INVESTMENT_PURPOSE"),
//                            dataKyc.get("investment_purpose").toString(), true);
//                    if (investmentPurpose == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : Investment Purpose");
//                        return result;
//                    }
//                    kyc.setInvestmentPurpose(investmentPurpose.getCode());
//                }
//
//                if (dataKyc.get("investment_experience") != null
//                        && !"".equals(String.valueOf(dataKyc.get("investment_experience")))) {
//
//                    LookupLine investmentExperience = lookupLineRepository.findByCategoryAndCodeAndPublishStatus(
//                            lookupHeaderRepository.findByCategory("INVESTMENT_EXPERIENCE"),
//                            dataKyc.get("investment_experience").toString(), true);
//                    if (investmentExperience == null) {
//                        result.put("code", 50);
//                        result.put("info", "Data not found : Investment Experiance");
//                        return result;
//                    }
//                    kyc.setInvestmentExperience(investmentExperience.getCode());
//
//                    // mandatory kalau investment_experience Other
//                    if (dataKyc.get("investment_experience").toString().equals("IE04")
//                            && (dataKyc.get("other_investment_experience") != null
//                            && !"".equals(String.valueOf(dataKyc.get("other_investment_experience"))))) {
//                        kyc.setOtherInvestmentExperience(dataKyc.get("other_investment_experience").toString());
//                    }
//                }
//
//                SettlementAccounts accounts = settlementAccountsRepository.findByKycs(kyc);
//                if (accounts != null) {
//                    if (dataKyc.get("settlement_bank") != null
//                            && !"".equals(String.valueOf(dataKyc.get("settlement_bank")))) {
//
//                        Bank bank = bankRepository.findByBankCode(dataKyc.get("settlement_bank").toString());
//                        if (bank == null) {
//                            result.put("code", 50);
//                            result.put("info", "Data not found : settlement bank");
//                            return result;
//                        }
//                        accounts.setBankId(bank);
//                        accounts.setUpdatedBy(kyc.getEmail());
//                        accounts.setUpdatedDate(new Date());
//                    }
//
//                    if (dataKyc.get("settlement_account_name") != null
//                            && !"".equals(String.valueOf(dataKyc.get("settlement_account_name")))) {
//                        accounts.setSettlementAccountName(dataKyc.get("settlement_account_name").toString());
//                        accounts.setUpdatedBy(kyc.getEmail());
//                        accounts.setUpdatedDate(new Date());
//                    }
//
//                    if (dataKyc.get("settlement_account_no") != null
//                            && !"".equals(String.valueOf(dataKyc.get("settlement_account_no")))) {
//                        accounts.setSettlementAccountNo(dataKyc.get("settlement_account_no").toString());
//                        accounts.setUpdatedBy(kyc.getEmail());
//                        accounts.setUpdatedDate(new Date());
//                    }
//
//                    settlementAccountsRepository.save(accounts);
//                }
//                kycRepository.save(kyc);
//            }
//
//            if (map.get("fatca") != null) {
//                Questionaires questionairesFatca = questionairesRepository
//                        .findByQuestionnaireCategory(Long.valueOf("2"));
//                List listFatca = (List) map.get("fatca");
//                if (listFatca.size() > 0) {
//                    for (int i = 0; i < listFatca.size(); i++) {
//                        Map dataFatca = (Map) listFatca.get(i);
//                        if (dataFatca.get("question") == null || "".equals(dataFatca.get("question").toString())) {
//                            result.put("code", 10);
//                            result.put("info", "Incomplete data : Fatca question");
//                            return result;
//                        }
//
//                        Question question = questionRepository.findFirstByQuestionairesAndQuestionNameOrderByIdDesc(questionairesFatca,
//                                dataFatca.get("question").toString());
//                        if (question == null) {
//                            result.put("code", 50);
//                            result.put("info", "Data not found: Fatca question");
//                            return result;
//                        }
//
//                        if (dataFatca.get("question") == null || "".equals(dataFatca.get("question").toString())) {
//                            result.put("code", 10);
//                            result.put("info", "Incomplete data : Fatca answer");
//                            return result;
//                        }
//
//                        List listAnswer = (List) dataFatca.get("answers");
//                        if (listAnswer.size() == 0) {
//                            result.put("code", 10);
//                            result.put("info", "Incomplete data : Fatca answer");
//                            return result;
//                        }
//
//                        for (int y = 0; y < listAnswer.size(); y++) {
//                            String answer = (String) listAnswer.get(y);
//                            Answer ans = answerRepository.findByAnswerNameAndQuestion(answer, question);
//                            if (ans == null) {
//                                result.put("code", 50);
//                                result.put("info", "Data not found: Fatca answer");
//                                return result;
//                            }
//                        }
//
//                        List<CustomerAnswer> listCustAnswer = customerAnswerRepository
//                                .findAllByQuestionAndKycOrderByCreatedDateAsc(question, kyc);
//                        if (listCustAnswer.size() == listAnswer.size()) {
//                            // update
//                            for (int y = 0; y < listAnswer.size(); y++) {
//                                CustomerAnswer cusAns = listCustAnswer.get(y);
//                                cusAns.setAnswer(answerRepository
//                                        .findByAnswerNameAndQuestion((String) listAnswer.get(y), question));
//                                cusAns.setQuestion(question);
//                                cusAns.setUpdatedDate(new Date());
//                                cusAns.setUpdatedBy(kyc.getEmail());
//                                customerAnswerRepository.save(cusAns);
//                            }
//                        } else if (listCustAnswer.size() > listAnswer.size()) {
//                            int dataUpdate = listCustAnswer.size() - listAnswer.size();
//
//                            // delete
//                            for (int y = 0; y < dataUpdate; y++) {
//                                customerAnswerRepository.delete(listCustAnswer.get(y));
//                            }
//
//                            // update
//                            for (int z = dataUpdate; z < listCustAnswer.size(); z++) {
//                                int index = 0;
//                                CustomerAnswer cusAns = listCustAnswer.get(z);
//                                cusAns.setAnswer(answerRepository
//                                        .findByAnswerNameAndQuestion((String) listAnswer.get(index), question));
//                                cusAns.setQuestion(question);
//                                cusAns.setUpdatedDate(new Date());
//                                cusAns.setUpdatedBy(kyc.getEmail());
//                                customerAnswerRepository.save(cusAns);
//                            }
//                        } else {
//                            int y;
//
//                            // update
//                            for (y = 0; y < listCustAnswer.size(); y++) {
//                                CustomerAnswer cusAns = listCustAnswer.get(y);
//                                cusAns.setAnswer(answerRepository
//                                        .findByAnswerNameAndQuestion((String) listAnswer.get(y), question));
//                                cusAns.setQuestion(question);
//                                cusAns.setUpdatedDate(new Date());
//                                cusAns.setUpdatedBy(kyc.getEmail());
//                                customerAnswerRepository.save(cusAns);
//                            }
//
//                            // insert
//                            for (int z = y; z < listAnswer.size(); z++) {
//                                CustomerAnswer cusAns = new CustomerAnswer();
//                                cusAns.setAnswer(answerRepository
//                                        .findByAnswerNameAndQuestion((String) listAnswer.get(z), question));
//                                cusAns.setQuestion(question);
//                                cusAns.setCreatedDate(new Date());
//                                cusAns.setCreatedBy(kyc.getEmail());
//                                customerAnswerRepository.save(cusAns);
//                            }
//                        }
//
//                    }
//                }
//            }
//
//            int score = 0;
//            if (map.get("risk_profile") != null) {
//                Questionaires questionairesRisk = questionairesRepository
//                        .findByQuestionnaireCategory(Long.valueOf("1"));
//                List listRisk = (List) map.get("risk_profile");
//                if (listRisk.size() > 0) {
//                    for (int i = 0; i < listRisk.size(); i++) {
//                        Map dataRisk = (Map) listRisk.get(i);
//                        if (dataRisk.get("question") == null || "".equals(dataRisk.get("question").toString())) {
//                            result.put("code", 10);
//                            result.put("info", "Incomplete data : Risk profile question");
//                            return result;
//                        }
//
//                        Question question = questionRepository.findByQuestionairesAndQuestionName(questionairesRisk,
//                                dataRisk.get("question").toString(), new Date());
//                        if (question == null) {
//                            result.put("code", 50);
//                            result.put("info", "Data not found: Risk profile question");
//                            return result;
//                        }
//
//                        if (dataRisk.get("question") == null || "".equals(dataRisk.get("question").toString())) {
//                            result.put("code", 10);
//                            result.put("info", "Incomplete data : Risk profile answer");
//                            return result;
//                        }
//
//                        List listAnswer = (List) dataRisk.get("answers");
//                        if (listAnswer.size() == 0) {
//                            result.put("code", 10);
//                            result.put("info", "Incomplete data : Risk profile answer");
//                            return result;
//                        }
//
//                        for (int y = 0; y < listAnswer.size(); y++) {
//                            String answer = (String) listAnswer.get(y);
//                            Answer ans = answerRepository.findByAnswerNameAndQuestion(answer, question);
//                            if (ans == null) {
//                                result.put("code", 50);
//                                result.put("info", "Data not found: Risk profile answer");
//                                return result;
//                            }
//                        }
//
//                        List<CustomerAnswer> listCustAnswer = customerAnswerRepository
//                                .findAllByQuestionAndKycOrderByCreatedDateAsc(question, kyc);
//                        if (listCustAnswer.size() == listAnswer.size()) {
//                            // update
//                            for (int y = 0; y < listAnswer.size(); y++) {
//                                CustomerAnswer cusAns = listCustAnswer.get(y);
//                                cusAns.setAnswer(answerRepository
//                                        .findByAnswerNameAndQuestion((String) listAnswer.get(y), question));
//                                cusAns.setQuestion(question);
//                                cusAns.setUpdatedDate(new Date());
//                                cusAns.setUpdatedBy(kyc.getEmail());
//                                customerAnswerRepository.save(cusAns);
//                            }
//                        } else if (listCustAnswer.size() > listAnswer.size()) {
//                            int dataUpdate = listCustAnswer.size() - listAnswer.size();
//
//                            // delete
//                            for (int y = 0; y < dataUpdate; y++) {
//                                customerAnswerRepository.delete(listCustAnswer.get(y));
//                            }
//
//                            // update
//                            for (int z = dataUpdate; z < listCustAnswer.size(); z++) {
//                                int index = 0;
//                                CustomerAnswer cusAns = listCustAnswer.get(z);
//                                cusAns.setAnswer(answerRepository
//                                        .findByAnswerNameAndQuestion((String) listAnswer.get(index), question));
//                                cusAns.setQuestion(question);
//                                cusAns.setUpdatedDate(new Date());
//                                cusAns.setUpdatedBy(kyc.getEmail());
//                                customerAnswerRepository.save(cusAns);
//                            }
//                        } else {
//                            int y;
//
//                            // update
//                            for (y = 0; y < listCustAnswer.size(); y++) {
//                                CustomerAnswer cusAns = listCustAnswer.get(y);
//                                cusAns.setAnswer(answerRepository
//                                        .findByAnswerNameAndQuestion((String) listAnswer.get(y), question));
//                                cusAns.setQuestion(question);
//                                cusAns.setUpdatedDate(new Date());
//                                cusAns.setUpdatedBy(kyc.getEmail());
//                                customerAnswerRepository.save(cusAns);
//                            }
//
//                            // insert
//                            for (int z = y; z < listAnswer.size(); z++) {
//                                CustomerAnswer cusAns = new CustomerAnswer();
//                                cusAns.setAnswer(answerRepository
//                                        .findByAnswerNameAndQuestion((String) listAnswer.get(z), question));
//                                cusAns.setQuestion(question);
//                                cusAns.setCreatedDate(new Date());
//                                cusAns.setCreatedBy(kyc.getEmail());
//                                customerAnswerRepository.save(cusAns);
//                            }
//                        }
//                    }
//
//                    List<CustomerAnswer> listCustAnswer = customerAnswerRepository.findAllByKycAndQuestionaries(kyc,
//                            questionairesRisk);
//                    for (int i = 0; i < listCustAnswer.size(); i++) {
//                        CustomerAnswer cusAnswer = listCustAnswer.get(i);
//                        if (cusAnswer.getAnswer() != null) {
//                            score += cusAnswer.getAnswer().getScore();
//                        }
//                    }
//                }
//            }
//
//            if (score > 0) {
//                Score riskProfile = scoreRepository.getScore(Long.valueOf(score), new Date());
//                kyc.setRiskProfile(riskProfile);
//                kycRepository.save(kyc);
//            }
//            Map dataScore = new HashMap();
//            dataScore.put("code", kyc.getRiskProfile().getScoreCode());
//            dataScore.put("value", kyc.getRiskProfile().getScoreName());
//
//            // doc type KTP
//            CustomerDocument custDocKTP = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user, true,
//                    "DocTyp01");
//            // doc type TTD
//            CustomerDocument custDocTTD = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user, true,
//                    "DocTyp03");
//            if (custDocKTP != null && custDocTTD != null && !user.getUserStatus().equalsIgnoreCase("PEN")) {
//                user.setUserStatusSebelumnya(user.getUserStatus());
//                user.setUserStatus("PEN");
//                user.setApprovalStatus(true);
//                userRepository.save(user);
//            }
//
//            Map cusDoc = new HashMap();
//            cusDoc.put("id_card_image", custDocKTP == null ? "" : custDocKTP.getFileKey());
//            cusDoc.put("signature_image", custDocTTD == null ? "" : custDocTTD.getFileKey());
//
//            Map data = new HashMap();
//            data.put("customer_key", user.getCustomerKey());
//            data.put("customer_id", kyc.getPortalcif());
//            data.put("customer_status", user.getUserStatus());
//            data.put("customer_document", cusDoc);
//            data.put("customer_risk_profile", dataScore);
//
//            result.put("code", 0);
//            result.put("info", "Customer profile successfully updated");
//            result.put("data", data);
//            return result;
//        } catch (DataIntegrityViolationException de) {
//            de.printStackTrace();
//            result.put("code", 99);
//            result.put("info", "General error");
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            result.put("code", 99);
//            result.put("info", "General error");
//            return result;
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Map profileView(Map map, User user) {
//        Map result = new HashMap();
//        try {
//            Kyc kyc = kycRepository.findByAccount(user);
//            if (kyc == null) {
//                result.put("code", 50);
//                result.put("info", "Data not found : customer");
//                return result;
//            }
//
//            Map dataGeneral = new HashMap();
//            dataGeneral.put("first_name", kyc.getFirstName());
//            dataGeneral.put("last_name", kyc.getLastName());
//            dataGeneral.put("phone_number", kyc.getMobileNumber());
//
//            Map dataKyc = new HashMap();
//            dataKyc.put("birth_date", new SimpleDateFormat("yyyy-MM-dd").format(kyc.getBirthDate()));
//            dataKyc.put("birth_place", kyc.getBirthPlace());
//            dataKyc.put("gender", kyc.getGender());
//            dataKyc.put("nationality",
//                    countriesRepository.findById(Long.valueOf(kyc.getNationality())).getAlpha3Code());
//            dataKyc.put("marital_status", kyc.getMaritalStatus());
//            dataKyc.put("mother_maiden_name", kyc.getMotherMaidenName());
//            dataKyc.put("annual_income", kyc.getTotalIncomePa());
//            dataKyc.put("education_background", kyc.getEducationBackground());
//            dataKyc.put("religion", kyc.getReligion());
//            dataKyc.put("statement_type", kyc.getPreferredMailingAddress());
//            dataKyc.put("occupation", kyc.getOccupation());
//            dataKyc.put("business_nature", kyc.getNatureOfBusiness());
//            dataKyc.put("id_number", kyc.getIdNumber());
//            dataKyc.put("id_expiration",
//                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,S'Z'").format(kyc.getIdExpirationDate()));
//
//            Map dataLegal = new HashMap();
//            dataLegal.put("country", countriesRepository.findById(Long.valueOf(kyc.getLegalCountry())).getAlpha3Code());
//            dataLegal.put("province", kyc.getLegalProvince());
//            dataLegal.put("city", citiesRepository.findById(Long.valueOf(kyc.getLegalCity())).getCityCode());
//            dataLegal.put("postal_code", kyc.getLegalPostalCode());
//            dataLegal.put("address", kyc.getLegalAddress());
//            dataLegal.put("phone", kyc.getLegalPhoneNumber());
//            dataKyc.put("legal", dataLegal);
//
//            Map dataMailing = new HashMap();
//            dataMailing.put("country",
//                    countriesRepository.findById(Long.valueOf(kyc.getHomeCountry())).getAlpha3Code());
//            dataMailing.put("province", kyc.getHomeProvince());
//            dataMailing.put("city", citiesRepository.findById(Long.valueOf(kyc.getHomeCity())).getCityCode());
//            dataMailing.put("postal_code", kyc.getHomePostalCode());
//            dataMailing.put("address", kyc.getHomeAddress());
//            dataMailing.put("phone", kyc.getHomePhoneNumber());
//            dataKyc.put("mailing", dataMailing);
//
//            dataKyc.put("income_source", kyc.getSourceOfIncome());
//            dataKyc.put("total_asset", kyc.getTotalAsset());
//            dataKyc.put("investment_purpose", kyc.getInvestmentPurpose());
//            dataKyc.put("investment_experience", kyc.getInvestmentExperience());
//
//            if ("IE04".equals(kyc.getInvestmentExperience())) {
//                dataKyc.put("other_investment_ experience", kyc.getOtherInvestmentExperience());
//            }
//
//            SettlementAccounts account = settlementAccountsRepository.findByKycs(kyc);
//            dataKyc.put("settlement_bank", account.getBankId().getBankCode());
//            dataKyc.put("settlement_account_name", account.getSettlementAccountName());
//            dataKyc.put("settlement_account_no", account.getSettlementAccountNo());
//
//            List listFatca = new ArrayList();
//            Questionaires questionairesFatca = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("2"));
//            List<Question> listQuestionFatca = questionRepository
//                    .findAllByQuestionairesOrderBySeqAsc(questionairesFatca);
//
//            for (int i = 0; i < listQuestionFatca.size(); i++) {
//                Map data = new HashMap();
//                Question question = listQuestionFatca.get(i);
//                List<CustomerAnswer> listFatcaAnswer = customerAnswerRepository
//                        .findAllByKycAndQuestionOrderByCreatedDateAsc(kyc, question);
//                List listAnswers = new ArrayList();
//                for (int y = 0; y < listFatcaAnswer.size(); y++) {
//                    listAnswers.add(listFatcaAnswer.get(y).getAnswer().getAnswerName());
//                }
//                data.put("question", question.getQuestionName());
//                data.put("answer", listAnswers);
//                listFatca.add(data);
//            }
//
//            List listRisk = new ArrayList();
//            Questionaires questionairesRisk = questionairesRepository.findByQuestionnaireCategory(Long.valueOf("1"));
//            List<Question> listQuestionRisk = questionRepository
//                    .findAllQuestionByQuestionairesWithQuery(questionairesRisk);
//
//            for (int i = 0; i < listQuestionRisk.size(); i++) {
//                Map data = new HashMap();
//                Question question = listQuestionRisk.get(i);
//                List<CustomerAnswer> listRiskAnswer = customerAnswerRepository
//                        .findAllByKycAndQuestionOrderByCreatedDateAsc(kyc, question);
//                List listAnswers = new ArrayList();
//                for (int y = 0; y < listRiskAnswer.size(); y++) {
//                    listAnswers.add(listRiskAnswer.get(y).getAnswer().getAnswerName());
//                }
//                data.put("question", question.getQuestionName());
//                data.put("answer", listAnswers);
//                listRisk.add(data);
//            }
//
//            Map dataCustomer = new HashMap();
//            dataCustomer.put("general", dataGeneral);
//            dataCustomer.put("kyc", dataKyc);
//            dataCustomer.put("fatca", listFatca);
//            dataCustomer.put("risk_profile", listRisk);
//
//            String cus = kyc.getAccount().getUserStatus(); // current user status
//            String pus = kyc.getAccount().getUserStatusSebelumnya(); // previous user status
//
//            // doc type KTP
//            CustomerDocument custDocKTP = null;
//            // doc type TTD
//            CustomerDocument custDocTTD = null;
//            if (cus.equals("VER") && pus.equals("PEN")) {
//                custDocKTP = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user, true, "DocTyp01");
//                custDocTTD = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user, true, "DocTyp03");
//            } else {
//                try {
//                    custDocKTP = (CustomerDocument) customerDocumentRepository
//                            .findTop1ByDocumentTypeAndUserOrderByCreatedOnDesc("DocTyp01", user).get(0);
//                    custDocTTD = (CustomerDocument) customerDocumentRepository
//                            .findTop1ByDocumentTypeAndUserOrderByCreatedOnDesc("DocTyp03", user).get(0);
//                } catch (Exception e) {
//                    System.out.println("error custDoc== " + e);
//                }
//            }
//
//            Map cusDoc = new HashMap();
//            cusDoc.put("id_card_image", custDocKTP == null ? null : custDocKTP.getFileKey());
//            cusDoc.put("signature_image", custDocTTD == null ? null : custDocTTD.getFileKey());
//
//            Map dataScore = new HashMap();
//            dataScore.put("code", kyc.getRiskProfile().getScoreCode());
//            dataScore.put("value", kyc.getRiskProfile().getScoreName());
//
//            Map dataProfile = new HashMap();
//            dataProfile.put("customer_id", kyc.getPortalcif());
//            dataProfile.put("customer_status", kyc.getAccount().getUserStatus());
//            dataProfile.put("customer_document", cusDoc);
//            dataProfile.put("customer_risk_profile", dataScore);
//            dataProfile.put("customer_data", dataCustomer);
//
//            result.put("code", 0);
//            result.put("info", "Customer profile successfully loaded");
//            result.put("data", dataProfile);
//            return result;
//        } catch (DataIntegrityViolationException de) {
//            de.printStackTrace();
//            result.put("code", 99);
//            result.put("info", "General error");
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            result.put("code", 99);
//            result.put("info", "General error");
//            return result;
//        }
//    }
//
//    public Map businessStatus(Map map, User user) {
//        Map result = new HashMap();
//        try {
//            Kyc kyc = kycRepository.findByAccount(user);
//            if (kyc == null) {
//                result.put("code", 50);
//                result.put("info", "Data not found : customer");
//                return result;
//            }
//
//            Map data = new HashMap();
//
//            // doc type KTP
//            CustomerDocument custDocKTP = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user, true,
//                    "DocTyp01");
//            // doc type TTD
//            CustomerDocument custDocTTD = customerDocumentRepository.findByUserAndRowStatusAndDocumentType(user, true,
//                    "DocTyp03");
//
//            Map cusDoc = new HashMap();
//            cusDoc.put("id_card_image", custDocKTP == null ? "" : custDocKTP.getFileKey());
//            cusDoc.put("signature_image", custDocTTD == null ? "" : custDocTTD.getFileKey());
//
//            Map dataScore = new HashMap();
//            dataScore.put("code", kyc.getRiskProfile().getScoreCode());
//            dataScore.put("value", kyc.getRiskProfile().getScoreName());
//
//            data.put("customer_status", kyc.getAccount().getUserStatus());
//            data.put("customer_document", cusDoc);
//            data.put("customer_risk_profile", dataScore);
//
//            result.put("code", 0);
//            result.put("info", "Customer business status loaded");
//            result.put("data", data);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            result.put("code", 99);
//            result.put("info", "General error");
//            return result;
//        }
//    }
//
//    @Override
//    public Map loginOfficer(Map map, String ip) {
//        Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
//        if (agent != null) {
//            AgentCredential agentCredential = agentCredentialRepository.findByAgent(agent);
//            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//            String hashedPassword = passwordEncoder.encode(String.valueOf(map.get("password")));
//            if (agentCredential != null) {
//                if (hashedPassword == agentCredential.getPassword()
//                        || agentCredential.getPassword().equals(hashedPassword)) {
//                    System.out.println("masuk");
//                }
//            }
//        }
//
//        return null;
//    }
//
//    @Override
//    public Map preRegister(Map map) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Map preRegisterAndOrder(Map map) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//}
