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
@Table(name="investment_account_balance")
public class InvestmentAccountBalance {

	private Long id;
	private InvestmentAccounts invAccount;
	private Date balanceDate;
	private Integer currentUnit;
	private Double currentAmount;
	private Integer broughtForwardUnit;
	private Double broughtForwardAmount;
	private Integer subscriptionUnit;
	private Double subscriptionAmount;
	private Integer redemptionUnit;
	private Double redemptionAmount;
	private Integer switchInUnit;
	private Double switchInAmount;
	private Integer switchOutUnit;
	private Double switchOutAmount;
	private Integer dividendUnit;
	private Double dividendAmount;
	private Integer accruedUnit;
	private Double accruedAmount;
	private Double totalCost;
	private Double averageCost;
	private Double realizePl;
	private Double unrealizePl;
	private String createdBy;
	private Date createdDate;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "investment_account_balance_generator")
	@SequenceGenerator(name="investment_account_balance_generator", sequenceName = "investment_account_balance_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="inv_account_id")
	public InvestmentAccounts getInvAccount() {
		return invAccount;
	}
	public void setInvAccount(InvestmentAccounts invAccount) {
		this.invAccount = invAccount;
	}
	
	@Column(name="balance_date")
	public Date getBalanceDate() {
		return balanceDate;
	}
	public void setBalanceDate(Date balanceDate) {
		this.balanceDate = balanceDate;
	}
	
	@Column(name="current_unit")
	public Integer getCurrentUnit() {
		return currentUnit;
	}
	public void setCurrentUnit(Integer currentUnit) {
		this.currentUnit = currentUnit;
	}
	
	@Column(name="current_amount")
	public Double getCurrentAmount() {
		return currentAmount;
	}
	public void setCurrentAmount(Double currentAmount) {
		this.currentAmount = currentAmount;
	}
	
	@Column(name="brought_forward_unit")
	public Integer getBroughtForwardUnit() {
		return broughtForwardUnit;
	}
	public void setBroughtForwardUnit(Integer broughtForwardUnit) {
		this.broughtForwardUnit = broughtForwardUnit;
	}
	
	@Column(name="brought_forward_amount")
	public Double getBroughtForwardAmount() {
		return broughtForwardAmount;
	}
	public void setBroughtForwardAmount(Double broughtForwardAmount) {
		this.broughtForwardAmount = broughtForwardAmount;
	}
	
	@Column(name="subscription_unit")
	public Integer getSubscriptionUnit() {
		return subscriptionUnit;
	}
	public void setSubscriptionUnit(Integer subscriptionUnit) {
		this.subscriptionUnit = subscriptionUnit;
	}
	
	@Column(name="subscription_amount")
	public Double getSubscriptionAmount() {
		return subscriptionAmount;
	}
	public void setSubscriptionAmount(Double subscriptionAmount) {
		this.subscriptionAmount = subscriptionAmount;
	}
	
	@Column(name="redemption_unit")
	public Integer getRedemptionUnit() {
		return redemptionUnit;
	}
	public void setRedemptionUnit(Integer redemptionUnit) {
		this.redemptionUnit = redemptionUnit;
	}
	
	@Column(name="redemption_amount")
	public Double getRedemptionAmount() {
		return redemptionAmount;
	}
	public void setRedemptionAmount(Double redemptionAmount) {
		this.redemptionAmount = redemptionAmount;
	}
	
	@Column(name="switch_in_unit")
	public Integer getSwitchInUnit() {
		return switchInUnit;
	}
	public void setSwitchInUnit(Integer switchInUnit) {
		this.switchInUnit = switchInUnit;
	}
	
	@Column(name="switch_in_amount")
	public Double getSwitchInAmount() {
		return switchInAmount;
	}
	public void setSwitchInAmount(Double switchInAmount) {
		this.switchInAmount = switchInAmount;
	}
	
	@Column(name="switch_out_unit")
	public Integer getSwitchOutUnit() {
		return switchOutUnit;
	}
	public void setSwitchOutUnit(Integer switchOutUnit) {
		this.switchOutUnit = switchOutUnit;
	}
	
	@Column(name="switch_out_amount")
	public Double getSwitchOutAmount() {
		return switchOutAmount;
	}
	public void setSwitchOutAmount(Double switchOutAmount) {
		this.switchOutAmount = switchOutAmount;
	}
	
	@Column(name="dividend_unit")
	public Integer getDividendUnit() {
		return dividendUnit;
	}
	public void setDividendUnit(Integer dividendUnit) {
		this.dividendUnit = dividendUnit;
	}
	
	@Column(name="dividend_amount")
	public Double getDividendAmount() {
		return dividendAmount;
	}
	public void setDividendAmount(Double dividendAmount) {
		this.dividendAmount = dividendAmount;
	}
	
	@Column(name="accrued_unit")
	public Integer getAccruedUnit() {
		return accruedUnit;
	}
	public void setAccruedUnit(Integer accruedUnit) {
		this.accruedUnit = accruedUnit;
	}
	
	@Column(name="accrued_amount")
	public Double getAccruedAmount() {
		return accruedAmount;
	}
	public void setAccruedAmount(Double accruedAmount) {
		this.accruedAmount = accruedAmount;
	}
	
	@Column(name="total_cost")
	public Double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}
	
	@Column(name="average_cost")
	public Double getAverageCost() {
		return averageCost;
	}
	public void setAverageCost(Double averageCost) {
		this.averageCost = averageCost;
	}
	
	@Column(name="realize_pl")
	public Double getRealizePl() {
		return realizePl;
	}
	public void setRealizePl(Double realizePl) {
		this.realizePl = realizePl;
	}
	
	@Column(name="unrealize_pl")
	public Double getUnrealizePl() {
		return unrealizePl;
	}
	public void setUnrealizePl(Double unrealizePl) {
		this.unrealizePl = unrealizePl;
	}
	
	@Column(name="created_by")
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	@Column(name="created_date")
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	
}
