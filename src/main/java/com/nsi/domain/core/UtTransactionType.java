package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="ut_transaction_type")
public class UtTransactionType extends BaseDomain {

	private Long id;
	private Integer version;
	private String trxCode;
	private String trxName;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ut_transaction_type_generator")
	@SequenceGenerator(name="ut_transaction_type_generator", sequenceName = "ut_transaction_type_trx_id_seq", allocationSize=1)
	@Column(name="trx_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="version")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name="trx_code")
	public String getTrxCode() {
		return trxCode;
	}
	public void setTrxCode(String trxCode) {
		this.trxCode = trxCode;
	}
	
	@Column(name="trx_name")
	public String getTrxName() {
		return trxName;
	}
	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}
	
	
}
