package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="questionaires")
public class Questionaires extends BaseDomain {

	private Long id;
	private String questionnaireName;
	private Long questionnaireCategory;
	private String atQuestionnaireId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questionaires_generator")
	@SequenceGenerator(name="questionaires_generator", sequenceName = "questionaires_questionnaire_id_seq", allocationSize=1)
	@Column(name="questionnaire_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="questionnaire_name", length=60)
	public String getQuestionnaireName() {
		return questionnaireName;
	}
	public void setQuestionnaireName(String questionnaireName) {
		this.questionnaireName = questionnaireName;
	}
	
	@Column(name="questionnaire_category")
	public Long getQuestionnaireCategory() {
		return questionnaireCategory;
	}
	public void setQuestionnaireCategory(Long questionnaireCategory) {
		this.questionnaireCategory = questionnaireCategory;
	}
	
	
	@Column(name="at_questionnaire_id")
	public String getAtQuestionnaireId() {
		return atQuestionnaireId;
	}
	public void setAtQuestionnaireId(String atQuestionnaireId) {
		this.atQuestionnaireId = atQuestionnaireId;
	}
	
	
}
