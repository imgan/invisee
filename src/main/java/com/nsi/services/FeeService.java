/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.services;

import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.UtTransactionType;

/**
 *
 * @author Hatta Palino
 */
public interface FeeService {
    Double checkFeeAmount(Double netAmountTrx, Double initialFee, FundPackages fp, UtTransactionType trxType);
}
