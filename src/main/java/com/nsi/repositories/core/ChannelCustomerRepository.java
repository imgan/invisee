package com.nsi.repositories.core;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsi.domain.core.ChannelCustomer;

public interface ChannelCustomerRepository extends JpaRepository<ChannelCustomer, Long> {

	public ChannelCustomer findByChannelCustomer(String customer);
}
