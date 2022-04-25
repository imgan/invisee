package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="scp_merchant_id")
public class ScpMerchantId {

	private Long id;
	private String merchantId;
	private String binScp;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scp_merchant_id_generator")
	@SequenceGenerator(name="scp_merchant_id_generator", sequenceName = "scp_merchant_id_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="merchant_id", length=10)
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	
	@Column(name="bin_scp", length=10)
	public String getBinScp() {
		return binScp;
	}
	public void setBinScp(String binScp) {
		this.binScp = binScp;
	}
	
	
}
