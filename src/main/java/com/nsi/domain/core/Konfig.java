package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="konfig")
public class Konfig {

	private Long id;
	private Integer version;
	private String key;
	private String value;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "konfig_generator")
	@SequenceGenerator(name="konfig_generator", sequenceName = "konfig_configuration_id_seq", allocationSize=1)
	@Column(name="configuration_id")
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
	
	@Column(name="_key")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@Column(name="_value")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
