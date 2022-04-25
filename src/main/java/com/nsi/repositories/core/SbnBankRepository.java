package com.nsi.repositories.core;

import com.nsi.domain.core.SbnBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SbnBankRepository extends JpaRepository<SbnBank, Long> {
    @Query(value = "SELECT bank_name, method, petunjuk FROM sbn_bank sbn where sbn.bank_name = :bankName ORDER BY bank_name ASC", nativeQuery = true)
    List<Object[]> findAllByBankNameWithCustomQuery(@Param("bankName") String bankName);

    @Query(value = "SELECT DISTINCT bank.bank_name, bank.image_key, bank.created_date FROM sbn_bank bank ORDER BY bank.bank_name DESC", nativeQuery = true)
    List<Object[]> getListBankWithCustomQuery();

    @Query(value = "SELECT * FROM sbn_bank sbn where sbn.bank_name = :bankName ORDER BY created_date ASC LIMIT 1", nativeQuery = true)
    SbnBank findByBankNameWithCustomQuery(@Param("bankName") String bankName);
}
