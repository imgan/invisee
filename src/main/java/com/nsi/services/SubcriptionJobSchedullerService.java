package com.nsi.services;

import java.util.Map;


public interface SubcriptionJobSchedullerService {

	public Map saveJob(String orderNo, String status, String dateCreated, String executeDate, String message, String payType);
	public Object createSubscriptionIntegrationAvantrade(String orderNo);
	public Object createInvestmentAccount(String orderNo);
}
