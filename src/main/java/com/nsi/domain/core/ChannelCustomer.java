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
@Table(name="channel_customer")
public class ChannelCustomer {

	private Long id;
	private String channelCustomer;
	private String name;
	private String email;
	private String mobile;
	private String createdBy;
	private Date createdOn;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "channel_customer_generator")
	@SequenceGenerator(name="channel_customer_generator", sequenceName = "channel_customer_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="channel_customer")
	public String getChannelCustomer() {
		return channelCustomer;
	}
	public void setChannelCustomer(String channelCustomer) {
		this.channelCustomer = channelCustomer;
	}
	
	@Column(name="name", length=100)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="email", length=100)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name="mobile", length=20)
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Column(name="created_by", length=100)
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
	
	
}
