package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="custody_bank")
public class CustodyBank extends BaseDomain {

	private Long id;
	private String custodyCode;
	private String fullName;
	private String custodyCodeSwift;
	private String taxIdNo;
	private String atCustodyId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "custody_bank_generator")
	@SequenceGenerator(name="custody_bank_generator", sequenceName = "custody_id_seq", allocationSize=1)
	@Column(name="custody_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="custody_code", length=15)
	public String getCustodyCode() {
		return custodyCode;
	}
	public void setCustodyCode(String custodyCode) {
		this.custodyCode = custodyCode;
	}
	
	@Column(name="full_name", length=150)
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	@Column(name="custody_code_swift", length=15)
	public String getCustodyCodeSwift() {
		return custodyCodeSwift;
	}
	public void setCustodyCodeSwift(String custodyCodeSwift) {
		this.custodyCodeSwift = custodyCodeSwift;
	}
	
	@Column(name="tax_id_no", length=30)
	public String getTaxIdNo() {
		return taxIdNo;
	}
	public void setTaxIdNo(String taxIdNo) {
		this.taxIdNo = taxIdNo;
	}
	
	@Column(name="at_custody_id", length=50)
	public String getAtCustodyId() {
		return atCustodyId;
	}
	public void setAtCustodyId(String atCustodyId) {
		this.atCustodyId = atCustodyId;
	}
	
	
}
