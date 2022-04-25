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
@Table(name="transaction_document")
public class TransactionDocument {

	private Long id;
	private CustomerDocument customerDocument;
	private String orderNo;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_document_generator")
	@SequenceGenerator(name="transaction_document_generator", sequenceName = "transaction_document_id_seq", allocationSize=1)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="customer_document_id")
	public CustomerDocument getCustomerDocument() {
		return customerDocument;
	}
	public void setCustomerDocument(CustomerDocument customerDocument) {
		this.customerDocument = customerDocument;
	}
	
	@Column(name="order_no")
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	
}
