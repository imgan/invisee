package com.nsi.util;

import org.springframework.stereotype.Component;

@Component
public class Generator {
  public String hexaDecimal(Long id) {
    return (0 + Long.toHexString(id));
  }
}
