/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.services;

import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author hatta.palino
 */
public interface ViseepayService {
    
    boolean topUp(Kyc kyc, String trxId, Double amount);
    boolean trx(Kyc kyc, String orderNumber);
    Map checkBalance(Kyc kyc);
    Map historys(Kyc kyc, Date from, Date to);
    User createAccount(Kyc kyc, String passwordPayment);
    
}
