package com.demo.service;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.*;
import io.github.cdimascio.dotenv.*;

import java.util.Properties;


public abstract class EmailService {
    static Dotenv dotenv = Dotenv.load();
    final static String user = dotenv.get("BREVO_USER");
    final static String password = dotenv.get("PASSWORD");
    final static String myEmailAddress = dotenv.get("MYEMAIL");
    public static void sendEmailTo(String emailClient, String bookName, String bookAuthor, String clientName, String dateEnd){
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp-relay.brevo.com");
        //props.put("mail.debug", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(props, null);
        try{
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(myEmailAddress);
            msg.setRecipients(Message.RecipientType.TO, emailClient);
            msg.setSubject("Library loan");
            msg.setText("Hi " + clientName + "\n"+
                    "The loan for the book " + bookName + " by " + bookAuthor + " has expired on date " + dateEnd + "\n"+
                    "You need to return it ASAP");
            Transport.send(msg, user, password);
        }
        catch(MessagingException e){
            System.out.println(e.getMessage());
        }
    }

}
