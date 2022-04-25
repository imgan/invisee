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
@Table(name="fund_packages")
public class FundPackages extends BaseDomain {

	private Long fundPackageId;
	private String packageCode;
	private String fundPackageName;
	private Date effectiveDate;
	private String packageDesc;
	private Double marketValue;
	private String redemptionRule;
	private String subscriptionRule;
	private String packageImage;
	private Date transactionCutOff;
	private Date settlementCutOff;
	private String currency;
	private String riskProfile;
	private Double minSubscriptionAmount;
	private Double minTopupAmount;
	private Double unrealizedGainLoss;
	private Double goal;
	private String settlementPeriod;
	private Boolean activeStatus;
	private Score risk_Profile;
	private Boolean publishStatus = false;
	private Boolean allowedSubscription;
	private Boolean allowedTopup;
	private Boolean allowedRedemption;
	private Boolean allowedSwitching;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fund_packages_generator")
	@SequenceGenerator(name="fund_packages_generator", sequenceName = "fund_packages_fund_package_id_seq", allocationSize=1)
	@Column(name="fund_package_id")
	public Long getFundPackageId() {
		return fundPackageId;
	}
	public void setFundPackageId(Long fundPackageId) {
		this.fundPackageId = fundPackageId;
	}
	
	@Column(name="package_code", length=10)
	public String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	
	@Column(name="fund_package_name", length=50)
	public String getFundPackageName() {
		return fundPackageName;
	}
	public void setFundPackageName(String fundPackageName) {
		this.fundPackageName = fundPackageName;
	}
	
	@Column(name="effective_date")
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="package_desc")
	public String getPackageDesc() {
		return packageDesc;
	}
	public void setPackageDesc(String packageDesc) {
		this.packageDesc = packageDesc;
	}
	
	@Column(name="market_value")
	public Double getMarketValue() {
		return marketValue;
	}
	public void setMarketValue(Double marketValue) {
		this.marketValue = marketValue;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="redemption_rule")
	public String getRedemptionRule() {
		return redemptionRule;
	}
	public void setRedemptionRule(String redemptionRule) {
		this.redemptionRule = redemptionRule;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="subscription_rule")
	public String getSubscriptionRule() {
		return subscriptionRule;
	}
	public void setSubscriptionRule(String subscriptionRule) {
		this.subscriptionRule = subscriptionRule;
	}
	
	@Column(name="package_image", length=36, nullable=true)
	public String getPackageImage() {
		return packageImage;
	}
	public void setPackageImage(String packageImage) {
		this.packageImage = packageImage;
	}
	
	@Column(name="transaction_cut_off")
	public Date getTransactionCutOff() {
		return transactionCutOff;
	}
	public void setTransactionCutOff(Date transactionCutOff) {
		this.transactionCutOff = transactionCutOff;
	}
	
	@Column(name="settlement_cut_off")
	public Date getSettlementCutOff() {
		return settlementCutOff;
	}
	public void setSettlementCutOff(Date settlementCutOff) {
		this.settlementCutOff = settlementCutOff;
	}
	
	@Column(name="currency")
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	@Column(name="risk_profile")
	public String getRiskProfile() {
		return riskProfile;
	}
	public void setRiskProfile(String riskProfile) {
		this.riskProfile = riskProfile;
	}
	
	@Column(name="min_subscription_amount")
	public Double getMinSubscriptionAmount() {
		return minSubscriptionAmount;
	}
	public void setMinSubscriptionAmount(Double minSubscriptionAmount) {
		this.minSubscriptionAmount = minSubscriptionAmount;
	}
	
	@Column(name="min_topup_amount")
	public Double getMinTopupAmount() {
		return minTopupAmount;
	}
	public void setMinTopupAmount(Double minTopupAmount) {
		this.minTopupAmount = minTopupAmount;
	}
	
	@Column(name="unrealized_gain_loss")
	public Double getUnrealizedGainLoss() {
		return unrealizedGainLoss;
	}
	public void setUnrealizedGainLoss(Double unrealizedGainLoss) {
		this.unrealizedGainLoss = unrealizedGainLoss;
	}
	
	@Column(name="goal")
	public Double getGoal() {
		return goal;
	}
	public void setGoal(Double goal) {
		this.goal = goal;
	}
	
	@Column(name="settlement_period")
	public String getSettlementPeriod() {
		return settlementPeriod;
	}
	public void setSettlementPeriod(String settlementPeriod) {
		this.settlementPeriod = settlementPeriod;
	}
	
	@Column(name="active_status")
	public Boolean getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(Boolean activeStatus) {
		this.activeStatus = activeStatus;
	}
	
	@ManyToOne
	@JoinColumn(name="risk_profile_id")
	public Score getRisk_Profile() {
		return risk_Profile;
	}
	public void setRisk_Profile(Score risk_Profile) {
		this.risk_Profile = risk_Profile;
	}

	@Column(name="publish_status")
	public Boolean getPublishStatus() {
		return publishStatus;
	}
	public void setPublishStatus(Boolean publishStatus) {
		this.publishStatus = publishStatus;
	}

	@Column(name="allowed_subscription")
	public Boolean getAllowedSubscription() {
		return allowedSubscription;
	}

	public void setAllowedSubscription(Boolean allowedSubscription) {
		this.allowedSubscription = allowedSubscription;
	}

	@Column(name="allowed_topup")
	public Boolean getAllowedTopup() {
		return allowedTopup;
	}

	public void setAllowedTopup(Boolean allowedTopup) {
		this.allowedTopup = allowedTopup;
	}

	@Column(name="allowed_redemption")
	public Boolean getAllowedRedemption() {
		return allowedRedemption;
	}

	public void setAllowedRedemption(Boolean allowedRedemption) {
		this.allowedRedemption = allowedRedemption;
	}

	@Column(name="allowed_switching")
	public Boolean getAllowedSwitching() {
		return allowedSwitching;
	}

	public void setAllowedSwitching(Boolean allowedSwitching) {
		this.allowedSwitching = allowedSwitching;
	}
}
