package com.nsi.scheduled;

import com.nsi.domain.core.CronEmail;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nsi.repositories.core.CronEmailRepository;
import com.nsi.services.EmailService;
import org.apache.log4j.Logger;

@Component
public class EmailScheduledTasks {

    @Autowired
    private CronEmailRepository cronEmailRepository;
    @Autowired
    private EmailService emailService;

    private final Logger logger = Logger.getLogger(this.getClass());

    @Scheduled(fixedRate = 60000, initialDelay = 60000)
    public void schedulerJob() {
        List<CronEmail> cronEmails = cronEmailRepository.findAllByStatus(new Date(), 0);
        if (cronEmails != null && !cronEmails.isEmpty()) {
            logger.info("========================= SCHEDULLER EMAIL RUN =======================");
            for (CronEmail cronEmail : cronEmails) {
                CronEmail ce = cronEmail;
                if (ce.getType().equalsIgnoreCase("OPEN_ACCOUNT")) {
                    if (emailService.sendToGroovy(ce.getUrl(), ce.getValue())) {
                        ce.setStatus(1);
                        
                    } else {
                        ce.setStatus(2);
                    }
                }
                cronEmailRepository.save(ce);
            }
            logger.info("========================= SCHEDULLER EMAIL END =======================");
        }
    }
}
