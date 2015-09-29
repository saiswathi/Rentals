package com.saiswathipujari.rentals;

import java.text.SimpleDateFormat;

/**
 * Created by saiswathipujari on 9/17/15.
 */
public class MyListItem {

    private String price, desc, locality,listing_id;
    private byte[] img;
    private long datetimeLong;
    private SimpleDateFormat df = new SimpleDateFormat("MMMM d, yy  h:mm");

    public MyListItem(String price, String desc, String locality, byte[] img,String listing_id) {
        this.price = price;
        this.desc = desc;
        this.locality = locality;
        this.img = img;
        this.listing_id = listing_id;
    }

    public MyListItem() {
    }

    public String getPrice() { return price; }


    public String getDesc() {return desc;}

    public String getLocality() {return locality;}

    public byte[] getImg(){return img;}

    public String getListing_id(){return listing_id;}



    public void setPrice(String price) {
        this.price = price;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }


    public void setImg(byte[] img) {
        this.img = img;
    }

    public void setListing_id(String listing_id){
        this.listing_id=listing_id;
    }

//    @Override public String toString() {
//        return "Title:" + title + "   " + df.format(getDatetime().getTime()) +
//                "\nDescription:" + description;
//    }

}
