package com.nsi.services;

import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import com.nsi.dto.request.PasswordChangeRequest;
import com.nsi.dto.request.PasswordForgotConfirmRequest;
import com.nsi.dto.request.PasswordForgotRequest;
import com.nsi.dto.response.BaseResponse;
import com.nsi.dto.response.CompletenessResponse;
import com.nsi.dto.response.PasswordChangeResponse;

import freemarker.template.TemplateException;

public interface CustomerService {

  BaseResponse passwordForgot(PasswordForgotRequest request);

  BaseResponse passwordForgotConfirm(PasswordForgotConfirmRequest request);

  PasswordChangeResponse passwordChange(PasswordChangeRequest request,
      HttpServletRequest httpServletRequest);

  Map loginByCustomerCif(String customerCif, String signature, String ip);

  Map loginByUsername(String username, String password, String ip);

  Map uploadDocument(User user, MultipartFile uploadfile, String documentType)
      throws Exception;

  Map profileView(User user);

  Map profileRegister(Map map);

  Map profileRegisterCustomer(Map map);

  Map preRegister(Map map);

  Map profileRegisterVer2(Map map) throws MessagingException, IOException, TemplateException;

  Map preRegisterAndOrder(Map map);

  Map profileUpdate(Map map, User user);

  Map profileUpdateSettlement(Map map, User user) throws Exception;

  Map login(Map map, String ip);

  Map loginOfficer(Map map, String ip);

  Map viewKyc(User user);

  Map viewFatca(User user);

  Map viewRiskProfile(User user);

  Double completenessFatca(Kyc kyc);

  Double completenessRiskProfile(Kyc kyc);

  Double completenessKyc(Kyc kyc);

  Map updateFatca(Map map, User user);

  Map updateRiskProfile(Map map, User user);

  Map profileView(Map map, User user);

  Map businessStatus(Map map, User user);

  BaseResponse<CompletenessResponse> completeness(HttpServletRequest httpServletRequest,
      String authorization);

  boolean isReferralExist(Map request);

  ResponseEntity<BaseResponse> logout(String token, HttpServletRequest httpServletRequest);

  Map registerAndOrder(Map map);

  Map loginPartner (String memberId, String jsessionId, String ip);

  Map connectToPartner (Map map);

  Map resendActivationCode(User user) throws MessagingException, IOException, TemplateException;

  Map activationCustomer(User user, String activationCode);
  Map getDataMigration(Integer offset, Integer limit, String key);

  Map profileRegisterV2(Map map) throws Exception;
  
  Map profileUpdateV2(Map map, User user);
}
