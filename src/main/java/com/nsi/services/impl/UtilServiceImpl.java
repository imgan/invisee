package com.nsi.services.impl;

import com.nsi.domain.core.AccessPermission;
import com.nsi.domain.core.Agent;
import com.nsi.domain.core.GroupAccess;
import com.nsi.domain.core.Groups;
import com.nsi.domain.core.User;
import com.nsi.dto.request.TokenValidationRequest;
import com.nsi.dto.response.TokenValidationResponse;
import com.nsi.repositories.core.*;
import com.nsi.services.UtilService;
import com.nsi.util.TokenGenerator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtilServiceImpl implements UtilService {

  @PersistenceContext
  EntityManager em;
  @Autowired
  GroupAccessRepository groupAccessRepository;
  @Autowired
  AccessPermissionRepository accessPermissionRepository;
  @Autowired
  KycRepository kycRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  GroupsRepository groupsRepository;
  @Autowired
  AgentRepository agentRepository;

  private final Long tokenTTLMillis = Long.valueOf(7200000);

  private final Long tokenTimeoutMillis = Long.valueOf(1800000);

  private final Long tokenTimePrecisionMillis = Long.valueOf(240000);

  @Override
  public Map checkToken(String token, String ip) {
    Map result = new HashMap();
    if (token == null || "".equals(token.trim())) {
      result.put("code", 99);
      result.put("info", "Token salah");
      return result;
    }
    String hashed = TokenGenerator.hash(token, ip);
    User user = userRepository.findByToken(hashed);
    if (user != null) {
      if (TokenGenerator.isExpired(token, this.tokenTTLMillis, this.tokenTimeoutMillis,
          this.tokenTimePrecisionMillis)) {
        user.setToken(token);
        userRepository.save(user);

        result.put("code", 100);
        result.put("info", "Token Expired");
        return result;
      }

      result.put("code", 1);
      result.put("user", user);
      return result;
    } else {
      result.put("code", 100);
      result.put("info", "Token salah");
      return result;
    }
  }

  @Override
  public Boolean checkAccessPermission(Agent agent) {
    Groups groups = agent.getAccessGroup();
    List<GroupAccess> access = groupAccessRepository.findAllByGroupAndRowStatus(groups, true);
    Boolean valid_role = false;
    //Boolean valid_role = true;
    for (GroupAccess acces : access) {
      AccessPermission check_permission = accessPermissionRepository.findById(acces.getId());
      if (check_permission.getCode().equals("PER002") && check_permission.getRowStatus().equals(true)) {
        valid_role = true;
        break;
      }
    }
    return valid_role;
  }

  @Override
  public String generatePortalCIF() {

    SimpleDateFormat sdf = new SimpleDateFormat("YYMM");
    String pref = "C" + sdf.format(new Date());
    Long seq = kycRepository.getNextSeriesId("seq_portalcif");
    String n = String.format("%06d", seq);
    String portalcif = pref.concat(n);

    return portalcif;
  }

  @Override
  public Boolean checkAccessPermissionSuperAdmin(Agent agent) {
    Groups groups = agent.getAccessGroup();
    Groups group_admin = groupsRepository.findByCodeAndRowStatus("GROUP003", true); //AGENT_ADMIN
    if (group_admin != groups) {
      return false;
    }
    List<GroupAccess> group_access = groupAccessRepository
        .findAllByGroupAndRowStatus(group_admin, true);
    AccessPermission admin_permission = accessPermissionRepository
        .findByCodeAndRowStatus("ADM001", true);

    if (group_access.isEmpty()) {
      return false;
    } else {
      return group_access.contains(admin_permission);
    }
  }

  public static final Pattern VALID_EMAIL_ADDRESS_REGEX
      = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

  public boolean validate(String emailStr) {
    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
    return matcher.find();
  }

  @Override
  public TokenValidationResponse tokenValidation(TokenValidationRequest request) {
    TokenValidationResponse response = new TokenValidationResponse();

    User user = this.findUser(request);
    Boolean isExpired = this.isExpired(request);
    Boolean isAllowed;
    String message;

    if (user != null) {
      if (!isExpired) {
        isAllowed = true;
        message = "Authorization granted";
      } else {
        isAllowed = false;
        message = "Authorization expired";
      }
    } else {
      isAllowed = false;
      message = "Authorization not found";
    }

    response.setAllowed(isAllowed);
    response.setMessage(message);
    response.setUser(user);

    return response;
  }

  @Override
  public String random(Integer n) {
    String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    StringBuilder result = new StringBuilder();
    Random r = new Random();
    while(result.length() < n) {
      result.append(alphabet.charAt(r.nextInt(alphabet.length())));
    }
    return result.toString();
  }

  private User findUser(TokenValidationRequest request) {
    String token = request.getAuthorization().split(" ")[1];
    String ip = request.getIpAddress();
    String hashed = TokenGenerator.hash(token, ip);
    return userRepository.findByToken(hashed);
  }

  private Boolean isExpired(TokenValidationRequest request) {
    String token = request.getAuthorization().split(" ")[1];
    return TokenGenerator.isExpired(token, this.tokenTTLMillis, this.tokenTimeoutMillis,
        this.tokenTimePrecisionMillis);
  }

  @Override
  public Map checkTokenAgent(String token, String ip) {
    Map result = new HashMap();
    if (token == null || "".equals(token.trim())) {
      result.put("code", 99);
      result.put("info", "Token salah");
      return result;
    }

    String hashed = TokenGenerator.hash(token, ip);
    Agent agent = agentRepository.findByToken(hashed);
    if (agent != null) {
      if (TokenGenerator.isExpired(token, this.tokenTTLMillis, this.tokenTimeoutMillis, this.tokenTimePrecisionMillis)) {
        agent.setToken(null);
        agentRepository.save(agent);

        result.put("code", 100);
        result.put("info", "Token Expired");
        return result;
      }

      result.put("code", 1);
      result.put("agent", agent);
      return result;
    } else {
      result.put("code", 100);
      result.put("info", "Token salah");
      return result;
    }
  }
}
