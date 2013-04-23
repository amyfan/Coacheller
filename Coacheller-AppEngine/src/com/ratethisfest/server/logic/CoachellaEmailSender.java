package com.ratethisfest.server.logic;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
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

import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.shared.Set;

public class CoachellaEmailSender {
  // TODO: put this in resource file
  public static final String SENDER_EMAIL = "info@coacheller.com";
  public static final String SENDER_TITLE = "Coacheller";
  public static final String SUBJECT = "Your Coachella Set Ratings";

  public static String emailRatings(String email) {
    String success = "Ratings successfully sent to " + email;
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_TITLE));
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
      msg.setSubject(SUBJECT);
      String messageBody = generateMessageBody(email);
      if (messageBody.isEmpty()) {
        success = "no ratings to send";
      } else {
        msg.setText(messageBody);
        Transport.send(msg);
      }
    } catch (AddressException ae) {
      success = "address exception";
      success = ae.getMessage();
    } catch (MessagingException me) {
      success = "messaging exception";
      success = me.getMessage();
    } catch (UnsupportedEncodingException uee) {
      success = "unsupported encoding exception";
      success = uee.getMessage();
    } catch (Exception e) {
      success = e.getMessage();
    }

    return success;
  }

  /*
   * TODO: update once user auth implemented for coachella
   */
  private static String generateMessageBody(String email) {
    // TODO: don't constrain by (& definitely don't hardcode) year here
    List<Rating> ratings = CoachellaRatingManager.getInstance().findRatingsByUserEmailAndYear(email, 2012);
    if (ratings == null) {
      return null;
    }
    List<String> ratingStrings = new ArrayList<String>();
    for (Rating rating : ratings) {
      Set set = CoachellaRatingManager.getInstance().findSet(rating.getSet().getId());
      StringBuilder ratingString = new StringBuilder();
      ratingString.append(set.getYear());
      ratingString.append(" Weekend ");
      ratingString.append(rating.getWeekend());
      ratingString.append(" ");
      ratingString.append(set.getDay());
      ratingString.append(" - ");
      ratingString.append(set.getArtistName());
      ratingString.append(": ");
      ratingString.append(rating.getScore());
      if (rating.getNotes() != null && !rating.getNotes().isEmpty()) {
        ratingString.append("\nNotes: ");
        ratingString.append(rating.getNotes());
      }
      ratingString.append("\n");
      ratingString.append("\n");
      ratingStrings.add(ratingString.toString());
    }
    ArrayList<String> sortedItems = new ArrayList<String>(ratingStrings);
    Collections.sort(sortedItems, STRING_NAME_COMPARATOR);
    StringBuilder messageBody = new StringBuilder();
    for (String ratingString : sortedItems) {
      messageBody.append(ratingString);
    }
    return messageBody.toString();
  }

  public static final Comparator<? super String> STRING_NAME_COMPARATOR = new Comparator<String>() {
    @Override
    public int compare(String t0, String t1) {
      return t0.compareToIgnoreCase(t1);
    }
  };

}
