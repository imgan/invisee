package com.nsi.domain.core;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sbn_transactions_history")
public class SbnTransactionsHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sbn_transactions_history_id_generator")
    @SequenceGenerator(name="sbn_transactions_history_id_generator", sequenceName = "sbn_transactions_history_id_seq", allocationSize=1)
    private Long id;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "transactions_code", nullable = false)
    private String transactionsCode;

    @Column(name = "status_desc")
    private String statusDesc;

    @Column(name = "transactions_amount")
    private Double transactionsAmount;

    @Column(name = "remaining_amount")
    private Double remainingAmount;

    @Column(name = "redeemable_amount")
    private Double redeemableAmount;

    @Column(name = "redeem_code")
    private String redeemCode;

    @Column(name = "settlement_date")
    private Date settlementDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getTransactionsCode() {
        return transactionsCode;
    }

    public void setTransactionsCode(String transactionsCode) {
        this.transactionsCode = transactionsCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public Double getTransactionsAmount() {
        return transactionsAmount;
    }

    public void setTransactionsAmount(Double transactionsAmount) {
        this.transactionsAmount = transactionsAmount;
    }

    public Double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Double getRedeemableAmount() {
        return redeemableAmount;
    }

    public void setRedeemableAmount(Double redeemableAmount) {
        this.redeemableAmount = redeemableAmount;
    }

    public String getRedeemCode() {
        return redeemCode;
    }

    public void setRedeemCode(String redeemCode) {
        this.redeemCode = redeemCode;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(Date settlementDate) {
        this.settlementDate = settlementDate;
    }
}
