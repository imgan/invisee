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
@Table(name="email")
public class Email extends BaseNewDomain {

	private Long id;
	private String code;
	private String avantradeEmail;
	private EmailType emailType;
	private String value;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_generator")
	@SequenceGenerator(name="email_generator", sequenceName = "email_id_seq", allocationSize=1)
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
	
	@Column(name="avantrade_email")
	public String getAvantradeEmail() {
		return avantradeEmail;
	}
	public void setAvantradeEmail(String avantradeEmail) {
		this.avantradeEmail = avantradeEmail;
	}
	
	@ManyToOne
	@JoinColumn(name="email_type_id")
	public EmailType getEmailType() {
		return emailType;
	}
	public void setEmailType(EmailType emailType) {
		this.emailType = emailType;
	}
	
	@Column(name="value")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
