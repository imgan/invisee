package com.nsi.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nsi.domain.core.Agent;

@JsonFormat
public class AgentDto extends Agent{

	@JsonProperty("total_market_value")
	private BigDecimal totalAumAgent = BigDecimal.ZERO;
	
	@JsonProperty("total_commission")
	private BigDecimal totalCommission = BigDecimal.ZERO;
	
	@JsonProperty("total_order_amount")
	private BigDecimal totalOrderAmount = BigDecimal.ZERO;
	
	@JsonProperty("avarage_percentage")
	private BigDecimal avaragePercentage = BigDecimal.ZERO;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("phone_number")
	private String phoneNumber;

	public BigDecimal getTotalAumAgent() {
		return totalAumAgent;
	}

	public void setTotalAumAgent(BigDecimal totalAumAgent) {
		this.totalAumAgent = totalAumAgent;
	}

	public BigDecimal getTotalCommission() {
		return totalCommission;
	}

	public void setTotalCommission(BigDecimal totalCommission) {
		this.totalCommission = totalCommission;
	}

	public BigDecimal getTotalOrderAmount() {
		return totalOrderAmount;
	}

	public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
		this.totalOrderAmount = totalOrderAmount;
	}

	public BigDecimal getAvaragePercentage() {
		return avaragePercentage;
	}

	public void setAvaragePercentage(BigDecimal avaragePercentage) {
		this.avaragePercentage = avaragePercentage;
	}
	
}