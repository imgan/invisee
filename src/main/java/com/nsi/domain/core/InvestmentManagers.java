package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="investment_managers")
public class InvestmentManagers extends BaseDomain {

	private Long id;
	private String invManagerCode;
	private String classification;
	private String fullName;
	private String displayName;
	private String address;
	private String addressCity;
	private String addressState;
	private String addressCountry;
	private String contactName;
	private String contactPhone;
	private String contactMobilePhone;
	private String contactEmail;
	private String atInvestmentManager;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "investment_managers_generator")
	@SequenceGenerator(name="investment_managers_generator", sequenceName = "investment_managers_inv_manager_id_seq", allocationSize=1)
	@Column(name="inv_manager_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="inv_manager_code")
	public String getInvManagerCode() {
		return invManagerCode;
	}
	public void setInvManagerCode(String invManagerCode) {
		this.invManagerCode = invManagerCode;
	}
	
	@Column(name="classification")
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	
	@Column(name="full_name")
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	@Column(name="display_name")
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@Column(name="address")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Column(name="address_city")
	public String getAddressCity() {
		return addressCity;
	}
	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}
	
	@Column(name="address_state")
	public String getAddressState() {
		return addressState;
	}
	public void setAddressState(String addressState) {
		this.addressState = addressState;
	}
	
	@Column(name="address_country")
	public String getAddressCountry() {
		return addressCountry;
	}
	public void setAddressCountry(String addressCountry) {
		this.addressCountry = addressCountry;
	}
	
	@Column(name="contact_name")
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	@Column(name="contact_phone")
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	
	@Column(name="contact_mobile_phone")
	public String getContactMobilePhone() {
		return contactMobilePhone;
	}
	public void setContactMobilePhone(String contactMobilePhone) {
		this.contactMobilePhone = contactMobilePhone;
	}
	
	@Column(name="contact_email")
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	
	@Column(name="at_investment_manager")
	public String getAtInvestmentManager() {
		return atInvestmentManager;
	}
	public void setAtInvestmentManager(String atInvestmentManager) {
		this.atInvestmentManager = atInvestmentManager;
	}
	
	
}
