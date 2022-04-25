/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.services;

import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.UtTransactionType;
import java.util.Map;

/**
 *
 * @author Hatta Palino
 */
public interface EmailService {
    boolean sendOrderTransaction(InvestmentAccounts investmentAccounts, String orderNo, FundPackages fundPackage, UtTransactionType trxType);
    boolean sendSettlementTransaction(Kyc kyc, Map map);
    boolean sendUserStatusActive(Kyc kyc);
    boolean sendOpenRekening(Kyc kyc);
    boolean sendNeedPayment(Kyc kyc, String orderNo);
    boolean sendRedeem(Kyc kyc, String orderNo);
    boolean sendToGroovy(String url, String listData);
    
}
