package com.nsi.services;

import com.nsi.dto.request.TokenValidationRequest;
import com.nsi.dto.response.TokenValidationResponse;
import java.util.Map;
import com.nsi.domain.core.Agent;

public interface UtilService {

  Map checkToken(String token, String ip);

  Boolean checkAccessPermission(Agent agent);

  String generatePortalCIF();

  Boolean checkAccessPermissionSuperAdmin(Agent agent);

  boolean validate(String str);

  TokenValidationResponse tokenValidation(TokenValidationRequest request);

  String random(Integer n);
  Map checkTokenAgent(String token, String ip);
}
