package com.example.chutiaap.lab_1;

/**
 * Created by Rehan Rajput on 29/05/2018.
 */

public class Exchange {
    private boolean isReturned;
    private String bookid;
    private long start;

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    private long end;

    Exchange(){

    }
    Exchange(String bookid){
        this.bookid = bookid;
        this.start = System.currentTimeMillis();
        this.end = 0;
        this.isReturned = false;
    }

    void hasEnded(){
        this.isReturned = true;
        this.end = System.currentTimeMillis();
    }
}
