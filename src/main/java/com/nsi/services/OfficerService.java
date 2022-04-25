package com.nsi.services;

import java.util.Map;

import com.nsi.domain.core.User;

public interface OfficerService {

	public Map detailChannel(User user, Map map);
	public Map monitorCustomer(User user);
	public Map listChannel(User user, Map map);
}
