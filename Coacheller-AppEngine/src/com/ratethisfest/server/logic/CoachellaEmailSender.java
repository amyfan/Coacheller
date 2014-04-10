package com.ratethisfest.server.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ratethisfest.data.FestivalEnum;
import com.ratethisfest.server.domain.Rating;
import com.ratethisfest.shared.Set;

public class CoachellaEmailSender extends EmailSender {
  // TODO: put this in resource file
  public static final String SENDER_EMAIL = "support@coacheller.com";
  public static final String SENDER_TITLE = "Coacheller";
  public static final String SUBJECT = "Your Coachella Set Ratings";

  public CoachellaEmailSender(String authType, String authId, String authToken, String email) {
    this.festival = FestivalEnum.COACHELLA;
    this.authType = authType;
    this.authId = authId;
    this.authToken = authToken;
    this.email = email;
    this.senderEmail = SENDER_EMAIL;
    this.senderTitle = SENDER_TITLE;
    this.subject = SUBJECT;
  }

  @Override
  protected ArrayList<String> getSortedRatingStrings(List<Rating> ratings) {
    List<String> ratingStrings = new ArrayList<String>();
    for (Rating rating : ratings) {
      Set set = RatingManager.getInstance().findSet(rating.getSet().getId());
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
        ratingString.append("\n");
        ratingString.append("\"");
        ratingString.append(rating.getNotes());
        ratingString.append("\"");
      }
      ratingString.append("\n");
      ratingString.append("\n");
      ratingStrings.add(ratingString.toString());
    }
    ArrayList<String> sortedItems = new ArrayList<String>(ratingStrings);
    Collections.sort(sortedItems, STRING_NAME_COMPARATOR);
    return sortedItems;
  }

}
