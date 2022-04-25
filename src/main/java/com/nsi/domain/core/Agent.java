package com.nsi.domain.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nsi.util.TokenGenerator;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name = "agent")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Agent implements Serializable {

    /**
	 * 
	 */
	private Long id;
    private String code;
    private String avantradeSales;
    private Channel channel;
    private Groups accessGroup;
    private String name;
    private String token;
    private Boolean rowStatus;
    private String createdBy;
    private Date createdOn;
    private String modifiedBy;
    private Date modifiedOn;
    private Integer version;
    private Boolean needTokenTrx;
    private Boolean emailCustom;
    private Agent spv;
    private MstFeeAgent agentFee;
    private Boolean sbn;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_generator")
    @SequenceGenerator(name = "agent_generator", sequenceName = "agent_id_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "code", length = 30)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "avantrade_sales")
    public String getAvantradeSales() {
        return avantradeSales;
    }

    public void setAvantradeSales(String avantradeSales) {
        this.avantradeSales = avantradeSales;
    }

    @ManyToOne
    @JoinColumn(name = "channel_id")
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @ManyToOne
    @JoinColumn(name = "access_group_id")
    public Groups getAccessGroup() {
        return accessGroup;
    }

    public void setAccessGroup(Groups accessGroup) {
        this.accessGroup = accessGroup;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(name = "row_status")
    public Boolean getRowStatus() {
        return rowStatus;
    }

    public void setRowStatus(Boolean rowStatus) {
        this.rowStatus = rowStatus;
    }

    @Column(name = "created_by", length = 50)
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "created_on")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Column(name = "modified_by", length = 50)
    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Column(name = "modified_on")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @Column(name = "version")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Column(name = "need_token_trx")
    public Boolean getNeedTokenTrx() {
        return needTokenTrx;
    }

    public void setNeedTokenTrx(Boolean needTokenTrx) {
        this.needTokenTrx = needTokenTrx;
    }

    @Column(name = "email_custom")
    public Boolean getEmailCustom() {
        return emailCustom;
    }

    public void setEmailCustom(Boolean emailCustom) {
        this.emailCustom = emailCustom;
    }

    @ManyToOne
    @JoinColumn(name = "spv_id")
	public Agent getSpv() {
		return spv;
	}
	public void setSpv(Agent spv) {
		this.spv = spv;
	}

	@ManyToOne
    @JoinColumn(name = "mst_agent_fee_role", referencedColumnName = "role")
	public MstFeeAgent getAgentFee() {
		return agentFee;
	}
	public void setAgentFee(MstFeeAgent agentFee) {
		this.agentFee = agentFee;
	}

    @Column(name = "sbn")
    public Boolean getSbn() {
        return sbn;
    }

    public void setSbn(Boolean sbn) {
        this.sbn = sbn;
    }

    public String generateNewToken(String salt) {
        String shownToken = TokenGenerator.generateToken();
        setToken(TokenGenerator.hash(shownToken, salt));
        return shownToken;
    }
}
