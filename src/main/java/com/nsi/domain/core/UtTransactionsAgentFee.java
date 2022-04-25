package com.nsi.domain.core;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ut_transactions_agent_fee")
public class UtTransactionsAgentFee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ut_transactions_id", nullable = false)
	private UtTransactions utTransactions;

	@Column(name = "order_no", length = 50)
	private String orderNo;

	@Column(name = "order_amount")
	private Double orderAmount;

	@Column(name = "fee_amount")
	private Double feeAmount;

	@Column(name = "fee_percentage")
	private Double feePercentage;
	
	@Column(name = "is_direct_fee")
	private Boolean isDirectFee;
	
	@Column(name = "transaction_date")
	private Date transactionDate;
	
	@ManyToOne
	@JoinColumn(name="agent_id")
	private Agent agent;

	@Column(name="created_date")
	private Date createdDate;

	@Column(name="created_by", length=50)
	private String createdBy;

	@Column(name="updated_date")
	private Date updatedDate;

	@Column(name="updated_by", length=50)
	private String updatedBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public UtTransactions getUtTransactions() {
		return utTransactions;
	}

	public void setUtTransactions(UtTransactions utTransactions) {
		this.utTransactions = utTransactions;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Double getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}

	public Double getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(Double feeAmount) {
		this.feeAmount = feeAmount;
	}

	public Double getFeePercentage() {
		return feePercentage;
	}

	public void setFeePercentage(Double feePercentage) {
		this.feePercentage = feePercentage;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Boolean getIsDirectFee() {
		return isDirectFee;
	}
	public void setIsDirectFee(Boolean isDirectFee) {
		this.isDirectFee = isDirectFee;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
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
}
