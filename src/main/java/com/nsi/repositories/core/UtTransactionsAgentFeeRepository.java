package com.nsi.repositories.core;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.UtTransactions;
import com.nsi.domain.core.UtTransactionsAgentFee;

public interface UtTransactionsAgentFeeRepository extends JpaRepository<UtTransactionsAgentFee, Long>{
	public List<UtTransactionsAgentFee> findByUtTransactions(UtTransactions utTransactions);
	public Page<UtTransactionsAgentFee> findAllByAgent(Agent agent, Pageable pageable);
	
	
	/**
	 * the object just a dummy to store Aggregate result
	 * @param agentId
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	@Query(nativeQuery = true, value = "SELECT -1 as id, -1 AS ut_transactions_id, MAX(created_date) AS created_date, 'SYSTEM' AS created_by, MAX(updated_date) AS updated_date, 'SYSTEM' AS updated_by, NULL AS is_direct_fee, '' AS order_no, SUM(fee_amount) as fee_amount, SUM(order_amount) as order_amount, AVG(fee_percentage) AS fee_percentage, MAX(transaction_date) AS transaction_date, agent_id "
			+ "FROM ut_transactions_agent_fee "
			+ "WHERE agent_id = ?1 AND transaction_date >= ?2 AND transaction_date < ?3 "
			+ "GROUP BY agent_id")
	public UtTransactionsAgentFee getSummaryByAgentIdAndPeriod(Long agentId, Date fromDate, Date toDate);
	
	/**
	 * the object just a dummy to store Aggregate result
	 * @param agentId
	 * @param fromDate
	 * @param toDate
	 * @return sum(feeAmount)
	 */
	@Query(nativeQuery = true, value = "SELECT -1 as id, -1 AS ut_transactions_id, MAX(created_date) AS created_date, 'SYSTEM' AS created_by, MAX(updated_date) AS updated_date, 'SYSTEM' AS updated_by, NULL AS is_direct_fee, '' AS order_no, SUM(fee_amount) as fee_amount, SUM(order_amount) as order_amount, AVG(fee_percentage) AS fee_percentage, MAX(transaction_date) AS transaction_date, agent_id "
			+ "FROM ut_transactions_agent_fee "
			+ "WHERE agent_id = ?1 "
			+ "GROUP BY agent_id")
	public UtTransactionsAgentFee getSummaryByAgentId(Long agentId);
}
