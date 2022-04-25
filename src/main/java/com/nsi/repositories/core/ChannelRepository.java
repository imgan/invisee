package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

	public Channel findByCodeAndRowStatus(String code, Boolean rowStatus);
	
	//TODO: List Channel by filter ****
	public List<Channel> findAllByRowStatus(Boolean rowStatus); //GET ALL
	public List<Channel> findAllByCodeContainingIgnoreCase(String code);
	public List<Channel> findAllByNameContainingIgnoreCase(String name);
	public List<Channel> findAllByCodeContainingIgnoreCaseAndNameContainingIgnoreCase(String code, String name);
	
}
