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
@Table(name="group_access")
public class GroupAccess extends BaseNewDomain {

	private Long id;
	private Groups group;
	private AccessPermission access;
	private Boolean rowStatus;
	private String createdBy;
	private Date createdOn;
	private Date endedOn;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_accesss_generator")
	@SequenceGenerator(name="group_accesss_generator", sequenceName = "group_access_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="group_id")
	public Groups getGroup() {
		return group;
	}
	public void setGroup(Groups group) {
		this.group = group;
	}
	
	@ManyToOne
	@JoinColumn(name="access_id")
	public AccessPermission getAccess() {
		return access;
	}
	public void setAccess(AccessPermission access) {
		this.access = access;
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
