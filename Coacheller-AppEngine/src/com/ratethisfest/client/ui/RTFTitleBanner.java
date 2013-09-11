package com.ratethisfest.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.ratethisfest.client.ClientResources;
import com.ratethisfest.data.FestivalEnum;

public class RTFTitleBanner extends Composite {

  private static RTFTitleBannerUiBinder uiBinder = GWT.create(RTFTitleBannerUiBinder.class);

  interface RTFTitleBannerUiBinder extends UiBinder<Widget, RTFTitleBanner> {
  }

  public RTFTitleBanner() {
    initWidget(uiBinder.createAndBindUi(this));
    String hostName = Window.Location.getHostName();
    FestivalEnum fest = FestivalEnum.fromHostname(hostName); // may be null

    ClientResources resources = GWT.create(ClientResources.class);

    if (FestivalEnum.COACHELLA.equals(fest)) {
      String bannerUrl = resources.coacheller_banner().getSafeUri().asString();
      bannerImage.setUrl(bannerUrl);
      bannerSubtitle.setText("Your unofficial " + fest.getName() + " ratings guide");

    } else if (FestivalEnum.LOLLAPALOOZA.equals(fest)) {
      String bannerUrl = resources.lollapaloozer_banner().getSafeUri().asString();
      bannerImage.setUrl(bannerUrl);
      bannerSubtitle.setText("Your unofficial " + fest.getName() + " ratings guide");

    } else if (FestivalEnum.TESTFEST.equals(fest)) {
      bannerImage.setUrl("testfestimageurl");
      bannerSubtitle.setText("Your unofficial TESTFEST ratings guide");

    } else {
      bannerImage.setUrl("unknownhosturl");
      bannerSubtitle.setText("Your unofficial ratings guide");
    }
  }

  @UiField
  Image bannerImage;

  @UiField
  Label bannerSubtitle;

  // public RTFTitleBanner(String firstName) {
  // initWidget(uiBinder.createAndBindUi(this));
  // button.setText(firstName);
  // }

}
