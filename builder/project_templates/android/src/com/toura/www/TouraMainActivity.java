package com.toura.www;

import org.apache.cordova.DroidGap;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.google.ads.*;
import com.toura.www.push.IntentReceiver;
import android.widget.LinearLayout;
import android.content.res.Configuration;
import android.util.Log;
import java.util.Vector;
import java.util.List;

public class TouraMainActivity extends DroidGap {
  private boolean isInForeground;
  private List pcListeners;
  public final static String PROP_ORIENTATION = "orientation";

  public WebView getAppView() {
    return appView;
  }

  /*
   * Disable trackball navigation etc.
   */
  @Override
  public boolean onTrackballEvent(MotionEvent event) {
  	return true;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    /* This code may be replaced by mulberry/builder/project.rb
     * If that 'R.drawable.splash' is present at all in this file when those resources
     * aren't present, which in many cases they will not be, the build will
     * fail. So we conditionally remove that code based on the presence
     * of load screens.
     * */
    /*!!! Do not remove this comment */
    super.setIntegerProperty("splashscreen", R.drawable.splash);
    super.loadUrl("file:///android_asset/www/index.html",10000);
    /* Do not remove this comment !!!*/

    /* Galaxy Tab enables pinch/zoom on the entire webview by
       default, thus you can zoom everywhere. Setting this to
       true on regular phone devices seems to have no effect,
       so unfortunately we can't use it for magic pinch/zoom
       in image detail etc. Anyway, disable for sake of
       Galaxy Tab
    */
    WebSettings ws = super.appView.getSettings();
    ws.setSupportZoom(false);
    ws.setBuiltInZoomControls(false);
    IntentReceiver.setTouraMainActivity(this);
  }

  public boolean isInForeground() {
      return isInForeground;
  }

  public void showAlert(String message) {
    appView.loadUrl("javascript: " + createShowAlertScript(message));
  }

  @Override
  protected void onPause() {
      super.onPause();
      isInForeground = false;
  }

  @Override
  protected void onResume() {
      super.onResume();
      isInForeground = true;
      Intent intent = getIntent();
      if (intent.hasExtra("alert")) {
        String url = "javascript:document.addEventListener('deviceready', function() {dojo.subscribe('/app/started', function() { " + createShowAlertScript(intent.getStringExtra("alert")) + " });}, false);";
        Log.i(TouraApplication.LOG_TAG, "Showing alert in TouraMainActivity.onResume()! url: " + url);
        appView.loadUrl(url);
      }
  }

  private String createShowAlertScript(String message) {
    return "mulberry.app.Notifications.notify({alert:'" + message.replace("'", "\\'") + "'});";
  }

  public LinearLayout getLayout() {
    return super.root;
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // Checks the orientation of the screen
    
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Object oldOrientation = "portrait";
        Object newOrientation = "landscape";
        firePropertyChange(PROP_ORIENTATION, oldOrientation, newOrientation);
    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        Object oldOrientation = "landscape";
        Object newOrientation = "portrait";
        firePropertyChange(PROP_ORIENTATION, oldOrientation, newOrientation);
    }
    
  }

  public void addPropertyChangeListener(PropertyChangeListener listen) {
    if( listen != null ) {
      if( pcListeners == null ) {
        pcListeners = new Vector();
      }
      
      //add the listener if it has not already been added.
      if( pcListeners.contains(listen) == false ) {
        pcListeners.add(listen);
      }
    }
  }

  public void removePropertyChangeListener(PropertyChangeListener listen) {
    if( listen != null && pcListeners != null ) {
      pcListeners.remove(listen);
    }
  }

  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    if( pcListeners != null && pcListeners.size() > 0 ) {
      PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
      for( int ndx=0; ndx < pcListeners.size(); ndx++) {
        PropertyChangeListener listener = (PropertyChangeListener)pcListeners.get(ndx);
        listener.propertyChange(evt);
      }
    }
  }

  public String getOrientation() {
    if(super.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      return "landscape";
    } else {
      return "portrait";
    }
  }


}
