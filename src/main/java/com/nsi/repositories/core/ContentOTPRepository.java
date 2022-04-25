package com.nsi.repositories.core;

import com.nsi.domain.core.ContentOTP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentOTPRepository extends JpaRepository<ContentOTP, Long> {
    ContentOTP findByTypeotp(String typeotp);
}
