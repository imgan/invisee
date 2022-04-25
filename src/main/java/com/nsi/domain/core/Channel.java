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
@Table(name="channel")
public class Channel {

	private Long id;
	private String code;
	private String name;
    private String description;
    private Boolean rowStatus;
    private String createdBy;
    private Date createdOn;
    private String modifiedBy;
    private Date modifiedOn;
    private String avantradeBranch;
    private Integer version;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "channel_generator")
	@SequenceGenerator(name="channel_generator", sequenceName = "channel_id_seq", allocationSize=1)
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
	
	@Column(name="name", length=70)
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
	
	@Column(name="modified_by")
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	@Column(name="modified_on")
	public Date getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	
	@Column(name="avantrade_branch")
	public String getAvantradeBranch() {
		return avantradeBranch;
	}
	public void setAvantradeBranch(String avantradeBranch) {
		this.avantradeBranch = avantradeBranch;
	}
	
	@Column(name="version")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
    
    
}
