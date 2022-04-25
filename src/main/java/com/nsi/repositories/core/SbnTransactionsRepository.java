package com.nsi.repositories.core;

import com.nsi.domain.core.SbnSid;
import com.nsi.domain.core.SbnTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigInteger;
import java.util.List;

public interface SbnTransactionsRepository extends JpaRepository<SbnTransactions, Long> {
    public SbnTransactions findByKodePemesananAndSbnSid(String kodePemesanan, SbnSid sbnSid);
    public List<SbnTransactions> findAllBySbnSidAndIdStatusOrderByCreatedDateDesc(SbnSid sbnSid, Long idStatus);
    public List<SbnTransactions> findAllBySbnSidOrderByCreatedDateDesc(SbnSid sbnSid);

    @Query(value = "SELECT SUM(sisa_kepemilikan) FROM sbn_transactions WHERE id_status=4 AND sbn_sid_id=:sid", nativeQuery = true)
    public BigInteger getTotalInvestment(@Param("sid") Long sbnSidId);
    public SbnTransactions findByIdAndSbnSid(Long trxId, SbnSid sbnSid);
}