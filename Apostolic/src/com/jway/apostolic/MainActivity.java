package com.jway.apostolic;

import java.util.HashMap;

import com.nostra13.socialsharing.common.AuthListener;
import com.nostra13.socialsharing.facebook.FacebookFacade;
import com.nostra13.socialsharing.twitter.TwitterFacade;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class MainActivity extends TabActivity {
	Dialog splashDialog;
	
	//facebook stuff
	private FacebookFacade facebook;
	private FacebookEventObserver facebookEventObserver;
	
	//twitter stuff
	private TwitterFacade twitter;
	private TwitterEventObserver twitterEventObserver;
	
	@Override
	public void onStart() {
		super.onStart();
		
		facebookEventObserver.registerListeners(this);
		twitterEventObserver.registerListeners(this);
		
		/*
		facebookEventObserver.registerListeners(this);
		if (!facebook.isAuthorized()) {
			facebook.authorize();
		}
		
		twitterEventObserver.registerListeners(this);
		if (!twitter.isAuthorized()) {
			twitter.authorize();
		}
		*/
	}

	@Override
	public void onStop() {
		facebookEventObserver.unregisterListeners();
		twitterEventObserver.unregisterListeners();
		super.onStop();
	}
	
	protected void showSplashScreen() {
	    splashDialog = new Dialog(this, R.style.SplashScreen);
	    splashDialog.setContentView(R.layout.splashscreen);
	    splashDialog.setCancelable(false);
	    splashDialog.show();
	     
	    // Set Runnable to remove splash screen just in case
	    final Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {
	      @Override
	      public void run() {
	  	    if (splashDialog != null) {
		        splashDialog.dismiss();
		        splashDialog = null;
		    }
	      }
	    }, 3000);//three seconds
	}
	
	public void fbPost(final String msg) {
	    	
	    	//DebugUtil.toast(this, "Click on Facebook Button");
	    	
			if (facebook.isAuthorized()) {
				facebook.publishMessage(msg);
			} else {
				// Start authentication dialog and publish message after successful authentication
				facebook.authorize(new AuthListener() {
					@Override
					public void onAuthSucceed() {
						facebook.publishMessage(msg);
					}

					@Override
					public void onAuthFail(String error) { // Do nothing
						Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
						Log.e("Error: ", "onAuthFail!");
					}
				});
			}
			
	}
	
    public void twPost(final String msg) {
    	//DebugUtil.toast(this, "Click on Twitter Button");
    	
		if (twitter.isAuthorized()) {
			//twitter.publishMessage(messageView.getText().toString());
			twitter.publishMessage(msg);		
			//finish();
		} else {
			// Start authentication dialog and publish message after successful authentication
			twitter.authorize(new AuthListener() {
				@Override
				public void onAuthSucceed() {
					//twitter.publishMessage(messageView.getText().toString());
					twitter.publishMessage(msg);	
					//finish();
				}

				@Override
				public void onAuthFail(String error) { // Do nothing
					Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show(); 
				}
			});
		}
    }
	
	private void twSetup() {
		twitter = new TwitterFacade(this, Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET);
		twitterEventObserver = TwitterEventObserver.newInstance();
	}
	
    private void fbSetup() { 	
		facebook = new FacebookFacade(this, Constants.FACEBOOK_APP_ID);
		facebookEventObserver = FacebookEventObserver.newInstance();
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        showSplashScreen();
        
		//facebook
		fbSetup();
		
		//twitter
		twSetup();
        
        TabHost tabHost = getTabHost();
        
        // Tab for Discover
        TabSpec discoverSpec = tabHost.newTabSpec("Discover");
        discoverSpec.setIndicator("Discover", getResources().getDrawable(R.drawable.icon_discover_tab));
        Intent discoverIntent = new Intent(this, DiscoverActivity.class);
        discoverSpec.setContent(discoverIntent);
        
        // Tab for Sermons
        TabSpec sermonsSpec = tabHost.newTabSpec("Sermons");
        sermonsSpec.setIndicator("Sermons", getResources().getDrawable(R.drawable.icon_sermons_tab));
        Intent sermonsIntent = new Intent(this, SermonsActivity.class);
        sermonsSpec.setContent(sermonsIntent);
        
        // Tab for Locator
        TabSpec locatorSpec = tabHost.newTabSpec("Locator");
        locatorSpec.setIndicator("Locator", getResources().getDrawable(R.drawable.icon_locator_tab));
        Intent locatorIntent = new Intent(this, LocatorActivity.class);
        locatorSpec.setContent(locatorIntent);
        
        // Tab for Media
        TabSpec mediaSpec = tabHost.newTabSpec("Media");
        mediaSpec.setIndicator("Media", getResources().getDrawable(R.drawable.icon_media_tab));
        Intent mediaIntent = new Intent(this, MediaActivity.class);
        mediaSpec.setContent(mediaIntent);
        
        // Tab for Connect
        TabSpec connectSpec = tabHost.newTabSpec("Connect");
        connectSpec.setIndicator("Connect", getResources().getDrawable(R.drawable.icon_connect_tab));
        Intent connectIntent = new Intent(this, ConnectActivity.class);
        connectSpec.setContent(connectIntent);
        
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(discoverSpec); // Adding Discover tab
        tabHost.addTab(sermonsSpec); // Adding Sermons tab
        tabHost.addTab(locatorSpec); // Adding Locator tab
        tabHost.addTab(mediaSpec); // Adding Media tab
        tabHost.addTab(connectSpec); // Adding Connect tab
    }
}