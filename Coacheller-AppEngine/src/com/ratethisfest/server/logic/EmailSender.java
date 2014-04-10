package com.ratethisfest.server.logic;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.server.domain.Rating;

public abstract class EmailSender {
  protected FestivalEnum festival;
  protected String authType;
  protected String authId;
  protected String authToken;
  protected String email;
  protected String senderEmail;
  protected String senderTitle;
  protected String subject;

  public String emailRatings() {
    String result;
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(senderEmail, senderTitle));
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
      msg.setSubject(subject);
      String messageBody = generateMessageBody();
      if (messageBody.isEmpty()) {
        result = "no ratings to send";
      } else {
        msg.setText(messageBody);
        Transport.send(msg);
        result = "Ratings successfully sent to " + email;
      }
    } catch (AddressException ae) {
      result = ae.getClass().getCanonicalName();
      result += ": " + ae.getMessage();
    } catch (MessagingException me) {
      result = me.getClass().getCanonicalName();
      result += ": " + me.getMessage();
    } catch (UnsupportedEncodingException uee) {
      result = uee.getClass().getCanonicalName();
      result += ": " + uee.getMessage();
    } catch (Exception e) {
      result = e.getClass().getCanonicalName();
      result += ": " + e.getMessage();
    }

    return result;
  }

  protected String generateMessageBody() {
    StringBuilder messageBody = new StringBuilder();

    int year = 2012;
    while (year < 2015) {
      List<Rating> ratings = RatingManager.getInstance().findRatingsByUserAndYear(festival, authType, authId,
          authToken, email, year);
      if (ratings != null) {
        messageBody.append("YEAR: ");
        messageBody.append(year);
        messageBody.append("\n");
        messageBody.append("\n");

        ArrayList<String> sortedItems = getSortedRatingStrings(ratings);

        for (String ratingString : sortedItems) {
          messageBody.append(ratingString);
        }
        messageBody.append("\n");
        messageBody.append("\n");
        messageBody.append("---");
        messageBody.append("\n");
        messageBody.append("\n");
      }
      year++;
    }

    return messageBody.toString();
  }

  // to be overridden
  protected ArrayList<String> getSortedRatingStrings(List<Rating> ratings) {
    return new ArrayList<String>();
  }

  public final Comparator<? super String> STRING_NAME_COMPARATOR = new Comparator<String>() {
    @Override
    public int compare(String t0, String t1) {
      return t0.compareToIgnoreCase(t1);
    }
  };

}
