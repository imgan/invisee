package com.nsi.domain.core;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mst_fee")
public class MstFee implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "role_base")
	private String roleBase;

	@Column(name = "fee_base")
	private double feeBase = 0d;

	@Column(name="created_date")
	private Date createdDate;

	@Column(name="created_by", length=50)
	private String createdBy;

	@Column(name="updated_date")
	private Date updatedDate;

	@Column(name="updated_by", length=50)
	private String updatedBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleBase() {
		return roleBase;
	}

	public void setRoleBase(String roleBase) {
		this.roleBase = roleBase;
	}

	public double getFeeBase() {
		return feeBase;
	}

	public void setFeeBase(double feeBase) {
		this.feeBase = feeBase;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
}
