package es.simbiosys.cordova.plugin.chromecast;

import android.support.v7.app.MediaRouteChooserDialog;
import android.support.v7.media.MediaRouteSelector;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;

import es.simbiosys.cordova.plugin.R;

public class ChromecastPlugin extends CordovaPlugin {

  private CastContext castContext;
  private MediaRouteChooserDialog castDialog;
  private CastSession castSession;
  private SessionManagerListener<CastSession> sessionManagerListener;

  private CallbackContext sessionEventsCallbackContext;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);

      // Initialize events callback context
      this.sessionEventsCallbackContext = null;
      
      // Initialize cast instance and devices dialog
      this.castContext = CastContext.getSharedInstance(cordova.getActivity().getApplicationContext());
      MediaRouteSelector selector = castContext.getMergedSelector();
      this.castDialog = new MediaRouteChooserDialog(cordova.getContext(), R.style.Theme_AppCompat_Dialog);
      this.castDialog.setRouteSelector(selector);

      // Set up cast events listener
      setupCastListener();
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("subscribeToSessionEvents")) {
      // Save events callback context to communicate Java with Javascript
      this.sessionEventsCallbackContext = callbackContext;

      // Return OK result
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "subscribedOk");
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);

      return true;
    }
    if (action.equals("castBtnClick")) {
      this.castBtnClick(callbackContext);
      return true;
    }

    return false;
  }

  private void setupCastListener() {
    this.sessionManagerListener = new SessionManagerListener<CastSession>() {
      @Override
      public void onSessionStarting(CastSession castSession) {

      }

      @Override
      public void onSessionStarted(CastSession castSession, String s) {
        sendSessionEvent("onSessionStarted");
      }

      @Override
      public void onSessionStartFailed(CastSession castSession, int i) {

      }

      @Override
      public void onSessionEnding(CastSession castSession) {

      }

      @Override
      public void onSessionEnded(CastSession castSession, int i) {

      }

      @Override
      public void onSessionResuming(CastSession castSession, String s) {

      }

      @Override
      public void onSessionResumed(CastSession castSession, boolean b) {

      }

      @Override
      public void onSessionResumeFailed(CastSession castSession, int i) {

      }

      @Override
      public void onSessionSuspended(CastSession castSession, int i) {

      }
    };

    this.castContext.getSessionManager().addSessionManagerListener(this.sessionManagerListener, CastSession.class);
  }

  private void sendSessionEvent(String eventName) {
    if (sessionEventsCallbackContext != null) {
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, eventName);
      pluginResult.setKeepCallback(true);
      sessionEventsCallbackContext.sendPluginResult(pluginResult);
    }
  }

  private void castBtnClick(CallbackContext callbackContext) {
    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
            if (!castDialog.isShowing()) {
              castDialog.show();
              callbackContext.success("show cast dialog");
            } else {
              callbackContext.success("cast dialog already showing");
            }
        }
      });
    } catch (Exception e) {
      callbackContext.error("Error: " + e.getMessage());
    }
  }
}