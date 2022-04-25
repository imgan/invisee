package com.nsi.domain.core;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import java.util.Date;

@Entity
@Table(name="customer_answer")
public class CustomerAnswer {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_ANSW_SEQ")
    @SequenceGenerator(sequenceName = "customer_answer_kyc_answer_id_seq", allocationSize = 1, name = "CUST_ANSW_SEQ")
	@Column(name="kyc_answer_id")
	private Long id;
	
	@Column(name="version")
	private Integer version;
	
	@ManyToOne
	@JoinColumn(name="kyc_id")
	private Kyc kyc;
	
	@ManyToOne
	@JoinColumn(name="question_id")
	private Question question;
	
	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="answer_id")
	private Answer answer;
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="answer_note")
	private String answerNote;

	@Column(name="created_by")
	private String createdBy;

	@Column(name="created_date")
	private Date createdDate;

	@Column(name="updated_by")
	private String updatedBy;

	@Column(name="updated_date")
	private Date updatedDate;
	
	
	
	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}

	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	public Kyc getKyc() {
		return kyc;
	}
	public void setKyc(Kyc kyc) {
		this.kyc = kyc;
	}

	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}

	public Answer getAnswer() {
		return answer;
	}
	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public String getAnswerNote() {
		return answerNote;
	}
	public void setAnswerNote(String answerNote) {
		this.answerNote = answerNote;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
}