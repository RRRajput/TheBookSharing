package com.example.chutiaap.lab_1;

/**
 * Created by Rehan Rajput on 28/05/2018.
 */

public class Review {

    private String name,id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private float rating;
    private String comment;

    Review(){

    }
    Review(String name,String id,float rating){
        this.name = name;
        this.id = id;
        this.rating = rating;
        this.comment ="";
    }
    Review(String name,String id,float rating,String comment){
        this(name,id,rating);
        this.comment = comment;
    }
}
