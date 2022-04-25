package com.nsi.repositories.core;

import com.nsi.domain.core.ApiOtpParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiOtpParameterRepository extends JpaRepository<ApiOtpParameter, String> {
  ApiOtpParameter findByCode(String code);
}
