package com.nsi.domain.core;

import java.util.Date;
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
@Table(name="score")
public class Score extends BaseDomain {

	private Long id;
	private String scoreCode;
	private String scoreName;
	private String description;
	private Long minScore;
	private Long maxScore;
	private Long stat;
	private Questionaires questionaires;
	private Date effectiveDateFrom;
	private Date effectiveDateTo ;   
	private String atScoreId   ; 
	private Float expectedReturn;
	private Float minRange;
	private Float maxRange;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "score_generator")
	@SequenceGenerator(name="score_generator", sequenceName = "score_score_id_seq", allocationSize=1)
	@Column(name="score_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="score_code", length=15)
	public String getScoreCode() {
		return scoreCode;
	}
	public void setScoreCode(String scoreCode) {
		this.scoreCode = scoreCode;
	}
	
	@Column(name="score_name", length=50)
	public String getScoreName() {
		return scoreName;
	}
	public void setScoreName(String scoreName) {
		this.scoreName = scoreName;
	}
	
	@Column(name="description",length=500)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="min_score")
	public Long getMinScore() {
		return minScore;
	}
	public void setMinScore(Long minScore) {
		this.minScore = minScore;
	}
	
	@Column(name="max_score")
	public Long getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(Long maxScore) {
		this.maxScore = maxScore;
	}
	
	@Column(name="stat")
	public Long getStat() {
		return stat;
	}
	public void setStat(Long stat) {
		this.stat = stat;
	}
	
	@ManyToOne
	@JoinColumn(name="questionaires_id")
	public Questionaires getQuestionaires() {
		return questionaires;
	}
	public void setQuestionaires(Questionaires questionaires) {
		this.questionaires = questionaires;
	}
	
	@Column(name="effective_date_from")
	public Date getEffectiveDateFrom() {
		return effectiveDateFrom;
	}
	public void setEffectiveDateFrom(Date effectiveDateFrom) {
		this.effectiveDateFrom = effectiveDateFrom;
	}
	
	@Column(name="effective_date_to")
	public Date getEffectiveDateTo() {
		return effectiveDateTo;
	}
	public void setEffectiveDateTo(Date effectiveDateTo) {
		this.effectiveDateTo = effectiveDateTo;
	}
	
	@Column(name="at_score_id")
	public String getAtScoreId() {
		return atScoreId;
	}
	public void setAtScoreId(String atScoreId) {
		this.atScoreId = atScoreId;
	}
	
	@Column(name="expected_return")
	public Float getExpectedReturn() {
		return expectedReturn;
	}
	public void setExpectedReturn(Float expectedReturn) {
		this.expectedReturn = expectedReturn;
	}
	
	@Column(name="min_range")
	public Float getMinRange() {
		return minRange;
	}
	public void setMinRange(Float minRange) {
		this.minRange = minRange;
	}
	
	@Column(name="max_range")
	public Float getMaxRange() {
		return maxRange;
	}
	public void setMaxRange(Float maxRange) {
		this.maxRange = maxRange;
	}
	
	
}
