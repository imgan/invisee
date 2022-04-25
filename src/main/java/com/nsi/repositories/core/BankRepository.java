package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.Bank;
import java.util.List;

public interface BankRepository extends JpaRepository<Bank, Long> {

	public Bank findByBankCode(String bankCode);
        public List<Bank> findAllByOrderByBankNameAsc();
     
}
