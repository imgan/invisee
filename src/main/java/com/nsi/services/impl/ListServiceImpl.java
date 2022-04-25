package com.nsi.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.Answer;
import com.nsi.domain.core.Bank;
import com.nsi.domain.core.Cities;
import com.nsi.domain.core.Countries;
import com.nsi.domain.core.LookupHeader;
import com.nsi.domain.core.LookupLine;
import com.nsi.domain.core.Question;
import com.nsi.domain.core.Questionaires;
import com.nsi.domain.core.States;
import com.nsi.repositories.core.AgentRepository;
import com.nsi.repositories.core.AnswerRepository;
import com.nsi.repositories.core.BankRepository;
import com.nsi.repositories.core.CitiesRepository;
import com.nsi.repositories.core.CountriesRepository;
import com.nsi.repositories.core.LookupHeaderRepository;
import com.nsi.repositories.core.LookupLineRepository;
import com.nsi.repositories.core.QuestionRepository;
import com.nsi.repositories.core.QuestionairesRepository;
import com.nsi.repositories.core.StatesRepository;
import com.nsi.services.ChannelService;
import com.nsi.services.ListService;
import com.nsi.services.UtilService;

@Service
public class ListServiceImpl extends BaseService implements ListService {

  @Autowired
  ChannelService channelService;
  @Autowired
  LookupHeaderRepository lookupHeaderRepository;
  @Autowired
  LookupLineRepository lookupLineRepository;
  @Autowired
  CountriesRepository countriesRepository;
  @Autowired
  StatesRepository statesRepository;
  @Autowired
  CitiesRepository citiesRepository;
  @Autowired
  BankRepository bankRepository;
  @Autowired
  UtilService utilService;
  @Autowired
  AgentRepository agentRepository;
  @Autowired
  QuestionairesRepository questionairesRepository;
  @Autowired
  QuestionRepository questionRepository;
  @Autowired
  AnswerRepository answerRepository;


  @Override
  public Map getListLookupLine(Map map, String lookupHeader) {
    Map resultMap = new HashMap();

    Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);

    Map result = channelService.generateAgentSignature(String.valueOf(map.get("agent")),
        String.valueOf(map.get("signature")));
    if (result.get("code").equals(0)) {
      if (utilService.checkAccessPermission(agent)) {
        LookupHeader header = lookupHeaderRepository.findByCategory(lookupHeader);
        List<LookupLine> lines = lookupLineRepository
            .findAllByCategoryOrderBySequenceLookupAsc(header);
        List<Map> dataMapList = new ArrayList<>();
        int sequence = 1;

        for (LookupLine line : lines) {
          Map dataMap = new HashMap<>();
          dataMap.put("sequence", sequence);
          dataMap.put("code", line.getCode());
          dataMap.put("value", line.getValue());
          dataMapList.add(dataMap);
          sequence++;
        }

        resultMap.put("code", 0);
        resultMap.put("data", dataMapList);
      } else {
        resultMap.put("code", 12);
        resultMap.put("info", "invalid access, you dont have permission");
      }
    } else {
      resultMap.put("code", result.get("code"));
      resultMap.put("info", result.get("info"));
    }
    return resultMap;
  }

  @Override
  public Map getListCountries(Map map) {
    Map resultMap = new HashMap();
    Map result = channelService.generateAgentSignature(String.valueOf(map.get("agent")),
        String.valueOf(map.get("signature")));
    if (result.get("code").equals(0)) {
      Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
      if (utilService.checkAccessPermission(agent)) {

        List<Countries> countries = countriesRepository.findAllByOrderByCountryNameAsc();
        List<Map> dataMapList = new ArrayList<>();
        for (Countries c : countries) {
          Map dataMap = new HashMap<>();
          dataMap.put("code", c.getAlpha3Code());
          dataMap.put("value", c.getCountryName());
          dataMapList.add(dataMap);
        }

        resultMap.put("code", 0);
        resultMap.put("data", dataMapList);
      } else {
        resultMap.put("code", 12);
        resultMap.put("info", "invalid access, you dont have permission");
      }
    } else {
      resultMap.put("code", result.get("code"));
      resultMap.put("info", result.get("info"));
    }
    return resultMap;
  }

  @Override
  public Map getListProvinces(Map map, String version) {
    Map resultMap = new HashMap();
    if (String.valueOf(map.get("agent")) == "" || String.valueOf(map.get("agent")).equals("")) {
      resultMap.put("code", 10);
      resultMap.put("info", "incomplete data");
    }

    Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);
    Map result = channelService.generateAgentSignature(String.valueOf(map.get("agent")), String.valueOf(map.get("signature")));
    if (result.get("code").equals(0)) {
      if (utilService.checkAccessPermission(agent)) {
        Countries countries = countriesRepository.findByAlpha3Code(String.valueOf(map.get("country")));
        List<States> states;
        if("2".equals(version)){
          states = statesRepository.findAllByCountriesOrderByStateNameAsc(countries);
        }else{
          states = statesRepository.findAllByCountriesOrderByCountriesAsc(countries);
        }
        List<Map> dataMapList = new ArrayList<>();
        for (States prov : states) {
          Map dataMap = new HashMap<>();
          dataMap.put("code", prov.getStateCode());
          dataMap.put("value", prov.getStateName().toUpperCase());
          dataMapList.add(dataMap);
        }

        resultMap.put("code", 0);
        resultMap.put("data", dataMapList);
      } else {
        resultMap.put("code", 12);
        resultMap.put("info", "invalid access, you dont have permission");
      }
    } else {
      resultMap.put("code", result.get("code"));
      resultMap.put("info", result.get("info"));
    }
    return resultMap;
  }

  @Override
  public Map getListCity(Map map) {
    Map resultMap = new HashMap();
    if (String.valueOf(map.get("agent")) == "" || String.valueOf(map.get("agent")).equals("")) {
      resultMap.put("code", 10);
      resultMap.put("info", "incomplete data");
    }

    Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);

    Map result = channelService.generateAgentSignature(String.valueOf(map.get("agent")),
        String.valueOf(map.get("signature")));
    if (result.get("code").equals(0)) {
      if (utilService.checkAccessPermission(agent)) {
        States prov = statesRepository.findByStateCode(String.valueOf(map.get("province")));
        List<Cities> cities = citiesRepository.findAllByStatesOrderByCityNameAsc(prov);
        List<Map> dataMapList = new ArrayList<>();
        for (Cities city : cities) {
          Map dataMap = new HashMap<>();
          dataMap.put("code", city.getCityCode());
          dataMap.put("value", city.getCityName().toString().toUpperCase());
          dataMapList.add(dataMap);
        }

        resultMap.put("code", 0);
        resultMap.put("data", dataMapList);
      } else {
        resultMap.put("code", 12);
        resultMap.put("info", "invalid access, you dont have permission");
      }
    } else {
      resultMap.put("code", result.get("code"));
      resultMap.put("info", result.get("info"));
    }
    return resultMap;
  }

  @Override
  public Map getListBank(Map map) {
    Map resultMap = new HashMap();
    if (String.valueOf(map.get("agent")) == "" || String.valueOf(map.get("agent")).equals("")) {
      resultMap.put("code", 10);
      resultMap.put("info", "incomplete data");
    }

    Agent agent = agentRepository.findByCodeAndRowStatus(String.valueOf(map.get("agent")), true);

    Map result = channelService.generateAgentSignature(String.valueOf(map.get("agent")),
        String.valueOf(map.get("signature")));
    if (result.get("code").equals(0)) {
      if (utilService.checkAccessPermission(agent)) {
        List<Bank> bankList = bankRepository.findAllByOrderByBankNameAsc();
        List<Map> dataMapList = new ArrayList<>();
        for (Bank bank : bankList) {
          Map dataMap = new HashMap<>();
          dataMap.put("code", bank.getBankCode());
          dataMap.put("value", bank.getBankName());
          dataMapList.add(dataMap);
        }

        resultMap.put("code", 0);
        resultMap.put("data", dataMapList);
      } else {
        resultMap.put("code", 12);
        resultMap.put("info", "invalid access, you dont have permission");
      }

    } else {
      resultMap.put("code", result.get("code"));
      resultMap.put("info", result.get("info"));
    }
    return resultMap;
  }

  @Override
  public Map getListFatca() {
    Questionaires fatcaDefault = questionairesRepository.findByQuestionnaireName("FATCA Default");
//        List<Question> questions = questionRepository.findAllByQuestionairesOrderBySeqAsc(fatcaDefault);
    List<Question> questions = questionRepository
        .findAllByQuestionairesAndParentqIdIsNullOrderBySeqAsc(fatcaDefault);
    List<Map> questionMap = new ArrayList<>();
    if (!questions.isEmpty()) {
      for (Question q : questions) {
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

        Map dataQuestion = new HashMap<>();
        dataQuestion.put("question", map);
        questionMap.add(dataQuestion);
      }
    }
    Map resultMap = new HashMap<>();
    resultMap.put("code", 0);
    resultMap.put("info", "FATCA list successfully loaded");
    resultMap.put("data", questionMap);
    return resultMap;
  }

  @Override
  public Map getListRiskProfile() {
    Questionaires questionaires = questionairesRepository.findByQuestionnaireCategory(1L);
    List<Question> questions = questionRepository
        .findAllQuestionByQuestionairesWithQuery(questionaires);
    List<Map> questionMap = new ArrayList<>();
    if (!questions.isEmpty()) {
      for (Question q : questions) {
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

        Map dataQuestion = new HashMap<>();
        dataQuestion.put("question", map);
        questionMap.add(dataQuestion);
      }
    }
    Map resultMap = new HashMap<>();
    resultMap.put("code", 0);
    resultMap.put("info", "Risk Profile list successfully loaded");
    resultMap.put("data", questionMap);
    return resultMap;
  }

}
