package com.nsi.domain.core;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="issues")
public class Issues {

	private Integer version;
	private String id;
	private String title;
	private CategoryIssue categoryId  ;   
	private String description;
	private String reportedBy;
	private String createdBy  ;  
	private Date createdOn;
	private User assignee;
	
	@Column(name="version")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Id
	@Column(name="issue_number", length=15)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@Column(name="description", length=500)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	@Column(name="created_on")
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@ManyToOne
	@JoinColumn(name="assignee")
	public User getAssignee() {
		return assignee;
	}

	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}
	
	
}
