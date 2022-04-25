package com.nsi.domain.core;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="payment_method_type")
public class PaymentMethodType {

	private Integer id;
	private String code;
	private String name;
	private String description;
	private Boolean rowStatus;
	private String createdBy;
	private Date createdOn;
	private Date endedOn;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_method_type_generator")
	@SequenceGenerator(name="payment_method_type_generator", sequenceName = "payment_method_type_id_seq", allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="code", length=10)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name="name", length=100)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="description", length=150)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	
	
}
