package com.nsi.listener;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.MstFeeAgent;
import com.nsi.domain.core.UtTransactions;
import com.nsi.domain.core.UtTransactionsAgentFee;
import com.nsi.enumeration.AgentRoleEnum;
import com.nsi.enumeration.TransactionStatusEnumeration;
import com.nsi.exception.ValidationException;
import com.nsi.repositories.core.UtTransactionsAgentFeeRepository;
import com.nsi.util.ConstantUtil;

@Component
@EnableJms
public class CommissionFeeAllocatedListener {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UtTransactionsAgentFeeRepository agentFeeRepository;

	@JmsListener(destination = ConstantUtil.FEE_CALCULATION_QUEUE, containerFactory = "myFactory")
	public void listener(UtTransactions utTransactions){
		logger.error("CommissionFeeAllocatedListener starting ....");
		try {
			if(utTransactions == null) {
				throw new ValidationException("UtTransactions is null");
			}else if (!utTransactions.getTrxStatus().equalsIgnoreCase(TransactionStatusEnumeration.ALLOCATED.getKey())) {
				throw new ValidationException("UtTransactions trxStatus is " + utTransactions.getTrxStatus()+ " | id : " + utTransactions.getId());
			}
			Agent agent = utTransactions.getKycId().getAccount().getAgent();
			MstFeeAgent feeAgent = agent.getAgentFee();
			UtTransactionsAgentFee existing = new UtTransactionsAgentFee();
			existing.setUtTransactions(utTransactions);
			existing.setAgent(agent);
			List<UtTransactionsAgentFee> listOfTrxAgentFee = agentFeeRepository.findByUtTransactions(utTransactions);
			if(listOfTrxAgentFee != null && listOfTrxAgentFee.size() > 0) {
				throw new ValidationException("UtTransactionsAgentFee has existed");
			}
			if(feeAgent != null) {
				Double feeForAgent = utTransactions.getOrderAmount() * agent.getAgentFee().getDirectFee();
				UtTransactionsAgentFee agentFee = new UtTransactionsAgentFee();
				agentFee.setAgent(agent);
				agentFee.setCreatedBy("system");
				agentFee.setCreatedDate(new Date());
				agentFee.setFeeAmount(feeForAgent);
				agentFee.setFeePercentage(agent.getAgentFee().getDirectFee()); 
				agentFee.setOrderAmount(utTransactions.getOrderAmount());
				agentFee.setIsDirectFee(true);
				agentFee.setOrderNo(utTransactions.getOrderNo());
				agentFee.setUtTransactions(utTransactions);
				agentFee.setTransactionDate(utTransactions.getTransactionDate());
				agentFeeRepository.save(agentFee);

				if(feeAgent.getRole().equalsIgnoreCase(AgentRoleEnum.AGENT.getValue())) {
					// calculate indirect fee for spv
					Double feeForSpv = utTransactions.getOrderAmount() * agent.getSpv().getAgentFee().getIndirectFee();
					UtTransactionsAgentFee spvFee = new UtTransactionsAgentFee();
					spvFee.setAgent(agent.getSpv());
					spvFee.setCreatedBy("system");
					spvFee.setCreatedDate(new Date());
					spvFee.setFeeAmount(feeForSpv);
					spvFee.setFeePercentage(agent.getSpv().getAgentFee().getIndirectFee());
					spvFee.setOrderAmount(utTransactions.getOrderAmount());
					spvFee.setIsDirectFee(false);
					spvFee.setOrderNo(utTransactions.getOrderNo());
					spvFee.setUtTransactions(utTransactions);
					spvFee.setTransactionDate(utTransactions.getTransactionDate());
					agentFeeRepository.save(spvFee);
				}
			}else {
				throw new ValidationException("MstFeeAgent is not set for agent " + agent.getId());
			}
		} catch (ValidationException ve) {
			logger.error(ve.getMessage());
		} catch (Exception e) {
			logger.error("[FATAL]", e);
		}
		logger.error("CommissionFeeAllocatedListener ends ....");
	}
}
