package rigor.io.irent.user;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Base64;
import java.util.Properties;

@Service
public class EmailSender {


  private String host = "https://gentle-stream-26956.herokuapp.com";
  private String username = "irent.mcc.ph@gmail.com";
  private String password = "makapagal";
  private Properties props;
  private Session session;

  private Message message;

  public boolean isValid(String email) {
    try {
      InternetAddress internetAddress = new InternetAddress(email);
      internetAddress.validate();
      return true;
    } catch (AddressException e) {
      return false;
    }
  }

  public EmailSender() throws MessagingException {
    props = getGmailProperties();
    System.out.println("session " + username + password);
    session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });
//    session  = Session.getInstance(props, new Authenticator() {
//      protected PasswordAuthentication getPasswordAuthentication() {
//        return new PasswordAuthentication(username, password);
//      }
//    });
    message = new MimeMessage(session);
    message.setFrom(new InternetAddress(username));
  }

  public void sendMail(String email) throws MessagingException {
    System.out.println("email " + email);
    message.setRecipients(
        Message.RecipientType.TO, InternetAddress.parse(email));
    MimeBodyPart mimeBodyPart = new MimeBodyPart();

    message.setSubject("iRent account verification");

    String code = Base64.getEncoder().withoutPadding().encodeToString(email.getBytes());

    String link = host + "/users/confirmation?code=" + code;
    String msg = "Please click the link to confirm your registration: " + link;

    sendMessage(mimeBodyPart, msg);
  }


  private void sendMessage(MimeBodyPart mimeBodyPart, String msg) throws MessagingException {
    mimeBodyPart.setContent(msg, "text/html");
    System.out.println(msg);
    System.out.println(mimeBodyPart);
    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(mimeBodyPart);

    message.setContent(multipart);
    System.out.println(message);
    Transport.send(message);
  }

  private Properties getGmailProperties() {
    Properties props = new Properties();
    props.put("mail.smtp.host", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.auth", "true");
    return props;
  }
}
