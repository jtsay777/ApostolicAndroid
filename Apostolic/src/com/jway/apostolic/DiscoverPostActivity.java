package com.jway.apostolic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class DiscoverPostActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discover_post);
		
		// get intent data
		Intent intent = getIntent();
		//FeedItem feedItem = (FeedItem)intent.getSerializableExtra("FEEDITEM");

		FeedItem feedItem = intent.getExtras().getParcelable("FEEDITEM");
		if (feedItem != null) {
			//DebugUtil.toast(getApplicationContext(), "Link = " + feedItem.link);
		}
		WebView engine = (WebView) findViewById(R.id.web_engine);  
		//engine.loadUrl("http://mobile.tutsplus.com");
		//engine.loadUrl(feedItem.link);
		engine.getSettings().setJavaScriptEnabled(true);//this line is must!
		engine.setWebViewClient(new WebViewClient(){
			    @Override
			    public void onPageFinished(WebView view, String url){
			      //webView.loadUrl(url);
			    }
			  });
		engine.loadUrl(feedItem.audio);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_discover_post, menu);
		return true;
	}

}
