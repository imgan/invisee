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
@Table(name="investment_promotion")
public class InvestmentPromotion {

	private Long id;
	private InvestmentAccounts investmentAccount;
	private Double minimalInvestmentAmount;
	private Date minimalInvestmentRedeemDate;
	private Boolean rowStatus;
	private String createdBy;
	private Date createdOn;
	private Date endedOn;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "investment_promotion_generator")
	@SequenceGenerator(name="investment_promotion_generator", sequenceName = "investment_promotion_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="investment_account")
	public InvestmentAccounts getInvestmentAccount() {
		return investmentAccount;
	}
	public void setInvestmentAccount(InvestmentAccounts investmentAccount) {
		this.investmentAccount = investmentAccount;
	}
	
	@Column(name="minimal_investment_amount")
	public Double getMinimalInvestmentAmount() {
		return minimalInvestmentAmount;
	}
	public void setMinimalInvestmentAmount(Double minimalInvestmentAmount) {
		this.minimalInvestmentAmount = minimalInvestmentAmount;
	}
	
	@Column(name="minimal_investment_redeem_date")
	public Date getMinimalInvestmentRedeemDate() {
		return minimalInvestmentRedeemDate;
	}
	public void setMinimalInvestmentRedeemDate(Date minimalInvestmentRedeemDate) {
		this.minimalInvestmentRedeemDate = minimalInvestmentRedeemDate;
	}
	
	@Column(name="row_status")
	public Boolean getRowStatus() {
		return rowStatus;
	}
	public void setRowStatus(Boolean rowStatus) {
		this.rowStatus = rowStatus;
	}
	
	@Column(name="created_by")
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	@Column(name="created_on")
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	@Column(name="ended_on")
	public Date getEndedOn() {
		return endedOn;
	}
	public void setEndedOn(Date endedOn) {
		this.endedOn = endedOn;
	}

    @Override
    public String toString() {
        return "InvestmentPromotion{" + "id=" + id + ", investmentAccount=" + investmentAccount + ", minimalInvestmentAmount=" + minimalInvestmentAmount + ", minimalInvestmentRedeemDate=" + minimalInvestmentRedeemDate + ", rowStatus=" + rowStatus + ", createdBy=" + createdBy + ", createdOn=" + createdOn + ", endedOn=" + endedOn + '}';
    }
	
}
