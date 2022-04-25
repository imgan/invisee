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
@Table(name="branch")
public class Branch extends BaseDomain {

	private Long id;
	private String branchCode;
	private String branchName;
	private Bank bank;
	private String atBranchId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "branch_generator")
	@SequenceGenerator(name="branch_generator", sequenceName = "branch_branch_id_seq", allocationSize=1)
	@Column(name="branch_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="branch_code")
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	
	@Column(name="branch_name")
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	@ManyToOne
	@JoinColumn(name="bank_id")
	public Bank getBank() {
		return bank;
	}
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	
	@Column(name="at_branch_id")
	public String getAtBranchId() {
		return atBranchId;
	}
	public void setAtBranchId(String atBranchId) {
		this.atBranchId = atBranchId;
	}
	
	
}
