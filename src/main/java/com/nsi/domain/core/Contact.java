package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="contact")
public class Contact extends BaseNewDomain {

	private Long id;
	private String code;
	private ContactType contactType;
	private String value;
	private String avantradeContact;
	private Integer version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_generator")
	@SequenceGenerator(name="contact_generator", sequenceName = "contact_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="code")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@ManyToOne
	@JoinColumn(name="contact_type_id")
	public ContactType getContactType() {
		return contactType;
	}
	public void setContactType(ContactType contactType) {
		this.contactType = contactType;
	}
	
	@Column(name="value", length=20)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Column(name="avantrade_contact")
	public String getAvantradeContact() {
		return avantradeContact;
	}
	public void setAvantradeContact(String avantradeContact) {
		this.avantradeContact = avantradeContact;
	}
	
	@Column(name="version")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
}
