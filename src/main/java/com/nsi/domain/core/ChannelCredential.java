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
@Table(name="channel_credential")
public class ChannelCredential extends BaseNewDomain {

	private Long id;
	private Channel channel;
	private String accessKey;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "channel_credential_generator")
	@SequenceGenerator(name="channel_credential_generator", sequenceName = "channel_credential_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="channel_id")
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	@Column(name="access_key")
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	
}
