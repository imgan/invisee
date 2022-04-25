/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsi.domain.core.Agent;
import com.nsi.util.ConstantUtil;
import com.nsi.util.DateTimeUtil;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Hatta Palino
 */
@SuppressWarnings("ALL")
public class BaseController {

    protected org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    
    protected String getIpAddress(HttpServletRequest request) {
        return request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR");
    }

    public boolean isExistingData(Object data) {
        return !(data == null || data.toString().isEmpty());
    }

    protected boolean isExistingDataAndStringValue(Object data) {
        if (isExistingData(data)) {
            return (data instanceof String);
        }
        return false;
    }

	protected boolean isExistingDataAndMapValue(Object data) {
        if (isExistingData(data)) {
            if (data instanceof Map) {
                return !((Map) data).isEmpty();
            }
        }
        return false;
    }

    protected boolean isExistingDataAndListValue(Object data) {
        if (isExistingData(data)) {
            if (data instanceof List) {
                return !((List) data).isEmpty();
            }
        }
        return false;
    }

    protected boolean isExistingDataAndIntegerValue(Object data) {
        if (isExistingData(data)) {
            return (data instanceof Integer);
        }
        return false;
    }

    protected boolean isExistingDataAndDateValue(Object data) {
        if (isExistingData(data)) {
            return (DateTimeUtil.convertStringToDateCustomized(data.toString(), DateTimeUtil.API_MCW) != null);
        }
        return false;
    }
    
    protected boolean isExistingDataAndDoubleValue(Object data) {
        if (isExistingData(data)) {
            try {
                Double.valueOf(data.toString());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    protected Map errorResponseIncompleteData(List<String> field) {
        String fields = "";
        for (int i = 0; i < field.size() ; i++) {
            if(i > 0) fields += ", "; 
            fields += field.get(i);
        }
        return errorResponse(10, fields, null);
    }
    
    protected void loggerHttp(HttpServletRequest request, String type, Map map) {
        String uri = request.getRequestURI();
        if (request.getQueryString() != null) uri += "?" + request.getQueryString();
        String json = map.toString();
        try {
            ObjectMapper om = new ObjectMapper();
            json = om.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        } catch (JsonProcessingException e) {
        	logger.error("[FATAL]", e);
        }
        logger.info(type + " " + uri + " : " + json);
    }

	protected Map errorResponse(int codeId, String field, Object data) {
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

    protected Map errorResponse(String code, String field, Object data) {
        String info;
        switch (code) {
            case "0":
                info = "successfully loaded : " + field;
                break;
            case "1":
                info = "login success";
                if (field != null) {
                    info += " : " + field;
                }
                break;
            case "10":
                info = "incomplete data (errorResponse Base): " + field;
                break;
            case "11":
                info = "invalid data format : " + field;
                break;
            case "12":
                info = "invalid access";
                if (field != null) {
                    info += " : " + field;
                }
                break;
            case "13":
                info = "Existing request : " + field;
                break;
            case "14":
                info = "Invalid request : " + field;
                break;
            case "50":
                info = "data not found : " + field;
                break;
            case "88":
                info = "fail execute : " + field;
                break;
            case "99":
                info = "general error";
                if (field != null) {
                    info += " : " + field;
                }
                break;
            case "100":
                info = "token invalid";
                if (field != null) {
                    info += " : " + field;
                }
                break;
            case "101":
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
        resultMap.put("code", code);
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
	
	public Agent getLoginedAgent(HttpServletRequest request) {
		return (Agent) request.getAttribute(ConstantUtil.LOGINED_AGENT);
	}

    protected HttpStatus httpStatusCode(Map data, boolean isTrx) {
        Integer code = (Integer) data.get("code");
        if(code == null){
            code = ((Long) data.get("Code")).intValue();
        }
        HttpStatus httpStatus;
        switch (code) {
            case 0:
                if(isTrx){
                    httpStatus = HttpStatus.CREATED;
                }else{
                    httpStatus = HttpStatus.OK;
                }
                break;
            case 1:
                httpStatus = HttpStatus.OK;
                break;
            case 10:
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case 11:
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case 12:
                httpStatus = HttpStatus.UNAUTHORIZED;
                break;
            case 13:
                httpStatus = HttpStatus.CONFLICT;
                break;
            case 14:
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case 50:
                httpStatus = HttpStatus.NOT_FOUND;
                break;
            case 88:
                httpStatus = HttpStatus.FORBIDDEN;
                break;
            case 99:
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
            case 100:
                httpStatus = HttpStatus.UNAUTHORIZED;
                break;
            case 101:
                httpStatus = HttpStatus.UNAUTHORIZED;
                break;
            default:
                httpStatus = HttpStatus.OK;
                break;
        }

        return httpStatus;
    }

    //for insight
    protected Map changeCodeIntToString(Map data){
        if(data.get("code") != null){
            String code = data.get("code").toString();
            data.remove("code");
            data.put("code", code);
        }

        return data;
    }
}