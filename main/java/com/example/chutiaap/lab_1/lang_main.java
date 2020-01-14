package com.example.chutiaap.lab_1;

/**
 * Created by Rehan Rajput on 17/03/2018.
 */

public class lang_main {
    private String language;

    public String getShow() {
        return show;
    }

    private String show;

    public String getChat() {
        return chat;
    }

    private String chat;
    private String logout;

    public String getSearch() {
        return search;
    }

    private String search;

    public String getLogout() {
        return logout;
    }

    public String getInsert() {
        return insert;
    }

    private String insert;

    public boolean isEng() {
        return isEng;
    }

    private boolean isEng;
    lang_main(String lang){
        if (lang.equals("English")){
            lang_en();
            isEng = true;
        }
        else{
            lang_it();
            isEng = false;
        }
    }

    void changeLang(){
        if (isEng){
            isEng=false;
            lang_it();
        }
        else{
            isEng = true;
            lang_en();
        }
    }

    private void lang_en(){
        this.language = "Italiano";


        this.show = "Show profile";
        this.insert = "Insert a book";
        this.search = "Search";
        this.logout = "Logout";
        this.chat = "Messages";
    }
    private void lang_it(){
        this.language = "English";
        this.show = "Mostra il profilo";
        this.insert = "Inserisci un libro";
        this.search = "Cerca";
        this.logout = "Esci";
        this.chat =" Messaggi";
    }

    public String getLanguage() {
        return language;
    }


}
