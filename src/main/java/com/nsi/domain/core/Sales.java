package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="sales")
public class Sales {

	private Long id;
	private String salesCode;
	private String salesName;
	private String atSalesId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_generator")
	@SequenceGenerator(name="sales_generator", sequenceName = "sales_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="sales_code")
	public String getSalesCode() {
		return salesCode;
	}
	public void setSalesCode(String salesCode) {
		this.salesCode = salesCode;
	}
	
	@Column(name="sales_name")
	public String getSalesName() {
		return salesName;
	}
	public void setSalesName(String salesName) {
		this.salesName = salesName;
	}
	
	@Column(name="at_sales_id")
	public String getAtSalesId() {
		return atSalesId;
	}
	public void setAtSalesId(String atSalesId) {
		this.atSalesId = atSalesId;
	}
	
	
}
