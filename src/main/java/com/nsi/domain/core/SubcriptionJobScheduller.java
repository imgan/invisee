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
@Table(name="subcription_job_scheduller")
public class SubcriptionJobScheduller {

	private Long id;
	private String orderNo;
	private String status;
	private Date dateCreated;
	private Date executeDate;
	private String message;
	private String payType;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subcription_job_scheduller_generator")
	@SequenceGenerator(name="subcription_job_scheduller_generator", sequenceName = "subcription_job_scheduller_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="order_no")
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	@Column(name="status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Column(name="date_created")
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	@Column(name="execute_date")
	public Date getExecuteDate() {
		return executeDate;
	}
	public void setExecuteDate(Date executeDate) {
		this.executeDate = executeDate;
	}
	
	@Column(name="message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Column(name="pay_type", length=10)
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	
	
}
