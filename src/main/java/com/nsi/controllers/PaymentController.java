package com.nsi.controllers;

import com.nsi.services.impl.PaymentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController extends BaseController{
  @Autowired
  PaymentServiceImpl paymentService;

  @PostMapping(value = "/validate")
  public ResponseEntity<Map> validatePayment(@RequestParam String atTrxId,
                                             @RequestParam String invoice,
                                             @RequestParam String result_code){

    if (invoice == null && invoice.equals("")){
      logger.error("Parameter invoice tidak boleh kosong di api -> /payment/validate");
      Map result = new HashMap();
      result.put("status", "parameter invoice tidak boleh kosong atau null");
      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    if (result_code == null && result_code.equals("")){
      logger.error("Parameter result_code tidak boleh kosong di api -> /payment/validate");
      Map result = new HashMap();
      result.put("status", "parameter result_code tidak boleh kosong atau null");
      return new ResponseEntity<>(result, HttpStatus.OK);
    }

    Map result = paymentService.validate(atTrxId, invoice, result_code);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}