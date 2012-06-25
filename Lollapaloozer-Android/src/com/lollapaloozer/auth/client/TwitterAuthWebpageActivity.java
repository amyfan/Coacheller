package com.lollapaloozer.auth.client;

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
import com.ratethisfest.shared.Constants;

public class TwitterAuthWebpageActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.twitter_auth_dialog);

    Intent callingIntent = getIntent();
    String authUrl = callingIntent.getStringExtra(Constants.INTENT_EXTRA_TWITTER_AUTHURL);

    ImageView close_dialog = (ImageView) findViewById(R.id.imageView_custom_dialog_close);

    close_dialog.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        setResult(Activity.RESULT_CANCELED);
        finish();
      }
    });

    WebView webview = (WebView) findViewById(R.id.webView1);
    // webview = new WebView(this);
    webview.getSettings().setJavaScriptEnabled(true);
    webview.setVerticalScrollBarEnabled(false);
    webview.setHorizontalScrollBarEnabled(false);
    webview.setVisibility(View.VISIBLE);

    // System.out.println("webview requesting focus ("+
    // webview.hasFocus()+")");
    // webview.requestFocusFromTouch();
    // System.out.println("webview requested focus ("+
    // webview.hasFocus()+")");

    webview.setWebViewClient(new WebViewClient() {

      private boolean shown = false;
      private String _requestTokenString;
      private String _requestTokenVerifierString;

      @Override
      public void onPageFinished(WebView view, String url) {
        // if (!shown) {
        System.out.println("onPageFinished showing dialog: " + url);
        // _alertDialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
        // LayoutParams.FILL_PARENT); //todo try commenting this

        // _alertDialog.show();
        // shown = true;
        // _activity.setContentView(layout);
        // }
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        System.out.println("onPageStarted : " + url);
        Uri uri = Uri.parse(url);

        if (url.startsWith(Constants.OAUTH_CALLBACK_URL) && url != null) {
          System.out.println("Special callback URL was detected");
          _requestTokenString = uri.getQueryParameter(Constants.OAUTH_CALLBACK_PARAM_TOKEN);
          _requestTokenVerifierString = uri
              .getQueryParameter(Constants.OAUTH_CALLBACK_PARAM_VERIFIER);

          System.out.println("OAuth token: " + _requestTokenString);
          System.out.println("OAuth verifier: " + _requestTokenVerifierString);
          // webview.setVisibility(View.GONE);
          // webview.destroy();

          Intent resultIntent = new Intent();
          resultIntent.putExtra(Constants.INTENT_EXTRA_OAUTH1_RETURN_TOKEN, _requestTokenString);
          resultIntent
              .putExtra(Constants.INTENT_EXTRA_OAUTH1_RETURN_VERIFIER, _requestTokenVerifierString);
          setResult(RESULT_OK, resultIntent);
          finish();

        }
      }

    });
    // webview.requestFocus(View.FOCUS_DOWN);
    // _alertDialog.show();
    webview.loadUrl(authUrl);
  }

  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
  }

}
