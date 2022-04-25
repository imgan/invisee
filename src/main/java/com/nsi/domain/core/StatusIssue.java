package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="status_issue")
public class StatusIssue {

	private Long id;
	private Integer version;
	private String statusIssueName;
	private String description;
	private Boolean activeStatus;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "status_issue_generator")
	@SequenceGenerator(name="status_issue_generator", sequenceName = "status_issue_status_issue_id_seq", allocationSize=1)
	@Column(name="status_issue_id")
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
	
	@Column(name="status_issue_name")
	public String getStatusIssueName() {
		return statusIssueName;
	}
	public void setStatusIssueName(String statusIssueName) {
		this.statusIssueName = statusIssueName;
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
