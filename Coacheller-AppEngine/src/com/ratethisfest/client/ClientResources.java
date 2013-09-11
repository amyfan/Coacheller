package com.ratethisfest.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface ClientResources extends ClientBundle {

  @Source("coacheller_banner.png")
  ImageResource coacheller_banner();

  @Source("lollapaloozer_banner.png")
  ImageResource lollapaloozer_banner();

  @Source("signin_google.png")
  ImageResource signin_google();

  @Source("signin_facebook.png")
  ImageResource signin_facebook();

  @Source("signin_twitter.png")
  ImageResource signin_twitter();

  @Source("post_facebook_large.png")
  ImageResource post_facebook_large();

  @Source("post_facebook_small.png")
  ImageResource post_facebook_small();

  @Source("post_twitter_large.png")
  ImageResource post_twitter_large();

  @Source("post_twitter_small.png")
  ImageResource post_twitter_small();

}
