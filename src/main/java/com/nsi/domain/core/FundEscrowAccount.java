package com.nsi.domain.core;

import java.io.Serializable;
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
@Table(name = "fund_escrow_account")
public class FundEscrowAccount implements Serializable {

    private Long id;
    private Integer version;
    private String escrowNumber;
    private FundPackages fundPackages;
    private Bank bank;
    private Boolean usingVa;
    private String vaCode;
    private String escrowName;
    private Branch branch;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fund_escrow_account_generator")
    @SequenceGenerator(name = "fund_escrow_account_generator", sequenceName = "fund_escrow_account_escrow_id_seq", allocationSize = 1)
    @Column(name = "escrow_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "version")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Column(name = "escrow_number")
    public String getEscrowNumber() {
        return escrowNumber;
    }

    public void setEscrowNumber(String escrowNumber) {
        this.escrowNumber = escrowNumber;
    }

    @ManyToOne
    @JoinColumn(name = "fund_packages_id")
    public FundPackages getFundPackages() {
        return fundPackages;
    }

    public void setFundPackages(FundPackages fundPackages) {
        this.fundPackages = fundPackages;
    }

    @ManyToOne
    @JoinColumn(name = "bank_id")
    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    @Column(name = "using_va")
    public Boolean getUsingVa() {
        return usingVa;
    }

    public void setUsingVa(Boolean usingVa) {
        this.usingVa = usingVa;
    }

    @Column(name = "va_code")
    public String getVaCode() {
        return vaCode;
    }

    public void setVaCode(String vaCode) {
        this.vaCode = vaCode;
    }

    @Column(name = "escrow_name")
    public String getEscrowName() {
        return escrowName;
    }

    public void setEscrowName(String escrowName) {
        this.escrowName = escrowName;
    }

    @ManyToOne
    @JoinColumn(name = "branch_id")
    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

}
