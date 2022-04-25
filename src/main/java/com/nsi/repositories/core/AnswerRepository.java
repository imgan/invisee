package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.Answer;
import com.nsi.domain.core.Question;

public interface AnswerRepository extends JpaRepository<Answer, Long>{

	public List<Answer> findAllByQuestionOrderByAnswerNameAsc(Question question);
	public Answer findByAnswerNameAndQuestion(String code, Question question);
}
