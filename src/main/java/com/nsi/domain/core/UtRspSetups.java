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
@Table(name="ut_rsp_setups")
public class UtRspSetups extends BaseDomain {

	private Long id;
	private Kyc kycId;
	private UtProducts productId;
	private String rspNo;
	private Date rspDate;
	private Double trxAmount;
	private Double feeAmount;
	private Double netAmount;
	private Double settlementAmount;
	private Integer installmentType;
	private Integer installmentPeriod;
	private Date installmentDate;
	private Boolean activeStatus;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ut_rsp_setups_generator")
	@SequenceGenerator(name="ut_rsp_setups_generator", sequenceName = "ut_rsp_setups_rsp_id_seq", allocationSize=1)
	@Column(name="rsp_id")
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
	
	@ManyToOne
	@JoinColumn(name="product_id_id")
	public UtProducts getProductId() {
		return productId;
	}
	public void setProductId(UtProducts productId) {
		this.productId = productId;
	}
	
	@Column(name="rsp_no")
	public String getRspNo() {
		return rspNo;
	}
	public void setRspNo(String rspNo) {
		this.rspNo = rspNo;
	}
	
	@Column(name="rsp_date")
	public Date getRspDate() {
		return rspDate;
	}
	public void setRspDate(Date rspDate) {
		this.rspDate = rspDate;
	}
	
	@Column(name="trx_amount")
	public Double getTrxAmount() {
		return trxAmount;
	}
	public void setTrxAmount(Double trxAmount) {
		this.trxAmount = trxAmount;
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
	
	@Column(name="settlement_amount")
	public Double getSettlementAmount() {
		return settlementAmount;
	}
	public void setSettlementAmount(Double settlementAmount) {
		this.settlementAmount = settlementAmount;
	}
	
	@Column(name="installment_type")
	public Integer getInstallmentType() {
		return installmentType;
	}
	public void setInstallmentType(Integer installmentType) {
		this.installmentType = installmentType;
	}
	
	@Column(name="installment_period")
	public Integer getInstallmentPeriod() {
		return installmentPeriod;
	}
	public void setInstallmentPeriod(Integer installmentPeriod) {
		this.installmentPeriod = installmentPeriod;
	}
	
	@Column(name="installment_date")
	public Date getInstallmentDate() {
		return installmentDate;
	}
	public void setInstallmentDate(Date installmentDate) {
		this.installmentDate = installmentDate;
	}
	
	@Column(name="active_status")
	public Boolean getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(Boolean activeStatus) {
		this.activeStatus = activeStatus;
	}
	
	
}
