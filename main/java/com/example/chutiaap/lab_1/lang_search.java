package com.example.chutiaap.lab_1;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Rehan Rajput on 07/05/2018.
 */

public class lang_search {
    private String search;

    public String getAddress() {
        return address;
    }

    private String address;

    public String getSearch() {
        return search;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getUser() {
        return user;
    }

    public String getLanguage() {
        return language;
    }

    public String getBack() {
        return back;
    }

    public String getForward() {
        return forward;
    }

    public ArrayList<String> getLst() {
        return lst;
    }

    public String getAny() {
        return any;
    }

    public boolean isEng() {
        return isEng;
    }

    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String user;
    private String language;
    private String back;
    private String forward;
    private ArrayList<String> lst;
    private String any;
    private boolean isEng;

    lang_search(String lang){
        if(lang=="English"){
            isEng=false;
        }else{
            isEng=true;
        }
        changeLang();
    }

    void changeLang() {
        if(isEng){
            lang_it();
        }
        else{
            lang_en();
        }
    }

    void lang_en(){
        isEng = true;
        search ="Search..";
        title ="Title";
        author ="Author";
        publisher="Publisher";
        isbn="ISBN";
        user="User";
        language = "Italiano";
        back = "Back";
        forward ="Done";
        address = "Address";
        any ="Any keyword";
        lst = new ArrayList<>(Arrays.asList(title,author,publisher,isbn,user,any));
    }
    void lang_it(){
        isEng = false;
        search ="Cerca..";
        title ="Titolo";
        author ="Autore";
        publisher="Publicatore";
        isbn="ISBN";
        user="Utente";
        address = "Indirizzo";
        language = "English";
        back = "Indietro";
        forward ="Fatto";
        any ="Qualsiasi parola chiave";
        lst = new ArrayList<>(Arrays.asList(title,author,publisher,isbn,user,any));
    }
}
