package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.AttachFile;

public interface AttachFileRepository extends JpaRepository<AttachFile, Long> {

	public AttachFile findByKey(String key);
}
