package com.jway.apostolic;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrilmottier.polaris.Annotation;
import com.cyrilmottier.polaris.MapCalloutView;
import com.cyrilmottier.polaris.PolarisMapView;
import com.cyrilmottier.polaris.PolarisMapView.OnAnnotationSelectionChangedListener;
import com.cyrilmottier.polaris.PolarisMapView.OnRegionChangedListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;


public class LocatorActivity extends MapActivity implements OnCheckedChangeListener, OnRegionChangedListener, OnAnnotationSelectionChangedListener {
//public class LocatorActivity extends MapActivity implements OnCheckedChangeListener, OnAnnotationSelectionChangedListener {
	public static final int Nearby = 0;
	public static final int Search = 1;
	public static final int Map = 2;

	
	// XML node keys
	static final String KEY_CHURCH = "church"; // parent node
	static final String KEY_PHONE = "phone";
	static final String KEY_CHURCH_NAME = "church_name";
	static final String KEY_LATITUDE = "latitude";
	static final String KEY_LONGITUDE = "longitude";
	static final String KEY_PASTOR = "pastor";
	static final String KEY_FIRSTNAME = "firstname";
	static final String KEY_LASTNAME = "lastname";
	static final String KEY_MIDDLEINIT = "middleinit";
	static final String KEY_DISTRICT_NAME = "district_name";
	static final String KEY_URL = "url";
	static final String KEY_EMAIL = "email";
	static final String KEY_ADDRESS = "address";
	static final String KEY_STREET = "street";
	static final String KEY_CITY = "city";
	static final String KEY_STATE = "state";
	static final String KEY_POSTALCODE = "postalCode";
	
	ArrayList<Church> churchesList = new ArrayList<Church>();
	ArrayList<Church> searchResultsList = new ArrayList<Church>();
	ArrayList<Church> lastChurchesList;
	ArrayList<ChurchInfo> churchInfoList = new ArrayList<ChurchInfo>();
    LazyAdapter adapter;
	
	// GPSTracker class
/*	GPSTracker gps;
	double latitude = 0;
	double longitude = 0;
	
	Location mostRecentLocation;*/
	
	private Handler doNearbyHandler;
	private Runnable doNearbyListening;
	private Handler stopLocationHandler;
	private Runnable stopLocationListening;
    private LocationManager locationManager;
    private LocationListener listenerCoarse;
    private LocationListener listenerFine;
    static boolean firsttime = true;

    // Holds the most up to date location.
    private Location currentLocation;
    
    // Set to false when location services are unavailable.
    private boolean locationAvailable = true;
    
    // detect if we are in church info page so that we can disable doNearBy occurring in church info page.
    private boolean inChurchInfoPage = false;

	
	int lastSegment = Nearby;
	boolean gettingDirection = false;
	String phoneData, linkData;
	int radius = 50;
	int churchesFound = 0;
	
	// working indicator
	private ProgressBar spinner;
	
	private static final String LOG_TAG = "LocatorActivity";
	private RelativeLayout parent;
	ImageView logo;
	private RelativeLayout searchTextLayout;
	private SegmentedControlView segmentedControl;
	private ListView listView, infoListView, searchListView;
	private TextView pastorTextView;
	private TextView districtTextView;
	private TextView churchTextView;
	private ImageButton backButton;
	private EditText searchText;
	private WebView webView;
	
	private PolarisMapView mMapView;
	private FrameLayout mapViewContainer;
	private SeekBar seekBar;
	private TextView foundTextView;
	
/*	private Handler locationHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			//Toast.makeText(getApplicationContext(), "onLocationChanged notification", Toast.LENGTH_LONG).show();
		}
	};*/
	
	//location stuff
    private void registerLocationListeners() {
		locationManager = (LocationManager) 
			getSystemService(LOCATION_SERVICE);
		
		// Initialize criteria for location providers
		Criteria fine = new Criteria();
		fine.setAccuracy(Criteria.ACCURACY_FINE);
		Criteria coarse = new Criteria();
		coarse.setAccuracy(Criteria.ACCURACY_COARSE);
		
		// Get at least something from the device,
		// could be very inaccurate though
		//currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(fine, true));
		currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(coarse, true));
		if ( currentLocation == null ) { // ? only S3, S4 gets this....
			currentLocation =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		}

			
		
		if (currentLocation != null) {
			Log.e(LOG_TAG, "getLastKnowLocation != null");
			if (firsttime) {
				//Toast.makeText(getApplicationContext(), "isFirsttime0", Toast.LENGTH_SHORT).show();
				doNearby();
				firsttime = false;				
			}
		}
		else {
			Log.e(LOG_TAG, "getLastKnowLocation == null");
		}
			
		
		if (listenerFine == null || listenerCoarse == null)
			createLocationListeners();
			
		// Will keep updating about every 500 ms until 
		// accuracy is about 1000 meters to get quick fix.
		locationManager.requestLocationUpdates(
			locationManager.getBestProvider(coarse, true), 
			0, 0, listenerCoarse);//500, 1000, listenerCoarse);
		// Will keep updating about every 500 ms until 
		// accuracy is about 50 meters to get accurate fix.
		locationManager.requestLocationUpdates(
			locationManager.getBestProvider(fine, true), 
			0, 0, listenerFine);//500, 10, listenerFine);
	}  
    
	private void createLocationListeners() {
		listenerCoarse = new LocationListener() {
			public void onStatusChanged(String provider, 
				int status, Bundle extras) {
				switch(status) {
				case LocationProvider.OUT_OF_SERVICE:
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					locationAvailable = false;
					break;
				case LocationProvider.AVAILABLE:
					locationAvailable = true;
				}
			}
			public void onProviderEnabled(String provider) {
			}
			public void onProviderDisabled(String provider) {
			}
			public void onLocationChanged(Location location) {
				
				if (firsttime) {
					currentLocation = location;
					//Toast.makeText(getApplicationContext(), "isFirsttime", Toast.LENGTH_SHORT).show();
					doNearby();
					firsttime = false;				
				}
				//currentLocation = location;
				if (location.getAccuracy() < 500 && 
					location.hasAccuracy()) {
					//locationManager.removeUpdates(this);
					if (currentLocation == null) {
						currentLocation = location;
					}
					else if (location.getAccuracy() < currentLocation.getAccuracy()) {
						currentLocation = location;
					}
					
/*					Toast.makeText(getBaseContext(),
							"COARSE2 Lat: " + location.getLatitude() +
							"\nLng: " + location.getLongitude()+
							"\nProvider: "+location.getProvider()+
							"\nAccuracy: "+location.getAccuracy(),
							Toast.LENGTH_LONG).show();*/
				}
				
/*				Toast.makeText(getBaseContext(),
						"COARSE1 Lat: " + location.getLatitude() +
						"\nLng: " + location.getLongitude()+
						"\nProvider: "+location.getProvider()+
						"\nAccuracy: "+location.getAccuracy(),
						Toast.LENGTH_SHORT).show();*/
				
			}
		};
		
		listenerFine = new LocationListener() {
			public void onStatusChanged(String provider, 
				int status, Bundle extras) {
				switch(status) {
				case LocationProvider.OUT_OF_SERVICE:
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					locationAvailable = false;
					break;
				case LocationProvider.AVAILABLE:
					locationAvailable = true;
				}
			}
			public void onProviderEnabled(String provider) {
			}
			public void onProviderDisabled(String provider) {
			}
			public void onLocationChanged(Location location) {
				//currentLocation = location;
				if (location.getAccuracy() < 50 
					&& location.hasAccuracy()) {
					//locationManager.removeUpdates(this);
					if (location.getAccuracy() < currentLocation.getAccuracy()) {
						currentLocation = location;
					}
					
/*					Toast.makeText(getBaseContext(),
							"Fine2 Lat: " + location.getLatitude() +
							"\nLng: " + location.getLongitude()+
							"\nProvider: "+location.getProvider()+
							"\nAccuracy: "+location.getAccuracy(),
							Toast.LENGTH_LONG).show();*/
				}
				
/*				Toast.makeText(getBaseContext(),
						"Fine1 Lat: " + location.getLatitude() +
						"\nLng: " + location.getLongitude()+
						"\nProvider: "+location.getProvider()+
						"\nAccuracy: "+location.getAccuracy(),
						Toast.LENGTH_SHORT).show();*/
				
			}
		};
	}

	
	protected void uiInit() {
		parent = (RelativeLayout) findViewById(R.id.parent);
		logo = (ImageView) findViewById(R.id.logo);
		searchTextLayout = (RelativeLayout) findViewById(R.id.searchTextLayout);
		segmentedControl = (SegmentedControlView) findViewById(R.id.locator_segmented_control);
		pastorTextView = (TextView) findViewById(R.id.pastor);
		districtTextView = (TextView) findViewById(R.id.district_name);
		churchTextView = (TextView) findViewById(R.id.church_name);
		backButton = (ImageButton) findViewById(R.id.back_button);
		searchText = (EditText) findViewById(R.id.searchText);
		webView = (WebView) findViewById(R.id.webView);
		seekBar = (SeekBar) findViewById(R.id.seekbar);
		seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_bg));
		foundTextView = (TextView) findViewById(R.id.churches_found);
		
        spinner = (ProgressBar) findViewById(R.id.progress_large);
        spinner.setVisibility(android.view.View.VISIBLE);
		
		listView=(ListView)findViewById(R.id.church_list);
		infoListView=(ListView)findViewById(R.id.church_info_list);
		searchListView=(ListView)findViewById(R.id.search_list);
		
		mMapView = new PolarisMapView(this, "0rsx4mjCOwSSc-Qsif-Nf6IDvORdUYYRZNNF1HQ");//debug version
		//mMapView = new PolarisMapView(this, "0rsx4mjCOwST_IekqR4Ce-fs4RuhxMsN7w8K5Fg");//release version
        //mMapView.setUserTrackingButtonEnabled(true);//comment out this to save battery life(this will turn on gps)
        mMapView.setOnRegionChangedListenerListener(this);
        mMapView.setOnAnnotationSelectionChangedListener(this);
        
        mapViewContainer = (FrameLayout) findViewById(R.id.map_view_container);
        mapViewContainer.addView(mMapView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
        mapViewContainer.setVisibility(android.view.View.INVISIBLE);
        
	    backButton.setVisibility(android.view.View.INVISIBLE);
	    pastorTextView.setVisibility(android.view.View.INVISIBLE);
	    districtTextView.setVisibility(android.view.View.INVISIBLE);
	    churchTextView.setVisibility(android.view.View.INVISIBLE);
	    infoListView.setVisibility(android.view.View.INVISIBLE);
	    //searchText.setVisibility(android.view.View.INVISIBLE);
	    searchTextLayout.setVisibility(android.view.View.INVISIBLE);
	    searchListView.setVisibility(android.view.View.INVISIBLE);
	    webView.setVisibility(android.view.View.INVISIBLE);
	    logo.setVisibility(android.view.View.INVISIBLE);
	    
	    addKeyListener();
	}
	
	private void addKeyListener() {

		// add a keylistener to keep track user input
		searchText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				// if keydown and "enter" is pressed
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {

					// display a floating message
					//Toast.makeText(LocatorActivity.this, searchText.getText(), Toast.LENGTH_LONG).show();
							
					
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
					
					String criteria = searchText.getText().toString();
					//parsingSearch(criteria);
					new parsingSearchAsync().execute(criteria);
					
					return true;
					
				}

				return false;
			}
		});

	}
	
    public void backButtonClicked(View v) {
    	//DebugUtil.toast(this, "Click on Back Button");
    	if (gettingDirection) {
    		webView.setVisibility(android.view.View.INVISIBLE);
    		gettingDirection = false;
    		return;
    	}
    	
    	backButton.setVisibility(android.view.View.INVISIBLE);
    	pastorTextView.setVisibility(android.view.View.INVISIBLE);
    	districtTextView.setVisibility(android.view.View.INVISIBLE);
    	churchTextView.setVisibility(android.view.View.INVISIBLE);
    	infoListView.setVisibility(android.view.View.INVISIBLE);
    	logo.setVisibility(android.view.View.INVISIBLE);
    	
		segmentedControl.setVisibility(android.view.View.VISIBLE);
    	if (lastSegment == Nearby) {
    		inChurchInfoPage = false;
    		listView.setVisibility(android.view.View.VISIBLE);
    		seekBar.setVisibility(android.view.View.VISIBLE);
    		foundTextView.setVisibility(android.view.View.VISIBLE);
    	}
    	else if (lastSegment == Search) {
    		searchTextLayout.setVisibility(android.view.View.VISIBLE);
    		searchListView.setVisibility(android.view.View.VISIBLE);
    	}
    	else if (lastSegment == Map) {
    		mapViewContainer.setVisibility(android.view.View.VISIBLE);
    	}
    	
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.aaapp_bg_plain_high); 
		parent.setBackgroundDrawable(drawable);
    }
    
    private void showSearch() {
    	searchListView.setVisibility(android.view.View.VISIBLE);
        adapter=new LazyAdapter(LocatorActivity.this, searchResultsList, LazyAdapter.Locator);
        searchListView.setAdapter(adapter);
        
        int count = searchResultsList.size();
		String foundText = count + " Churches found";
		foundTextView.setText(foundText);
    }
    
    
    
    class doNearbyAsync extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			spinner.setVisibility(android.view.View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			prepareData();
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					showNearBy();
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
	
	private void doNearby() {
		
		///*
		if (!inChurchInfoPage) {
			new doNearbyAsync().execute("ChurchesView");
		}
		//*/
		//new doNearbyAsync().execute("ChurchesView");
		
/*		spinner.setVisibility(android.view.View.VISIBLE);
		parsingNearby();
		
		mapViewContainer.setVisibility(android.view.View.INVISIBLE);
		searchListView.setVisibility(android.view.View.INVISIBLE);
		
		seekBar.setVisibility(android.view.View.VISIBLE);
		listView.setVisibility(android.view.View.VISIBLE);
		foundTextView.setVisibility(android.view.View.VISIBLE);
		String foundText = churchesFound + " Churches found within " + radius + " miles";
		foundTextView.setText(foundText);
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
		searchTextLayout.setVisibility(android.view.View.INVISIBLE);
		
		spinner.setVisibility(android.view.View.INVISIBLE);*/
			
	}
	
	private void doSearch() {
		foundTextView.setText("");
		listView.setVisibility(android.view.View.INVISIBLE);
		mapViewContainer.setVisibility(android.view.View.INVISIBLE);
		seekBar.setVisibility(android.view.View.INVISIBLE);
		
		searchListView.setVisibility(android.view.View.VISIBLE);
		
		searchTextLayout.setVisibility(android.view.View.VISIBLE);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);
	}
	
	private void doMap() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
		searchTextLayout.setVisibility(android.view.View.INVISIBLE);
		seekBar.setVisibility(android.view.View.INVISIBLE);
		foundTextView.setVisibility(android.view.View.INVISIBLE);
		
        final ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        int count = 0;
        if (lastSegment == Nearby) {
        	count = churchesList.size();
        	lastChurchesList = churchesList;
        }
        else if (lastSegment == Search) {
        	count = searchResultsList.size();
        	lastChurchesList = searchResultsList;
        }
 
        for (int i = 0; i < count; i++) {
        	Church church = null;
        	if (lastSegment == Nearby)
        		church = churchesList.get(i);
        	else if (lastSegment == Search)
        		church = searchResultsList.get(i);
        	int latitude = (int)(Float.parseFloat(church.latitude)*1E6);
        	int longitude = (int)(Float.parseFloat(church.longitude)*1E6);
        	String address = church.street + ", " + church.city + ", " + church.state + " " + church.postalCode;
            Annotation item =  new Annotation(new GeoPoint(latitude, longitude), church.name, address);
            annotations.add(item);     	
        }

        mMapView.setAnnotations(annotations, R.drawable.map_pin_holed_blue);
        mapViewContainer.setVisibility(android.view.View.VISIBLE);
	}
	
	public void onCheckedChanged(RadioGroup group, int checkedId) {
	    //DebugUtil.toast(this, "Click on " + ((RadioButton) findViewById(checkedId)).getText());
	    String selection = (String) ((RadioButton) findViewById(checkedId)).getText();
	    if (selection.equals("Nearby")) {
	    	//new loadRSSFeedItems().execute("News");
	    	doNearby();
	    	lastSegment = Nearby;
	    }
	    else if (selection.equals("Search")) {
	    	//new loadRSSFeedItems().execute("Events");
	    	doSearch();
	    	lastSegment = Search;
	    }
	    else if (selection.equals("Map")) {
	    	//new loadRSSFeedItems().execute("Blog");
	    	doMap();
	    	lastSegment = Map;
	    }
	}
	
	   class parsingSearchAsync extends AsyncTask<String, String, String> {

			/**
			 * Before starting background thread Show Progress Dialog
			 * */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				
				spinner.setVisibility(android.view.View.VISIBLE);
			}

			@Override
			protected String doInBackground(String... args) {
				// TODO Auto-generated method stub
				String criteria = args[0];
				parsingSearch(criteria);
				
				// updating UI from Background Thread
				runOnUiThread(new Runnable() {
					public void run() {
						showSearch();
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
	
	private void parsingSearch(String criteria) {
		
		XMLParser parser = new XMLParser();
		String temp = criteria.replaceAll(" ", "%20");
		String urlString = "http://aa4god.com/apostolic/churches?criteria=" + temp;
		String xml = parser.getXmlFromUrl(urlString); // getting XML from URL
		Document doc = parser.getDomElement(xml); // getting DOM element
		
		NodeList nl = doc.getElementsByTagName(KEY_CHURCH);
		
		searchResultsList.clear();
		// looping through all item nodes <item>
		for (int i = 0; i < nl.getLength(); i++) {
			// creating new Church
			Church church = new Church();
			Element e = (Element) nl.item(i);

			// adding properties to church
			church.name = parser.getValue(e, KEY_CHURCH_NAME);
			church.district = parser.getValue(e, KEY_DISTRICT_NAME);
			church.phone = parser.getValue(e, KEY_PHONE);
			church.latitude = parser.getValue(e, KEY_LATITUDE);
			church.longitude = parser.getValue(e, KEY_LONGITUDE);
			church.website = parser.getValue(e, KEY_URL);
			church.email = parser.getValue(e, KEY_EMAIL);
			Log.d("Debug: ", "Church = " + church.name);
			
			String firstname = parser.getValue(e, KEY_FIRSTNAME);
			String lastname = parser.getValue(e, KEY_LASTNAME);
			String middleinit = parser.getValue(e, KEY_MIDDLEINIT);
			if (middleinit.length() == 1 && !middleinit.equals(" ")) {
				church.pastor = firstname + " " + middleinit + " " + lastname;
			}
			else {
				church.pastor = firstname + " " + lastname;
			}			
			Log.d("Debug: ", "Pastor = " + church.pastor);
			
			church.street = parser.getValue(e, KEY_STREET);
			church.city = parser.getValue(e, KEY_CITY);
			church.state = parser.getValue(e, KEY_STATE);
			church.postalCode = parser.getValue(e, KEY_POSTALCODE);
			
			Log.d("Debug: ", "Address = " + church.street + ", " + church.city + ", " + church.state + " " + church.postalCode);
			
			// adding church to ArrayList
			searchResultsList.add(church);
		}
		
		//showSearch();
	}
	
	private void showNearBy() {
		
		churchesFound = churchesList.size();
        adapter=new LazyAdapter(LocatorActivity.this, churchesList, LazyAdapter.Locator);
        listView.setAdapter(adapter);
        
		
		mapViewContainer.setVisibility(android.view.View.INVISIBLE);
		searchListView.setVisibility(android.view.View.INVISIBLE);
		
		seekBar.setVisibility(android.view.View.VISIBLE);
		listView.setVisibility(android.view.View.VISIBLE);
		foundTextView.setVisibility(android.view.View.VISIBLE);
		String foundText = churchesFound + " Churches found within " + radius + " miles";
		foundTextView.setText(foundText);
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
		searchTextLayout.setVisibility(android.view.View.INVISIBLE);
		
		spinner.setVisibility(android.view.View.INVISIBLE);
	}
	
	private void prepareData() {
		
		XMLParser parser = new XMLParser();
		
		String urlString = null;
		
		Log.e(LOG_TAG, "parsingNearby()");
		if (currentLocation == null) {
			Log.e(LOG_TAG, "currentLocation == null");
			//urlString = "http://aa4god.com/apostolic/churches?latitude=" + Double.toString(0.0) + "&longitude=" + Double.toString(0.0) + "&radius=" + radius;
			return;
		}
		else {
			urlString = "http://aa4god.com/apostolic/churches?latitude=" + Double.toString(currentLocation.getLatitude()) + "&longitude=" + Double.toString(currentLocation.getLongitude()) + "&radius=" + radius;
		}
		
		//urlString = "http://aa4god.com/apostolic/churches?latitude=" + Double.toString(currentLocation.getLatitude()) + "&longitude=" + Double.toString(currentLocation.getLongitude()) + "&radius=" + radius;
		//String xml = parser.getXmlFromUrl("http://aa4god.com/apostolic/churches?latitude=37.774900&longitude=-122.419400&radius=50"); // getting XML from URL
		//doNearbyHandler.postDelayed(doNearbyListening, 3*1000);//3 seconds, if 3 seconds later spinner is still visible fire doNearby() one more time.
		//spinner.setVisibility(android.view.View.VISIBLE);
		String xml = parser.getXmlFromUrl(urlString);
		//spinner.setVisibility(android.view.View.INVISIBLE);
		
		Document doc = parser.getDomElement(xml); // getting DOM element
		
		NodeList nl = doc.getElementsByTagName(KEY_CHURCH);
		
		churchesList.clear();
		
		// looping through all item nodes <item>
		for (int i = 0; i < nl.getLength(); i++) {
			// creating new Church
			Church church = new Church();
			Element e = (Element) nl.item(i);

			// adding properties to church
			church.name = parser.getValue(e, KEY_CHURCH_NAME);
			church.district = parser.getValue(e, KEY_DISTRICT_NAME);
			church.phone = parser.getValue(e, KEY_PHONE);
			church.latitude = parser.getValue(e, KEY_LATITUDE);
			church.longitude = parser.getValue(e, KEY_LONGITUDE);
			church.website = parser.getValue(e, KEY_URL);
			church.email = parser.getValue(e, KEY_EMAIL);
			Log.d("Debug: ", "Church = " + church.name);
			
			String firstname = parser.getValue(e, KEY_FIRSTNAME);
			String lastname = parser.getValue(e, KEY_LASTNAME);
			String middleinit = parser.getValue(e, KEY_MIDDLEINIT);
			if (middleinit.length() == 1 && !middleinit.equals(" ")) {
				church.pastor = firstname + " " + middleinit + " " + lastname;
			}
			else {
				church.pastor = firstname + " " + lastname;
			}			
			Log.d("Debug: ", "Pastor = " + church.pastor);
			
			church.street = parser.getValue(e, KEY_STREET);
			church.city = parser.getValue(e, KEY_CITY);
			church.state = parser.getValue(e, KEY_STATE);
			church.postalCode = parser.getValue(e, KEY_POSTALCODE);
			
			Log.d("Debug: ", "Address = " + church.street + ", " + church.city + ", " + church.state + " " + church.postalCode);
			
			// adding church to ArrayList
			churchesList.add(church);
		}
		
	}

	private void parsingNearby() {
		
		//if (inChurchInfoPage) return;
		
		XMLParser parser = new XMLParser();
		
		String urlString = null;
		
		Log.e(LOG_TAG, "parsingNearby()");
		if (currentLocation == null) {
			Log.e(LOG_TAG, "currentLocation == null");
			//urlString = "http://aa4god.com/apostolic/churches?latitude=" + Double.toString(0.0) + "&longitude=" + Double.toString(0.0) + "&radius=" + radius;
			return;
		}
		else {
			urlString = "http://aa4god.com/apostolic/churches?latitude=" + Double.toString(currentLocation.getLatitude()) + "&longitude=" + Double.toString(currentLocation.getLongitude()) + "&radius=" + radius;
		}
		
		//urlString = "http://aa4god.com/apostolic/churches?latitude=" + Double.toString(currentLocation.getLatitude()) + "&longitude=" + Double.toString(currentLocation.getLongitude()) + "&radius=" + radius;
		//String xml = parser.getXmlFromUrl("http://aa4god.com/apostolic/churches?latitude=37.774900&longitude=-122.419400&radius=50"); // getting XML from URL
		//doNearbyHandler.postDelayed(doNearbyListening, 3*1000);//3 seconds, if 3 seconds later spinner is still visible fire doNearby() one more time.
		//spinner.setVisibility(android.view.View.VISIBLE);
		String xml = parser.getXmlFromUrl(urlString);
		//spinner.setVisibility(android.view.View.INVISIBLE);
		
		Document doc = parser.getDomElement(xml); // getting DOM element
		
		NodeList nl = doc.getElementsByTagName(KEY_CHURCH);
		
		churchesList.clear();
		
		// looping through all item nodes <item>
		for (int i = 0; i < nl.getLength(); i++) {
			// creating new Church
			Church church = new Church();
			Element e = (Element) nl.item(i);

			// adding properties to church
			church.name = parser.getValue(e, KEY_CHURCH_NAME);
			church.district = parser.getValue(e, KEY_DISTRICT_NAME);
			church.phone = parser.getValue(e, KEY_PHONE);
			church.latitude = parser.getValue(e, KEY_LATITUDE);
			church.longitude = parser.getValue(e, KEY_LONGITUDE);
			church.website = parser.getValue(e, KEY_URL);
			church.email = parser.getValue(e, KEY_EMAIL);
			Log.d("Debug: ", "Church = " + church.name);
			
			String firstname = parser.getValue(e, KEY_FIRSTNAME);
			String lastname = parser.getValue(e, KEY_LASTNAME);
			String middleinit = parser.getValue(e, KEY_MIDDLEINIT);
			if (middleinit.length() == 1 && !middleinit.equals(" ")) {
				church.pastor = firstname + " " + middleinit + " " + lastname;
			}
			else {
				church.pastor = firstname + " " + lastname;
			}			
			Log.d("Debug: ", "Pastor = " + church.pastor);
			
			church.street = parser.getValue(e, KEY_STREET);
			church.city = parser.getValue(e, KEY_CITY);
			church.state = parser.getValue(e, KEY_STATE);
			church.postalCode = parser.getValue(e, KEY_POSTALCODE);
			
			Log.d("Debug: ", "Address = " + church.street + ", " + church.city + ", " + church.state + " " + church.postalCode);
			
			// adding church to ArrayList
			churchesList.add(church);
		}
		
		churchesFound = churchesList.size();
        adapter=new LazyAdapter(LocatorActivity.this, churchesList, LazyAdapter.Locator);
        listView.setAdapter(adapter);
	}
	
	private void polarisTest() {
		
		mMapView = new PolarisMapView(this, "0rsx4mjCOwSSc-Qsif-Nf6IDvORdUYYRZNNF1HQ");
        //mMapView.setUserTrackingButtonEnabled(true);//comment out to save battery life(this will turn on gps)
        mMapView.setOnRegionChangedListenerListener(this);
        mMapView.setOnAnnotationSelectionChangedListener(this);
        
        final ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        
        Annotation item =  new Annotation(new GeoPoint(37774900, -122419400), "San Francisco", "this is subtitle");
        annotations.add(item);
        mMapView.setAnnotations(annotations, R.drawable.map_pin_holed_blue);

        //final FrameLayout mapViewContainer = (FrameLayout) findViewById(R.id.map_view_container);
        mapViewContainer = (FrameLayout) findViewById(R.id.map_view_container);
        mapViewContainer.addView(mMapView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	private void doPhone(String phone) {
		AlertDialog alertView = new AlertDialog.Builder(this).create();
		alertView.setTitle("Confirmation");
		alertView.setMessage("OK to call:\n" + phone);
		phoneData = "tel:" + phone;
		Log.d("phoneData: ", phoneData);
		alertView.setButton("YES", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		 
		       //here you can add functions 
		    	Log.d("alertDialog: ", "YES to execute!");
	    			
	        	Uri uriUrl = Uri.parse(phoneData);//Uri.parse("tel:408.425.2673");//Uri.parse(phoneData); 
	        	startActivity(new Intent(Intent.ACTION_CALL, uriUrl));
		    } }); 
		alertView.setButton2("CANCEL", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		 
		       //here you can add functions
		    	  Log.d("alertDialog: ", "NO to execute!");
		    } }); 
		alertView.show();
		
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
		Log.d("Debug: ", "linkData = " + linkData);
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
	
	private void doChurchInfo(ChurchInfo churchInfo) {
		
		switch (churchInfo.type) {
		case ChurchInfo.Address:
			/*
			//the following works
			if (true) {
			String temp = "http://maps.google.com/maps?saddr="+latitude+","+longitude+"&daddr="+churchInfo.title+","+churchInfo.subTitle;
			Uri uriUrl = Uri.parse(temp);
			
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
			startActivity(launchBrowser);
			}
			*/

			///*
			// the following not working!
			//String urlString = "http://maps.google.com/maps?saddr="+latitude+","+longitude+"&daddr="+churchInfo.title+","+churchInfo.subTitle+"&key=0rsx4mjCOwSSc-Qsif-Nf6IDvORdUYYRZNNF1HQ";
			//String urlString = "http://maps.google.com/maps?saddr="+latitude+","+longitude+"&daddr="+churchInfo.title+","+churchInfo.subTitle;
			String urlString = "http://maps.google.com/maps?saddr="+currentLocation.getLatitude()+","+currentLocation.getLongitude()+"&daddr="+churchInfo.title+","+churchInfo.subTitle;
			Log.d("Debug: ", "Direction's url = " + urlString);
			webView.setVisibility(android.view.View.VISIBLE);
			
			  webView.getSettings().setJavaScriptEnabled(true);//this line is must!
			  //Wait for the page to load then send the location information
			  webView.setWebViewClient(new WebViewClient(){
			    @Override
			    public void onPageFinished(WebView view, String url){
			      //webView.loadUrl(url);
			    }
			  });

			//urlString = urlString.replaceAll(" ", "%20");
			webView.loadUrl(urlString);
			//webView.loadUrl("http://maps.google.com/maps?q=47.404376,8.601478");
			//*/

			gettingDirection = true;;
			break;
		case ChurchInfo.Phone:
			//String phone = churchInfo.title.replaceAll(".", "");
			doPhone(churchInfo.title);
			break;
		case ChurchInfo.Website:
			doWebsite(churchInfo.title);
			break;
		case ChurchInfo.Email:
			doEmail(churchInfo.title);
			break;
		}
	}
	
	/*
			webView.setWebViewClient(new WebViewClient() 
			    {
			        @Override
			        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
			        {
			            // Handle the error
			        }

			        public boolean shouldOverrideUrlLoading(WebView view, String url) {

			            if(url.startsWith("http:") || url.startsWith("https:")) {
			                view.loadUrl(url);
			            }
			            return true;
			        }
			    });
	 */
	
	private void showChurchInfo(Church church) {
		
		segmentedControl.setVisibility(android.view.View.INVISIBLE);
		listView.setVisibility(android.view.View.INVISIBLE);
		searchListView.setVisibility(android.view.View.INVISIBLE);
		searchTextLayout.setVisibility(android.view.View.INVISIBLE);
		mapViewContainer.setVisibility(android.view.View.INVISIBLE);
		seekBar.setVisibility(android.view.View.INVISIBLE);
		foundTextView.setVisibility(android.view.View.INVISIBLE);
		
		logo.setVisibility(android.view.View.VISIBLE);
		backButton.setVisibility(android.view.View.VISIBLE);
		pastorTextView.setVisibility(android.view.View.VISIBLE);
		pastorTextView.setText("Pastor " + church.pastor);
		districtTextView.setVisibility(android.view.View.VISIBLE);
		districtTextView.setText(church.district + " - ");
		churchTextView.setVisibility(android.view.View.VISIBLE);
		churchTextView.setText(church.name);
		
		infoListView.setVisibility(android.view.View.VISIBLE);
		//ArrayList<ChurchInfo> infoList = new ArrayList<ChurchInfo>();
		churchInfoList.clear();
		ChurchInfo info = new ChurchInfo();
		info.type = ChurchInfo.Address;
		info.title = church.street;
		info.subTitle = church.city + ", " + church.state + " " + church.postalCode;
		churchInfoList.add(info);
		
		info = new ChurchInfo();
		info.type = ChurchInfo.Phone;
		info.title = church.phone;
		churchInfoList.add(info);
		
		if (church.website !=null &&  church.website.length() != 0){
			info = new ChurchInfo();
			info.type = ChurchInfo.Website;
			info.title = church.website;
			churchInfoList.add(info);
		}
		
		if (church.email !=null &&  church.email.length() != 0){
			info = new ChurchInfo();
			info.type = ChurchInfo.Email;
			info.title = church.email;
			churchInfoList.add(info);
		}
		
        LazyAdapter adapter=new LazyAdapter(LocatorActivity.this, churchInfoList, LazyAdapter.ChurchContact);
        infoListView.setAdapter(adapter);
		
		Resources res = getResources();
		//Drawable drawable = res.getDrawable(R.drawable.aaapp_bg_results_high);
		Drawable drawable = res.getDrawable(R.drawable.aaapp_bg_plain_high);
		parent.setBackgroundDrawable(drawable);
	}
	
	private void mapTest() {
	       // Displaying Zooming controls
        //MapView mapView = (MapView) findViewById(R.id.mapView);
        //mapView.setBuiltInZoomControls(true);
        
        /**
         * Changing Map Type
         * */
        // mapView.setSatellite(true); // Satellite View
        // mapView.setStreetView(true); // Street View
        // mapView.setTraffic(true); // Traffic view
        

        //showing location by Latitude and Longitude
 
		/*
        MapController mc = mapView.getController();
        double lat = Double.parseDouble("37.31479715");//Double.parseDouble("48.85827758964043");
        double lon = Double.parseDouble("-121.97537634");//Double.parseDouble("2.294543981552124");
        GeoPoint geoPoint = new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));
        mc.animateTo(geoPoint);
        mc.setZoom(15);
        mapView.invalidate(); 
        
        

        // Placing Marker
 
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.mark_red);
        AddItemizedOverlay itemizedOverlay = 
             new AddItemizedOverlay(drawable, this);
        
        
        OverlayItem overlayitem = new OverlayItem(geoPoint, "Hello", "Sample Overlay item");
        
        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);
        */
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
 

		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_locator);
		
	       stopLocationHandler=new Handler();
	        stopLocationListening =new Runnable()
	        {
	            public void run() 
	            {
	            	locationManager.removeUpdates(listenerCoarse); 
	            	locationManager.removeUpdates(listenerFine);
	            	//Toast.makeText(getApplicationContext(), "Timeout!", Toast.LENGTH_LONG).show();
	            }
	        };
	        
		   doNearbyHandler=new Handler();
	        doNearbyListening =new Runnable()
	        {
	            public void run() 
	            {
	            	if (spinner.isShown()) {
	            		//somehow last doNearby() stuck on parser.getXmlFromUrl(urlString), try it again.
	            		Toast.makeText(getApplicationContext(), "isShown", Toast.LENGTH_SHORT).show();
	            		doNearby();
	            	}
	            	else {
	            		//do nothing
	            		Toast.makeText(getApplicationContext(), "isNotShown", Toast.LENGTH_SHORT).show();
	            	}
	            }
	        };
	 
	        
			//registerLocationListeners();		
			//stopLocationHandler.postDelayed(stopLocationListening, 10*1000);//10 seconds
				
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
	     // Set segmented control listener.
	      ((SegmentedControlView) findViewById(R.id.locator_segmented_control))
	          .setOnCheckedChangeListener(this);
		
		uiInit();
		
		DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    float factor = metrics.density;
	    if (factor >= 2.0) {
	    	ImageView header = (ImageView) findViewById(R.id.header);
	    	header.setImageDrawable(getResources().getDrawable(R.drawable.aa_header_540x44));//aa_header_540x44
	    }
		
	    seekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
	      {
	      public void onProgressChanged(SeekBar seekBar, int progress,
	                                                                      boolean fromUser)
	      		{
	                 // TODO Auto-generated method stub
	                 //value.setText("SeekBar value is "+progress);
	            }

	      public void onStartTrackingTouch(SeekBar seekBar)
	      	   {
	                 // TODO Auto-generated method stub
	           }

	      public void onStopTrackingTouch(SeekBar seekBar)
	           {
	                // TODO Auto-generated method stub
	    	  		radius = seekBar.getProgress();
	    	  		doNearby();
	    	  		//DebugUtil.toast(getApplicationContext(), "radius = " + radius);
	           }
	      });
		
		// Click event for single list row
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> _parent, View view,
					int position, long id) {
				//DebugUtil.toast(getApplicationContext(), "Click on " + position);

				Church church = churchesList.get(position);
				inChurchInfoPage = true;
				showChurchInfo(church);
			}
		});	
        
        searchListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> _parent, View view,
					int position, long id) {
				//DebugUtil.toast(getApplicationContext(), "Click on " + position);

				Church church = searchResultsList.get(position);
				showChurchInfo(church);
			}
		});	
        
        infoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> _parent, View view,
					int position, long id) {
				//DebugUtil.toast(getApplicationContext(), "Click on " + position);

				ChurchInfo churchInfo = churchInfoList.get(position);
				doChurchInfo(churchInfo);
			}
		});	
		
		
		//mMapView = new PolarisMapView(this, "0rsx4mjCOwSSc-Qsif-Nf6IDvORdUYYRZNNF1HQ");
		//polarisTest();//works
		
		//mapTest();
		
		//parsingNearby(); 
		//doNearby();	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}
	
	@Override
	public void onResume()
	{
		//Toast.makeText(getApplicationContext(), "onResume()", Toast.LENGTH_LONG).show();
		Log.e(LOG_TAG, "OnResume()");
		
		if (lastSegment == Nearby) {
			registerLocationListeners();		
			stopLocationHandler.postDelayed(stopLocationListening, 1*1000);//12 seconds before
		
			firsttime = true;//this line must have
		}
		
		mMapView.setUserTrackingButtonEnabled(true);

	    super.onResume();
	}
	
	@Override
	protected void onPause() {
		// Make sure that when the activity goes to 
		// background, the device stops getting locations
		// to save battery life.
		//Toast.makeText(getApplicationContext(), "onPause()", Toast.LENGTH_LONG).show();
		
		locationManager.removeUpdates(listenerCoarse);
		locationManager.removeUpdates(listenerFine);	
		stopLocationHandler.removeCallbacks(stopLocationListening);
		
		mMapView.setUserTrackingButtonEnabled(false);
		
		super.onPause();
	}	

	
	@Override
	public void onStop()
	{
		//Toast.makeText(getApplicationContext(), "OnStop", Toast.LENGTH_LONG).show();
		super.onStop();
		
		//gps.stopUsingGPS();
	}
	

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	   @Override
	    public void onRegionChanged(PolarisMapView mapView) {
	        if (Config.INFO_LOGS_ENABLED) {
	            Log.i(LOG_TAG, "onRegionChanged");
	        }
	    }

	    @Override
	    public void onRegionChangeConfirmed(PolarisMapView mapView) {
	        if (Config.INFO_LOGS_ENABLED) {
	            Log.i(LOG_TAG, "onRegionChangeConfirmed");
	        }
	    }

	    @Override
	    public void onAnnotationSelected(PolarisMapView mapView, MapCalloutView calloutView, int position, Annotation annotation) {
	        if (Config.INFO_LOGS_ENABLED) {
	            Log.i(LOG_TAG, "onAnnotationSelected " + position);
	        }
	        calloutView.setDisclosureEnabled(true);
	        calloutView.setClickable(true);
	        if (!TextUtils.isEmpty(annotation.getSnippet())) {
	            calloutView.setLeftAccessoryView(getLayoutInflater().inflate(R.layout.accessory, calloutView, false));
	        } else {
	            calloutView.setLeftAccessoryView(null);
	        }
	    }

	    @Override
	    public void onAnnotationDeselected(PolarisMapView mapView, MapCalloutView calloutView, int position, Annotation annotation) {
	        if (Config.INFO_LOGS_ENABLED) {
	            Log.i(LOG_TAG, "onAnnotationDeselected");
	        }
	    }

	    @Override
	    public void onAnnotationClicked(PolarisMapView mapView, MapCalloutView calloutView, int position, Annotation annotation) {
	        if (Config.INFO_LOGS_ENABLED) {
	            Log.i(LOG_TAG, "onAnnotationClicked " + position);
	        }
	        //Toast.makeText(this, getString(R.string.annotation_clicked, annotation.getTitle()), Toast.LENGTH_SHORT).show();
	        
			Church church = lastChurchesList.get(position);
			showChurchInfo(church);
	    }

}
