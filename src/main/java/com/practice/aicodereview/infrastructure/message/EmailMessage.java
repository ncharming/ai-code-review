package com.practice.aicodereview.infrastructure.message;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @description:
 * @author：nihongyu
 * @date: 2026/4/20
 */
@Component
public class EmailMessage {

    private final String sendEmail;

    private final String sendEmailPwa;
    private final String acceptEmail;



    public EmailMessage(String sendEmail, String sendEmailPwa, String acceptEmail) {
        this.sendEmail = sendEmail;
        this.sendEmailPwa = sendEmailPwa;
        this.acceptEmail = acceptEmail;
    }

    public void snedMessage(String logUrl){


        // SMTP 配置（这里以 Gmail 为例）
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // 创建 Session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sendEmail, sendEmailPwa);
            }
        });

        try {
            // 创建邮件对象
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sendEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(acceptEmail)
            );

            // 邮件标题
            message.setSubject("AI-CODE-REVIEW ");

            // 邮件内容（纯文本）
            message.setText("你好，你的代码评审结果已出，请查收："+logUrl);

            // 发送
            Transport.send(message);

            System.out.println("邮件发送成功！");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
