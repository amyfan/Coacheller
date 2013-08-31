package auth.logins.test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.GoogleApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import auth.logins.ServletConfig;
import auth.logins.ServletInterface;
import auth.logins.data.AuthProviderAccount;
import auth.logins.data.MasterAccount;
import auth.logins.other.LoginManager;
import auth.logins.other.LoginType;
import auth.logins.other.RTFAccountException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class sessionsTestServlet extends HttpServlet {



  private static final Logger log = Logger.getLogger(new Object() {
  }.getClass().getEnclosingClass().getName());
  public static final String IMAGE_URL_SIGNIN_GOOGLE = "https://developers.google.com/accounts/images/sign-in-with-google.png";
  public static final String IMAGE_URL_SIGNIN_FACEBOOK = "http://dragon.ak.fbcdn.net/hphotos-ak-ash3/851558_153968161448238_508278025_n.png";
  public static final String IMAGE_URL_SIGNIN_TWITTER = "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcSdYJnRQqyWHMkm9VgP_aHf0gc4wbREFiv0z72T_xVltn4vNWb9NA";

  // Facebook redirects user back to us after auth

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    HttpSession session = req.getSession();
    PrintWriter outWriter = resp.getWriter();

    Document doc = Document.createShell("");
    writeStartOverLink(doc);
    doc.body().appendText("Handling GET - This is the document body").appendElement("br");

    writeDebugInfo(req, doc);

    MasterAccount currentLogin = LoginManager.getCurrentLogin(session);
    if (currentLogin == null) {
      writeGoogleLoginButton(doc, req.getLocalName());
      writeFacebookLoginButton(doc);
      writeTwitterLoginButton(doc);
    } else {
      // Drawing should come after action handling...
      if (!currentLogin.isLoggedInAPType(LoginType.GOOGLE)) {
        writeGoogleLoginButton(doc, req.getLocalName());
      }

      if (!currentLogin.isLoggedInAPType(LoginType.FACEBOOK)) {
        writeFacebookLoginButton(doc);
      }

      if (!currentLogin.isLoggedInAPType(LoginType.TWITTER)) {
        writeTwitterLoginButton(doc);
      }
    }

    
    try {
      String redirectStr = ServletInterface.libraryHandleRTFAction(session, req);
      if (redirectStr != null) {
        //Redirect string is null if there was no action requiring a redirect or reload
      resp.sendRedirect(redirectStr);
      }
    } catch (RTFAccountException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      writeAccountConflictMessage(doc, e);
    }

    // writeForm(doc);

    doc.body().appendText("Done");
    outWriter.write(doc.toString());
  }


  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    HttpSession session = req.getSession();
    PrintWriter outWriter = resp.getWriter();

    Document doc = Document.createShell("");
    writeStartOverLink(doc);
    doc.body().appendText("Handling POST - This is the document body").appendElement("br");
    writeDebugInfo(req, doc);

    // Find request parameters
    Map<String, String[]> parameters = req.getParameterMap();
    for (String parameter : parameters.keySet()) {
      // Here we ASSUME that no parameters are included more than once...
      if (parameters.get(parameter).length > 1) {
        // Should log duplicate parameter
        doc.body().appendText("Warning: Duplicate parameter!!!").appendElement("br");
      }
      // session.setAttribute(parameter, parameters.get(parameter)[0]);

    }

    doc.body().appendText("Done");
    outWriter.write(doc.toString());
    resp.sendRedirect(ServletConfig.SERVLET_BASEPATH);
  }


  private void writeAccountConflictMessage(Document doc, RTFAccountException e) {
    long originalRTFAcctId = e.getOriginalOwner().getAppEngineKeyLong();
    long contestorRTFAcctId = e.getNewClaimant().getAppEngineKeyLong();
    String apName = e.getContestedAPAccount().getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME);
    String apDetails = e.getContestedAPAccount().getDescription();

    doc.body().appendElement("h2").appendText("Account Ownership Conflict").appendElement("br");
    doc.body()
        .appendText(
            "The " + apName + " account you are trying to add to your RateThisFest account ID " + contestorRTFAcctId
                + " is already being used by another RateThisFest user ID " + originalRTFAcctId + ".")
        .appendElement("br").appendElement("br");
    doc.body().appendElement("a").attr("href", ServletConfig.SERVLET_BASEPATH).appendText("Back To Home Page")
        .appendElement("br");
  }


  private void writeStartOverLink(Document doc) {
    doc.body().appendElement("a").attr("href", ServletConfig.SERVLET_BASEPATH).appendText("Home Page").appendElement("br");
    doc.body().appendElement("a")
        .attr("href", ServletConfig.SERVLET_BASEPATH + "?" + ServletInterface.PARAM_NAME_RTFACTION + "=" + ServletInterface.ACTION_LOGOUT)
        .appendText("Wipe Login Data").appendElement("br");
    doc.body().appendElement("br").appendElement("a")
        .attr("href", ServletConfig.SERVLET_BASEPATH + "?" + ServletInterface.PARAM_NAME_RTFACTION + "=" + ServletInterface.ACTION_DESTROY_ACCOUNT)
        .attr("align", "right").appendText("DESTROY MY RateThisFest ACCOUNT").appendElement("br");
  }

  private void writeForm(Document doc) {
    // <form action="/sign" method="post">
    Element formElement = doc.body().appendElement("form").attr("action", ServletConfig.SERVLET_BASEPATH)
        .attr("method", "post");

    // <div><textarea name="content" rows="3" cols="60"></textarea></div>
    formElement.appendElement("div").appendElement("textarea").attr("name", "content").attr("rows", "3")
        .attr("cols", "60");

    // <input type="hidden" name="guestbookName"
    // value="${fn:escapeXml(guestbookName)}"/>
    formElement.appendElement("input").attr("type", "hidden").attr("name", "hiddenAttributeName")
        .attr("value", "hiddenAttributeValue");

    // <div><input type="submit" value="Post Greeting" /></div>
    formElement.appendElement("div").appendElement("input").attr("type", "submit").attr("value", "Set New Attribute");

    // </form>
  }

  private void writeGoogleLoginButton(Document doc, String reqHost) {
    String urlToUse = ServletConfig.HTTP + reqHost + ServletConfig.GOOGLE_USER_AUTH_START_PATH;
    doc.body().appendElement("A").attr("href", urlToUse).appendElement("img")
        .attr("src", sessionsTestServlet.IMAGE_URL_SIGNIN_GOOGLE).attr("border", "0").appendElement("br");
  }

  private void writeFacebookLoginButton(Document doc) {
    String urlToUseWhenYouClickOnTheFacebookButton = ServletConfig.FACEBOOK_USER_AUTH_SCRIBE_START_URL;

    doc.body().appendElement("A").attr("href", urlToUseWhenYouClickOnTheFacebookButton).appendElement("img")
        .attr("src", sessionsTestServlet.IMAGE_URL_SIGNIN_FACEBOOK).attr("border", "0").appendElement("br");

  }

  private void writeTwitterLoginButton(Document doc) {
    String urlToUse = ServletConfig.SERVLET_BASEPATH + "?" + ServletInterface.PARAM_NAME_RTFACTION + "=" + ServletInterface.ACTION_TWITTER_AUTH;
    doc.body().appendElement("A").attr("href", urlToUse).appendElement("img")
        .attr("src", sessionsTestServlet.IMAGE_URL_SIGNIN_TWITTER).attr("border", "0").appendElement("br");
  }

  private void writeDebugInfo(HttpServletRequest req, Document doc) throws IOException {
    // Output session attributes
    HttpSession session = req.getSession();
    // session.setAttribute("sessionType", "The wrong type!!!");
    doc.body().appendText("Session ID:[" + req.getSession().getId() + "] Attributes:").appendElement("br");

    Enumeration attributeNameEnumeration = session.getAttributeNames();
    if (!attributeNameEnumeration.hasMoreElements()) {
      doc.body().appendText("(none)").appendElement("br");
    }

    String attribName;
    Object attribValue;
    while (attributeNameEnumeration.hasMoreElements()) {
      attribName = (String) attributeNameEnumeration.nextElement();
      attribValue = session.getAttribute(attribName);
      doc.body().appendText(attribName + " = " + attribValue).appendElement("br");
    }

    // Output Query String
    String queryString = req.getQueryString();
    doc.body().appendElement("br").appendText("Query String:").appendElement("br");
    if (queryString == null) {
      doc.body().appendText("").appendElement("br");
    } else {
      doc.body().appendText(queryString).appendElement("br");
    }

    // Output Request Parameters
    Map<String, String[]> parameters = req.getParameterMap();
    doc.body().appendElement("br").appendText("Request Parameters(" + parameters.size() + ")").appendElement("br");

    for (String parameter : parameters.keySet()) {
      // Here we ASSUME that no parameters are included more than once...
      if (parameters.get(parameter).length > 1) {
        doc.body().appendText("Warning: Duplicate parameter!!!").appendElement("br");
      }
      doc.body().appendText(parameter + " = " + parameters.get(parameter)[0]).appendElement("br");
    }

    MasterAccount currentLogin = LoginManager.getCurrentLogin(session);
    if (currentLogin != null) {
      long rtfAccountId = currentLogin.getAppEngineKeyLong();
      String personName = currentLogin.getProperty(MasterAccount.PROPERTY_PERSON_NAME);
      doc.body().appendText("Logged in as RateThisFest Account#: " + rtfAccountId + " Name: " + personName)
          .appendElement("br");

      Collection<AuthProviderAccount> providerAccounts = currentLogin.getAPAccounts();
      for (AuthProviderAccount apAccount : providerAccounts) {
        doc.body()
            .appendText(
                apAccount.getProperty(AuthProviderAccount.AUTH_PROVIDER_NAME) + ": " + apAccount.getDescription())
            .appendElement("br");
      }
    }

    doc.body().appendText("Remainder of request is as follows:").appendElement("br");
    InputStreamReader streamReader = new InputStreamReader(req.getInputStream());
    BufferedReader bufReader = new BufferedReader(streamReader);

    String lineRead = null;
    while ((lineRead = bufReader.readLine()) != null) {
      doc.body().appendText(lineRead).appendElement("br");
    }
    doc.body().appendElement("hr");
  }

}
