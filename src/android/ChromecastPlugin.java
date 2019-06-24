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

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadOptions;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;

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
    } else if (action.equals("castBtnClick")) {
      this.castBtnClick(callbackContext);
      return true;
    } else if (action.equals("loadRemoteMedia")) {
      String url = args.optString(0);
      String contentType = args.optString(1);
      int position = args.optInt(2);
      boolean autoPlay = args.optBoolean(3);
      this.loadRemoteMedia(callbackContext, url, contentType, position, autoPlay);
    }

    return false;
  }

  private void setupCastListener() {
    this.sessionManagerListener = new SessionManagerListener<CastSession>() {
      @Override
      public void onSessionStarting(CastSession session) {
        sendSessionEvent("onSessionStarting");
      }

      @Override
      public void onSessionStarted(CastSession session, String s) {
        castSession = session;
        sendSessionEvent("onSessionStarted");
      }

      @Override
      public void onSessionStartFailed(CastSession session, int i) {
        sendSessionEvent("onSessionStartFailed");
      }

      @Override
      public void onSessionEnding(CastSession session) {
        sendSessionEvent("onSessionEnding");
      }

      @Override
      public void onSessionEnded(CastSession session, int i) {
        sendSessionEvent("onSessionEnded");
      }

      @Override
      public void onSessionResuming(CastSession session, String s) {
        sendSessionEvent("onSessionResuming");
      }

      @Override
      public void onSessionResumed(CastSession session, boolean b) {
        sendSessionEvent("onSessionResumed");
      }

      @Override
      public void onSessionResumeFailed(CastSession session, int i) {
        sendSessionEvent("onSessionResumeFailed");
      }

      @Override
      public void onSessionSuspended(CastSession session, int i) {
        sendSessionEvent("onSessionSuspended");
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

  private void loadRemoteMedia(CallbackContext callbackContext, String url, String contentType, int position, boolean autoPlay) {
    if (castSession == null) {
      callbackContext.error("No cast session active");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
          if (remoteMediaClient == null) {
            callbackContext.error("No remote media client");
            return;
          }
          remoteMediaClient.load(buildMediaInfo(url, contentType),
                  new MediaLoadOptions.Builder()
                          .setAutoplay(autoPlay)
                          .setPlayPosition(position).build());
          callbackContext.success("Media loaded successfully");
        }
      });
    } catch (Exception e) {
      callbackContext.error("Error: " + e.getMessage());
    }
  }

  private MediaInfo buildMediaInfo(String url, String contentType) {
    return new MediaInfo.Builder(url)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType(contentType)
            .build();

  }
}