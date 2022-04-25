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
@Table(name="score_composition")
public class ScoreComposition extends BaseDomain {

	private Long id;
	private Long compositionValue;
	private Score score;
	private String atCompositionId;
	private String atScoreId;
	private String atCompositionValue;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "score_composition_generator")
	@SequenceGenerator(name="score_composition_generator", sequenceName = "score_composition_composition_id_seq", allocationSize=1)
	@Column(name="composition_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="composition_value")
	public Long getCompositionValue() {
		return compositionValue;
	}
	public void setCompositionValue(Long compositionValue) {
		this.compositionValue = compositionValue;
	}
	
	@ManyToOne
	@JoinColumn(name="score_id")
	public Score getScore() {
		return score;
	}
	public void setScore(Score score) {
		this.score = score;
	}
	
	@Column(name="at_composition_id")
	public String getAtCompositionId() {
		return atCompositionId;
	}
	public void setAtCompositionId(String atCompositionId) {
		this.atCompositionId = atCompositionId;
	}
	
	@Column(name="at_score_id")
	public String getAtScoreId() {
		return atScoreId;
	}
	public void setAtScoreId(String atScoreId) {
		this.atScoreId = atScoreId;
	}
	
	@Column(name="at_composition_value")
	public String getAtCompositionValue() {
		return atCompositionValue;
	}
	public void setAtCompositionValue(String atCompositionValue) {
		this.atCompositionValue = atCompositionValue;
	}
	
	
}
