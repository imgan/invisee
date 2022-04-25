package com.nsi.domain.core;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="agent_address")
public class AgentAddress {

	private Long id;
	private Agent agent;
	private Address address;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_address_generator")
	@SequenceGenerator(name="agent_address_generator", sequenceName = "agent_address_id_seq", allocationSize=1)
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
	
	@ManyToOne
	@JoinColumn(name="address_id")
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
}