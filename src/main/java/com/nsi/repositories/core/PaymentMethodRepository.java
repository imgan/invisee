package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {

	public PaymentMethod findByCode(String code);
}
