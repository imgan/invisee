package com.nsi.dto;

public class RedemptionDto {

	private String productId;
	private String feeRate;
	private String feeAmount;
	private String transactionDate;
	private String settlementAccountId;
	private String customerId;
	private String investmentAccountId;
	private String orderNumber;
	private String transactionNumber;
	private String transactionId;
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(String feeRate) {
		this.feeRate = feeRate;
	}
	public String getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(String feeAmount) {
		this.feeAmount = feeAmount;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getSettlementAccountId() {
		return settlementAccountId;
	}
	public void setSettlementAccountId(String settlementAccountId) {
		this.settlementAccountId = settlementAccountId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getInvestmentAccountId() {
		return investmentAccountId;
	}
	public void setInvestmentAccountId(String investmentAccountId) {
		this.investmentAccountId = investmentAccountId;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getTransactionNumber() {
		return transactionNumber;
	}
	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

    @Override
    public String toString() {
        return "RedemptionDto{" + "productId=" + productId + ", feeRate=" + feeRate + ", feeAmount=" + feeAmount + ", transactionDate=" + transactionDate + ", settlementAccountId=" + settlementAccountId + ", customerId=" + customerId + ", investmentAccountId=" + investmentAccountId + ", orderNumber=" + orderNumber + ", transactionNumber=" + transactionNumber + ", transactionId=" + transactionId + '}';
    }
	
}
