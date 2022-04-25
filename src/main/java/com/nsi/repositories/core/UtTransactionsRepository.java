package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.nsi.domain.core.Channel;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.UtTransactionType;
import com.nsi.domain.core.UtTransactions;

@Transactional
public interface UtTransactionsRepository extends JpaRepository<UtTransactions, Long> {

    UtTransactions findByOrderNoAndAtTrxNo(String orderNo, String trxNo);

    UtTransactions findByOrderNoAndKycId(String orderNo, Kyc kyc);

    List<UtTransactions> findAllByChannelOrderIdAndKycId(String channelOrderId, Kyc kyc);

    @Query(value = "SELECT investement_account_id FROM ut_transactions WHERE trx_status='STL' AND kyc_id_id=:kycId AND transaction_type_id=(SELECT transaction_type_id FROM ut_transaction_type WHERE trx_code='SUBCR') LIMIT 1", nativeQuery = true)
    Long checkTransactionOrdered(@Param("kycId") Long kycId);

    public int countByTrxNoLike(String pref);

    @Query("select count(ut_t) from UtTransactions ut_t where ut_t.trxNo =?1")
    public int countByTrxNoWithQuery(String trxNo);

    @Modifying(clearAutomatically = true)
    @Query("update UtTransactions ut set ut.priceDate=(select min(x.priceDate) from UtTransactions x group by x.orderNo having x.orderNo=:orderNo) where ut.orderNo=:orderNo")
    public void markEntryAsRead(@Param("orderNo") String orderNo);

    public List<UtTransactions> findAllByOrderNo(String orderNo);

    public List<UtTransactions> findAllByOrderNoAndKycId(String orderNo, Kyc kyc);

    public List<UtTransactions> findAllByOrderNoAndKycIdOrderByIdDesc(String orderNo, Kyc kyc);

    @Query("select count(ut_t) from UtTransactions ut_t inner join ut_t.kycId kyc inner join kyc.account _user inner join _user.agent _ag where ut_t.trxType in (1,3) and _ag.channel=?1 and ut_t.trxStatus='ALL' ")
    public Long countSubsAndTopupWithQuery(Channel channel);

    @Query("select count(ut_t) from UtTransactions ut_t inner join ut_t.kycId kyc inner join kyc.account _user inner join _user.agent _ag where ut_t.trxType=2 and _ag.channel=?1 and ut_t.trxStatus='STL' ")
    public Long countRedemptionWithQuery(Channel channel);

    @Query("select sum(ut_t.orderAmount) from UtTransactions ut_t inner join ut_t.kycId kyc inner join kyc.account _user inner join _user.agent _ag where ut_t.trxType in (1,3) and _ag.channel=?1 and ut_t.trxStatus='ALL' ")
    public Double sumSubsAndTopupWithQuery(Channel channel);

    @Query("select sum(ut_t.orderAmount) from UtTransactions ut_t inner join ut_t.kycId kyc inner join kyc.account _user inner join _user.agent _ag where ut_t.trxType=2 and _ag.channel=?1 and ut_t.trxStatus='STL' ")
    public Double sumRedemptionWithQuery(Channel channel);

    @Query("select count(ut_t) from UtTransactions ut_t where ut_t.trxType in (1,3) and ut_t.trxStatus='ALL' and ut_t.kycId=?1 ")
    public Long countSubsTopByKycWithQuery(Kyc kyc);

    @Query("select count(ut_t) from UtTransactions ut_t where ut_t.trxType = 2 and ut_t.trxStatus='STL' and ut_t.kycId=?1 ")
    public Long countRedemptionByKycWithQuery(Kyc kyc);

    @Query("select sum(ut_t.orderAmount) from UtTransactions ut_t where ut_t.trxType in (1,3) and ut_t.trxStatus='ALL' and ut_t.kycId=?1 ")
    public Double sumSubsAndTopupByKycWithQuery(Kyc kyc);

    @Query("select sum(ut_t.orderAmount) from UtTransactions ut_t where ut_t.trxType=2 and ut_t.trxStatus='STL' and ut_t.kycId=?1 ")
    public Double sumRedemptionByKycWithQuery(Kyc kyc);

    @Query("select count(ut_t) from UtTransactions ut_t where ut_t.trxType in (1,3) and ut_t.trxStatus='ALL' and ut_t.kycId.account.agent.channel.code <> 'INVPTL'")
    public Long countChannelCustomerSubscription();

    @Query("select count(ut_t) from UtTransactions ut_t where ut_t.trxType = 2 and ut_t.trxStatus='STL' and ut_t.kycId.account.agent.channel.code <> 'INVPTL'")
    public Long countChannelCustomerRedemption();

    @Query("select sum(ut_t.orderAmount) from UtTransactions ut_t where ut_t.trxType in (1,3) and ut_t.trxStatus='ALL' and ut_t.kycId.account.agent.channel.code <> 'INVPTL' ")
    public Double channelAmountSubscription();

    @Query("select sum(ut_t.orderAmount) from UtTransactions ut_t where ut_t.trxType=2 and ut_t.trxStatus='STL' and ut_t.kycId.account.agent.channel.code <> 'INVPTL' ")
    public Double channelAmountRedemption();

    @Query("select distinct trx.investementAccount from UtTransactions trx where trx.orderNo=?1 and (trx.transactionType=?2 or trx.transactionType=?3)")
    public List<InvestmentAccounts> getInvestAccountByOrderNoAndTrxTypeWithQuery(String orderNo, UtTransactionType trxType, UtTransactionType topupTrxType);

    public List<UtTransactions> findAllByInvestementAccount(InvestmentAccounts invest);

    public List<UtTransactions> findAllByInvestementAccountAndTransactionTypeAndTrxStatus(InvestmentAccounts invest, UtTransactionType transactionType, String trxStatus);

    @Query(value = "SELECT NEXTVAl(?1)", nativeQuery = true)
    Long getNextSeriesId(String seq_orderno);

    @Query("FROM UtTransactions trx WHERE trx.investementAccount.id=:invAcctId AND trx.trxStatus='ORD' AND trx.trxType=2")
    List<UtTransactions> getPendingTrxRedemp(@Param("invAcctId") Long invAcctId);

    @Query("FROM UtTransactions trx WHERE trx.investementAccount.id=:invAcctId AND trx.trxStatus in ('ORD', 'STL') AND trx.trxType=3")
    List<UtTransactions> getPendingTrxTopUp(@Param("invAcctId") Long invAcctId);
}
