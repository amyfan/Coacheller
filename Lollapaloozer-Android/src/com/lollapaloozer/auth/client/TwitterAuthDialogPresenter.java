package com.lollapaloozer.auth.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;

import com.lollapaloozer.ui.TwitterAuthWebpageActivity;
import com.ratethisfest.shared.Constants;

public class TwitterAuthDialogPresenter {

  private Activity _activity;
  private AlertDialog _alertDialog;
  private String _requestTokenString;
  private String _requestTokenVerifierString;

  public TwitterAuthDialogPresenter(Activity activity) {
    _activity = activity;
  }

  public void showDialog(String authUrl) {
    Intent twitterAuthIntent = new Intent(_activity, TwitterAuthWebpageActivity.class);
    twitterAuthIntent.putExtra(Constants.INTENT_EXTRA_TWITTER_AUTHURL, authUrl);
    _activity.startActivityForResult(twitterAuthIntent, Constants.INTENT_REQ_TWITTER_LOGIN);
  }

  // Dialog implementation has been abandoned due to bugs in Android framework
  // This class contributes nothing useful now.

  // AlertDialog.Builder builder;
  // builder = new AlertDialog.Builder(_activity);
  // LayoutInflater inflater = (LayoutInflater)
  // _activity.getSystemService(_activity.LAYOUT_INFLATER_SERVICE);
  // final View layout = inflater.inflate(R.layout.twitter_auth_dialog,
  // (ViewGroup) _activity.findViewById(R.id.layout_root));
  // builder.setView(layout);

  // _alertDialog = builder.create();

  // ImageView close_dialog = (ImageView)
  // layout.findViewById(R.id.imageView_custom_dialog_close);

  // close_dialog.setOnClickListener(new View.OnClickListener() {
  // public void onClick(View v) {
  // _alertDialog.dismiss();
  // }
  // });

  /*
   * final WebView webview = (WebView) layout.findViewById(R.id.webView1); //
   * webview = new WebView(this);
   * webview.getSettings().setJavaScriptEnabled(true);
   * webview.setVerticalScrollBarEnabled(false);
   * webview.setHorizontalScrollBarEnabled(false);
   * webview.setVisibility(View.VISIBLE);
   * 
   * System.out.println("webview requesting focus ("+ webview.hasFocus()+")");
   * webview.requestFocusFromTouch();
   * System.out.println("webview requested focus ("+ webview.hasFocus()+")");
   * 
   * webview.setOnTouchListener(new View.OnTouchListener() {
   * 
   * @Override public boolean onTouch(View v, MotionEvent event) { switch
   * (event.getAction()) { case MotionEvent.ACTION_DOWN:
   * System.out.println("MotionEvent Action Down"+ webview.hasFocus()); case
   * MotionEvent.ACTION_UP: System.out.println("MotionEvent Action Up"+
   * webview.hasFocus()); if (!v.hasFocus()) {
   * //webview.requestFocus(View.FOCUS_DOWN); v.requestFocusFromTouch(); }
   * 
   * if (v instanceof EditText) { System.out.println("EditText touched"); }
   * break; } return false; } });
   */
  // webview.requestFocus(View.FOCUS_DOWN);
  // webview.requestFocusFromTouch();
  /*
   * webview.setWebViewClient(new WebViewClient() {
   * 
   * private boolean shown = false;
   * 
   * @Override public void onPageFinished(WebView view, String url) { if
   * (!shown) { System.out.println("onPageFinished showing dialog: " + url); //
   * _alertDialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
   * LayoutParams.FILL_PARENT); //todo try commenting this
   * 
   * //layout.requestFocus(View.FOCUS_DOWN);
   * 
   * 
   * //_alertDialog.show(); shown = true; _activity.setContentView(layout); } }
   * 
   * @Override public void onPageStarted(WebView view, String url, Bitmap
   * favicon) { System.out.println("onPageStarted : " + url); Uri uri =
   * Uri.parse(url);
   * 
   * if (url.startsWith(Constants.OAUTH_CALLBACK_URL) && url != null) {
   * System.out.println("Special callback URL was detected");
   * _requestTokenString = uri
   * .getQueryParameter(Constants.OAUTH_CALLBACK_PARAM_TOKEN);
   * _requestTokenVerifierString = uri
   * .getQueryParameter(Constants.OAUTH_CALLBACK_PARAM_VERIFIER);
   * 
   * System.out.println("OAuth token: " + _requestTokenString);
   * System.out.println("OAuth verifier: " + _requestTokenVerifierString); //
   * webview.setVisibility(View.GONE); // webview.destroy();
   * _alertDialog.dismiss(); // maybe use OnCancelListener?
   * 
   * // todo MOVE OAUTH CODE
   * 
   * Verifier verifier = new Verifier(_requestTokenVerifierString); Token
   * accessToken = _service.getAccessToken(_requestToken, verifier); // the
   * requestToken you had from step 2 OAuthRequest request = new
   * OAuthRequest(Verb.GET,
   * "http://api.twitter.com/1/account/verify_credentials.xml" );
   * _service.signRequest(accessToken, request); // the access // token from //
   * step 4 Response response = request.send();
   * 
   * System.out.println(response.getBody());
   * 
   * } }
   * 
   * });
   */
  // webview.requestFocus(View.FOCUS_DOWN);
  // _alertDialog.show();

  // webview.loadUrl(authUrl);

}