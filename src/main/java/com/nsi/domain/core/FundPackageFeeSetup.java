package com.nsi.domain.core;

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
@Table(name="fund_package_fee_setup")
public class FundPackageFeeSetup extends BaseDomain {

	private Long id;
	private UtTransactionType transactionType;
	private Double amountMin;
	private Double amountMax;
	private Double feeAmount;
	private FundPackages fundPackages;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fund_package_fee_setup_generator")
	@SequenceGenerator(name="fund_package_fee_setup_generator", sequenceName = "fund_package_fee_setup_package_fee_id_seq", allocationSize=1)
	@Column(name="package_fee_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="transaction_type_id")
	public UtTransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(UtTransactionType transactionType) {
		this.transactionType = transactionType;
	}
	
	@Column(name="amount_min")
	public Double getAmountMin() {
		return amountMin;
	}
	public void setAmountMin(Double amountMin) {
		this.amountMin = amountMin;
	}
	
	@Column(name="amount_max")
	public Double getAmountMax() {
		return amountMax;
	}
	public void setAmountMax(Double amountMax) {
		this.amountMax = amountMax;
	}
	
	@Column(name="fee_amount")
	public Double getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(Double feeAmount) {
		this.feeAmount = feeAmount;
	}
	
	@ManyToOne
	@JoinColumn(name="fund_packages_id")
	public FundPackages getFundPackages() {
		return fundPackages;
	}
	public void setFundPackages(FundPackages fundPackages) {
		this.fundPackages = fundPackages;
	}

    @Override
    public String toString() {
        return "FundPackageFeeSetup{" + "id=" + id + ", transactionType=" + transactionType + ", amountMin=" + amountMin + ", amountMax=" + amountMax + ", feeAmount=" + feeAmount + ", fundPackages=" + fundPackages + '}';
    }
	
}
