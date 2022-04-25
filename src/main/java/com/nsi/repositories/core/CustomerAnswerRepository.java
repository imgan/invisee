package com.nsi.repositories.core;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nsi.domain.core.CustomerAnswer;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.Question;
import com.nsi.domain.core.Questionaires;
import com.nsi.domain.core.User;

public interface CustomerAnswerRepository extends JpaRepository<CustomerAnswer, Long> {

  @Query("select c from CustomerAnswer c where c.kyc =?1 and c.question =?2 order by c.answer.answerName asc ")
  public List<CustomerAnswer> findAllByKycAndQuestionWithQuery(Kyc kyc, Question question);

  @Query("select count(distinct question) FROM CustomerAnswer WHERE kyc=?1 AND question IN (?2)")
  public Integer findByQuestionWithQuery(Kyc kyc, List<Question> questions);

  @Query("select c FROM CustomerAnswer c WHERE c.kyc=?1 AND c.question IN (?2)")
  public List<CustomerAnswer> findAllByQuestionWithQuery(Kyc kyc, List<Question> questions);

  @Query("select c from CustomerAnswer c join c.question q where c.kyc.account=?1 and q.questionaires=?2")
  public List<CustomerAnswer> findAllByUserAndQuestionariesWithQuery(User user,
      Questionaires questionaires);

  @Query("select distinct c.question from CustomerAnswer c join c.question q where c.kyc.account=?1 and q.questionaires=?2")
  public List<Question> findAllQuestionByUserAndQuestionariesWithQuery(User user,
      Questionaires questionaires);

  @Query("select c from CustomerAnswer c where c.kyc.account=?1 and c.question=?2")
  public List<CustomerAnswer> findAllByUserAndQuestionWithQuery(User user, Question question);

  @Modifying
  @Transactional
  @Query("delete from CustomerAnswer c where c.kyc=?1 and c.question in (select q from Question q where q.questionaires=?2)")
  public void deleteByKycAndQuestionaries(Kyc kyc, Questionaires questionaires);

  @Query("delete from CustomerAnswer c where c.kyc=?1 and c.question =?2")
  public void deleteByKycAndQuestionaries(Kyc kyc, Question question);

  public List<CustomerAnswer> findAllByQuestionAndKycOrderByCreatedDateAsc(Question question,
      Kyc kyc);

  @Query("from CustomerAnswer custAnswer where custAnswer.kyc=:kyc and custAnswer.question in "
      + "(from Question q where q.questionaires=:questionaires and "
      + "CURRENT_TIMESTAMP between q.effectiveDateFrom and q.effectiveDateTo)")
  public List<CustomerAnswer> findAllByKycAndQuestionaries(@Param("kyc") Kyc kyc,
      @Param("questionaires") Questionaires questionaires);

  public List<CustomerAnswer> findAllByKycAndQuestionOrderByCreatedDateAsc(Kyc kyc,
      Question question);
}
