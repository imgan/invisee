package com.nsi.domain.core;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="customer_issues")
public class CustomerIssues {

	private String id;
	private Integer version;
	private String title;
	private CategoryIssue categoryId ;
	private StatusIssue status;
	private User assignee;    
	private String description;
	private String responses ;   
	private String reportedBy;
	private String createdBy ;   
	private Date createdDate;
	private Date closedDate;
	
	@Id
	@Column(name="issue_number")
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
	
	@Column(name="title")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@ManyToOne
	@JoinColumn(name="category_id")
	public CategoryIssue getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(CategoryIssue categoryId) {
		this.categoryId = categoryId;
	}
	
	@ManyToOne
	@JoinColumn(name="status")
	public StatusIssue getStatus() {
		return status;
	}
	public void setStatus(StatusIssue status) {
		this.status = status;
	}
	
	@ManyToOne
	@JoinColumn(name="assignee")
	public User getAssignee() {
		return assignee;
	}
	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}
	
	@Column(name="description", length=500)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="responses")
	public String getResponses() {
		return responses;
	}
	public void setResponses(String responses) {
		this.responses = responses;
	}
	
	@Column(name="reported_by", length=50)
	public String getReportedBy() {
		return reportedBy;
	}
	public void setReportedBy(String reportedBy) {
		this.reportedBy = reportedBy;
	}
	
	@Column(name="created_by", length=50)
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	@Column(name="created_date")
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	@Column(name="closed_date")
	public Date getClosedDate() {
		return closedDate;
	}
	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}
	
	
}
