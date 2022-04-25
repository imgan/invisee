package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Date;
import com.nsi.domain.core.Question;
import com.nsi.domain.core.Questionaires;

public interface QuestionRepository extends JpaRepository<Question, Long> {

  Question findFirstByQuestionairesAndIdOrderByIdDesc(Questionaires questionaires, Long questionId);

  @Query("select q from Question q where q.questionaires = ?1 AND cast(q.effectiveDateFrom as date) <= CURRENT_DATE AND CURRENT_DATE >= cast(q.effectiveDateFrom as date) AND cast(q.effectiveDateTo as date) >= CURRENT_DATE AND CURRENT_DATE <= cast(q.effectiveDateTo as date) order by q.questionName asc ")
  public List<Question> findAllQuestionByQuestionairesWithQuery(Questionaires questionaires);

  public List<Question> findAllByQuestionairesOrderBySeqAsc(Questionaires questionaires);
  public List<Question> findAllByQuestionairesAndParentqIdIsNullOrderBySeqAsc(Questionaires questionaires);

  public Long countByQuestionaires(Questionaires questionaires);

  public Question findFirstByQuestionairesAndQuestionNameOrderByIdDesc(Questionaires questionaires,
      String questionName);

  @Query("select q from Question q where q.questionaires = ?1 and q.questionName= ?2 and ?3 between q.effectiveDateFrom AND q.effectiveDateTo")
  public Question findByQuestionairesAndQuestionName(Questionaires questionaires,
      String questionName, Date currentTime);
}
