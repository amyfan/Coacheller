package com.lollapaloozer;

import android.content.Context;
import android.os.Handler;


//This class was a bad idea

public class LollapaloozerUIWorker extends Thread {
  
  private Handler _handler;
  private Runnable _runnable;
  private Context _context;

  
  public LollapaloozerUIWorker(Handler handler, Runnable runnable, Context context) {
    _runnable = runnable;
    _handler = handler;
    _context = context;

  }
  /*
  public void run() {
    LollapaloozerApplication.debug(this, "rebuildJAHM()");
    if (_obtained_email != null) { // Get my ratings

      JSONArray myRatings = null;
      try {
        myRatings = ServiceUtils.getRatings(_obtained_email, _dayToQuery, this);
        _storageManager.putJSONArray(DATA_RATINGS, myRatings);
      } catch (Exception e1) {
        _networkErrors = true;
        LollapaloozerApplication.debug(this,
            "Exception getting Ratings data, loading from storage if available");
        try {
          myRatings = _storageManager.getJSONArray(DATA_RATINGS);
        } catch (JSONException e) {
          e.printStackTrace();
          LollapaloozerApplication.debug(this, "JSONException loading ratings from storage");
        }
      }

      try {
        // TODO this may not be correct. JAHM should only be initialized once.
        // _myRatings_JAHM = new JSONArrayHashMap(myRatings,
        // QUERY_RATINGS__SET_ID, QUERY_RATINGS__WEEK);

        if (myRatings == null) {
          LollapaloozerApplication.debug(this, "Had to initialize ratings data JSONArray");
          myRatings = new JSONArray();
        }

        _myRatings_JAHM.setData(myRatings);

      } catch (JSONException e) {
        // TODO Auto-generated catch block
        // Could not get my ratings :(
        e.printStackTrace();
      }
    } else {
      // TODO This may not be correct. initJAHM() may have already been called
      // on startup and
      // TODO the reference to the JAHM may have been passed to the adapter
      // already
      // initJAHM(); //Commented in hope of addressing crash issue
    }

    _setListAdapter.updateJAHM(_myRatings_JAHM);

    JSONArray setData = null;
    try {
      // TODO: pass proper values (year can remain hard-coded for now)
      setData = ServiceUtils.getSets("2012", _dayToExamine, this);
      _storageManager.putJSONArray(DATA_SETS, setData);
    } catch (Exception e) {
      _networkErrors = true;
      LollapaloozerApplication.debug(this,
          "Exception getting Set data, loading from storage if available");
      setData = _storageManager.getJSONArray(DATA_SETS);
    }

    if (setData == null) {
      LollapaloozerApplication.debug(this, "Had to initialize set data JSONArray");
      setData = new JSONArray();
    }
    _setListAdapter.setData(setData);
    
    _handler.post(_runnable);
  }
  */
  
  

}
