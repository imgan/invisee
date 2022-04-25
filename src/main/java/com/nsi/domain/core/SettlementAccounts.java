package com.nsi.domain.core;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="settlement_accounts")
public class SettlementAccounts {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SETTLEMENT_ACCOUNT_SEQ")
    @SequenceGenerator(sequenceName = "settlement_accounts_settlement_account_id_seq", allocationSize = 1, name = "SETTLEMENT_ACCOUNT_SEQ")
	@Column(name="settlement_account_id")
	private Long id;
	
	@Column(name="settlement_account_no", length=50)
	private String settlementAccountNo;
	
	@Column(name="settlement_account_name", length=100)
	private String settlementAccountName;
	
	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="kycs_id")
	private Kyc kycs;
	
	@ManyToOne
	@JoinColumn(name="bank_id_id")
	private Bank bankId;
	
	@ManyToOne
	@JoinColumn(name="branch_id_id")
	private Branch branchId;
	
	@Column(name="at_settlement_account_id")
	private String atSettlementAccountId;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="created_date")
	private Date createdDate;

	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="updated_date")
	private Date updatedDate;
	
	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}

	public String getSettlementAccountNo() {
		return settlementAccountNo;
	}
	public void setSettlementAccountNo(String settlementAccountNo) {
		this.settlementAccountNo = settlementAccountNo;
	}

	public String getSettlementAccountName() {
		return settlementAccountName;
	}
	public void setSettlementAccountName(String settlementAccountName) {
		this.settlementAccountName = settlementAccountName;
	}

	public Kyc getKycs() {
		return kycs;
	}
	public void setKycs(Kyc kycs) {
		this.kycs = kycs;
	}

	public Bank getBankId() {
		return bankId;
	}
	public void setBankId(Bank bankId) {
		this.bankId = bankId;
	}

	public Branch getBranchId() {
		return branchId;
	}
	public void setBranchId(Branch branchId) {
		this.branchId = branchId;
	}

	public String getAtSettlementAccountId() {
		return atSettlementAccountId;
	}
	public void setAtSettlementAccountId(String atSettlementAccountId) {
		this.atSettlementAccountId = atSettlementAccountId;
	}

	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
}
