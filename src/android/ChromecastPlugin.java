package es.simbiosys.cordova.plugin.chromecast;

import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouteSelector;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastButtonFactory;

public class ChromecastPlugin extends CordovaPlugin {

  private CastContext castContext;
  private MediaRouteButton castButton;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);
      
      // Initialize chromecast plugin
      castContext = CastContext.getSharedInstance(cordova.getActivity().getApplicationContext());
      CastButtonFactory.setUpMediaRouteButton(cordova.getActivity().getApplicationContext(), castButton);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("echo")) {
      String message = args.getString(0);
      this.echo(message, callbackContext);
      return true;
    } else if (action.equals("castBtnClick")) {
      this.castBtnClick(callbackContext);
      return true;
    }

    return false;
  }

  private void echo(String message, CallbackContext callbackContext) {
    if (message != null && message.length() > 0) {
      callbackContext.success(message);
    } else {
      callbackContext.error("Expected one non-empty string argument.");
    }
  }

  private void castBtnClick(CallbackContext callbackContext) {
    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
            MediaRouteSelector selector = castContext.getMergedSelector();
            callbackContext.success(selector.toString()); // Thread-safe.
        }
      });
      /* if (castButton.performClick()) {
        callbackContext.success("performClick OK");
      } else {
        callbackContext.error("performClick KO");
      } */
    } catch (Exception e) {
      callbackContext.error("Error: " + e.getMessage());
    }
  }
}