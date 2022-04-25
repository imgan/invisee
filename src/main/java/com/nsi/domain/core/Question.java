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
@Table(name = "question")
public class Question extends BaseDomain {

  private Long id;
  private String questionName;
  private String questionText;
  private Long weighted;
  private Long seq;
  private Long questionType;
  private Questionaires questionaires;
  private Date effectiveDateFrom;
  private Date effectiveDateTo;
  private String atQuestionId;
  private Long parentqId;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_generator")
  @SequenceGenerator(name = "question_generator", sequenceName = "question_question_id_seq", allocationSize = 1)
  @Column(name = "question_id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "question_name")
  public String getQuestionName() {
    return questionName;
  }

  public void setQuestionName(String questionName) {
    this.questionName = questionName;
  }

  @Column(name = "question_text", length = 2000)
  public String getQuestionText() {
    return questionText;
  }

  public void setQuestionText(String questionText) {
    this.questionText = questionText;
  }

  @Column(name = "weighted")
  public Long getWeighted() {
    return weighted;
  }

  public void setWeighted(Long weighted) {
    this.weighted = weighted;
  }

  @Column(name = "seq")
  public Long getSeq() {
    return seq;
  }

  public void setSeq(Long seq) {
    this.seq = seq;
  }

  @Column(name = "question_type")
  public Long getQuestionType() {
    return questionType;
  }

  public void setQuestionType(Long questionType) {
    this.questionType = questionType;
  }

  @ManyToOne
  @JoinColumn(name = "questionaires_id")
  public Questionaires getQuestionaires() {
    return questionaires;
  }

  public void setQuestionaires(Questionaires questionaires) {
    this.questionaires = questionaires;
  }

  @Column(name = "effective_date_from")
  public Date getEffectiveDateFrom() {
    return effectiveDateFrom;
  }

  public void setEffectiveDateFrom(Date effectiveDateFrom) {
    this.effectiveDateFrom = effectiveDateFrom;
  }

  @Column(name = "effective_date_to")
  public Date getEffectiveDateTo() {
    return effectiveDateTo;
  }

  public void setEffectiveDateTo(Date effectiveDateTo) {
    this.effectiveDateTo = effectiveDateTo;
  }

  @Column(name = "at_question_id")
  public String getAtQuestionId() {
    return atQuestionId;
  }

  public void setAtQuestionId(String atQuestionId) {
    this.atQuestionId = atQuestionId;
  }

  @Column(name = "parentq_id")
  public Long getParentqId() {
    return parentqId;
  }

  public void setParentqId(Long parentqId) {
    this.parentqId = parentqId;
  }
}
