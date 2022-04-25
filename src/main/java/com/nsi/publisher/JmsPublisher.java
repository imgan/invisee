package com.nsi.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.nsi.domain.core.UtTransactions;
import com.nsi.util.ConstantUtil;

@Component
public class JmsPublisher {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired 
	private JmsTemplate jmsTemplate;

	public void run(UtTransactions utTransactions) {
		logger.error("Sending a transaction.");
		try {
			// Post message to the message queue named "OrderTransactionQueue"
			jmsTemplate.convertAndSend(ConstantUtil.FEE_CALCULATION_QUEUE, utTransactions);
		} catch (Exception e) {
			logger.error("[FATAL]", e);
		}
		logger.error("end of a transaction.");
	}

}
