package com.nsi.domain.core;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="ut_transactions")
public class UtTransactions extends BaseDomain {

	private Long id;
	private Kyc kycId;
	private String orderNo;
	private String trxNo;
	private Integer trxType;
	private Date trxDate;
	private Date priceDate;
	private Double orderAmount;
	private Double orderUnit;
	private Double feeAmount;
	private Double taxAmount;
	private Double netAmount;
	private Double settlementAmount;
	private String trxStatus;
	private String settlementStatus;
	private Date transactionDate;
	private String note;
	private String trxNotes;
	private String settlementRefNo;
	private UtRspSetups utRspSetups;
	private UtTransactionType transactionType;
	private FundEscrowAccount settlementNoRef;
	private FundPackages fundPackageRef;
	private InvestmentAccounts investementAccount;
	private UtTransactionsGroup transactionsGroup;
	private UtProducts productId;
	private String atTrxNo;
	private String channelOrderId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ut_transactions_generator")
	@SequenceGenerator(name="ut_transactions_generator", sequenceName = "ut_transactions_trx_id_seq", allocationSize=1)
	@Column(name="trx_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="kyc_id_id")
	public Kyc getKycId() {
		return kycId;
	}
	public void setKycId(Kyc kycId) {
		this.kycId = kycId;
	}
	
	@Column(name="order_no", length=50)
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	@Column(name="trx_no", length=50)
	public String getTrxNo() {
		return trxNo;
	}
	public void setTrxNo(String trxNo) {
		this.trxNo = trxNo;
	}
	
	@Column(name="trx_type")
	public Integer getTrxType() {
		return trxType;
	}
	public void setTrxType(Integer trxType) {
		this.trxType = trxType;
	}
	
	@Column(name="trx_date")
	public Date getTrxDate() {
		return trxDate;
	}
	public void setTrxDate(Date trxDate) {
		this.trxDate = trxDate;
	}
	
	@Column(name="price_date")
	public Date getPriceDate() {
		return priceDate;
	}
	public void setPriceDate(Date priceDate) {
		this.priceDate = priceDate;
	}
	
	@Column(name="order_amount")
	public Double getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}
	
	@Column(name="order_unit")
	public Double getOrderUnit() {
		return orderUnit;
	}
	public void setOrderUnit(Double orderUnit) {
		this.orderUnit = orderUnit;
	}
	
	@Column(name="fee_amount")
	public Double getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(Double feeAmount) {
		this.feeAmount = feeAmount;
	}
	
	@Column(name="tax_amount")
	public Double getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(Double taxAmount) {
		this.taxAmount = taxAmount;
	}
	
	@Column(name="net_amount")
	public Double getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(Double netAmount) {
		this.netAmount = netAmount;
	}
	
	@Column(name="settlement_amount")
	public Double getSettlementAmount() {
		return settlementAmount;
	}
	public void setSettlementAmount(Double settlementAmount) {
		this.settlementAmount = settlementAmount;
	}
	
	@Column(name="trx_status", length=10)
	public String getTrxStatus() {
		return trxStatus;
	}
	public void setTrxStatus(String trxStatus) {
		this.trxStatus = trxStatus;
	}
	
	@Column(name="settlement_status", length=10)
	public String getSettlementStatus() {
		return settlementStatus;
	}
	public void setSettlementStatus(String settlementStatus) {
		this.settlementStatus = settlementStatus;
	}
	
	@Column(name="transaction_date")
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="note")
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	@Column(name="settlement_ref_no", length=50)
	public String getSettlementRefNo() {
		return settlementRefNo;
	}
	public void setSettlementRefNo(String settlementRefNo) {
		this.settlementRefNo = settlementRefNo;
	}
	
	@ManyToOne
	@JoinColumn(name="ut_rsp_setups_id")
	public UtRspSetups getUtRspSetups() {
		return utRspSetups;
	}
	public void setUtRspSetups(UtRspSetups utRspSetups) {
		this.utRspSetups = utRspSetups;
	}
	
	@ManyToOne
	@JoinColumn(name="transaction_type_id")
	public UtTransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(UtTransactionType transactionType) {
		this.transactionType = transactionType;
	}
	
	@ManyToOne
	@JoinColumn(name="settlement_no_ref_id")
	public FundEscrowAccount getSettlementNoRef() {
		return settlementNoRef;
	}
	public void setSettlementNoRef(FundEscrowAccount settlementNoRef) {
		this.settlementNoRef = settlementNoRef;
	}
	
	@ManyToOne
	@JoinColumn(name="fund_package_ref_id")
	public FundPackages getFundPackageRef() {
		return fundPackageRef;
	}
	public void setFundPackageRef(FundPackages fundPackageRef) {
		this.fundPackageRef = fundPackageRef;
	}
	
	@ManyToOne
	@JoinColumn(name="investement_account_id")
	public InvestmentAccounts getInvestementAccount() {
		return investementAccount;
	}
	public void setInvestementAccount(InvestmentAccounts investementAccount) {
		this.investementAccount = investementAccount;
	}
	
	@ManyToOne
	@JoinColumn(name="transactions_group_id")
	public UtTransactionsGroup getTransactionsGroup() {
		return transactionsGroup;
	}
	public void setTransactionsGroup(UtTransactionsGroup transactionsGroup) {
		this.transactionsGroup = transactionsGroup;
	}
	
	@ManyToOne
	@JoinColumn(name="product_id_id")
	public UtProducts getProductId() {
		return productId;
	}
	public void setProductId(UtProducts productId) {
		this.productId = productId;
	}
	
	@Column(name="at_trx_no")
	public String getAtTrxNo() {
		return atTrxNo;
	}
	public void setAtTrxNo(String atTrxNo) {
		this.atTrxNo = atTrxNo;
	}
	
	@Column(name="trx_notes")
	public String getTrxNotes() {
		return trxNotes;
	}
	public void setTrxNotes(String trxNotes) {
		this.trxNotes = trxNotes;
	}
	
	@Column(name="channel_order_id")
	public String getChannelOrderId() {
		return channelOrderId;
	}
	public void setChannelOrderId(String channelOrderId) {
		this.channelOrderId = channelOrderId;
	}
}
