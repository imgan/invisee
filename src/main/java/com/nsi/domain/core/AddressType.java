package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="address_type")
public class AddressType {

	private Long id;
	private String code;
	private String name;
	private String description;
	private Boolean rowStatus;
	private String createdBy;
	private Date createdOn;
	private Date endedOn;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_type_generator")
	@SequenceGenerator(name="address_type_generator", sequenceName = "address_type_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="code")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="description")
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
