package com.nsi.services;

import java.util.Date;
import java.util.Map;

public interface UtTransactionsAgentFeeService {

	public Map<String, Object> findAllHistoryByAgentAndPeriod(Long agentId, Date fromDate, Date toDate, int offset, int limit) throws Exception;
}
