package com.jway.apostolic;

import android.os.Parcel;
import android.os.Parcelable;

public class FeedItem implements Parcelable{
	public static final int News = 0;
	public static final int Events = 1;
	public static final int Blog = 2;
	public static final int Sermons = 3;
	public static final int Media = 4;
	
	//private variables
	int type;
	String title;
	String link;
	String thumbnailURL;
	String creator;
	String year;
	String month;
	String day;
	String video;
	String audio;
	String featuredImageURL;
	String speaker;
	
	// Empty constructor
    public FeedItem(){
    }

    // constructor
    public FeedItem(int type, String title, String creator, String year, String month, String day) {
    	this.type = type;
    	this.title = title;
    	this.creator = creator;
    	this.year = year;
    	this.month = month;
    	this.day = day;
    }
    
    // getting type
    public int getType() {
    	return this.type;
    }
    
    // setting type
    public void setType(int type) {
    	this.type = type;
    }

    // getting title
    public String getTitle() {
    	return this.title;
    }
     
    // setting title
    public void setTitle(String title) {
    	this.title = title;
    }
    
    // getting link
    public String getLink() {
    	return this.link;
    }
     
    // setting link
    public void setLink(String link) {
    	this.link = link;
    }
    
    // getting thumbnailURL
    public String getThumbnailURL() {
    	return this.thumbnailURL;
    }
     
    // setting thumbnailURL
    public void setThumbnailURL(String thumbnailURL) {
    	this.thumbnailURL = thumbnailURL;
    }
    
    // getting creator
    public String getCreator() {
    	return this.creator;
    }
     
    // setting creator
    public void setCreator(String creator) {
    	this.creator = creator;
    }
    
    // getting year
    public String getYear() {
    	return this.year;
    }
     
    // setting year
    public void setYear(String year) {
    	this.year = year;
    }
    
    // getting month
    public String getMonth() {
    	return this.month;
    }
     
    // setting month
    public void setMonth(String month) {
    	this.month = month;
    }
    
    // getting day
    public String getDay() {
    	return this.day;
    }
     
    // setting day
    public void setDay(String day) {
    	this.day = day;
    }
    
    // getting video
    public String getVideo() {
    	return this.video;
    }
     
    // setting video
    public void setVideo(String video) {
    	this.video = video;
    }
    
    // getting audio
    public String getAudio() {
    	return this.audio;
    }
     
    // setting audio
    public void setAudio(String audio) {
    	this.audio = audio;
    }
    
    // getting featuredImageURL
    public String getFeaturedImageURL() {
    	return this.featuredImageURL;
    }
     
    // setting featuredImageURL
    public void setFeaturedImageURL(String featuredImageURL) {
    	this.featuredImageURL = featuredImageURL;
    }
    
    // getting speaker
    public String getSpeaker() {
    	return this.speaker;
    }
     
    // setting speaker
    public void setSpeaker(String speaker) {
    	this.speaker = speaker;
    }
    
    public FeedItem (Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public FeedItem createFromParcel(Parcel in) {
            return new FeedItem (in);
        }

        public FeedItem [] newArray(int size) {
            return new FeedItem [size];
        }
    };


    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	dest.writeInt(type);
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(thumbnailURL);
        dest.writeString(creator);
        dest.writeString(year);
        dest.writeString(month);
        dest.writeString(day);
        dest.writeString(video);
        dest.writeString(audio);
        dest.writeString(featuredImageURL);
        dest.writeString(speaker);
}

    private void readFromParcel(Parcel in) {
    	
        type = in.readInt();
        title = in.readString();
        link = in.readString();
        thumbnailURL = in.readString();
        creator = in.readString();
        year = in.readString();
        month = in.readString();
        day = in.readString();
        video = in.readString();
        audio = in.readString();
        featuredImageURL = in.readString();
        speaker = in.readString();      
}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
