package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="lookup_header")
public class LookupHeader extends BaseDomain {

	private Long id;
	private String category;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lookup_header_generator")
	@SequenceGenerator(name="lookup_header_generator", sequenceName = "lookup_header_category_id_seq", allocationSize=1)
	@Column(name="category_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="category")
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
