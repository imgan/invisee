package com.nsi.services.impl;

import com.nsi.domain.core.UtTransactions;
import com.nsi.repositories.core.UtTransactionsRepository;
import com.nsi.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl extends BaseService implements PaymentService {
  @Autowired
  UtTransactionsRepository utTransactionsRepository;

  @Override
  public Map validate(String atTrxNo, String invoice, String result_code) {
    logger.info("===================== Start Validate FINPAY =====================");
    logger.info("AtTrxNo -> " + atTrxNo);
    logger.info("Invoice -> " + invoice);
    logger.info("ResultCode -> " + result_code);

    Map map = new LinkedHashMap();
    UtTransactions trx = utTransactionsRepository.findByOrderNoAndAtTrxNo(invoice, atTrxNo);

    if (trx.getTrxStatus().equalsIgnoreCase("ORD")){
      if (result_code.equals("00")){
        trx.setTrxStatus("STL");
        utTransactionsRepository.save(trx);
        map.put("status", "Pembayaran Berhasil");
      }else {
        trx.setTrxStatus("CAN");
        utTransactionsRepository.save(trx);
        map.put("status", "Pembayaran Gagal");
      }
    }else {
      map.put("status", "Transaction Status telah diupdate");
    }

    logger.info("===================== Finish Validate FINPAY =====================");
    logger.info("Result -> " + map);

    return map;
  }
}

