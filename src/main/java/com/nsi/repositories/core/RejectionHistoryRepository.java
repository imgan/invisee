package com.nsi.repositories.core;

import com.nsi.domain.core.RejectionHistory;
import com.nsi.domain.core.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RejectionHistoryRepository extends JpaRepository<RejectionHistory, Long> {
  RejectionHistory findFirstByRejectedUserIdOrderByCreatedOnDesc(User user);
}
