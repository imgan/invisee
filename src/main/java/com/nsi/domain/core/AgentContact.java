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
@Table(name="agent_contact")
public class AgentContact {

	private Long id;
	private Agent agent;
	private Contact contact;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_contact_generator")
	@SequenceGenerator(name="agent_contact_generator", sequenceName = "agent_contact_id_seq", allocationSize=1)
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
	@JoinColumn(name="contact_id")
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}


}
