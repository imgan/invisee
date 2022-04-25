package com.nsi.domain.core;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sbn_account_detail")
public class SbnAccountDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sbn_account_detail_id_generator")
    @SequenceGenerator(name="sbn_account_detail_id_generator", sequenceName = "sbn_account_detail_id_seq", allocationSize=1)
    private Long id;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "id_rekening_dana_sbn")
    private Long idRekeningDanaSbn;

    @Column(name = "id_subregistry")
    private Long idSubregistry;

    @ManyToOne
    @JoinColumn(name = "settlement_accounts_id", nullable = false)
    private SettlementAccounts settlementAccounts;

    @ManyToOne
    @JoinColumn(name = "sbn_sid_id", nullable = false)
    private SbnSid sbnSid;

    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @ManyToOne
    @JoinColumn(name = "sbn_subregistry_id")
    private SbnSubregistry sbnSubregistry;

    @Column(name = "id_partisipan_subregistry", length = 64)
    private String idPartisipanSubregistry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getIdRekeningDanaSbn() {
        return idRekeningDanaSbn;
    }

    public void setIdRekeningDanaSbn(Long idRekeningDanaSbn) {
        this.idRekeningDanaSbn = idRekeningDanaSbn;
    }

    public Long getIdSubregistry() {
        return idSubregistry;
    }

    public void setIdSubregistry(Long idSubregistry) {
        this.idSubregistry = idSubregistry;
    }

    public SettlementAccounts getSettlementAccounts() {
        return settlementAccounts;
    }

    public void setSettlementAccounts(SettlementAccounts settlementAccounts) {
        this.settlementAccounts = settlementAccounts;
    }

    public SbnSid getSbnSid() {
        return sbnSid;
    }

    public void setSbnSid(SbnSid sbnSid) {
        this.sbnSid = sbnSid;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public SbnSubregistry getSbnSubregistry() {
        return sbnSubregistry;
    }

    public void setSbnSubregistry(SbnSubregistry sbnSubregistry) {
        this.sbnSubregistry = sbnSubregistry;
    }

    public String getIdPartisipanSubregistry() {
        return idPartisipanSubregistry;
    }

    public void setIdPartisipanSubregistry(String idPartisipanSubregistry) {
        this.idPartisipanSubregistry = idPartisipanSubregistry;
    }
}
