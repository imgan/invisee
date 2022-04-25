package com.nsi.services;

import com.nsi.domain.core.Kyc;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;

public interface SendingEmailService {
    public void sendActivationEmail(Kyc kyc) throws MessagingException, IOException, TemplateException;
}
