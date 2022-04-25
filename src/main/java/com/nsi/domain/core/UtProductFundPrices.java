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
@Table(name="ut_product_fund_prices")
public class UtProductFundPrices extends BaseDomain {

	private Long id;
	private Date priceDate;
	private Double bidPrice;
	private Double offerPrice;
	private Double dividendRate;
	private UtProducts utProducts;
	private String priceGroupID;
	private String atUtProductFundPricesId;
	private String atUtProductsId;
	private String atPriceGroupId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ut_product_fund_prices_generator")
	@SequenceGenerator(name="ut_product_fund_prices_generator", sequenceName = "ut_product_fund_prices_ut_product_fund_prices_id_seq", allocationSize=1)
	@Column(name="ut_product_fund_prices_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="price_date")
	public Date getPriceDate() {
		return priceDate;
	}
	public void setPriceDate(Date priceDate) {
		this.priceDate = priceDate;
	}
	
	@Column(name="bid_price")
	public Double getBidPrice() {
		return bidPrice;
	}
	public void setBidPrice(Double bidPrice) {
		this.bidPrice = bidPrice;
	}
	
	@Column(name="offer_price")
	public Double getOfferPrice() {
		return offerPrice;
	}
	public void setOfferPrice(Double offerPrice) {
		this.offerPrice = offerPrice;
	}
	
	@Column(name="dividend_rate")
	public Double getDividendRate() {
		return dividendRate;
	}
	public void setDividendRate(Double dividendRate) {
		this.dividendRate = dividendRate;
	}
	
	@ManyToOne
	@JoinColumn(name="products_id")
	public UtProducts getUtProducts() {
		return utProducts;
	}
	public void setUtProducts(UtProducts utProducts) {
		this.utProducts = utProducts;
	}
	
	@Column(name="price_group_id")
	public String getPriceGroupID() {
		return priceGroupID;
	}
	public void setPriceGroupID(String priceGroupID) {
		this.priceGroupID = priceGroupID;
	}
	
	@Column(name="at_ut_product_fund_prices_id")
	public String getAtUtProductFundPricesId() {
		return atUtProductFundPricesId;
	}
	public void setAtUtProductFundPricesId(String atUtProductFundPricesId) {
		this.atUtProductFundPricesId = atUtProductFundPricesId;
	}
	
	@Column(name="at_ut_products_id")
	public String getAtUtProductsId() {
		return atUtProductsId;
	}
	public void setAtUtProductsId(String atUtProductsId) {
		this.atUtProductsId = atUtProductsId;
	}
	
	@Column(name="at_price_group_id")
	public String getAtPriceGroupId() {
		return atPriceGroupId;
	}
	public void setAtPriceGroupId(String atPriceGroupId) {
		this.atPriceGroupId = atPriceGroupId;
	}
	
	
}
