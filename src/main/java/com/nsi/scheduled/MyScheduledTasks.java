package com.nsi.scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nsi.domain.core.SubcriptionJobScheduller;
import com.nsi.enumeration.AvantradeIntegrationReturnCodeEnumeration;
import com.nsi.repositories.core.SubcriptionJobSchedullerRepository;
import com.nsi.services.SubcriptionJobSchedullerService;

@Component
public class MyScheduledTasks {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	@Autowired
	SubcriptionJobSchedullerRepository subscriptionJobSchedullerRepository;
	@Autowired
	SubcriptionJobSchedullerService subscriptionJobSchedullerService;

	    @Scheduled(fixedRate = 300000, initialDelay = 300000)
	    public void schedulerJob() {
	    	List<SubcriptionJobScheduller> jobSchedullers = subscriptionJobSchedullerRepository.findAllByPayTypeAndStatus("CHAN", "0");
	    	for(SubcriptionJobScheduller job : jobSchedullers){
	    		Object subsResponse = subscriptionJobSchedullerService.createSubscriptionIntegrationAvantrade(job.getOrderNo());
	    		if(subsResponse != null){
	    			if(AvantradeIntegrationReturnCodeEnumeration.TRANSACTION_SUCCESSFULLY_INSERT.getCode().equals(Integer.parseInt(String.valueOf(subsResponse))) ||
		    				AvantradeIntegrationReturnCodeEnumeration.DATA_ALREADY_EXISTS.getCode().equals(Integer.parseInt(String.valueOf(subsResponse)))){
		    			job.setExecuteDate(new Date());
		    			job.setStatus("1");
		    			job = subscriptionJobSchedullerRepository.save(job);
		    		}
	    		}
	    		
	    	}
	    	System.out.println("Finish Send subscription to avantrade , Job ran at " 
	            + dateFormat.format(new Date()));

	    }
}
