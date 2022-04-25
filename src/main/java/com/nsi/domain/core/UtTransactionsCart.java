package com.nsi.domain.core;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="ut_transactions_cart")
public class UtTransactionsCart extends BaseDomain {

	private Long id;
	private Date trxDate = new Date();
	private Double orderAmount;
	private String trxStatus;
	private String settlementStatus;
	private String settlementRefNo;
	private Kyc kyc;
	private FundPackages fundPackages;
	private UtTransactionType transactionType;
	private String orderNo;
	private String paymentType;
	private InvestmentAccounts investmentAccount;
	private Double feeAmount;
	private Double netAmount;
	private GoalPlanner goalPlanner;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ut_transactions_cart_generator")
	@SequenceGenerator(name="ut_transactions_cart_generator", sequenceName = "ut_transactions_cart_trx_id_seq", allocationSize=1)
	@Column(name="trx_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="trx_date")
	public Date getTrxDate() {
		return trxDate;
	}
	public void setTrxDate(Date trxDate) {
		this.trxDate = trxDate;
	}
	
	@Column(name="order_amount")
	public Double getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}
	
	@Column(name="trx_status")
	public String getTrxStatus() {
		return trxStatus;
	}
	public void setTrxStatus(String trxStatus) {
		this.trxStatus = trxStatus;
	}
	
	@Column(name="settlement_status")
	public String getSettlementStatus() {
		return settlementStatus;
	}
	public void setSettlementStatus(String settlementStatus) {
		this.settlementStatus = settlementStatus;
	}
	
	@Column(name="settlement_ref_no")
	public String getSettlementRefNo() {
		return settlementRefNo;
	}
	public void setSettlementRefNo(String settlementRefNo) {
		this.settlementRefNo = settlementRefNo;
	}
	
	@ManyToOne
	@JoinColumn(name="kyc_id")
	public Kyc getKyc() {
		return kyc;
	}
	public void setKyc(Kyc kyc) {
		this.kyc = kyc;
	}
	
	@ManyToOne
	@JoinColumn(name="fund_packages_id")
	public FundPackages getFundPackages() {
		return fundPackages;
	}
	public void setFundPackages(FundPackages fundPackages) {
		this.fundPackages = fundPackages;
	}
	
	@ManyToOne
	@JoinColumn(name="transaction_type_id")
	public UtTransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(UtTransactionType transactionType) {
		this.transactionType = transactionType;
	}
	
	@Column(name="order_no", length=50)
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	@Column(name="payment_type", length=50)
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	
	@ManyToOne
	@JoinColumn(name="investment_account_id")
	public InvestmentAccounts getInvestmentAccount() {
		return investmentAccount;
	}
	public void setInvestmentAccount(InvestmentAccounts investmentAccount) {
		this.investmentAccount = investmentAccount;
	}
	
	@Column(name="fee_amount")
	public Double getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(Double feeAmount) {
		this.feeAmount = feeAmount;
	}
	
	@Column(name="net_amount")
	public Double getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(Double netAmount) {
		this.netAmount = netAmount;
	}
	
	@ManyToOne
	@JoinColumn(name="goal_planner_id")
	public GoalPlanner getGoalPlanner() {
		return goalPlanner;
	}
	public void setGoalPlanner(GoalPlanner goalPlanner) {
		this.goalPlanner = goalPlanner;
	}
	
	
}
