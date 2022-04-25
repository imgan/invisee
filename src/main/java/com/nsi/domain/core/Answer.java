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
@Table(name="answer")
public class Answer extends BaseDomain {

	private Long id;
	private String answerName;
	private String answerText;
	private Long score;
	private Long seq;
	private Long stat;
	private String forcingCategory;
	private Question question;
	private String atAnswerId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_generator")
	@SequenceGenerator(name="answer_generator", sequenceName = "answer_answer_id_seq", allocationSize=1)
	@Column(name="answer_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="answer_name", length=250)
	public String getAnswerName() {
		return answerName;
	}
	public void setAnswerName(String answerName) {
		this.answerName = answerName;
	}
	
	@Column(name="answer_text", length=2000)
	public String getAnswerText() {
		return answerText;
	}
	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
	
	@Column(name="score")
	public Long getScore() {
		return score;
	}
	public void setScore(Long score) {
		this.score = score;
	}
	
	@Column(name="seq")
	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	
	@Column(name="stat")
	public Long getStat() {
		return stat;
	}
	public void setStat(Long stat) {
		this.stat = stat;
	}
	
	@Column(name="forcing_category", length=30)
	public String getForcingCategory() {
		return forcingCategory;
	}
	public void setForcingCategory(String forcingCategory) {
		this.forcingCategory = forcingCategory;
	}
	
	@ManyToOne
	@JoinColumn(name="question_id")
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	
	@Column(name="at_answer_id")
	public String getAtAnswerId() {
		return atAnswerId;
	}
	public void setAtAnswerId(String atAnswerId) {
		this.atAnswerId = atAnswerId;
	}
	
	
}
