package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="category_issue")
public class CategoryIssue {

	private Long id;
	private Integer version;
	private String categoryIssueName;
	private String description;
	private Boolean activeStatus;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_issue_generator")
	@SequenceGenerator(name="category_issue_generator", sequenceName = "category_issue_category_issue_id_seq", allocationSize=1)
	@Column(name="category_issue_id")
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
	
	@Column(name="category_issue_name")
	public String getCategoryIssueName() {
		return categoryIssueName;
	}
	public void setCategoryIssueName(String categoryIssueName) {
		this.categoryIssueName = categoryIssueName;
	}
	
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
