package com.nsi.controllers;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
  @GetMapping("/local")
  public String getLocal() {
    return "OK";
  }

  @PostMapping("/local")
  public Boolean postLocal(@RequestBody Map request) {
    return false;
  }
}
