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
@Table(name="customer_service_history")
public class CustomerServiceHistory {

	private Long id;
	private Integer version;
	private Issues issueNumber;
	private Date createdOn;
	private String createdBy;
	private StatusIssue statusId;
	private int rowStatus;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_service_history_generator")
	@SequenceGenerator(name="customer_service_history_generator", sequenceName = "customer_service_history_customer_service_history_id_seq", allocationSize=1)
	@Column(name="customer_service_history_id")
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
	
	@ManyToOne
	@JoinColumn(name="issue_number_id")
	public Issues getIssueNumber() {
		return issueNumber;
	}
	public void setIssueNumber(Issues issueNumber) {
		this.issueNumber = issueNumber;
	}
	
	@Column(name="created_on")
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	@Column(name="created_by", length=50)
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	@ManyToOne
	@JoinColumn(name="status_id")
	public StatusIssue getStatusId() {
		return statusId;
	}
	public void setStatusId(StatusIssue statusId) {
		this.statusId = statusId;
	}
	
	@Column(name="row_status")
	public int getRowStatus() {
		return rowStatus;
	}
	public void setRowStatus(int rowStatus) {
		this.rowStatus = rowStatus;
	}
	
	
}
