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
@Table(name="agent_email")
public class AgentEmail {

	private Long id;
	private Integer version;
	private Email email;
	private Agent agent;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_email_generator")
	@SequenceGenerator(name="agent_email_generator", sequenceName = "agent_email_id_seq", allocationSize=1)
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
	
	@ManyToOne
	@JoinColumn(name="email_id")
	public Email getEmail() {
		return email;
	}
	public void setEmail(Email email) {
		this.email = email;
	}
	
	@ManyToOne
	@JoinColumn(name="agent_id")
	public Agent getAgent() {
		return agent;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	
	
}
