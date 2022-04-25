package com.nsi.repositories.core;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.Channel;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KycRepository extends JpaRepository<Kyc, Long> {

  Kyc findByAccount_UsernameAndAccount_Agent_CodeIgnoreCase(String username, String agentCode);

  Kyc findByEmailAndAccount_Agent_CodeIgnoreCase(String username, String agentCode);

  Kyc findByAccount(User user);

  Kyc findByEmail(String email);

  Kyc findByPortalcif(String cif);

  //TODO: Filter channel/detail_customer by Group "AGENT"
  List<Kyc> findAllByAccountAgentAndEmailAndMobileNumber(Agent agent, String email,
      String mobileNumber);

  List<Kyc> findAllByAccountAgentAndEmail(Agent agent, String email);

  List<Kyc> findAllByAccountAgentAndMobileNumber(Agent agent, String mobileNumber);

  List<Kyc> findAllByAccount_Agent(Agent agent);
  
  Page<Kyc> findAllByAccount_Agent(Agent agent, Pageable pageable);

  @Query("select kyc from Kyc kyc where kyc.account.agent in (?1)")
  Page<Kyc> findAllByAccount_AgentInList(List<Agent> agent, Pageable pageable);
  
  List<Kyc> findAllByAccount_AgentOrderByIdDesc(Agent agent);

  @Query("select kyc from Kyc kyc where kyc.account.agent=?1 and concat(trim(lower(kyc.firstName)), trim(lower(kyc.middleName)),trim(lower(kyc.lastName))) like %?2%")
  List<Kyc> findAllByAgentAndCustomerName(Agent agent, String customerName);

  List<Kyc> findAllByAccount_AgentAndAccount_EmailContainingIgnoreCase(Agent agent,
      String email);

  @Query("select kyc from Kyc kyc where kyc.account.agent=?1 and concat(trim(lower(kyc.firstName)), trim(lower(kyc.middleName)),trim(lower(kyc.lastName))) like %?2% and kyc.email like %?3% ")
  List<Kyc> findAllByAgentAndEmailAndCustomerName(Agent agent, String name, String email);

  //TODO: Filter channel/detail_customer by Group "AGENT_DEFAULT"
  List<Kyc> findAllByAccount_Agent_Channel(Channel channel);

  @Query("select kyc from Kyc kyc where kyc.account.agent.channel=?1 and concat(trim(lower(kyc.firstName)), trim(lower(kyc.middleName)),trim(lower(kyc.lastName))) like %?2%")
  List<Kyc> findAllByChannelAndCustomerName(Channel channel, String name);

  List<Kyc> findAllByAccount_Agent_ChannelAndAccount_EmailContainingIgnoreCase(
      Channel channel, String email);

  @Query("select kyc from Kyc kyc where kyc.account.agent.channel=?1 and concat(trim(lower(kyc.firstName)), trim(lower(kyc.middleName)),trim(lower(kyc.lastName))) like %?2% and lower(kyc.email) like %?3% ")
  List<Kyc> findAllByChannelAndCustomerNameAndEmail(Channel channel, String name,
      String email);

  @Query("select kyc from Kyc kyc where kyc.account.agent.channel=?1 and concat(trim(lower(kyc.firstName)), trim(lower(kyc.middleName)),trim(lower(kyc.lastName))) like %?2% and lower(kyc.account.agent.name) like %?3% ")
  List<Kyc> findAllByChannelAndCustomerNameAndAgentName(Channel channel, String name,
      String agentName);
//	@Query("select kyc from Kyc kyc where kyc.account.agent.channel=?1 and concat(trim(lower(kyc.firstName)), trim(lower(kyc.middleName)),trim(lower(kyc.lastName))) like %?2% and lower(kyc.account.agent.code) like %?3% ")
//	public List<Kyc> findAllByChannelAndCustomerNameAndAgentEmail(Channel channel, String name, String agentEmail);

  Kyc findById(Long kycId);

  Kyc findByReferralCodeAndAccount_Agent(String referralCode, Agent agent);

  List<Kyc> findAllByReferralCodeAndAccount_Agent_Code(String referralCode, String agentCode);

  @Query(value = "SELECT NEXTVAl(?1)", nativeQuery = true)
  public Long getNextSeriesId(String portalcif);
}
