package com.nsi.repositories.core;

import com.nsi.domain.core.Agent;
import com.nsi.domain.core.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

  User findById(Long id);
  User findByUsernameAndAgent_CodeIgnoreCase(String username, String agentCode);

  User findByToken(String token);

  User findByEmail(String email);

  @Query("select count(_user) from User _user where _user.agent =?1")
  Long countByAgentChannelWithQuery(Agent agent);

  @Query("from User _user where _user.agent=?1 and _user.channelCustomer=?2")
  User findByAgentChannelAndChannelCustomerWithQuery(Agent agent, String channelCustomer);

  @Query("select count(_user) from User _user where _user.agent.channel.code <> 'INVPTL' ")
  Long countByOtherInviseeWithQuery();

  User findByChannelCustomer(String key);

  @Query("from User where agent.channel.code=:channelCode and channelCustomer=:channelCustomer and agent.code=:agentCode")
  User findByChannelCustomer(@Param("channelCode") String channelCode,
      @Param("channelCustomer") String key, @Param("agentCode") String agentCode);

  User findByEmailAndAgent(String email, Agent agent);

  List<User> findAllByUsername(String username);

  List<User> findAllByUsernameIsLike(String username);
  List<User> findAllByAgent_Code(String agentCode);

  User findByUsernameAndPasswordTemp(String username, String password);
}
