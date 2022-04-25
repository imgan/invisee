package com.nsi.services;

import java.util.Map;

public interface PaymentService {
  Map validate(String atTrxNo, String invoice, String result_code);
}
