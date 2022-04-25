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
@Table(name="access_permission")
public class AccessPermission {

	private Long id;
	private String code;
    private String name;
    private Boolean rowStatus;
	private String createdBy;
	private Date createdOn;
	private Date endedOn;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "access_permission_generator")
	@SequenceGenerator(name="access_permission_generator", sequenceName = "access_permission_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="code", length=30)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name="name", length=50)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
