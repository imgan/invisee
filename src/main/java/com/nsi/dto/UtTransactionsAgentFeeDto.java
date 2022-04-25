package com.nsi.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonFormat
@JsonPropertyOrder
public class UtTransactionsAgentFeeDto{
	
	@JsonProperty("agent_name")
	private String agentName;
	
	@JsonProperty("order_number")
	private String orderNumber;
	
	@JsonProperty("product_name")
	private String productName;
	
	@JsonProperty("transaction_type")
	private String transactionType;
	
	@JsonProperty("fee_type")
	private String feeType;
	
	@JsonProperty("transaction_amount")
	private BigDecimal transactionAmount;
	
	@JsonProperty("fee_commission")
	private BigDecimal feeCommission;
	
	@JsonProperty("fee_percentage")
	private BigDecimal feePercentage;
	
	@JsonProperty("transaction_date")
	private Date transactionDate;

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public BigDecimal getFeeCommission() {
		return feeCommission;
	}

	public void setFeeCommission(BigDecimal feeCommission) {
		this.feeCommission = feeCommission;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public BigDecimal getFeePercentage() {
		return feePercentage;
	}

	public void setFeePercentage(BigDecimal feePercentage) {
		this.feePercentage = feePercentage;
	}

}
