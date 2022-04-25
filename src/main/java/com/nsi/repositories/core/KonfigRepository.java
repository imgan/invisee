package com.nsi.repositories.core;

import com.nsi.domain.core.Konfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KonfigRepository extends JpaRepository<Konfig, Long> {
  Konfig findByKey(String key);
}
