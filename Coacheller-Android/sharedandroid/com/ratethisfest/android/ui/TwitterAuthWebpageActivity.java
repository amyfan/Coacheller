package com.ratethisfest.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.lollapaloozer.R;
import com.ratethisfest.shared.AuthConstants;

public class TwitterAuthWebpageActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.twitter_auth_dialog);

    // Setup 'X' close widget
    ImageView close_dialog = (ImageView) findViewById(R.id.imageView_custom_dialog_close);
    close_dialog.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setResult(Activity.RESULT_CANCELED);
        finish();
      }
    });

    WebView webview = (WebView) findViewById(R.id.webView1);
    webview.getSettings().setJavaScriptEnabled(true);
    webview.setVerticalScrollBarEnabled(false);
    webview.setHorizontalScrollBarEnabled(false);
    webview.setVisibility(View.VISIBLE);
    webview.setWebViewClient(new WebViewClient() {

      private String _requestTokenString;
      private String _requestTokenVerifierString;

      @Override
      public void onPageFinished(WebView view, String url) {
        System.out.println("onPageFinished: " + url);
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        System.out.println("onPageStarted : " + url);
        Uri uri = Uri.parse(url);

        // OK to use AuthConstants constant directly if this code is used in COACHELLER ONLY
        if (url.startsWith(AuthConstants.LOLLA_TWITTER_OAUTH_CALLBACK_URL) && url != null) {
          System.out.println("Special callback URL was detected");
          _requestTokenString = uri.getQueryParameter(AuthConstants.OAUTH_CALLBACK_PARAM_TOKEN);
          _requestTokenVerifierString = uri.getQueryParameter(AuthConstants.OAUTH_CALLBACK_PARAM_VERIFIER);

          System.out.println("OAuth token: " + _requestTokenString);
          System.out.println("OAuth verifier: " + _requestTokenVerifierString);

          Intent resultIntent = new Intent();
          resultIntent.putExtra(AuthConstants.INTENT_EXTRA_OAUTH1_RETURN_TOKEN, _requestTokenString);
          resultIntent.putExtra(AuthConstants.INTENT_EXTRA_OAUTH1_RETURN_VERIFIER, _requestTokenVerifierString);
          setResult(RESULT_OK, resultIntent);
          finish();

        }
      }

    });

    Intent callingIntent = getIntent();
    String authUrl = callingIntent.getStringExtra(AuthConstants.INTENT_EXTRA_AUTH_URL);
    webview.loadUrl(authUrl);
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

}
