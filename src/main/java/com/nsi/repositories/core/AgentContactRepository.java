package com.nsi.repositories.core;

import com.nsi.domain.core.AgentContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentContactRepository extends JpaRepository<AgentContact, Long> {
    public AgentContact findByAgent_CodeAndContact_ContactType_Code(String agentCode, String contactTypeCode);
}
