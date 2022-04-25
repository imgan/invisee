package com.nsi.repositories.core;

import com.nsi.domain.core.SbnAccountDetail;
import com.nsi.domain.core.SbnSid;
import com.nsi.domain.core.SbnSubregistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SbnAccountDetailRepository extends JpaRepository<SbnAccountDetail, Long> {
    public SbnAccountDetail findBySbnSid(SbnSid sbnSid);
    public SbnAccountDetail findBySbnSidAndSbnSubregistry(SbnSid sbnSid, SbnSubregistry sbnSubregistry);

    @Query(value = "SELECT id_partisipan_subregistry FROM sbn_account_detail WHERE sbn_sid_id=:sidId", nativeQuery = true)
    public List<String> getAllPartisipanSubregistry(@Param("sidId")Long sbnSidId);
}
