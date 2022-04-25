package com.nsi.domain.core;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="portfolio_model")
public class PortfolioModel extends BaseDomain {

	private String id= UUID.randomUUID().toString();
	private Integer version;
	private String name;
	private Float expectedReturn;
	private String description;
	private Boolean activeStatus;
	
	@Id
	@Column(name="portfolio_model_id", length=36)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name="version")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name="name", length=30)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="expected_return")
	public Float getExpectedReturn() {
		return expectedReturn;
	}
	public void setExpectedReturn(Float expectedReturn) {
		this.expectedReturn = expectedReturn;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="active_status")
	public Boolean getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(Boolean activeStatus) {
		this.activeStatus = activeStatus;
	}
	
	
}
