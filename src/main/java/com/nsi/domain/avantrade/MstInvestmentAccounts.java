package com.nsi.domain.avantrade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MST_INVESTMENT_ACCOUNTS")
public class MstInvestmentAccounts {
    private String invAccountId;
    private Long invAccountIdPortal;

    @Id
    @Column(name = "INV_ACCOUNT_ID")
    public String getInvAccountId() {
        return invAccountId;
    }

    public void setInvAccountId(String invAccountId) {
        this.invAccountId = invAccountId;
    }

    @Column(name="INV_ACCOUNT_ID_PORTAL")
    public Long getInvAccountIdPortal() {
        return invAccountIdPortal;
    }

    public void setInvAccountIdPortal(Long invAccountIdPortal) {
        this.invAccountIdPortal = invAccountIdPortal;
    }

    @Override
    public String toString() {
        return "MstInvestmentAccounts{" +
                "invAccountId='" + invAccountId + '\'' +
                ", invAccountIdPortal=" + invAccountIdPortal +
                '}';
    }
}
