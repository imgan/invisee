package com.nsi.domain.core;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "mst_fee_agent")
public class MstFeeAgent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "role", nullable = false)
	private String role;

	@Column(name = "direct_fee", nullable = false)
	private Double directFee = 0d;

	@Column(name = "indirect_fee", nullable = false)
	private Double indirectFee= 0d;
	
	@Column(name = "row_status")
	private Boolean rowStatus = true;

	@Column(name="created_date")
	private Date createdDate;

	@Column(name="created_by", length=50)
	private String createdBy;

	@Column(name="updated_date")
	private Date updatedDate;

	@Column(name="updated_by", length=50)
	private String updatedBy;

	public Boolean getRowStatus() {
		return rowStatus;
	}

	public void setRowStatus(Boolean rowStatus) {
		this.rowStatus = rowStatus;
	}

	@ManyToOne
	@JoinColumn(name = "mst_fee_id", nullable = false)
	private MstFee mstFee;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Double getDirectFee() {
		return directFee;
	}

	public void setDirectFee(Double directFee) {
		this.directFee = directFee;
	}

	public Double getIndirectFee() {
		return indirectFee;
	}

	public void setIndirectFee(Double indirectFee) {
		this.indirectFee = indirectFee;
	}

	public MstFee getMstFee() {
		return mstFee;
	}

	public void setMstFee(MstFee mstFee) {
		this.mstFee = mstFee;
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