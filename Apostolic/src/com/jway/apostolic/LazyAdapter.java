package com.jway.apostolic;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class LazyAdapter extends BaseAdapter {
	
	public static final int Discover = 0;
	public static final int Locator = 1;
	public static final int ChurchContact = 2;
	public static final int Sermons = 3;
	public static final int Media = 4;
	public static final int Connect = 5;
    
    private Activity activity;
    //private ArrayList<FeedItem> data;
    private ArrayList<?> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
    
    private int type;
    //public ImageLoader imageLoader; 
    
    /*
    public LazyAdapter(Activity a, ArrayList<FeedItem> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImageLoader(activity.getApplicationContext());
    }
    */
    public LazyAdapter(Activity a, ArrayList d, int type) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.type = type;
        
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

	public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        
        if (this.type == Discover) {
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.discover_list_row, null);
	
	        TextView title = (TextView)vi.findViewById(R.id.title); // title
	        TextView author = (TextView)vi.findViewById(R.id.author); // author
	        TextView month = (TextView)vi.findViewById(R.id.month);
	        TextView day = (TextView)vi.findViewById(R.id.day);
	  
	        FeedItem feedItem = new FeedItem();
	        feedItem = (FeedItem)data.get(position);
	        
	        // Setting all values in listview
	        title.setText(feedItem.title);
	        author.setText(feedItem.creator);
	        month.setText(feedItem.month);
	        day.setText(feedItem.day);
        }
        else if (this.type == Locator) {
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.locator_list_row, null);
	
	        TextView pastor = (TextView)vi.findViewById(R.id.pastor); // Pastor
	        TextView address = (TextView)vi.findViewById(R.id.address); // Address

	        Church church = new Church();
	        church = (Church)data.get(position);
	        
	        // Setting all values in listview
	        pastor.setText(church.pastor);
	        String temp = church.street + ", " + church.city + ", " + church.state + " " + church.postalCode;
	        address.setText(temp); 

        }
        else if (this.type == ChurchContact) {
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.church_info_list_row, null);
	        
	        TextView title = (TextView)vi.findViewById(R.id.title); // title
	        TextView subTitle = (TextView)vi.findViewById(R.id.subTitle); // subTitle
	        ImageView thumbnail = (ImageView)vi.findViewById(R.id.thumbnail); // thumbnail
	        
	        ChurchInfo info = new ChurchInfo();
	        info = (ChurchInfo)data.get(position);
	        
	        title.setText(info.title);
	        switch (info.type) {
	        	case ChurchInfo.Address:
	        		subTitle.setText(info.subTitle);
	        		thumbnail.setImageResource(R.drawable.aaapp_ico_address);
	        		break;
	        	case ChurchInfo.Phone:
	        		thumbnail.setImageResource(R.drawable.aaapp_ico_phone);
	        		break;
	        	case ChurchInfo.Website:
	        		thumbnail.setImageResource(R.drawable.aaapp_ico_link);
	        		break;
	        	case ChurchInfo.Email:
	        		thumbnail.setImageResource(R.drawable.aaapp_ico_mail);
	        		break;
	        }
        }
        else if (this.type == Sermons) {
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.sermons_list_row, null);
	        
	        TextView title = (TextView)vi.findViewById(R.id.title); // title
	        TextView creator = (TextView)vi.findViewById(R.id.creator); // creator
	        ImageView thumbnail = (ImageView)vi.findViewById(R.id.thumbnail); // thumbnail
	        
	        FeedItem feedItem = new FeedItem();
	        feedItem = (FeedItem)data.get(position);
	        
	        title.setText(feedItem.title);
	        creator.setText(feedItem.creator);
			
			imageLoader.DisplayImage(feedItem.thumbnailURL, thumbnail);
        }
        else if (this.type == Media) {
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.media_list_row, null);
	        
	        TextView title = (TextView)vi.findViewById(R.id.title); // title
	        TextView creator = (TextView)vi.findViewById(R.id.creator); // creator
	        ImageView thumbnail = (ImageView)vi.findViewById(R.id.thumbnail); // thumbnail
	        
	        FeedItem feedItem = new FeedItem();
	        feedItem = (FeedItem)data.get(position);
	        
	        title.setText(feedItem.title);
	        creator.setText(feedItem.creator);
			
			imageLoader.DisplayImage(feedItem.thumbnailURL, thumbnail);
        } 
        else if (this.type == Connect) {
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.connect_info_list_row, null);
	        
	        TextView title = (TextView)vi.findViewById(R.id.title); // title
	        TextView subTitle = (TextView)vi.findViewById(R.id.subTitle); // subTitle
	        ImageView thumbnail = (ImageView)vi.findViewById(R.id.thumbnail); // thumbnail
	        
	        ConnectInfo info = new ConnectInfo();
	        info = (ConnectInfo)data.get(position);
	        
	        title.setText(info.title);
	        switch (info.type) {
	        	case ConnectInfo.Facebook:
	        		subTitle.setText(info.subTitle);
	        		thumbnail.setImageResource(R.drawable.aaapp_ico_fb);
	        		break;
	        	case ConnectInfo.Twitter:
	        		thumbnail.setImageResource(R.drawable.aaapp_ico_tw);
	        		break;
	        	case ConnectInfo.Email:
	        		thumbnail.setImageResource(R.drawable.aaapp_ico_em);
	        		break;
	        	case ConnectInfo.Website:
	        		thumbnail.setImageResource(R.drawable.aaapp_ico_aa);
	        		break;
	        }       	
        }
  
        return vi;
    }
}