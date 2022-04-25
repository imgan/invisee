package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="faq")
public class FAQ extends BaseDomain {

	private Long id;
	private String faqNumber;
	private String question;
	private String answer;
	private String faqStatus;
	private String atFaqId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "faq_generator")
	@SequenceGenerator(name="faq_generator", sequenceName = "faq_faq_id_seq", allocationSize=1)
	@Column(name="faq_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="faq_number", length=50)
	public String getFaqNumber() {
		return faqNumber;
	}
	public void setFaqNumber(String faqNumber) {
		this.faqNumber = faqNumber;
	}
	
	@Column(name="question", length=2000)
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	
	@Column(name="answer", length=2000)
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	@Column(name="faq_status")
	public String getFaqStatus() {
		return faqStatus;
	}
	public void setFaqStatus(String faqStatus) {
		this.faqStatus = faqStatus;
	}
	
	@Column(name="at_faq_id")
	public String getAtFaqId() {
		return atFaqId;
	}
	public void setAtFaqId(String atFaqId) {
		this.atFaqId = atFaqId;
	}
	
	
}
