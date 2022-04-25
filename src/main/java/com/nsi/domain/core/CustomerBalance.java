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
@Table(name = "customer_balance")
public class CustomerBalance extends BaseDomain {

    private Long id;
    private Integer version;
    private Kyc customer;
    private InvestmentAccounts invAccount;
    private UtProducts utProduct;
    private Date balanceDate;
    private Double currentUnit;
    private Double currentAmount;
    private Double broughtForwardUnit;
    private Double broughtForwardAmount;
    private Double subscriptionUnit;
    private Double subscriptionAmount;
    private Double redemptionUnit;
    private Double redemptionAmount;
    private Double averageCost;
    private Double realizedGainLoss;
    private Double unrealizedGainLoss;
    private String atCustBalId;
    private String atInvestmentAccountId;
    private String atProductId;
    private Double switchInUnit;
    private Double switchInAmount;
    private Double switchOutUnit;
    private Double switchOutAmount;
    private Double dividendUnit;
    private Double dividendAmount;
    private Double accruedUnit;
    private Double accruedAmount;
    private Double totalCost;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_balance_generator")
    @SequenceGenerator(name = "customer_balance_generator", sequenceName = "customer_balance_cust_bal_id_seq", allocationSize = 1)
    @Column(name = "cust_bal_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "version")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @ManyToOne
    @JoinColumn(name = "customer_id")
    public Kyc getCustomer() {
        return customer;
    }

    public void setCustomer(Kyc customer) {
        this.customer = customer;
    }

    @ManyToOne
    @JoinColumn(name = "inv_account_id")
    public InvestmentAccounts getInvAccount() {
        return invAccount;
    }

    public void setInvAccount(InvestmentAccounts invAccount) {
        this.invAccount = invAccount;
    }

    @ManyToOne
    @JoinColumn(name = "ut_product_id")
    public UtProducts getUtProduct() {
        return utProduct;
    }

    public void setUtProduct(UtProducts utProduct) {
        this.utProduct = utProduct;
    }

    @Column(name = "balance_date")
    public Date getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(Date balanceDate) {
        this.balanceDate = balanceDate;
    }

    @Column(name = "current_unit")
    public Double getCurrentUnit() {
        return currentUnit;
    }

    public void setCurrentUnit(Double currentUnit) {
        this.currentUnit = currentUnit;
    }

    @Column(name = "current_amount")
    public Double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Double currentAmount) {
        this.currentAmount = currentAmount;
    }

    @Column(name = "brought_forward_unit")
    public Double getBroughtForwardUnit() {
        return broughtForwardUnit;
    }

    public void setBroughtForwardUnit(Double broughtForwardUnit) {
        this.broughtForwardUnit = broughtForwardUnit;
    }

    @Column(name = "brought_forward_amount")
    public Double getBroughtForwardAmount() {
        return broughtForwardAmount;
    }

    public void setBroughtForwardAmount(Double broughtForwardAmount) {
        this.broughtForwardAmount = broughtForwardAmount;
    }

    @Column(name = "subscription_unit")
    public Double getSubscriptionUnit() {
        return subscriptionUnit;
    }

    public void setSubscriptionUnit(Double subscriptionUnit) {
        this.subscriptionUnit = subscriptionUnit;
    }

    @Column(name = "subscription_amount")
    public Double getSubscriptionAmount() {
        return subscriptionAmount;
    }

    public void setSubscriptionAmount(Double subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
    }

    @Column(name = "redemption_unit")
    public Double getRedemptionUnit() {
        return redemptionUnit;
    }

    public void setRedemptionUnit(Double redemptionUnit) {
        this.redemptionUnit = redemptionUnit;
    }

    @Column(name = "redemption_amount")
    public Double getRedemptionAmount() {
        return redemptionAmount;
    }

    public void setRedemptionAmount(Double redemptionAmount) {
        this.redemptionAmount = redemptionAmount;
    }

    @Column(name = "average_cost")
    public Double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(Double averageCost) {
        this.averageCost = averageCost;
    }

    @Column(name = "realized_gain_loss")
    public Double getRealizedGainLoss() {
        return realizedGainLoss;
    }

    public void setRealizedGainLoss(Double realizedGainLoss) {
        this.realizedGainLoss = realizedGainLoss;
    }

    @Column(name = "unrealized_gain_loss")
    public Double getUnrealizedGainLoss() {
        return unrealizedGainLoss;
    }

    public void setUnrealizedGainLoss(Double unrealizedGainLoss) {
        this.unrealizedGainLoss = unrealizedGainLoss;
    }

    @Column(name = "at_cust_bal_id")
    public String getAtCustBalId() {
        return atCustBalId;
    }

    public void setAtCustBalId(String atCustBalId) {
        this.atCustBalId = atCustBalId;
    }

    @Column(name = "at_investment_account_id")
    public String getAtInvestmentAccountId() {
        return atInvestmentAccountId;
    }

    public void setAtInvestmentAccountId(String atInvestmentAccountId) {
        this.atInvestmentAccountId = atInvestmentAccountId;
    }

    @Column(name = "at_product_id")
    public String getAtProductId() {
        return atProductId;
    }

    public void setAtProductId(String atProductId) {
        this.atProductId = atProductId;
    }

    @Column(name = "switch_in_unit")
    public Double getSwitchInUnit() {
        return switchInUnit;
    }

    public void setSwitchInUnit(Double switchInUnit) {
        this.switchInUnit = switchInUnit;
    }

    @Column(name = "switch_in_amount")
    public Double getSwitchInAmount() {
        return switchInAmount;
    }

    public void setSwitchInAmount(Double switchInAmount) {
        this.switchInAmount = switchInAmount;
    }

    @Column(name = "switch_out_unit")
    public Double getSwitchOutUnit() {
        return switchOutUnit;
    }

    public void setSwitchOutUnit(Double switchOutUnit) {
        this.switchOutUnit = switchOutUnit;
    }

    @Column(name = "switch_out_amount")
    public Double getSwitchOutAmount() {
        return switchOutAmount;
    }

    public void setSwitchOutAmount(Double switchOutAmount) {
        this.switchOutAmount = switchOutAmount;
    }

    @Column(name = "dividend_unit")
    public Double getDividendUnit() {
        return dividendUnit;
    }

    public void setDividendUnit(Double dividendUnit) {
        this.dividendUnit = dividendUnit;
    }

    @Column(name = "dividend_amount")
    public Double getDividendAmount() {
        return dividendAmount;
    }

    public void setDividendAmount(Double dividendAmount) {
        this.dividendAmount = dividendAmount;
    }

    @Column(name = "accrued_unit")
    public Double getAccruedUnit() {
        return accruedUnit;
    }

    public void setAccruedUnit(Double accruedUnit) {
        this.accruedUnit = accruedUnit;
    }

    @Column(name = "accrued_amount")
    public Double getAccruedAmount() {
        return accruedAmount;
    }

    public void setAccruedAmount(Double accruedAmount) {
        this.accruedAmount = accruedAmount;
    }

    @Column(name = "total_cost")
    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public String toString() {
        return "CustomerBalance{" + "id=" + id + ", version=" + version + ", customer=" + customer + ", invAccount=" + invAccount + ", utProduct=" + utProduct + ", balanceDate=" + balanceDate + ", currentUnit=" + currentUnit + ", currentAmount=" + currentAmount + ", broughtForwardUnit=" + broughtForwardUnit + ", broughtForwardAmount=" + broughtForwardAmount + ", subscriptionUnit=" + subscriptionUnit + ", subscriptionAmount=" + subscriptionAmount + ", redemptionUnit=" + redemptionUnit + ", redemptionAmount=" + redemptionAmount + ", averageCost=" + averageCost + ", realizedGainLoss=" + realizedGainLoss + ", unrealizedGainLoss=" + unrealizedGainLoss + ", atCustBalId=" + atCustBalId + ", atInvestmentAccountId=" + atInvestmentAccountId + ", atProductId=" + atProductId + ", switchInUnit=" + switchInUnit + ", switchInAmount=" + switchInAmount + ", switchOutUnit=" + switchOutUnit + ", switchOutAmount=" + switchOutAmount + ", dividendUnit=" + dividendUnit + ", dividendAmount=" + dividendAmount + ", accruedUnit=" + accruedUnit + ", accruedAmount=" + accruedAmount + ", totalCost=" + totalCost + '}';
    }

}
