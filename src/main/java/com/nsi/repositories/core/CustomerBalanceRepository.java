package com.nsi.repositories.core;

import com.nsi.domain.core.CustomerBalance;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.UtProducts;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerBalanceRepository extends JpaRepository<CustomerBalance, Long> {

  List<CustomerBalance> findAllByInvAccountOrderByBalanceDateAsc(InvestmentAccounts accounts);

  List<CustomerBalance> findTop1ByUtProductAndInvAccountOrderByBalanceDateDesc(UtProducts products,
      InvestmentAccounts investmentAccounts);

  List<CustomerBalance> findByInvAccountOrderByBalanceDateDesc(
      InvestmentAccounts investmentAccounts, PageRequest pageRequest);

  @Query("select cb from CustomerBalance cb where cb.customer.id=?1 and cb.currentAmount>0 and cb.balanceDate=?2 and cb.invAccount.id=?3 and cb.utProduct.id=?4")
  CustomerBalance findCustomerBalanceWithCustomQuery(Long customer, Date tempDate, Long invAccount,
      Long utProduct);

  @Query("select distinct cb.invAccount from CustomerBalance cb where cb.customer.id =?1 and cb.currentAmount>0 ")
  List<InvestmentAccounts> findAllByCustomerAndCurrentAmountWithCustomQuery(Long customer);

  @Query("select distinct cb.invAccount from CustomerBalance cb where cb.customer.id =?1 and cb.currentAmount>0 and cb.invAccount.fundPackages.packageCode = ?2 ")
  List<InvestmentAccounts> findAllByCustomerAndCurrentAmountAndPackageCodeWithCustomQuery(
      Long customer, String packageCode);

  @Query("select max(cb.balanceDate) from CustomerBalance cb where cb.invAccount=?1 and cb.utProduct=?2")
  Date getMaxDatePerInvestmentAndProductWithQuery(InvestmentAccounts invest, UtProducts product);

  @Query("select sum(cb.currentAmount) from CustomerBalance cb where cb.invAccount=?1 and cb.balanceDate=?2")
  Double getTotalMarketValueWithQuery(InvestmentAccounts invest, Date balanceDate);

  CustomerBalance findByInvAccountAndUtProductAndBalanceDate(InvestmentAccounts invest, UtProducts utProducts, Date balanceDate);

  CustomerBalance findFirstByInvAccount_InvestmentAccountNoOrderByBalanceDateDesc(String invNo);

  @Query(value = "SELECT cb.* FROM customer_balance cb " +
          "JOIN (SELECT MAX(price_date) as price_date, products_id FROM ut_product_fund_prices GROUP BY products_id) fp ON (fp.price_date=cb.balance_date AND fp.products_id=cb.ut_product_id) " +
          "JOIN investment_accounts ia ON (ia.investment_account_id=cb.inv_account_id) " +
          "WHERE ia.investment_account_id=:invAcctId", nativeQuery = true)
  List<CustomerBalance> getLatestBalance(@Param("invAcctId") Long invAcctId);

  @Query(value = "SELECT cb.* FROM customer_balance cb " +
          "JOIN (SELECT MAX(price_date) as price_date, products_id FROM ut_product_fund_prices WHERE CAST(price_date as DATE)<=:balanceDate GROUP BY products_id) fp ON (fp.price_date=cb.balance_date AND cb.ut_product_id=fp.products_id) " +
          "WHERE cb.customer_id=:customerId AND cb.inv_account_id=:invId", nativeQuery = true)
  List<CustomerBalance> findAllByInvAccount_InvestmentAccountNoAndCustomerAndBalanceDate(@Param("invId") Long invId, @Param("customerId") Long kyc, @Param("balanceDate") Date balanceDate);
  List<CustomerBalance> findAllByInvAccountAndUtProductOrderByBalanceDateDesc(InvestmentAccounts ia, UtProducts products);

  @Query(value = "SELECT COUNT(DISTINCT balance_date) FROM customer_balance WHERE inv_account_id=:invAccountId", nativeQuery = true)
  Integer countDistinctBalanceDateByInvAccount(@Param("invAccountId") Long invAccountId);

  @Query(value = "SELECT SUM(current_amount) as current_amount, SUM(subscription_amount) as subscription_amount, balance_date, " +
          "SUM(redemption_amount) as redemption_amount FROM customer_balance " +
          "WHERE inv_account_id=:invAccountId " +
          "AND balance_date BETWEEN :startDate AND :endDate " +
          "GROUP BY balance_date ORDER BY balance_date ASC", nativeQuery = true)
  List<Object[]> getDataPerformanceInvestment(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("invAccountId") Long invAccountId);
}