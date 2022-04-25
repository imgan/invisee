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
import java.util.Date;

@Entity
@Table(name="agent_commission")
public class AgentCommission {

	private Long id;
	private Agent agent;
	private Integer commission;
	private Boolean rowStatus;
	private String createdBy;
	private Date createdOn;
	private Date endedOn;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_commission_generator")
	@SequenceGenerator(name="agent_commission_generator", sequenceName = "agent_commission_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="agent_id")
	public Agent getAgent() {
		return agent;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	
	@Column(name="commission")
	public Integer getCommission() {
		return commission;
	}
	public void setCommission(Integer commission) {
		this.commission = commission;
	}

	@Column(name="row_status")
	public Boolean getRowStatus() {
		return rowStatus;
	}

	public void setRowStatus(Boolean rowStatus) {
		this.rowStatus = rowStatus;
	}

	@Column(name="created_by")
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Column(name="created_on")
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Column(name="ended_on")
	public Date getEndedOn() {
		return endedOn;
	}

	public void setEndedOn(Date endedOn) {
		this.endedOn = endedOn;
	}
}
