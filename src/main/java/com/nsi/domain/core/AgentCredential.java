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
@Table(name="agent_credential")
public class AgentCredential extends BaseNewDomain {

	private Long id;
	private Agent agent;
	private String password;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_credential_generator")
	@SequenceGenerator(name="agent_credential_generator", sequenceName = "agent_credential_id_seq", allocationSize=1)
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
	
	@Column(name="password")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
