package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="ut_product_fee_tiers")
public class UtProductFeeTiers extends BaseDomain {

	private String id;
	private Integer feeType;
	private Double lowerBoundAmount;
	private Double upperBoundAmount;
	private Double lowerBoundPeriod;
	private Double upperBoundPeriod;
	private Double feeRate;
	private UtProducts utProducts;
	
	@Id
	@Column(name="ut_product_fee_tiers_id", length=36)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name="fee_type")
	public Integer getFeeType() {
		return feeType;
	}
	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}
	
	@Column(name="lower_bound_amount")
	public Double getLowerBoundAmount() {
		return lowerBoundAmount;
	}
	public void setLowerBoundAmount(Double lowerBoundAmount) {
		this.lowerBoundAmount = lowerBoundAmount;
	}
	
	@Column(name="upper_bound_amount")
	public Double getUpperBoundAmount() {
		return upperBoundAmount;
	}
	public void setUpperBoundAmount(Double upperBoundAmount) {
		this.upperBoundAmount = upperBoundAmount;
	}
	
	@Column(name="lower_bound_period")
	public Double getLowerBoundPeriod() {
		return lowerBoundPeriod;
	}
	public void setLowerBoundPeriod(Double lowerBoundPeriod) {
		this.lowerBoundPeriod = lowerBoundPeriod;
	}
	
	@Column(name="upper_bound_period")
	public Double getUpperBoundPeriod() {
		return upperBoundPeriod;
	}
	public void setUpperBoundPeriod(Double upperBoundPeriod) {
		this.upperBoundPeriod = upperBoundPeriod;
	}
	
	@Column(name="fee_rate")
	public Double getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(Double feeRate) {
		this.feeRate = feeRate;
	}
	
	@ManyToOne
	@JoinColumn(name="ut_products_id")
	public UtProducts getUtProducts() {
		return utProducts;
	}
	public void setUtProducts(UtProducts utProducts) {
		this.utProducts = utProducts;
	}
	
	
}
