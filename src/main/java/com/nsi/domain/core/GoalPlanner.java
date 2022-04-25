package com.nsi.domain.core;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="goal_planner")
public class GoalPlanner extends BaseDomain {

	private String id = UUID.randomUUID().toString();
	private String goalName;    
	private BigDecimal targetAmount;
	private BigDecimal lumpSump;
	private BigDecimal monthlyTopup;
	private BigDecimal inflationRate;  
	private Date targetDate;
	private Boolean activeStatus;    
	private GoalCategory goalCategory;
	private Score riskProfile;
	private PortfolioModel portfolioModel;
	private Kyc kyc;
	
	@Id
	@Column(name="goal_planner_id", length=36)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name="goal_name", length=30)
	public String getGoalName() {
		return goalName;
	}
	public void setGoalName(String goalName) {
		this.goalName = goalName;
	}
	
	@Column(name="target_amount")
	public BigDecimal getTargetAmount() {
		return targetAmount;
	}
	public void setTargetAmount(BigDecimal targetAmount) {
		this.targetAmount = targetAmount;
	}
	
	@Column(name="lump_sump")
	public BigDecimal getLumpSump() {
		return lumpSump;
	}
	public void setLumpSump(BigDecimal lumpSump) {
		this.lumpSump = lumpSump;
	}
	
	@Column(name="monthly_topup")
	public BigDecimal getMonthlyTopup() {
		return monthlyTopup;
	}
	public void setMonthlyTopup(BigDecimal monthlyTopup) {
		this.monthlyTopup = monthlyTopup;
	}
	
	@Column(name="inflation_rate")
	public BigDecimal getInflationRate() {
		return inflationRate;
	}
	public void setInflationRate(BigDecimal inflationRate) {
		this.inflationRate = inflationRate;
	}
	
	@Column(name="target_date")
	public Date getTargetDate() {
		return targetDate;
	}
	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}
	
	@Column(name="active_status")
	public Boolean getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(Boolean activeStatus) {
		this.activeStatus = activeStatus;
	}
	
	@ManyToOne
	@JoinColumn(name="goal_category_id")
	public GoalCategory getGoalCategory() {
		return goalCategory;
	}
	public void setGoalCategory(GoalCategory goalCategory) {
		this.goalCategory = goalCategory;
	}
	
	@ManyToOne
	@JoinColumn(name="risk_profile_id")
	public Score getRiskProfile() {
		return riskProfile;
	}
	public void setRiskProfile(Score riskProfile) {
		this.riskProfile = riskProfile;
	}
	
	@ManyToOne
	@JoinColumn(name="portfolio_model_id")
	public PortfolioModel getPortfolioModel() {
		return portfolioModel;
	}
	public void setPortfolioModel(PortfolioModel portfolioModel) {
		this.portfolioModel = portfolioModel;
	}
	
	@ManyToOne
	@JoinColumn(name="kyc_id")
	public Kyc getKyc() {
		return kyc;
	}
	public void setKyc(Kyc kyc) {
		this.kyc = kyc;
	}
    
    
}
