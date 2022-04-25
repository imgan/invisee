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
@Table(name="channel_contact")
public class ChannelContact {

	private Contact contact;
	private Channel channel;
	private Long id;
	private Integer version;
	
	@ManyToOne
	@JoinColumn(name="contact_id")
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
	@ManyToOne
	@JoinColumn(name="channel_id")
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "channel_contact_generator")
	@SequenceGenerator(name="channel_contact_generator", sequenceName = "channel_contact_id_seq", allocationSize=1)
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
	
	
}
