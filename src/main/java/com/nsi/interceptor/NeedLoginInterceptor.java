package com.nsi.interceptor;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.nsi.domain.core.User;
import com.nsi.services.AgentService;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsi.domain.core.Agent;
import com.nsi.services.UtilService;
import com.nsi.util.ConstantUtil;
import com.nsi.util.Validator;

@SuppressWarnings("rawtypes")
public class NeedLoginInterceptor extends HandlerInterceptorAdapter{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UtilService utilService;
	@Autowired
	private AgentService agentService;

	private final String FAIL_PAGE_URL = "/agent/failed";
	private final String FAIL_V2_PAGE_URL = "/agent/v2/failed";
	private final String CUSTOMER_FAIL_PAGE_URL = "/customer/failed";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		loggerHttp(request);
		if (handler instanceof HandlerMethod) {
			// because all requests will go through here, so only dynamical
			// request should validate this.
			HandlerMethod method = (HandlerMethod) handler;

			NeedLogin needLogin = method.getMethodAnnotation(NeedLogin.class);
			if (needLogin != null) {
				if(needLogin.value() == NeedLogin.UserLogin.AGENT_APPS) {
					return validateAgentApps(request, response);
				}else if(needLogin.value() == NeedLogin.UserLogin.AGENT){
					return validateAgent(request, response);
				}else if(needLogin.value() == NeedLogin.UserLogin.CUSTOMER_WITHOUT_SIGNATURE){
					return validateCustomer(request, response, false);
				}else {
					return validateCustomer(request, response, true);
				}
			}
		}
		return true;
	}

	private boolean validateAgentApps(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String xAgentToken = "";
		try {
			xAgentToken = request.getHeader(ConstantUtil.X_AGENT_TOKEN); // get agent token from header
			String ipAddress = request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR");
			if(Validator.isNullOrEmpty(xAgentToken)){
				xAgentToken = request.getParameter(ConstantUtil.X_AGENT_TOKEN);
			}
			if(Validator.isNullOrEmpty(xAgentToken) ) {
				logger.error("[INFO] xAgentToken is null");
				request.setAttribute(ConstantUtil.DATA_NOT_FOUND, ConstantUtil.X_AGENT_TOKEN);
				RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(FAIL_PAGE_URL);
				dispatcher.forward(request, response);
				return false;
			}
			Map data = utilService.checkTokenAgent(xAgentToken, ipAddress);
			if ((int) (data.get("code")) != 1) {
				logger.error("[INFO] Invalid token: {}", xAgentToken);
				request.setAttribute("data", data);
				RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(FAIL_PAGE_URL);
				dispatcher.forward(request, response);
				return false;
			}
			Agent loginAgent = (Agent) data.get("agent");
			request.setAttribute(ConstantUtil.LOGINED_AGENT, loginAgent);
		} catch (Exception e) {
			logger.error("[FATAL] error while validate token : {}", xAgentToken);
			throw new Exception(e);
		}
		return true;
	}

	private boolean validateAgent(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String requestBody = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
		JSONParser parser = new JSONParser();
		JSONObject map = (JSONObject) parser.parse(requestBody);
		try {
			if(Validator.isNullOrEmpty(map.get("agent")) ) {
				logger.error("[INFO] agent is null");
				Map resultMap = errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "agent", null);
				request.setAttribute("data", resultMap);
				RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(FAIL_V2_PAGE_URL);
				dispatcher.forward(request, response);
				return false;
			}

			if(Validator.isNullOrEmpty(map.get("signature")) ) {
				logger.error("[INFO] signature is null");
				Map resultMap = errorResponse(ConstantUtil.STATUS_INCOMPLETE_DATA, "signature", null);
				request.setAttribute("data", resultMap);
				RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(FAIL_V2_PAGE_URL);
				dispatcher.forward(request, response);
				return false;
			}

			if (!agentService.checkSignatureAgent(map.get("agent").toString(), map.get("signature").toString())) {
				Map resultMap = errorResponse(ConstantUtil.STATUS_ACCESS_DENIED, "Channel invalid", null);
				logger.error("[INFO] Invalid signature: {}", map.get("agent") + ", "+map.get("signature"));
				request.setAttribute("data", resultMap);
				RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(FAIL_V2_PAGE_URL);
				dispatcher.forward(request, response);
				return false;
			}

			request.setAttribute(ConstantUtil.REQ_BODY, map);
		} catch (Exception e) {
			logger.error("[FATAL] error while validate signature agent : {}", map.get("agent"));
			throw new Exception(e);
		}
		return true;
	}

	private boolean validateCustomer(HttpServletRequest request, HttpServletResponse response, Boolean withsignature) throws Exception{
		String requestBody = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
		JSONParser parser = new JSONParser();
		JSONObject map = (JSONObject) parser.parse(requestBody);
		String ipAddress = request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR");
		if(Validator.isNullOrEmpty(map.get("token")) ) {
			logger.error("[INFO] token is null");
			request.setAttribute(ConstantUtil.DATA_NOT_FOUND, ConstantUtil.TOKEN.toLowerCase());
			RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(CUSTOMER_FAIL_PAGE_URL);
			dispatcher.forward(request, response);
			return false;
		}

		if(withsignature){
			if(Validator.isNullOrEmpty(map.get("signature")) ) {
				logger.error("[INFO] signature is null");
				request.setAttribute(ConstantUtil.DATA_NOT_FOUND, ConstantUtil.SIGNATURE);
				RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(CUSTOMER_FAIL_PAGE_URL);
				dispatcher.forward(request, response);
				return false;
			}
		}

		Map data = utilService.checkToken(String.valueOf(map.get("token")), ipAddress);
		if ((int) data.get("code") != 1) {
			logger.error("[INFO] Invalid customer: {}", map.get("token"));
			request.setAttribute("data", data);
			RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(CUSTOMER_FAIL_PAGE_URL);
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			dispatcher.forward(request, response);
			return false;
		}

		User user = (User) data.get("user");
		if (user.getAgent() == null || user.getCustomerKey() == null) {
			RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(CUSTOMER_FAIL_PAGE_URL);
			data = new LinkedHashMap();
			data.put("code", 10);
			data.put("info", "incomplete data");

			request.setAttribute("data", data);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			dispatcher.forward(request, response);
			return false;
		}

		if(withsignature){
			Boolean checkSignature = agentService.checkSignatureCustomer(user, String.valueOf(map.get("signature")));
			if (!checkSignature) {
				RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(CUSTOMER_FAIL_PAGE_URL);
				data = new LinkedHashMap();
				data.put("code", 12);
				data.put("info", "Invalid access");
				request.setAttribute("data", data);
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				dispatcher.forward(request, response);
				return false;
			}
		}

		request.setAttribute(ConstantUtil.LOGINED_USER, user);
		request.setAttribute(ConstantUtil.REQ_BODY, map);
		return true;
	}

	void loggerHttp(HttpServletRequest request) {
		if(logger.isDebugEnabled()) {
			String uri = request.getServerName() + ":" + request.getServerPort() + request.getRequestURI();
			if (request.getQueryString() != null) uri += "?" + request.getQueryString();
			String parameterJson = request.getParameterMap().toString();
			String headersJson = "";
			try {
				ObjectMapper om = new ObjectMapper();
				parameterJson = om.writerWithDefaultPrettyPrinter().writeValueAsString(request.getParameterMap());
				Map<String, String> headers = new HashMap<String, String>();
				for (Enumeration names = request.getHeaderNames(); names.hasMoreElements();) {
					String name = (String)names.nextElement();
					for (Enumeration values = request.getHeaders(name); values.hasMoreElements();) {
						String value = (String)values.nextElement();
						headers.put(name,value);
					}
				}
				headersJson = om.writerWithDefaultPrettyPrinter().writeValueAsString(headers);
			} catch (Exception e) {
				logger.error("failed to log request", e);
			}
			logger.error("request " + request.getMethod() + " : " + uri 
					+ "\nParameter : " + parameterJson 
					+ "\nHeaders : " + headersJson);
		}
	}

	Map errorResponse(int codeId, String field, Object data) {
		String info;
		switch (codeId) {
			case 0:
				info = "successfully loaded : " + field;
				break;
			case 1:
				info = "login success";
				if (field != null) {
					info += " : " + field;
				}
				break;
			case 10:
				info = "incomplete data (errorResponse Base): " + field;
				break;
			case 11:
				info = "invalid data format : " + field;
				break;
			case 12:
				info = "invalid access";
				if (field != null) {
					info += " : " + field;
				}
				break;
			case 13:
				info = "Existing request : " + field;
				break;
			case 14:
				info = "Invalid request : " + field;
				break;
			case 50:
				info = "data not found : " + field;
				break;
			case 88:
				info = "fail execute : " + field;
				break;
			case 99:
				info = "general error";
				if (field != null) {
					info += " : " + field;
				}
				break;
			case 100:
				info = "token invalid";
				if (field != null) {
					info += " : " + field;
				}
				break;
			case 101:
				info = "token expired";
				if (field != null) {
					info += " : " + field;
				}
				break;
			default:
				info = "Unknown error : " + field;
				break;
		}

		Map resultMap = new HashMap();
		resultMap.put("code", codeId);
		resultMap.put("info", info);

		if (data != null) {
			if(data instanceof String) {
				Map map = new HashMap();
				map.put("description", data);
				data = map;
			}
		}

		resultMap.put("data", data);
		return resultMap;
	}
}