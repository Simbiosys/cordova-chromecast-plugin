package es.simbiosys.cordova.plugin.chromecast;

import android.support.v7.app.MediaRouteChooserDialog;
import android.support.v7.media.MediaRouteSelector;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.cast.framework.CastContext;

import es.simbiosys.cordova.plugin.R;

public class ChromecastPlugin extends CordovaPlugin {

  private CastContext castContext;
  private MediaRouteChooserDialog castDialog;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);
      
      // Initialize chromecast plugin
      castContext = CastContext.getSharedInstance(cordova.getActivity().getApplicationContext());
      MediaRouteSelector selector = castContext.getMergedSelector();
      castDialog = new MediaRouteChooserDialog(cordova.getContext(), R.style.Theme_AppCompat_Dialog);
      castDialog.setRouteSelector(selector);
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