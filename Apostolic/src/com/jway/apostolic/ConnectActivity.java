package com.jway.apostolic;

import java.util.ArrayList;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectActivity extends Activity {
	private static final String LOG_TAG = "ConnectActivity";

	private MainFragment mainFragment;
	
	// working indicator
	private ProgressBar spinner;
	private String linkData;
	
	ImageButton backButton;
	WebView webView;
	TextView titleTextView;

	ListView listView;
    LazyAdapter adapter;
    
	ArrayList<ConnectInfo> connectsList = new ArrayList<ConnectInfo>();
	
	protected void uiInit() {
        // Make sure the progress bar is visible
        //setProgressBarVisibility(true); 		
        spinner = (ProgressBar) findViewById(R.id.progress_large);
        spinner.setVisibility(android.view.View.VISIBLE);
        
        listView=(ListView)findViewById(R.id.list);
        backButton = (ImageButton) findViewById(R.id.back_button);
        webView = (WebView) findViewById(R.id.webView);
        titleTextView = (TextView) findViewById(R.id.title);
        
        titleTextView.setVisibility(android.view.View.VISIBLE);
        
        backButton.setVisibility(android.view.View.INVISIBLE);
        webView.setVisibility(android.view.View.INVISIBLE);
	}
	
   public void backButtonClicked(View v) {
		backButton.setVisibility(android.view.View.INVISIBLE);
		webView.setVisibility(android.view.View.INVISIBLE);
		listView.setVisibility(android.view.View.VISIBLE);
   }
   
   protected void showConnect() {
	   ConnectInfo connect = new ConnectInfo();
	   connect.type = ConnectInfo.Facebook;
	   connect.title = "Facebook";
	   connect.subTitle = "Like Us on Facebook";
	   connect.link = "http://www.facebook.com/officialapostolicassembly";  
	   connectsList.add(connect);
	   
	   connect = new ConnectInfo();
	   connect.type = ConnectInfo.Twitter;
	   connect.title = "Twitter";
	   connect.subTitle = "Follow Apostolic Assembly on Twitter";
	   connect.link = "https://twitter.com/Apostolic_";  
	   connectsList.add(connect);
	   
	   connect = new ConnectInfo();
	   connect.type = ConnectInfo.Email;
	   connect.title = "Email";
	   connect.subTitle = "pr@apostolicassembly.org";
	   connect.link = "pr@apostolicassembly.org";  
	   connectsList.add(connect);
	   
	   connect = new ConnectInfo();
	   connect.type = ConnectInfo.Website;
	   connect.title = "ApostolicAssembly.org";
	   connect.subTitle = "Apostolic Assembly's offical Website";
	   connect.link = "http://www.apostolicassembly.org/";  
	   connectsList.add(connect);
	   
		adapter=new LazyAdapter(ConnectActivity.this, connectsList, LazyAdapter.Connect);
        listView.setAdapter(adapter);
        
        spinner.setVisibility(android.view.View.INVISIBLE);
   }
   
   protected void doConnect(ConnectInfo info) {
       switch (info.type) {
	   	case ConnectInfo.Facebook:
	   		doEmbeddedWebview(info.link);
	   		break;
	   	case ConnectInfo.Twitter:
	   		//doEmbeddedWebview(info.link);//somehow not work!
	   		doWebsite(info.link);
	   		break;
	   	case ConnectInfo.Email:
	   		doEmail(info.link);
	   		break;
	   	case ConnectInfo.Website:
	   		doWebsite(info.link);
	   		break;	
       }
   }

	private void doEmail(String recipient) {
		
   		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{recipient});
		try {
		    startActivity(Intent.createChooser(intent, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_LONG).show();
		}
		
	}
	
	private void doWebsite(String link) {
 		AlertDialog alertView = new AlertDialog.Builder(this).create();
		alertView.setTitle("Confirmation");
		alertView.setMessage("OK to browse:\n" + link);
		linkData = link;
		Log.d("Debug: ", "linkData = " + link);
		alertView.setButton("YES", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		 
		       //here you can add functions 
		    	Log.d("alertDialog: ", "YES to execute!");
	    			
	        	Uri uriUrl = Uri.parse(linkData); 
	        	startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
		    } }); 
		alertView.setButton2("CANCEL", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		 
		       //here you can add functions
		    	  Log.d("alertDialog: ", "NO to execute!");
		    } }); 
		alertView.show();
		
	}
	
	private void doEmbeddedWebview(String link) {
		spinner.setVisibility(android.view.View.VISIBLE);
		backButton.setVisibility(android.view.View.VISIBLE);
		webView.setVisibility(android.view.View.INVISIBLE);
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setPluginsEnabled(true);

		webView.setWebViewClient(new WebViewClient(){

		    @Override
		    public void onPageFinished(WebView view, String url){
		    	spinner.setVisibility(android.view.View.INVISIBLE);
		    	webView.setVisibility(android.view.View.VISIBLE);
		    }

		    
		    @Override
		    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		    	//Log.d(LOG_TAG, "onReceivedSslError");
		    	Toast.makeText(getApplicationContext(), "onReceivedSslError", Toast.LENGTH_LONG).show();//seems not been called at all?
		        handler.proceed(); // Ignore SSL certificate errors
		    }
		  });
		
		Log.d(LOG_TAG, "url = " + link);
		webView.loadUrl(link);		
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		
		//fbSetup(savedInstanceState);
		
		uiInit();
		
		DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    float factor = metrics.density;
	    if (factor >= 2.0) {
	    	ImageView header = (ImageView) findViewById(R.id.header);
	    	header.setImageDrawable(getResources().getDrawable(R.drawable.aa_header_540x44));//aa_header_540x44
	    }
		
		showConnect();
		
        // Click event for single list row
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> _parent, View view,
					int position, long id) {
				//DebugUtil.toast(getApplicationContext(), "Click on " + position);
				
				ConnectInfo info = connectsList.get(position);
				doConnect(info);
			}
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

}
