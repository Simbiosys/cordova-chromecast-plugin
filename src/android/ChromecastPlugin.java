package es.simbiosys.cordova.plugin.chromecast;

import androidx.annotation.NonNull;
import androidx.mediarouter.app.MediaRouteChooserDialog;
import androidx.mediarouter.media.MediaRouteSelector;

import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadOptions;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChromecastPlugin extends CordovaPlugin {

  private static final String TAG = "ChromecastPlugin";

  private CastContext castContext;
  private MediaRouteChooserDialog castDialog;
  private CastSession castSession;
  private SessionManagerListener<CastSession> sessionManagerListener;

  private CallbackContext eventsCallbackContext;
  private ResultCallback<RemoteMediaClient.MediaChannelResult> resultCallback;
  private CallbackContext resultCallbackContext;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);

      // Initialize callback contexts
      this.eventsCallbackContext = null;
      this.resultCallbackContext = null;
      
      // Initialize cast instance and devices dialog
      this.castContext = CastContext.getSharedInstance(cordova.getActivity().getApplicationContext());
      MediaRouteSelector selector = castContext.getMergedSelector();
      this.castDialog = new MediaRouteChooserDialog(
              cordova.getContext(),
              androidx.appcompat.R.style.Theme_AppCompat
      );
      this.castDialog.setRouteSelector(selector);

      // Set up cast events listener
      setupCastListener();

      // Set up result callback listener
      setupResultCallback();
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("subscribeToSessionEvents")) {
      // Save events callback context to communicate Java with Javascript
      this.eventsCallbackContext = callbackContext;

      // Return OK result
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "subscribedOk");
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);

      return true;
    } else if (action.equals("getCastState")) {
      this.getCastState(callbackContext);
      return true;
    } else if (action.equals("getCastDeviceFriendlyName")) {
      this.getCastDeviceFriendlyName(callbackContext);
      return true;
    }else if (action.equals("castBtnClick")) {
      this.castBtnClick(callbackContext);
      return true;
    } else if (action.equals("loadRemoteMedia")) {
      String url = args.optString(0);
      int streamType = args.optInt(1);
      String contentType = args.optString(2);
      int position = args.optInt(3);
      boolean autoPlay = args.optBoolean(4);
      this.loadRemoteMedia(callbackContext, url, streamType, contentType, position, autoPlay);
      return true;
    } else if (action.equals("loadRemoteMediaQueue")) {
      JSONArray urls = args.optJSONArray(0);
      int streamType = args.optInt(1);
      String contentType = args.optString(2);
      int startIndex = args.optInt(3);
      int repeatMode = args.optInt(4);
      int position = args.optInt(5);
      this.loadRemoteMediaQueue(callbackContext, urls, streamType, contentType, startIndex, repeatMode, position);
      return true;
    } else if (action.equals("play")) {
      this.play(callbackContext);
      return true;
    } else if (action.equals("pause")) {
      this.pause(callbackContext);
      return true;
    } else if (action.equals("stop")) {
      this.stop(callbackContext);
      return true;
    } else if (action.equals("stepForward")) {
      long step = args.optLong(0);
      this.stepForward(step, callbackContext);
      return true;
    } else if (action.equals("stepBackward")) {
      long step = args.optLong(0);
      this.stepBackward(step, callbackContext);
      return true;
    } else if (action.equals("forwards")){
      this.forwards(callbackContext);
    } else if (action.equals("backwards")) {
      this.backwards(callbackContext);
    } else if (action.equals("endCurrentSession")) {
      this.endCurrentSession(callbackContext);
      return true;
    } else if (action.equals("getPlayerState")) {
      this.getPlayerState(callbackContext);
      return true;
    }

    return false;
  }

  private void setupCastListener() {
    this.sessionManagerListener = new SessionManagerListener<CastSession>() {
      @Override
      public void onSessionStarting(CastSession session) {
        Log.d(TAG, "onSessionStarting");

        triggerJsEvent("onSessionStarting");
      }

      @Override
      public void onSessionStarted(CastSession session, String s) {
        Log.d(TAG, "onSessionStarted");

        castSession = session;
        triggerJsEvent("onSessionStarted");
      }

      @Override
      public void onSessionStartFailed(CastSession session, int i) {
        Log.d(TAG, "onSessionStartFailed");

        triggerJsEvent("onSessionStartFailed");
      }

      @Override
      public void onSessionEnding(CastSession session) {
        Log.d(TAG, "onSessionEnding");

        triggerJsEvent("onSessionEnding");
      }

      @Override
      public void onSessionEnded(CastSession session, int i) {
        Log.d(TAG, "onSessionEnded");

        triggerJsEvent("onSessionEnded");
      }

      @Override
      public void onSessionResuming(CastSession session, String s) {
        Log.d(TAG, "onSessionResuming");

        triggerJsEvent("onSessionResuming");
      }

      @Override
      public void onSessionResumed(CastSession session, boolean b) {
        Log.d(TAG, "onSessionResumed");

        castSession = session;
        triggerJsEvent("onSessionResumed");
      }

      @Override
      public void onSessionResumeFailed(CastSession session, int i) {
        Log.d(TAG, "onSessionResumeFailed");

        triggerJsEvent("onSessionResumeFailed");
      }

      @Override
      public void onSessionSuspended(CastSession session, int i) {
        Log.d(TAG, "onSessionSuspended");

        triggerJsEvent("onSessionSuspended");
      }
    };

    this.castContext.getSessionManager().addSessionManagerListener(this.sessionManagerListener, CastSession.class);
  }

  private void setupResultCallback() {
    this.resultCallback = new ResultCallback<RemoteMediaClient.MediaChannelResult>() {
      @Override
      public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {
        Status status = mediaChannelResult.getStatus();

        if (status.isSuccess()) {
          resultCallbackContext.success("operation was successful");
        } else if (status.isCanceled()) {
          resultCallbackContext.error("operation was canceled");
        } else if (status.isInterrupted()) {
          resultCallbackContext.error("operation was interrupted");
        } else {
          resultCallbackContext.error(status.toString());
        }
      }
    };
  }

  private void triggerJsEvent(String eventName) {
    if (eventsCallbackContext != null) {
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, eventName);
      pluginResult.setKeepCallback(true);
      eventsCallbackContext.sendPluginResult(pluginResult);
    }
  }

  private void getCastState(CallbackContext callbackContext) {
    if (castContext == null) {
      callbackContext.error("No cast context");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          callbackContext.success(castContext.getCastState());
        }
      });
    } catch (Exception e) {
      callbackContext.error("Error: " + e.getMessage());
    }
  }

  private void getCastDeviceFriendlyName(CallbackContext callbackContext) {
    if (castSession == null) {
      callbackContext.error("No cast session active");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          callbackContext.success(castSession.getCastDevice().getFriendlyName());
        }
      });
    } catch (Exception e) {
      callbackContext.error("Error: " + e.getMessage());
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

  private void loadRemoteMedia(CallbackContext callbackContext, String url, int streamType, String contentType, int position, boolean autoPlay) {
    // Set result callback context
    resultCallbackContext = callbackContext;

    if (castSession == null) {
      resultCallbackContext.error("No cast session active");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
          if (remoteMediaClient == null) {
            resultCallbackContext.error("No remote media client");
            return;
          }
          remoteMediaClient.load(buildMediaInfo(url, streamType, contentType),
                  new MediaLoadOptions.Builder()
                          .setAutoplay(autoPlay)
                          .setPlayPosition(position).build())
          .setResultCallback(resultCallback);
        }
      });
    } catch (Exception e) {
      resultCallbackContext.error("Error: " + e.getMessage());
    }
  }

  private void loadRemoteMediaQueue(CallbackContext callbackContext, JSONArray urls, int streamType, String contentType, int startIndex, int repeatMode, int position) {
    // Set result callback context
    resultCallbackContext = callbackContext;

    if (castSession == null) {
      resultCallbackContext.error("No cast session active");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
          if (remoteMediaClient == null) {
            resultCallbackContext.error("No remote media client");
            return;
          }

          // Create MediaQueueItem[] from urls parameter
          List<MediaQueueItem> queueItemsList = new ArrayList<MediaQueueItem>();
          for (int i=0; i<urls.length(); i++) {
            String url = urls.optString(i);
            MediaQueueItem item = new MediaQueueItem.Builder(
                    buildMediaInfo(url, streamType, contentType)
            ).build();
            queueItemsList.add(item);
          }
          MediaQueueItem[] queueItems = new MediaQueueItem[queueItemsList.size()];
          queueItems = queueItemsList.toArray(queueItems);

          // Load queue
          remoteMediaClient.queueLoad(
                  queueItems,
                  startIndex,
                  repeatMode,
                  position,
                  null
          ).setResultCallback(resultCallback);
        }
      });
    } catch (Exception e) {
      resultCallbackContext.error("Error: " + e.getMessage());
    }
  }

  private MediaInfo buildMediaInfo(String url, int streamType, String contentType) {
    int[] streamTypes = new int[]{
            MediaInfo.STREAM_TYPE_INVALID,
            MediaInfo.STREAM_TYPE_NONE,
            MediaInfo.STREAM_TYPE_BUFFERED,
            MediaInfo.STREAM_TYPE_LIVE
    };

    if (!Arrays.asList(streamTypes).contains(streamType)) {
      streamType = MediaInfo.STREAM_TYPE_INVALID;
    }

    return new MediaInfo.Builder(url)
            .setStreamType(streamType)
            .setContentType(contentType)
            .build();
  }

  private void play(CallbackContext callbackContext) {
    // Set result callback context
    resultCallbackContext = callbackContext;

    if (castSession == null) {
      resultCallbackContext.error("No cast session active");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
          if (remoteMediaClient == null) {
            resultCallbackContext.error("No remote media client");
            return;
          }

          if (remoteMediaClient.isPlaying()) {
            resultCallbackContext.error("Media is already playing");
            return;
          }

          remoteMediaClient.play().setResultCallback(resultCallback);
        }
      });
    }  catch (Exception e) {
      resultCallbackContext.error("Error: " + e.getMessage());
    }
  }

  private void pause(CallbackContext callbackContext) {
    // Set result callback context
    resultCallbackContext = callbackContext;

    if (castSession == null) {
      resultCallbackContext.error("No cast session active");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
          if (remoteMediaClient == null) {
            resultCallbackContext.error("No remote media client");
            return;
          }

          if (remoteMediaClient.isPaused()) {
            resultCallbackContext.error("Media is already paused");
            return;
          }

          remoteMediaClient.pause().setResultCallback(resultCallback);
        }
      });
    }  catch (Exception e) {
      resultCallbackContext.error("Error: " + e.getMessage());
    }
  }

  private void stop(CallbackContext callbackContext) {
    // Set result callback context
    resultCallbackContext = callbackContext;

    if (castSession == null) {
      resultCallbackContext.error("No cast session active");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
          if (remoteMediaClient == null) {
            resultCallbackContext.error("No remote media client");
            return;
          }

          remoteMediaClient.stop().setResultCallback(resultCallback);
        }
      });
    }  catch (Exception e) {
      resultCallbackContext.error("Error: " + e.getMessage());
    }
  }

  private void stepForward(long stepInMillis, CallbackContext callbackContext) {
    // Set result callback context
    resultCallbackContext = callbackContext;

    if (castSession == null) {
      resultCallbackContext.error("No cast session active");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
          if (remoteMediaClient == null) {
            resultCallbackContext.error("No remote media client");
            return;
          }

          long currentPosition = remoteMediaClient.getMediaStatus().getStreamPosition();
          long position = currentPosition + stepInMillis;
          remoteMediaClient.seek(position).setResultCallback(resultCallback);
        }
      });
    }  catch (Exception e) {
      resultCallbackContext.error("Error: " + e.getMessage());
    }
  }

  private void stepBackward(long stepInMillis, CallbackContext callbackContext) {
    // Set result callback context
    resultCallbackContext = callbackContext;

    if (castSession == null) {
      resultCallbackContext.error("No cast session active");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
          if (remoteMediaClient == null) {
            resultCallbackContext.error("No remote media client");
            return;
          }

          long currentPosition = remoteMediaClient.getMediaStatus().getStreamPosition();
          long position = currentPosition - stepInMillis;
          remoteMediaClient.seek(position).setResultCallback(resultCallback);
        }
      });
    }  catch (Exception e) {
      resultCallbackContext.error("Error: " + e.getMessage());
    }
  }

  private void forwards(CallbackContext callbackContext) {
    // Set result callback context
    resultCallbackContext = callbackContext;

    if (castSession == null) {
      resultCallbackContext.error("No cast session active");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
          if (remoteMediaClient == null) {
            resultCallbackContext.error("No remote media client");
            return;
          }

          remoteMediaClient.queueNext(null).setResultCallback(resultCallback);
        }
      });
    }  catch (Exception e) {
      resultCallbackContext.error("Error: " + e.getMessage());
    }
  }

  private void backwards (CallbackContext callbackContext) {
    // Set result callback context
    resultCallbackContext = callbackContext;

    if (castSession == null) {
      resultCallbackContext.error("No cast session active");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
          if (remoteMediaClient == null) {
            resultCallbackContext.error("No remote media client");
            return;
          }

          remoteMediaClient.queuePrev(null).setResultCallback(resultCallback);
        }
      });
    }  catch (Exception e) {
      resultCallbackContext.error("Error: " + e.getMessage());
    }
  }

  private void endCurrentSession(CallbackContext callbackContext) {
    if (castContext == null) {
      callbackContext.error("No cast context");
      return;
    }

    try {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          castContext.getSessionManager().endCurrentSession(true);
          callbackContext.success("End current session");
        }
      });
    } catch (Exception e) {
      callbackContext.error("Error: " + e.getMessage());
    }
  }

  private void getPlayerState(CallbackContext callbackContext) {
    if (castSession == null) {
      callbackContext.error("No session active");
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

          callbackContext.success(remoteMediaClient.getPlayerState());
        }
      });
    } catch (Exception e) {
      callbackContext.error("Error: " + e.getMessage());
    }
  }
}