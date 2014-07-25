package com.jway.apostolic;

//import com.chauhai.android.batsg.util.DebugUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

//checking testing
public class DiscoverActivity extends Activity implements OnCheckedChangeListener, Runnable{
	// All static variables
	static final String URL = "http://app.apostolicassembly.org/index.php/feed/";
	// XML node keys
	static final String KEY_ITEM = "item"; // parent node
	static final String KEY_CATEGORY = "category";
	static final String KEY_TITLE = "title";
	static final String KEY_LINK = "link";
	static final String KEY_CREATOR = "dc:creator";
	static final String KEY_DATE = "pubDate";
	
	//private TextView messageView;

	ImageButton fbButton;
	
	// working indicator
	private ProgressBar spinner;
	
	RelativeLayout parent;
	ImageView logo;
	WebView webView;
	ImageButton backButton, prevButton, nextButton;
	SegmentedControlView segmentedControl;
	TextView titleTextView;
	TextView dateTextView;
	RelativeLayout sharingBar;
	int currentSelection = 0;

	ListView list;
    LazyAdapter adapter;
    
	ArrayList<FeedItem> itemsList = new ArrayList<FeedItem>();
	private FeedItem lastItem;
    
    protected ArrayList<FeedItem> getNews() {
		ArrayList<FeedItem> itemsList = new ArrayList<FeedItem>();

		XMLParser parser = new XMLParser();
		String xml = parser.getXmlFromUrl(URL); // getting XML from URL
		Document doc = parser.getDomElement(xml); // getting DOM element
		
		//Log.e("getNews()", xml);
		
		NodeList nl = doc.getElementsByTagName(KEY_ITEM);
		Log.e("nl.getLength()=", Integer.toString(nl.getLength()));
		// looping through all item nodes <item>
		int newsCount = 0;
		for (int i = 0; i < nl.getLength(); i++) {
			// creating new FeedItem
			FeedItem feedItem = new FeedItem();
			Element e = (Element) nl.item(i);
			
			if (parser.getValue(e, KEY_CATEGORY).equals("News")) {
				newsCount++;
				// adding properties to feedItem
				feedItem.title = parser.getValue(e, KEY_TITLE);
				feedItem.link = parser.getValue(e, KEY_LINK);
				feedItem.creator = parser.getValue(e, KEY_CREATOR);
				
				String[] temp = parser.getValue(e, KEY_DATE).split(" ");
				if (temp.length > 3) {
					feedItem.year = temp[3];
					feedItem.month = temp[2];
					feedItem.day = temp[1];
				}
				
				// adding HashList to ArrayList
				feedItem.type = FeedItem.News;
				itemsList.add(feedItem);
			}
			
		}
		Log.e("newsCount=", Integer.toString(newsCount));
		
		return itemsList;
    }
    
	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discover);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		uiInit();
		
		DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    float factor = metrics.density;
	    Log.e("factor=", Float.toString(factor));
	    if (factor >= 2.0) {
	    	ImageView header = (ImageView) findViewById(R.id.header);
	    	header.setImageDrawable(getResources().getDrawable(R.drawable.aa_header_540x44));//aa_header_540x44
	    	
	       	//RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) header.getLayoutParams();
	    	//lp.setMargins(0, 0, 0, 0);//public void setMargins (int left, int top, int right, int bottom)
	    }
		
	     // Set segmented control listener.
	      ((SegmentedControlView) findViewById(R.id.segmented_control))
	          .setOnCheckedChangeListener(this);
	      
	      //new loadRSSFeedItems().execute("News");//will crash

	        // Click event for single list row
	        list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> _parent, View view,
						int position, long id) {
					//DebugUtil.toast(getApplicationContext(), "Click on " + position);
					/*
		            // Starting new intent
	                Intent intent = new Intent(getApplicationContext(), DiscoverPostActivity.class);
	                FeedItem item = itemsList.get(position);
	                //DebugUtil.toast(getApplicationContext(), "link = " + item.link);
	                intent.putExtra("FEEDITEM",itemsList.get(position));
	                startActivity(intent);
	                */
					FeedItem item = itemsList.get(position);
					lastItem = item;
					currentSelection = position;
					
					titleTextView.setVisibility(android.view.View.VISIBLE);
					titleTextView.setText(item.title);
					
					dateTextView.setVisibility(android.view.View.VISIBLE);
					String date = item.month + " " + item.day + ", " + item.year;
					dateTextView.setText(date);
					
					sharingBar.setVisibility(android.view.View.VISIBLE);
					
					webView.setVisibility(android.view.View.VISIBLE);
					spinner.setVisibility(android.view.View.VISIBLE);
					webView.loadUrl(item.link);
					backButton.setVisibility(android.view.View.VISIBLE);
					
					segmentedControl.setVisibility(android.view.View.INVISIBLE);
					logo.setVisibility(android.view.View.INVISIBLE);
					
					Resources res = getResources();
					Drawable drawable = res.getDrawable(R.drawable.aaapp_bg_plain_high); 
					parent.setBackgroundDrawable(drawable);
				}
			});	
	        
	        // Make sure the progress bar is visible
	        //setProgressBarVisibility(true);        
	        spinner = (ProgressBar) findViewById(R.id.progress_large);
	        
	        // initializing and starting a new local Thread object 
	        spinner.setVisibility(android.view.View.VISIBLE);
	        Thread currentThread = new Thread(this);
	        currentThread.start(); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
	protected void uiInit() {
		parent = (RelativeLayout) findViewById(R.id.parent);
		logo = (ImageView) findViewById(R.id.logo);
		webView = (WebView) findViewById(R.id.web_engine);
		backButton = (ImageButton) findViewById(R.id.back_button);
	   	list=(ListView)findViewById(R.id.list);
    	segmentedControl = (SegmentedControlView) findViewById(R.id.segmented_control);
    	titleTextView = (TextView) findViewById(R.id.title);
    	dateTextView = (TextView) findViewById(R.id.date);
    	sharingBar = (RelativeLayout) findViewById(R.id.sharing_bar);
    	prevButton = (ImageButton) findViewById(R.id.prev_button);
    	nextButton = (ImageButton) findViewById(R.id.next_button);
    	
	    webView.setVisibility(android.view.View.INVISIBLE);
	    backButton.setVisibility(android.view.View.INVISIBLE);
	    titleTextView.setVisibility(android.view.View.INVISIBLE);
	    sharingBar.setVisibility(android.view.View.INVISIBLE);
	    
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setPluginsEnabled(true);
		
		webView.setWebViewClient(new WebViewClient(){
		    @Override
		    public void onPageFinished(WebView view, String url){
		    	spinner.setVisibility(android.view.View.INVISIBLE);
		    	//webView.setVisibility(android.view.View.VISIBLE);
		    }
		  });
	}
	
    public void backButtonClicked(View v) {
    	//DebugUtil.toast(this, "Click on Back Button");
    	backButton.setVisibility(android.view.View.INVISIBLE);
    	webView.setVisibility(android.view.View.INVISIBLE);
    	sharingBar.setVisibility(android.view.View.INVISIBLE);
    	titleTextView.setVisibility(android.view.View.INVISIBLE);
    	dateTextView.setVisibility(android.view.View.INVISIBLE);
    	
    	segmentedControl.setVisibility(android.view.View.VISIBLE);
    	logo.setVisibility(android.view.View.VISIBLE);
    	
		Resources res = getResources();
		//Drawable drawable = res.getDrawable(R.drawable.aaapp_bg_feed_high);
		Drawable drawable = res.getDrawable(R.drawable.aaapp_bg_plain_high);
		parent.setBackgroundDrawable(drawable);
    }
    
    public void prevButtonClicked(View v) {
    	//DebugUtil.toast(this, "Click on Prev Button");
        if (currentSelection > 0) {
            currentSelection--;
            nextButton.setEnabled(true);
            if (currentSelection == 0) {
                prevButton.setEnabled(false);
            }
            
            FeedItem item = itemsList.get(currentSelection);
            titleTextView.setText(item.title);
            String date = item.month + " " + item.day + ", " + item.year;
			dateTextView.setText(date);
			spinner.setVisibility(android.view.View.VISIBLE);
            webView.loadUrl(item.link);
        }
    }
    
    public void nextButtonClicked(View v) {
    	//DebugUtil.toast(this, "Click on Next Button");
    	
        if (currentSelection < itemsList.size()-1) {
            currentSelection++;
            prevButton.setEnabled(true);
            if (currentSelection == itemsList.size()-1) {
                nextButton.setEnabled(false);
            }
            
            FeedItem item = itemsList.get(currentSelection); 
            titleTextView.setText(item.title);
            String date = item.month + " " + item.day + ", " + item.year;
			dateTextView.setText(date);
			spinner.setVisibility(android.view.View.VISIBLE);
            webView.loadUrl(item.link);
        }

    }
    
    public void fbButtonClicked(View v) {
    	
    	DebugUtil.toast(this, "Click on Facebook Button");
    	
       	MainActivity parent = (MainActivity) getParent();
    	parent.fbPost(lastItem.title);
    }
    
    public void twButtonClicked(View v) {
    	DebugUtil.toast(this, "Click on Twitter Button");
    	
    	MainActivity parent = (MainActivity) getParent();
    	parent.twPost(lastItem.title);
    }

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		    //DebugUtil.toast(this, "Click on " + ((RadioButton) findViewById(checkedId)).getText());
		    String selection = (String) ((RadioButton) findViewById(checkedId)).getText();
		    if (selection.equals("News")) {
		    	new loadRSSFeedItems().execute("News");
		    }
		    else if (selection.equals("Events")) {
		    	new loadRSSFeedItems().execute("Events");
		    }
		    else if (selection.equals("Blog")) {
		    	new loadRSSFeedItems().execute("Blog");
		    }
	}
	
	class loadRSSFeedItems extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			spinner.setVisibility(android.view.View.VISIBLE);
		}

		/**
		 * getting all recent articles and showing them in listview
		 * */
		@Override
		protected String doInBackground(String... args) {
			
			String typeString = args[0];
			int type;
			
			if (typeString.equals("News")) {
				type = FeedItem.News;
			}
			else if (typeString.equals("Events")) {
				type = FeedItem.Events;
			}
			else if (typeString.equals("Blog")) {
				type = FeedItem.Blog;
			}
			
			itemsList.clear();
			
			XMLParser parser = new XMLParser();
			String xml = parser.getXmlFromUrl(URL); // getting XML from URL
			Document doc = parser.getDomElement(xml); // getting DOM element
			
			NodeList nl = doc.getElementsByTagName(KEY_ITEM);
			// looping through all item nodes <item>
			for (int i = 0; i < nl.getLength(); i++) {
				// creating new FeedItem
				FeedItem feedItem = new FeedItem();
				Element e = (Element) nl.item(i);
				if (parser.getValue(e, KEY_CATEGORY).equals(typeString)) {
					// adding properties to feedItem
					feedItem.title = parser.getValue(e, KEY_TITLE);
					feedItem.link = parser.getValue(e, KEY_LINK);
					feedItem.creator = parser.getValue(e, KEY_CREATOR);
					
					String[] temp = parser.getValue(e, KEY_DATE).split(" ");
					if (temp.length > 3) {
						feedItem.year = temp[3];
						feedItem.month = temp[2];
						feedItem.day = temp[1];
					}
					
					// adding HashList to ArrayList
					feedItem.type = FeedItem.News;
					itemsList.add(feedItem);
				}
			}
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed items into listview
					 * */

					
					list=(ListView)findViewById(R.id.list);
					
					// Getting adapter by passing xml data ArrayList
			        //adapter=new LazyAdapter(DiscoverActivity.this, itemsList);
			        adapter=new LazyAdapter(DiscoverActivity.this, itemsList, LazyAdapter.Discover);
			        list.setAdapter(adapter);
			        
				    if (currentSelection == 0) {
				    	prevButton.setEnabled(false);
				    }
				    
				    if (currentSelection == itemsList.size() - 1) {
				        nextButton.setEnabled(false);
				    }
	
				}
			});
			
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String args) {

			spinner.setVisibility(android.view.View.INVISIBLE);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		itemsList = getNews();
		threadHandler.sendEmptyMessage(0);
		
		/*
		//testing the following Message with an object - works
		FeedItem item = itemsList.get(0);
		Message msg = Message.obtain();
		msg.obj = item;
		threadHandler.sendMessage(msg);
		*/
	}
	
	// Receives Thread's messages, interprets them and acts on the 
	// current Activity as needed
	private Handler threadHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// whenever the Thread notifies this handler we have 
			// only this behavior
			//threadModifiedText.setText("my text changed by the thread");
			list=(ListView)findViewById(R.id.list);
			
			// Getting adapter by passing xml data ArrayList
	        //adapter=new LazyAdapter(DiscoverActivity.this, itemsList);
	        adapter=new LazyAdapter(DiscoverActivity.this, itemsList, LazyAdapter.Discover);
	        list.setAdapter(adapter);
			spinner.setVisibility(android.view.View.INVISIBLE);
			
		    if (currentSelection == 0) {
		    	prevButton.setEnabled(false);
		    }
		    
		    if (currentSelection == itemsList.size() - 1) {
		        nextButton.setEnabled(false);
		    }

			
			/*
			//testing the sent Message with an object - works
			FeedItem item = (FeedItem) msg.obj;
			DebugUtil.toast(getApplicationContext(), "Message: " + item.title);
			*/
		}
	};
}
