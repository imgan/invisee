package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nsi.domain.core.Channel;
import com.nsi.domain.core.ChannelCommission;

public interface ChannelCommissionRepository extends JpaRepository<ChannelCommission, Long>{

	public ChannelCommission findByChannelAndRowStatus(Channel channel, Boolean rowStatus);
	
	//TODO: List Channel by filter ****
	@Query("select distinct cc.channel from ChannelCommission cc where cc.commission=?1")
	public  List<Channel> findAllByCommissionWithQuery(Integer commission);
	@Query("select distinct cc.channel from ChannelCommission cc where cc.commission=?1 and lower(cc.channel.code) like '%?2%'")
	public  List<Channel> findAllByCommissionAndCodeWithQuery(Integer commission, String code);
	@Query("select distinct cc.channel from ChannelCommission cc where cc.commission=?1 and lower(cc.channel.name) like '%?2%'")
	public  List<Channel> findAllByCommissionAndNameWithQuery(Integer commission, String name);
	@Query("select distinct cc.channel from ChannelCommission cc where cc.commission=?1 and lower(cc.channel.name) like '%?2%' and lower(cc.channel.code) like '%?3%'")
	public  List<Channel> findAllByCommissionAndNameAndCodeWithQuery(Integer commission, String name, String code);
}
