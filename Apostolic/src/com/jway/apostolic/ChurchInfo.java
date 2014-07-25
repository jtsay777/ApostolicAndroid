package com.jway.apostolic;

public class ChurchInfo {

	public static final int Address = 0;
	public static final int Phone = 1;
	public static final int Website = 2;
	public static final int Email = 3;
	
	//private variables
	int type;
	String title;
	String subTitle;
	
	// Empty constructor
    public ChurchInfo(){
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


}
