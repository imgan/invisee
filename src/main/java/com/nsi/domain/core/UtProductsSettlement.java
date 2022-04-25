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
@Table(name="ut_products_settlement")
public class UtProductsSettlement extends BaseDomain {

	private Long id;
	private UtProducts utProduct;
	private Branch branchId;
	private String accountName;
	private String accountNumber;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ut_products_settlement_generator")
	@SequenceGenerator(name="ut_products_settlement_generator", sequenceName = "ut_products_settlement_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="ut_product_id")
	public UtProducts getUtProduct() {
		return utProduct;
	}
	public void setUtProduct(UtProducts utProduct) {
		this.utProduct = utProduct;
	}
	
	@ManyToOne
	@JoinColumn(name="branch_id_id")
	public Branch getBranchId() {
		return branchId;
	}
	public void setBranchId(Branch branchId) {
		this.branchId = branchId;
	}
	
	@Column(name="account_name")
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	@Column(name="account_number")
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	
}
