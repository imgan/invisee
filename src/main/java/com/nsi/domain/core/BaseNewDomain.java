package com.nsi.domain.core;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseNewDomain {
	private Boolean row_status;
	private String created_by;
	private Date created_on;
	private Date ended_on;

	@Column(name="row_status")
	public Boolean getRowStatus() {
		return row_status;
	}
	public void setRowStatus(Boolean rowStatus) {
		this.row_status = rowStatus;
	}

	@Column(name="created_by")
	public String getCreatedBy() {
		return created_by;
	}
	public void setCreatedBy(String createdBy) {
		this.created_by = createdBy;
	}

	@Column(name="created_on")
	public Date getCreatedOn() {
		return created_on;
	}
	public void setCreatedOn(Date createdOn) {
		this.created_on = createdOn;
	}
	@Column(name="ended_on")
	public Date getEndedOn() {
		return ended_on;
	}
	public void setEndedOn(Date endedOn) {
		this.ended_on = endedOn;
	}
}
