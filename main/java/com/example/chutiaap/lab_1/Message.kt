package com.example.chutiaap.lab_1

/**
 * Created by Rehan Rajput on 18/05/2018.
 */
class Message {

    var message: String? = null
    var to: String? = null
    var from: String? = null
    var timestamp: Long = 0
    var isRead: Boolean = false
    var borrow: Boolean? = null
    var bookid: String? = null;
    var bookTitle: String? = null
    var owner: String? = null
    var toreturn : Boolean? = null

    constructor()
    constructor(message: String,to: String,from: String){
        this.message = message
        this.to = to
        this.from = from
        timestamp = System.currentTimeMillis()
        isRead = false
        borrow = false
        bookid = ""
        bookTitle = ""
        owner = ""
        toreturn = false
    }
}