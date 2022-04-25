/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.services.impl;

import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.UtTransactionType;
import com.nsi.repositories.core.FundPackageFeeSetupRepository;
import com.nsi.services.FeeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Hatta Palino
 */
@Service
public class FeeServiceImpl implements FeeService {

    @Autowired
    private FundPackageFeeSetupRepository fundPackageFeeSetupRepository;
    
    @Override
    public Double checkFeeAmount(Double netAmountTrx, Double initialFee, FundPackages fp, UtTransactionType trxType) {
        System.out.println("netAmountTrx : " + netAmountTrx + ", initialFee : " + initialFee);
        List<Double> fees = fundPackageFeeSetupRepository.getFeeSetup(fp, trxType, netAmountTrx);
        
        if(fees != null && !fees.isEmpty()) {
            Double fee = fees.get(0) * netAmountTrx;
            if(!fee.equals(initialFee)) initialFee = fee;
        }
        return initialFee;
    }
    
}
