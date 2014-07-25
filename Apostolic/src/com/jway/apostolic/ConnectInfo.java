package com.jway.apostolic;

public class ConnectInfo {
	public static final int Facebook = 0;
	public static final int Twitter = 1;
	public static final int Email = 2;
	public static final int Website = 3;
	
	//private variables
	int type;
	String title;
	String subTitle;
	String link;
	
	// Empty constructor
    public ConnectInfo(){
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
    
    // getting subTitle
    public String getSubTitle() {
    	return this.subTitle;
    }
     
    // setting subTitle
    public void setSubTitle(String subTitle) {
    	this.subTitle = subTitle;
    }
    
    // getting link
    public String getLink() {
    	return this.link;
    }
     
    // setting link
    public void setLink(String link) {
    	this.link = link;
    }

}
