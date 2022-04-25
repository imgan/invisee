package com.nsi.services.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsi.domain.core.UtTransactions;
import com.nsi.dto.UtTransactionsAgentFeeDto;
import com.nsi.repositories.core.UtTransactionsRepository;
import com.nsi.services.UtTransactionsAgentFeeService;
import com.nsi.util.DateTimeUtil;

@SuppressWarnings("unchecked")
@Service
public class UtTransactionsAgentFeeServiceImpl implements UtTransactionsAgentFeeService{

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private UtTransactionsRepository utTransactionsRepository;

	@Override
	public Map<String, Object> findAllHistoryByAgentAndPeriod(Long agentId, Date fromDate, Date toDate, int offset, int limit) throws Exception{
		Map<String, Object> map = new HashMap<>();
		String sqlSelect = "SELECT AF.*, count(AF.*) OVER() AS full_count, T.trx_type "
				+ "FROM ut_transactions_agent_fee AF "
				+ "LEFT JOIN ut_transactions T ON AF.ut_transactions_id = T.trx_id "
				+ "WHERE AF.agent_id = :agentId ";
		StringBuffer sb = new StringBuffer() ; 
		sb.append(sqlSelect);
		if(fromDate != null) {
			sb.append("AND AF.transaction_date >= :fromDate ");
		}
		if(toDate != null) {
			sb.append("AND AF.transaction_date < :toDate ");
		}
		sb.append("ORDER BY AF.transaction_date LIMIT :limit OFFSET :offset ");
		Query query = entityManager.createNativeQuery(sb.toString());
		if(fromDate != null) {
			query.setParameter("fromDate", fromDate);
		}
		if(toDate != null) {
			query.setParameter("toDate", toDate);
		}
		query.setParameter("agentId", agentId).setParameter("offset", offset).setParameter("limit", limit);
		List<Object[]> list = query.getResultList();
		List<UtTransactionsAgentFeeDto> utTransactionsAgentFees = new ArrayList<>();
		double totalPage = 0;
		for (Object[] obj : list) {
			UtTransactionsAgentFeeDto fee = new UtTransactionsAgentFeeDto();
			UtTransactions utTransactions = utTransactionsRepository.findOne(Long.valueOf(obj[1].toString()));
			fee.setAgentName(utTransactions.getKycId().getAccount().getAgent().getName());
			fee.setFeeCommission(new BigDecimal(obj[5].toString()));
			fee.setOrderNumber(obj[9].toString());
			fee.setProductName(utTransactions.getProductId().getProductName());
			if(obj[12] != null) {
				fee.setTransactionDate(DateTimeUtil.convertStringToDateCustomized(obj[12].toString(), DateTimeUtil.DATE_TIME_MCW));				
			}
			fee.setTransactionAmount(new BigDecimal(obj[8].toString()));
			fee.setTransactionType(utTransactions.getTransactionType().getTrxName());
			fee.setFeeType(Boolean.valueOf(obj[7].toString()) ? "Direct" : "Indirect");
			fee.setFeePercentage(new BigDecimal(Double.valueOf(obj[6].toString()), MathContext.DECIMAL64).stripTrailingZeros());
			utTransactionsAgentFees.add(fee);
		}
		map.put("list", utTransactionsAgentFees);
		map.put("totalPages", (int) totalPage);
		return map;
	}

}
