package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsi.domain.core.Channel;
import com.nsi.domain.core.ChannelCredential;

public interface ChannelCredentialRepository extends JpaRepository<ChannelCredential, Long>{

	public ChannelCredential findByChannelAndRowStatus(Channel channel, Boolean rowStatus);
}
