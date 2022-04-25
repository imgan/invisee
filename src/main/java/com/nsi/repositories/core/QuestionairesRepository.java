package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.Questionaires;

public interface QuestionairesRepository extends JpaRepository<Questionaires, Long> {

	public Questionaires findByQuestionnaireCategory(Long id);
	public Questionaires findByQuestionnaireName(String fatcaDefault);
}
