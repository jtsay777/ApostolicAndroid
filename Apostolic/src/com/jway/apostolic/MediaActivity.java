package com.jway.apostolic;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.jway.apostolic.SermonsActivity.loadRSSFeedItems;

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
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MediaActivity extends Activity {
	private static final String LOG_TAG = "MediaActivity";
	public static final int MediaMainPage = 0;
	public static final int MediaPostPage = 1;


	// All static variables
	static final String URL = "http://app.apostolicassembly.org/index.php/feed/";
	// XML node keys
	static final String KEY_ITEM = "item"; // parent node
	static final String KEY_CATEGORY = "category";
	static final String KEY_TITLE = "title";
	static final String KEY_THUMBNAIL = "thumbnail";
	static final String KEY_CREATOR = "dc:creator";
	static final String KEY_VIDEO = "video";
	static final String KEY_DATE = "pubDate";
	
	// working indicator
	private ProgressBar spinner;
	
	ImageButton backButton;
	ImageButton refreshButton;
	ImageButton watchButton;
	//WebView webView;
	//WebView webViewPost;
	TextView titleTextView;
	TextView creatorTextView;
	TextView dateTextView;
	RelativeLayout sharingBar;
	ImageView featuredImage;
	
	ListView listView;
    LazyAdapter adapter;
    
    public ImageLoader imageLoader; 
    
	ArrayList<FeedItem> itemsList = new ArrayList<FeedItem>();
	private FeedItem lastFeedItem = null;
	private int lastMediaPage = MediaMainPage;
	
	protected void uiInit() {
        // Make sure the progress bar is visible
        //setProgressBarVisibility(true); 
		imageLoader=new ImageLoader(getApplicationContext());
		
        spinner = (ProgressBar) findViewById(R.id.progress_large);
        spinner.setVisibility(android.view.View.VISIBLE);
        
        listView=(ListView)findViewById(R.id.media_list);
        backButton = (ImageButton) findViewById(R.id.back_button);
        refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        //webView = (WebView) findViewById(R.id.webView);
        //webViewPost = (WebView) findViewById(R.id.webViewPost);
        
	   	titleTextView = (TextView) findViewById(R.id.title);
	   	creatorTextView = (TextView) findViewById(R.id.creator);
    	dateTextView = (TextView) findViewById(R.id.date);
    	sharingBar = (RelativeLayout) findViewById(R.id.sharing_bar);
    	watchButton = (ImageButton) findViewById(R.id.watch_button);
    	featuredImage = (ImageView) findViewById(R.id.featuredImage);
        
        backButton.setVisibility(android.view.View.INVISIBLE);
        //webView.setVisibility(android.view.View.INVISIBLE);
        //webViewPost.setVisibility(android.view.View.INVISIBLE);
        
        titleTextView.setVisibility(android.view.View.INVISIBLE);
        creatorTextView.setVisibility(android.view.View.INVISIBLE);
        dateTextView.setVisibility(android.view.View.INVISIBLE);
        sharingBar.setVisibility(android.view.View.INVISIBLE);
        watchButton.setVisibility(android.view.View.INVISIBLE);
        featuredImage.setVisibility(android.view.View.INVISIBLE);
	}
	
	public void refreshButtonClicked(View v) {
		new loadRSSFeedItems().execute("Media");
	}
	
	 public void backButtonClicked(View v) {
	    backButton.setVisibility(android.view.View.INVISIBLE);
	    sharingBar.setVisibility(android.view.View.INVISIBLE);
	    watchButton.setVisibility(android.view.View.INVISIBLE);
	    featuredImage.setVisibility(android.view.View.INVISIBLE);
	    
        //webViewPost.setVisibility(android.view.View.INVISIBLE);	  
        //webViewPost.removeAllViews();
        
        titleTextView.setVisibility(android.view.View.INVISIBLE);
        creatorTextView.setVisibility(android.view.View.INVISIBLE);
        dateTextView.setVisibility(android.view.View.INVISIBLE);
        
        listView.setVisibility(android.view.View.VISIBLE);
        refreshButton.setVisibility(android.view.View.VISIBLE);
        //webView.setVisibility(android.view.View.VISIBLE);
        
        lastMediaPage = MediaMainPage;
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
   
   public void watchButtonClicked(View v) {
	      String url = lastFeedItem.video;
	      Log.d("YoutubePlayer", url);
	      int slashIndex = url.lastIndexOf('/');
	      String videoId = url.substring(slashIndex + 1);
	      Log.e("watchButtonClicked", videoId);
	      
	      //need fine tune the final videoId in some cases
	      if (videoId.startsWith("watch?v=")) {
	    	  int firstAmpersand = videoId.indexOf("&");
	    	  videoId = videoId.substring("watch?v=".length(), firstAmpersand);
	    	  Log.e("watchButtonClicked", videoId);
	      }
	      if (videoId.endsWith("?")) {
	    	  videoId = videoId.substring(0, videoId.length()-1);
	    	  Log.e("watchButtonClicked", videoId);
	      }
	     
	      Intent videoActivity = new Intent(getApplicationContext(), VideoActivity.class);
	      videoActivity.putExtra("id", videoId);
	      startActivity(videoActivity);
   }
	 
	class showMediaPostPage extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			spinner.setVisibility(android.view.View.VISIBLE);
			backButton.setVisibility(android.view.View.VISIBLE);
			titleTextView.setVisibility(android.view.View.VISIBLE);
			creatorTextView.setVisibility(android.view.View.VISIBLE);
			dateTextView.setVisibility(android.view.View.VISIBLE);
			sharingBar.setVisibility(android.view.View.VISIBLE);
			watchButton.setVisibility(android.view.View.VISIBLE);
			featuredImage.setVisibility(android.view.View.VISIBLE);
			
			titleTextView.setText(lastFeedItem.title);
			creatorTextView.setText(lastFeedItem.creator);
			String date = lastFeedItem.month + " " + lastFeedItem.day + ", " + lastFeedItem.year;
			dateTextView.setText(date);
			
			listView.setVisibility(android.view.View.INVISIBLE);
			refreshButton.setVisibility(android.view.View.INVISIBLE);
			//webView.setVisibility(android.view.View.INVISIBLE);
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
				      String url = lastFeedItem.video;
				      
				      int slashIndex = url.lastIndexOf('/');
				      String videoId = url.substring(slashIndex + 1);
					String videoImageUrl = "http://img.youtube.com/vi/"+videoId+"/hqdefault.jpg";
					Log.d("MediaActivity", videoImageUrl);
					imageLoader.DisplayImage(videoImageUrl, featuredImage);
				}
			});
			
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String args) {

			spinner.setVisibility(android.view.View.INVISIBLE);
			
			/*
			webViewPost.getSettings().setJavaScriptEnabled(true);
			webViewPost.getSettings().setPluginsEnabled(true);
			webViewPost.setWebViewClient(new WebViewClient(){
			    @Override
			    public void onPageFinished(WebView view, String url){
			    	spinner.setVisibility(android.view.View.INVISIBLE);
			    	webViewPost.setVisibility(android.view.View.VISIBLE);
			    }
			  });
			
			webViewPost.loadUrl(lastFeedItem.video);
			Log.d(LOG_TAG, "Video's url = " + lastFeedItem.video);
			*/
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
					feedItem.video = parser.getValue(e, KEY_VIDEO);
					Log.i(LOG_TAG, "audio = " + feedItem.audio);
					feedItem.creator = parser.getValue(e, KEY_CREATOR);
					feedItem.thumbnailURL = parser.getValue(e, KEY_THUMBNAIL);
					
					String[] temp = parser.getValue(e, KEY_DATE).split(" ");
					if (temp.length > 3) {
						feedItem.year = temp[3];
						feedItem.month = temp[2];
						feedItem.day = temp[1];
					}
					
					// adding HashList to ArrayList
					feedItem.type = FeedItem.Media;
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
					adapter=new LazyAdapter(MediaActivity.this, itemsList, LazyAdapter.Media);
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
			
			/*
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setPluginsEnabled(true);
			webView.setWebViewClient(new WebViewClient(){
			    @Override
			    public void onPageFinished(WebView view, String url){
			    	spinner.setVisibility(android.view.View.INVISIBLE);
			    	webView.setVisibility(android.view.View.VISIBLE);
			    }
			  });
			
			FeedItem item = itemsList.get(0);
			webView.loadUrl(item.video);
			Log.d(LOG_TAG, "Video's url = " + item.video);
			*/
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		uiInit();
		
		DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    float factor = metrics.density;
	    if (factor >= 2.0) {
	    	ImageView header = (ImageView) findViewById(R.id.header);
	    	header.setImageDrawable(getResources().getDrawable(R.drawable.aa_header_540x44));//aa_header_540x44
	    }
		
		new loadRSSFeedItems().execute("Media");
		
		
        // Click event for single list row
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> _parent, View view,
					int position, long id) {
				//DebugUtil.toast(getApplicationContext(), "Click on " + position);
				
				
				lastFeedItem = itemsList.get(position);
				lastMediaPage = MediaPostPage;
				new showMediaPostPage().execute("Showing Post Page");
				
				//testing
			  	//Uri uriUrl = Uri.parse(lastFeedItem.video); 
				//startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
				
			}
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
	@Override
	public void onPause()
	{
	    super.onPause();
	    //Toast.makeText(getApplicationContext(), "OnPause(Media)", Toast.LENGTH_LONG).show();
	    
	    /*
	    if (lastMediaPage == MediaMainPage) {
	    	webView.removeAllViews();
	    	webView.setVisibility(android.view.View.INVISIBLE);
	    }
	    else if (lastMediaPage == MediaPostPage) {
	    	webView.removeAllViews();
	    	webViewPost.removeAllViews();
	    	webViewPost.setVisibility(android.view.View.INVISIBLE);
	    }
	    */
	}

	@Override
	public void onResume()
	{
	    super.onResume();
	    
	    /*
	    //Toast.makeText(getApplicationContext(), "OnResume(Media)", Toast.LENGTH_LONG).show();
	    if (itemsList.size() > 0 && lastMediaPage == MediaMainPage) {
	    	webView.setVisibility(android.view.View.VISIBLE);
			FeedItem item = itemsList.get(0);
			webView.loadUrl(item.video);
	    }
	    else if (lastMediaPage == MediaPostPage) {
	    	webViewPost.setVisibility(android.view.View.VISIBLE);
	    	webViewPost.loadUrl(lastFeedItem.video);
	    }
	    */
	}

	/*
	  @Override
	  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
	      boolean wasRestored) {
	    if (!wasRestored) {
	      //player.cueVideo("wKJ9KzGQq0w");
	      player.cueVideo("tP6yVP7vnr0");
	      //player.cueVideo("http://www.youtube.com/embed/WE54MzrYpHE");

	      String url = itemsList.get(0).video;
	      Log.d("YoutubePlayer", url);
	      int slashIndex = url.lastIndexOf('/');
	      player.cueVideo(url.substring(slashIndex + 1));
	    }
	  }

	  @Override
	  protected YouTubePlayer.Provider getYouTubePlayerProvider() {
	    return (YouTubePlayerView) findViewById(R.id.youtube_view);
	  }
	 */
}
