package com.nsi.repositories.core;

import com.nsi.domain.core.SbnTransactionsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface SbnTransactionsHistoryRepository extends JpaRepository<SbnTransactionsHistory, Long> {
    public List<SbnTransactionsHistory> findAllByTransactionsCodeOrderByCreatedDateDesc(String trxCode);
    @Query(value = "SELECT * FROM sbn_transactions_history WHERE transactions_code=:trxCode ORDER BY created_date DESC LIMIT 1", nativeQuery = true)
    public SbnTransactionsHistory findByTransactionsCodeWithCustomQuery(@Param("trxCode") String trxCode);
}
