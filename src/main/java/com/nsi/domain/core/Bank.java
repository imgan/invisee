package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="bank")
public class Bank extends BaseDomain {

	private Long id;
	private String bankCode;
	private String bankName;
	private String swiftCode;
	private String atBankId;
	private String imageKey;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_generator")
	@SequenceGenerator(name="bank_generator", sequenceName = "bank_bank_id_seq", allocationSize=1)
	@Column(name="bank_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="bank_code")
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	
	@Column(name="bank_name")
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	@Column(name="swift_code")
	public String getSwiftCode() {
		return swiftCode;
	}
	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}
	
	@Column(name="at_bank_id")
	public String getAtBankId() {
		return atBankId;
	}
	public void setAtBankId(String atBankId) {
		this.atBankId = atBankId;
	}
	
	@Column(name="image_key")
	public String getImageKey() {
		return imageKey;
	}
	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}
	
	
}
