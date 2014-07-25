package com.jway.apostolic;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SermonsActivity extends Activity {
	private static final String LOG_TAG = "SermonsActivity";
	public static final int Posting = 0;
	public static final int Listening = 1;
	// All static variables
	static final String URL = "http://app.apostolicassembly.org/index.php/feed/";
	// XML node keys
	static final String KEY_ITEM = "item"; // parent node
	static final String KEY_CATEGORY = "category";
	static final String KEY_TITLE = "title";
	static final String KEY_THUMBNAIL = "thumbnail";
	static final String KEY_FEATUREDIMG = "featuredimg";
	static final String KEY_CREATOR = "dc:creator";
	static final String KEY_AUDIO = "audio";
	static final String KEY_DATE = "pubDate";
	
	// working indicator
	private ProgressBar spinner;
	
	RelativeLayout parent;
	ImageView logo;
	ImageButton backButton;
	ImageButton refreshButton;
	ImageButton listenButton;
	ImageView featuredImage;
	WebView webView;
	TextView titleTextView;
	TextView creatorTextView;
	TextView dateTextView;
	RelativeLayout sharingBar;
	
	ListView listView;
    LazyAdapter adapter;
    
    public ImageLoader imageLoader; 
    
	ArrayList<FeedItem> itemsList = new ArrayList<FeedItem>();
	private FeedItem lastFeedItem = null;
	private int lastAction = 0;
	
	protected void uiInit() {
        // Make sure the progress bar is visible
        //setProgressBarVisibility(true); 
		imageLoader=new ImageLoader(getApplicationContext());
		parent = (RelativeLayout) findViewById(R.id.parent);
		logo = (ImageView) findViewById(R.id.logo);
		
	   	titleTextView = (TextView) findViewById(R.id.title);
	   	creatorTextView = (TextView) findViewById(R.id.creator);
    	dateTextView = (TextView) findViewById(R.id.date);
		
        spinner = (ProgressBar) findViewById(R.id.progress_large);
        spinner.setVisibility(android.view.View.VISIBLE);
        
        listView=(ListView)findViewById(R.id.sermons_list);
        backButton = (ImageButton) findViewById(R.id.back_button);
        refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        listenButton = (ImageButton) findViewById(R.id.listen_button);
        featuredImage = (ImageView) findViewById(R.id.featuredImage);
        webView = (WebView) findViewById(R.id.webView);
        sharingBar = (RelativeLayout) findViewById(R.id.sharing_bar);
        
        backButton.setVisibility(android.view.View.INVISIBLE);
        listenButton.setVisibility(android.view.View.INVISIBLE);
        featuredImage.setVisibility(android.view.View.INVISIBLE);
        webView.setVisibility(android.view.View.INVISIBLE);
        sharingBar.setVisibility(android.view.View.INVISIBLE);
        
        titleTextView.setVisibility(android.view.View.INVISIBLE);
        creatorTextView.setVisibility(android.view.View.INVISIBLE);
        dateTextView.setVisibility(android.view.View.INVISIBLE);    
	}
	
	public void refreshButtonClicked(View v) {
		new loadRSSFeedItems().execute("Sermons");
	}
	
   public void backButtonClicked(View v) {
	   if (lastAction == Posting) {
		   backButton.setVisibility(android.view.View.INVISIBLE);
		   listenButton.setVisibility(android.view.View.INVISIBLE);
		   featuredImage.setVisibility(android.view.View.INVISIBLE);
		   titleTextView.setVisibility(android.view.View.INVISIBLE);
		   creatorTextView.setVisibility(android.view.View.INVISIBLE);
		   dateTextView.setVisibility(android.view.View.INVISIBLE);
		   sharingBar.setVisibility(android.view.View.INVISIBLE);
		   
		   listView.setVisibility(android.view.View.VISIBLE);
		   refreshButton.setVisibility(android.view.View.VISIBLE);
		   logo.setVisibility(android.view.View.VISIBLE);
	   }
	   /*
	   else if (lastAction == Listening) {
		   //backButton.setVisibility(android.view.View.INVISIBLE);
		   webView.setVisibility(android.view.View.INVISIBLE);
		   
		   featuredImage.setVisibility(android.view.View.VISIBLE);
		   listenButton.setVisibility(android.view.View.VISIBLE);
		   
		   lastAction = Posting;
	   }
	   */
   }
   
   public void fbButtonClicked(View v) {
   	
   	DebugUtil.toast(this, "Click on Facebook Button");
  	MainActivity parent = (MainActivity) getParent();
	parent.fbPost(lastFeedItem.title);
	
   }
   
   public void twButtonClicked(View v) {
   	DebugUtil.toast(this, "Click on Twitter Button");
  	MainActivity parent = (MainActivity) getParent();
	parent.twPost(lastFeedItem.title);
   }
   
   public void emailButtonClicked(View v) {
	   	DebugUtil.toast(this, "Click on Email Button");
	   	
   		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		//intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{recipient});
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Apostolic Assembly sharing");     
		intent.putExtra(android.content.Intent.EXTRA_TEXT, lastFeedItem.title); 
		try {
		    startActivity(Intent.createChooser(intent, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_LONG).show();
		}
   }
   
   public void listenButtonClicked(View v) {
	
	///*
	//the following sometimes works!(actually 4 out of 5 work)
	//AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	//am.setStreamMute(AudioManager.STREAM_MUSIC, false);
   	Uri uriUrl = Uri.parse(lastFeedItem.audio); 
	startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
	//*/
	
	/*
	// the following not working!
	   lastAction = Listening;
	   featuredImage.setVisibility(android.view.View.INVISIBLE);
	   listenButton.setVisibility(android.view.View.INVISIBLE);
	   
	   webView.setVisibility(android.view.View.VISIBLE);
	   
	  webView.getSettings().setJavaScriptEnabled(true);//this line is must!
	  webView.getSettings().setPluginsEnabled(true);
	  webView.setWebViewClient(new WebViewClient(){
	    @Override
	    public void onPageFinished(WebView view, String url){
	    	spinner.setVisibility(android.view.View.INVISIBLE);
	    }
	  });

	  spinner.setVisibility(android.view.View.VISIBLE);
	  webView.loadUrl(lastFeedItem.audio);
	  Log.d(LOG_TAG, "Audio's url = " + lastFeedItem.audio);
	*/
   }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sermons);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		uiInit();
		
		DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    float factor = metrics.density;
	    if (factor >= 2.0) {
	    	ImageView header = (ImageView) findViewById(R.id.header);
	    	header.setImageDrawable(getResources().getDrawable(R.drawable.aa_header_540x44));//aa_header_540x44
	    }
		
		new loadRSSFeedItems().execute("Sermons");
		
        // Click event for single list row
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> _parent, View view,
					int position, long id) {
				//DebugUtil.toast(getApplicationContext(), "Click on " + position);
				/*
	            // Starting new intent, testing only
                Intent intent = new Intent(getApplicationContext(), DiscoverPostActivity.class);
                //FeedItem item = itemsList.get(position);
                //DebugUtil.toast(getApplicationContext(), "link = " + item.link);
                intent.putExtra("FEEDITEM",itemsList.get(position));
                startActivity(intent);
                */
				
				lastFeedItem = itemsList.get(position);
				lastAction = Posting;
				new showSermonsPostPage().execute("Showing Post Page");
				
			}
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
	class loadRSSFeedItems extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			spinner.setVisibility(android.view.View.VISIBLE);
			refreshButton.setEnabled(false);
		}

		/**
		 * getting all recent articles and showing them in listview
		 * */
		@Override
		protected String doInBackground(String... args) {
			
			String typeString = args[0];
			
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
					feedItem.audio = parser.getValue(e, KEY_AUDIO);
					Log.i(LOG_TAG, "audio = " + feedItem.audio);
					feedItem.creator = parser.getValue(e, KEY_CREATOR);
					feedItem.thumbnailURL = parser.getValue(e, KEY_THUMBNAIL);
					feedItem.featuredImageURL = parser.getValue(e, KEY_FEATUREDIMG);
					
					String[] temp = parser.getValue(e, KEY_DATE).split(" ");
					if (temp.length > 3) {
						feedItem.year = temp[3];
						feedItem.month = temp[2];
						feedItem.day = temp[1];
					}
					
					// adding HashList to ArrayList
					feedItem.type = FeedItem.Sermons;
					itemsList.add(feedItem);
				}
			}
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed items into listview
					 * */

					// Getting adapter by passing xml data ArrayList
			        //adapter=new LazyAdapter(SermonsActivity.this, itemsList, LazyAdapter.Sermons);
					adapter=new LazyAdapter(SermonsActivity.this, itemsList, LazyAdapter.Sermons);
			        listView.setAdapter(adapter);	
				}
			});
			
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String args) {

			spinner.setVisibility(android.view.View.INVISIBLE);
			refreshButton.setEnabled(true);
		}
	}

	class showSermonsPostPage extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			spinner.setVisibility(android.view.View.VISIBLE);
			backButton.setVisibility(android.view.View.VISIBLE);
			listenButton.setVisibility(android.view.View.VISIBLE);
			titleTextView.setVisibility(android.view.View.VISIBLE);
			creatorTextView.setVisibility(android.view.View.VISIBLE);
			dateTextView.setVisibility(android.view.View.VISIBLE);
			sharingBar.setVisibility(android.view.View.VISIBLE);
			
			titleTextView.setText(lastFeedItem.title);
			creatorTextView.setText(lastFeedItem.creator);
			String date = lastFeedItem.month + " " + lastFeedItem.day + ", " + lastFeedItem.year;
			dateTextView.setText(date);
			
			listView.setVisibility(android.view.View.INVISIBLE);
			refreshButton.setVisibility(android.view.View.INVISIBLE);
			logo.setVisibility(android.view.View.INVISIBLE);
		}

		/**
		 * getting all recent articles and showing them in listview
		 * */
		@Override
		protected String doInBackground(String... args) {
			
			String typeString = args[0];

			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					
					imageLoader.DisplayImage(lastFeedItem.featuredImageURL, featuredImage);

				}
			});
			
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String args) {

			spinner.setVisibility(android.view.View.INVISIBLE);
			featuredImage.setVisibility(android.view.View.VISIBLE);			
		}
	}
	
	
	@Override
	public void onResume()
	{
	    super.onResume();
	    
	    //Toast.makeText(getApplicationContext(), "OnResume(Sermoms)", Toast.LENGTH_LONG).show();

	    //AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	    //am.setStreamMute(AudioManager.STREAM_MUSIC, true);
	   
	}
}
