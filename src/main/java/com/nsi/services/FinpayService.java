package com.nsi.services;

import com.nsi.domain.core.User;

import java.util.Map;

public interface FinpayService {
  Map topUpFinpay(User user, String trxNo);
}
