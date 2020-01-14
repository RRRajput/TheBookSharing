package com.example.chutiaap.lab_1;

/**
 * Created by Rehan Rajput on 19/05/2018.
 */

public class SearchItem {
    String rating;

    public String getRating() {
        return rating;
    }

    public String getUserID() {
        return userID;
    }

    String userID;
    String title;
    String author;
    String pic;

    public boolean isAvailable() {
        return isAvailable;
    }

    boolean isAvailable;

    public String getPicID() {
        return picID;
    }

    public void setPicID(String picID) {
        this.picID = picID;
    }

    String picID;


    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPic() {
        return pic;
    }

    public String getAddress() {
        return address;
    }

    public String getUser() {
        return user;
    }

    String address;
    String user;
    SearchItem(Book b,RegisteredUser ru){
        title = b.getTitle();
        author = b.getAuthor();
        pic = b.getPic();
        address = ru.getAddress();
        user = ru.getName() + " " + ru.getSurname();
        userID = b.getUserID();
        rating = b.getRating();
        isAvailable = b.isAvailable();
        picID = "";
    }

}
