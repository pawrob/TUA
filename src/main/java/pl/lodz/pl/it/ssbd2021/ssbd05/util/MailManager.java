package pl.lodz.pl.it.ssbd2021.ssbd05.util;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@LocalBean
@DenyAll
public class MailManager {

    @Resource(name = "mailUsername")
    private String username;
    @Resource(name = "mailPassword")
    private String password;

    @Resource(name = "mailHost")
    private String host;
    @Resource(name = "mailPort")
    private String port;

    private final String mailWithButtonTemplate = "templates/mailWithButtonTemplate.html";
    private final String infoMailButtonTemplate = "templates/infoMailTemplate.html";
    private final String mailWithContentTemplate = "templates/mailWithContentTemplate.html";


    @Resource(name = "enableMailing")
    private boolean isEnabled;

    public String getMailWithButtonTemplate() {
        return getResourceAsString(mailWithButtonTemplate);
    }

    public String getInfoMailButtonTemplate() {
        return getResourceAsString(infoMailButtonTemplate);
    }

    public String getMailWithContentTemplate() {
        return getResourceAsString(mailWithContentTemplate);
    }

    private final Properties properties = new Properties();

    public MailManager() {

    }

    @PostConstruct
    public void init() {
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", port);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    }

    private String getResourceAsString(String path) {
        return new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(path)),
                        StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private void sendMail(String recipient, String subject, String content) {
        sendMail(recipient, subject, content, null, null);
    }

    private void sendMail(String recipient, String subject, String content, List<String> CC, List<String> BCC) {
        if (!isEnabled)
            return;
        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipient)
            );
            if (CC != null) {
                message.setRecipients(
                        Message.RecipientType.CC,
                        InternetAddress.parse(String.join(",", CC))
                );
            }
            if (BCC != null) {
                message.setRecipients(
                        Message.RecipientType.BCC,
                        InternetAddress.parse(String.join(",", BCC))
                );
            }
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=UTF-8");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    @PermitAll
    @Asynchronous
    public void createAndSendEmailFromTemplate(UserEntity userEntity, String title, String headerText, String buttonText, String footerText) {
        String body = getInfoMailButtonTemplate();

        Locale[] locales = {
                new Locale("en", "EN"),
                new Locale("pl", "PL")
        };

        ResourceBundle eng = ResourceBundle.getBundle("i18n/mail", locales[0]);
        ResourceBundle pl = ResourceBundle.getBundle("i18n/mail", locales[1]);

        if (!buttonText.equals("noButton")) {
            body = getMailWithButtonTemplate();
            body = body.replace("[EMAIL_BUTTON_CONTENT]", buttonText);
        }

        if (buttonText.equals("content")) {
            body = getMailWithContentTemplate();
            body = body.replace("[EMAIL_FOOTER_CONTENT]", footerText);
        }


        if (userEntity.getPersonalData().getLanguage().equals("POL")) {
            body = body.replace("[LOGIN]", pl.getString("hello") + ", " + userEntity.getLogin());
            body = body.replace("[EMAIL_HEADER]", pl.getString(headerText));
            body = body.replace("[CLICK_BUTTON]", "Naciśnij aby kontynuowac ");
            if (!buttonText.equals("content")) {
                body = body.replace("[EMAIL_FOOTER]", pl.getString(footerText));
            }
            sendMail(userEntity.getEmail(), pl.getString(title), body);
        } else {
            body = body.replace("[LOGIN]", eng.getString("hello") + ", " + userEntity.getLogin());
            body = body.replace("[EMAIL_HEADER]", eng.getString(headerText));
            body = body.replace("[CLICK_BUTTON]", "Click to proceed");
            if (!buttonText.equals("content")) {
                body = body.replace("[EMAIL_FOOTER]", eng.getString(footerText));
            }
            sendMail(userEntity.getEmail(), eng.getString(title), body);
        }

    }
}
