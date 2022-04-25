package com.nsi.domain.core;

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
@Table(name="portfolio_model_composition")
public class PortfolioModelComposition {

	private Long id;
	private Integer version;
	private Double portion;
	private LookupLine fundType;
	private PortfolioModel portfolioModel;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "portfolio_model_composition_generator")
	@SequenceGenerator(name="portfolio_model_composition_generator", sequenceName = "portfolio_model_composition_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="version")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name="portion")
	public Double getPortion() {
		return portion;
	}
	public void setPortion(Double portion) {
		this.portion = portion;
	}
	
	@ManyToOne
	@JoinColumn(name="fund_type_id")
	public LookupLine getFundType() {
		return fundType;
	}
	public void setFundType(LookupLine fundType) {
		this.fundType = fundType;
	}
	
	@ManyToOne
	@JoinColumn(name="portfolio_model_id")
	public PortfolioModel getPortfolioModel() {
		return portfolioModel;
	}
	public void setPortfolioModel(PortfolioModel portfolioModel) {
		this.portfolioModel = portfolioModel;
	}
	
	
}
