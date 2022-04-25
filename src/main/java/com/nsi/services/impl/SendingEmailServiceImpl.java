package com.nsi.services.impl;

import com.nsi.domain.core.Kyc;
import com.nsi.services.SendingEmailService;
import com.nsi.util.ConstantUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class SendingEmailServiceImpl implements SendingEmailService {
    protected Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    @Qualifier("emailSender")
    private JavaMailSender emailSender;
    @Autowired
    @Qualifier("emailConfigBean")
    private Configuration emailConfig;

    public void sendActivationEmail(Kyc kyc) throws MessagingException, IOException, TemplateException {
        try{
            Map model = new HashMap();
            String customerName = "";
            if(kyc.getFirstName() != null){
                customerName = customerName.concat(kyc.getFirstName());
            }

            if(kyc.getMiddleName() != null){
                if(!customerName.equals("")){
                    customerName = customerName.concat(" ");
                }
                customerName = customerName.concat(kyc.getMiddleName());
            }

            if(kyc.getLastName() != null){
                if(!customerName.equals("")){
                    customerName = customerName.concat(" ");
                }
                customerName = customerName.concat(kyc.getLastName());
            }

            model.put("customerName", customerName);
            model.put("resetCode", kyc.getAccount().getResetCode());
            model.put("callCenter", ConstantUtil.CALL_CENTER_NUMBER);
            model.put("supportEmailAddr", ConstantUtil.SUPPORT_EMAIL_ADDRESS);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Template template = emailConfig.getTemplate("emailUserActivation.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            mimeMessageHelper.setTo(kyc.getEmail());
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject("Aktivasi Akun Invisee");
            mimeMessageHelper.setFrom(ConstantUtil.NO_REPLY_EMAIL);
            emailSender.send(message);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}