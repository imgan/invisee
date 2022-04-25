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
@Table(name = "investment_accounts")
public class InvestmentAccounts extends BaseDomain {

    private Long id;
    private String investmentAccountNo;
    private String investmentAccountName;
    private Kyc kycs;
    private FundPackages fundPackages;
    private String atInvestmentAccountId;
    private Date taggedDate;
    private GoalPlanner goalPlanner;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "investment_accounts_generator")
    @SequenceGenerator(name = "investment_accounts_generator", sequenceName = "investment_accounts_investment_account_id_seq", allocationSize = 1)
    @Column(name = "investment_account_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "investment_account_no", length = 50)
    public String getInvestmentAccountNo() {
        return investmentAccountNo;
    }

    public void setInvestmentAccountNo(String investmentAccountNo) {
        this.investmentAccountNo = investmentAccountNo;
    }

    @Column(name = "investment_account_name", length = 50)
    public String getInvestmentAccountName() {
        return investmentAccountName;
    }

    public void setInvestmentAccountName(String investmentAccountName) {
        this.investmentAccountName = investmentAccountName;
    }

    @ManyToOne
    @JoinColumn(name = "kycs_id")
    public Kyc getKycs() {
        return kycs;
    }

    public void setKycs(Kyc kycs) {
        this.kycs = kycs;
    }

    @ManyToOne
    @JoinColumn(name = "fund_packages_id")
    public FundPackages getFundPackages() {
        return fundPackages;
    }

    public void setFundPackages(FundPackages fundPackages) {
        this.fundPackages = fundPackages;
    }

    @Column(name = "at_investment_account_id")
    public String getAtInvestmentAccountId() {
        return atInvestmentAccountId;
    }

    public void setAtInvestmentAccountId(String atInvestmentAccountId) {
        this.atInvestmentAccountId = atInvestmentAccountId;
    }

    @Column(name = "tagged_date")
    public Date getTaggedDate() {
        return taggedDate;
    }

    public void setTaggedDate(Date taggedDate) {
        this.taggedDate = taggedDate;
    }

    @ManyToOne
    @JoinColumn(name = "goal_planner_id")
    public GoalPlanner getGoalPlanner() {
        return goalPlanner;
    }

    public void setGoalPlanner(GoalPlanner goalPlanner) {
        this.goalPlanner = goalPlanner;
    }

    @Override
    public String toString() {
        return "InvestmentAccounts{" + "id=" + id + ", investmentAccountNo=" + investmentAccountNo + ", investmentAccountName=" + investmentAccountName + ", kycs=" + kycs + ", fundPackages=" + fundPackages + ", atInvestmentAccountId=" + atInvestmentAccountId + ", taggedDate=" + taggedDate + ", goalPlanner=" + goalPlanner + '}';
    }

}
