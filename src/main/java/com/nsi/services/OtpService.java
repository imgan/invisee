/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.services;

import com.nsi.domain.core.Kyc;

/**
 * @author hatta.palino
 */
public interface OtpService {

  String send(String channelId, String typeotp, String type, Kyc kyc, String agentCode);

  boolean validate(String channelId, Kyc kyc, String keyOtp, String valueOtp);

}
