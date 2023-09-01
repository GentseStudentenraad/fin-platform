package be.ugent.gsr.financien.service;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;


@Service("mailService")
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.copy.mailadress}")
    private String bccMailAdress;

    @Value("${mail.platform.mailadress}")
    private String fromMailAdress;

    public void sendMailWithAttachment(String to, String subject, String body, InputStreamSource fileToAttach) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                mimeMessage.setRecipient(Message.RecipientType.BCC, new InternetAddress(bccMailAdress));
                mimeMessage.setFrom(new InternetAddress(fromMailAdress));
                mimeMessage.setSubject(subject);
                mimeMessage.setText(body);
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.addAttachment("onkost.pdf", fileToAttach);
            }
        };
        try {
            mailSender.send(preparator);
        } catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }

}
