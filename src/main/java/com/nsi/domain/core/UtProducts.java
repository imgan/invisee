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
@Table(name="ut_products")
public class UtProducts extends BaseDomain {

	private Long id;
	private String productCode;
	private String productName;
	private String productType;
	private String currency;
	private CustodyBank custodyId;
	private Score riskProfile;
	private Date issueDate;
	private Integer settlementPeriod;
	private Double minSubscriptionAmount;
	private Double minSubscriptionTopup;
	private Double minRedemptionUnit;
	private Double maxRedemptionUnit;
	private Double minBalAfterRedemptionUnit;
	private Double minSwitchingUnit;
	private Double maxSwitchingUnit;
	private Double minBalAfterSwithingUnit;
	private Double minRedemptionAmount;
	private Double maxRedemptionAmount;
	private Integer allowSubscription;
	private Integer allowRedemption;
	private Integer allowSwitching;
	private Integer allowRsp;
	private Boolean activeStatus;
	private Double initialNav;
	private Date dailyCutOffTime;
	private InvestmentManagers investmentManagers;
	private String atProductId;
	private String atInvestmentManagerId;
	private String atCustodyId;
	private String atRiskProfileId;
	private String atProductType;
	private String prospectusKey;
	private String fundFactSheetKey;
	private String prospectusKeyHistory;
	private String fundFactSheetKeyHistory;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ut_products_generator")
	@SequenceGenerator(name="ut_products_generator", sequenceName = "ut_products_product_id_seq", allocationSize=1)
	@Column(name="product_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="product_code", length=50)
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	@Column(name="product_name")
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	@Column(name="product_type", length=50)
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	@Column(name="currency", length=50)
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	@ManyToOne
	@JoinColumn(name="custody_id_id")
	public CustodyBank getCustodyId() {
		return custodyId;
	}
	public void setCustodyId(CustodyBank custodyId) {
		this.custodyId = custodyId;
	}
	
	@ManyToOne
	@JoinColumn(name="risk_profile_id")
	public Score getRiskProfile() {
		return riskProfile;
	}
	public void setRiskProfile(Score riskProfile) {
		this.riskProfile = riskProfile;
	}
	
	@Column(name="issue_date")
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	
	@Column(name="settlement_period")
	public Integer getSettlementPeriod() {
		return settlementPeriod;
	}
	public void setSettlementPeriod(Integer settlementPeriod) {
		this.settlementPeriod = settlementPeriod;
	}
	
	@Column(name="min_subscription_amount")
	public Double getMinSubscriptionAmount() {
		return minSubscriptionAmount;
	}
	public void setMinSubscriptionAmount(Double minSubscriptionAmount) {
		this.minSubscriptionAmount = minSubscriptionAmount;
	}
	
	@Column(name="min_subscription_topup")
	public Double getMinSubscriptionTopup() {
		return minSubscriptionTopup;
	}
	public void setMinSubscriptionTopup(Double minSubscriptionTopup) {
		this.minSubscriptionTopup = minSubscriptionTopup;
	}
	
	@Column(name="min_redemption_unit")
	public Double getMinRedemptionUnit() {
		return minRedemptionUnit;
	}
	public void setMinRedemptionUnit(Double minRedemptionUnit) {
		this.minRedemptionUnit = minRedemptionUnit;
	}
	
	@Column(name="max_redemption_unit")
	public Double getMaxRedemptionUnit() {
		return maxRedemptionUnit;
	}
	public void setMaxRedemptionUnit(Double maxRedemptionUnit) {
		this.maxRedemptionUnit = maxRedemptionUnit;
	}
	
	@Column(name="min_bal_after_redemption_unit")
	public Double getMinBalAfterRedemptionUnit() {
		return minBalAfterRedemptionUnit;
	}
	public void setMinBalAfterRedemptionUnit(Double minBalAfterRedemptionUnit) {
		this.minBalAfterRedemptionUnit = minBalAfterRedemptionUnit;
	}
	
	@Column(name="min_switching_unit")
	public Double getMinSwitchingUnit() {
		return minSwitchingUnit;
	}
	public void setMinSwitchingUnit(Double minSwitchingUnit) {
		this.minSwitchingUnit = minSwitchingUnit;
	}
	
	@Column(name="max_switching_unit")
	public Double getMaxSwitchingUnit() {
		return maxSwitchingUnit;
	}
	public void setMaxSwitchingUnit(Double maxSwitchingUnit) {
		this.maxSwitchingUnit = maxSwitchingUnit;
	}
	
	@Column(name="min_bal_after_swithing_unit")
	public Double getMinBalAfterSwithingUnit() {
		return minBalAfterSwithingUnit;
	}
	public void setMinBalAfterSwithingUnit(Double minBalAfterSwithingUnit) {
		this.minBalAfterSwithingUnit = minBalAfterSwithingUnit;
	}
	
	@Column(name="min_redemption_amount")
	public Double getMinRedemptionAmount() {
		return minRedemptionAmount;
	}
	public void setMinRedemptionAmount(Double minRedemptionAmount) {
		this.minRedemptionAmount = minRedemptionAmount;
	}
	
	@Column(name="max_redemption_amount")
	public Double getMaxRedemptionAmount() {
		return maxRedemptionAmount;
	}
	public void setMaxRedemptionAmount(Double maxRedemptionAmount) {
		this.maxRedemptionAmount = maxRedemptionAmount;
	}
	
	@Column(name="allow_subscription")
	public Integer getAllowSubscription() {
		return allowSubscription;
	}
	public void setAllowSubscription(Integer allowSubscription) {
		this.allowSubscription = allowSubscription;
	}
	
	@Column(name="allow_redemption")
	public Integer getAllowRedemption() {
		return allowRedemption;
	}
	public void setAllowRedemption(Integer allowRedemption) {
		this.allowRedemption = allowRedemption;
	}
	
	@Column(name="allow_switching")
	public Integer getAllowSwitching() {
		return allowSwitching;
	}
	public void setAllowSwitching(Integer allowSwitching) {
		this.allowSwitching = allowSwitching;
	}
	
	@Column(name="allow_rsp")
	public Integer getAllowRsp() {
		return allowRsp;
	}
	public void setAllowRsp(Integer allowRsp) {
		this.allowRsp = allowRsp;
	}
	
	@Column(name="active_status")
	public Boolean getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(Boolean activeStatus) {
		this.activeStatus = activeStatus;
	}
	
	@Column(name="initial_nav")
	public Double getInitialNav() {
		return initialNav;
	}
	public void setInitialNav(Double initialNav) {
		this.initialNav = initialNav;
	}
	
	@Column(name="daily_cut_off_time")
	public Date getDailyCutOffTime() {
		return dailyCutOffTime;
	}
	public void setDailyCutOffTime(Date dailyCutOffTime) {
		this.dailyCutOffTime = dailyCutOffTime;
	}
	
	@ManyToOne
	@JoinColumn(name="investment_managers_id")
	public InvestmentManagers getInvestmentManagers() {
		return investmentManagers;
	}
	public void setInvestmentManagers(InvestmentManagers investmentManagers) {
		this.investmentManagers = investmentManagers;
	}
	
	@Column(name="at_product_id")
	public String getAtProductId() {
		return atProductId;
	}
	public void setAtProductId(String atProductId) {
		this.atProductId = atProductId;
	}
	
	@Column(name="at_investment_manager_id")
	public String getAtInvestmentManagerId() {
		return atInvestmentManagerId;
	}
	public void setAtInvestmentManagerId(String atInvestmentManagerId) {
		this.atInvestmentManagerId = atInvestmentManagerId;
	}
	
	@Column(name="at_custody_id")
	public String getAtCustodyId() {
		return atCustodyId;
	}
	public void setAtCustodyId(String atCustodyId) {
		this.atCustodyId = atCustodyId;
	}
	
	@Column(name="at_risk_profile_id")
	public String getAtRiskProfileId() {
		return atRiskProfileId;
	}
	public void setAtRiskProfileId(String atRiskProfileId) {
		this.atRiskProfileId = atRiskProfileId;
	}
	
	@Column(name="at_product_type")
	public String getAtProductType() {
		return atProductType;
	}
	public void setAtProductType(String atProductType) {
		this.atProductType = atProductType;
	}
	
	@Column(name="prospectus_key", length=32)
	public String getProspectusKey() {
		return prospectusKey;
	}
	public void setProspectusKey(String prospectusKey) {
		this.prospectusKey = prospectusKey;
	}
	
	@Column(name="fund_fact_sheet_key", length=36)
	public String getFundFactSheetKey() {
		return fundFactSheetKey;
	}
	public void setFundFactSheetKey(String fundFactSheetKey) {
		this.fundFactSheetKey = fundFactSheetKey;
	}
	
	@Column(name="prospectus_key_history")
	public String getProspectusKeyHistory() {
		return prospectusKeyHistory;
	}
	public void setProspectusKeyHistory(String prospectusKeyHistory) {
		this.prospectusKeyHistory = prospectusKeyHistory;
	}
	
	@Column(name="fund_fact_sheet_key_history")
	public String getFundFactSheetKeyHistory() {
		return fundFactSheetKeyHistory;
	}
	public void setFundFactSheetKeyHistory(String fundFactSheetKeyHistory) {
		this.fundFactSheetKeyHistory = fundFactSheetKeyHistory;
	}
	
	
}
