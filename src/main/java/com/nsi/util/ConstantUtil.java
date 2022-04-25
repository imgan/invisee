/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.util;

/**
 *
 * @author Hatta Palino
 */
public class ConstantUtil {
    
    public static String STATUS   = "STATUS";
    public static String REQUEST  = "REQUEST";
    public static String RESPONSE = "RESPONSE";
    public static String INFO     = "INFO";
    public static String DATA     = "DATA";
    public static String TOKEN    = "TOKEN";
    public static String TYPE     = "TYPE";
    public static String KYC      = "KYC";
    public static String DOCUMENT = "DOCUMENT";
    public static String USER     = "USER";
    public static String SETTLEMENT = "SETTLEMENT";
    public static String QUESTION = "QUESTION";
    public static String SCORE = "SCORE";
    public static String CODE = "code";
    public static String SIGNATURE = "signature";

    public final static String X_AGENT_TOKEN = "x-agent_token";
    public final static String LOGINED_AGENT = "LOGINED_AGENT";
    public final static String LOGINED_USER = "LOGINED_USER";
    public final static String REQ_BODY = "reqBody";

    public static String SUCCESS = "Success";
    public static String DATA_NOT_FOUND = "Data not found";
    
    public static Integer STATUS_SUCCESS = 0;
    public static Integer STATUS_ERROR = 1;
    public static Integer STATUS_INCOMPLETE_DATA = 10;
    public static Integer STATUS_INVALID_FORMAT = 11;
    public static Integer STATUS_ACCESS_DENIED = 12;
    public static Integer STATUS_EXISTING_DATA = 13;
    public static Integer STATUS_DATA_NOT_FOUND = 50;
    public static Integer STATUS_ERROR_SYSTEM = 99;
    
    public static String GLOBAL_PARAM_CUSTOMER_FILE_PATH = "CUSTOMER_FILE_PATH";
    public static String GLOBAL_PARAM_ACCESS_KEY = "ACCESS_KEY";
    
    public static final String FEE_CALCULATION_QUEUE = "FEE_CALCULATION_QUEUE";
    public static final String CALLBACK_RESPONSE_API = "CALLBACK_RESPONSE_API";

    public static final String EMPTHY_STRING = "";

    //Email
    public static final String NO_REPLY_EMAIL = "no-reply@invisee.com";
    public static final String SUPPORT_EMAIL_ADDRESS = "support@invisee.com";
    public static final String CALL_CENTER_NUMBER = "(021) 22455763";

    public static final String USER_STATUS_REGISTERED = "REG";
    public static final String USER_STATUS_ACTIVATED = "ACT";
    public static final String USER_STATUS_PENDING = "PEN";
    public static final String USER_STATUS_VERIFIED = "VER";
}
