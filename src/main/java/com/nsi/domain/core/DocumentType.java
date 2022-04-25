package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="document_type")
public class DocumentType extends BaseNewDomain {

	private Long id;
	private String code;
	private String type;
	private String description;
	private Integer version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_type_generator")
	@SequenceGenerator(name="document_type_generator", sequenceName = "document_type_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="code", length=10)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name="type", length=30)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="version")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
}
