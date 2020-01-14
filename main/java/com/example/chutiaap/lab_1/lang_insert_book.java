package com.example.chutiaap.lab_1;

/**
 * Created by Stefania Titone on 26/04/2018.
 */

public class lang_insert_book {
    public String getBarcode() {
        return barcode;
    }

    private String barcode;

    public boolean isEng() {
        return isEng;
    }
    String ISBN;
    String Title;

    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return Title;
    }

    public String getPublisher() {
        return Publisher;
    }

    public String getEdition() {
        return Edition;
    }

    public String getTags() {
        return Tags;
    }

    public String getRating() {
        return Rating;
    }

    public String getAuthor() {
        return Author;
    }

    public String getSubmit() {
        return Submit;
    }

    public String getBack() {
        return Back;
    }

    public String getLanguage() {
        return language;
    }

    String Publisher;
    String Edition;
    String Tags;
    String Rating;
    String Author;
    String Submit;
    String Back;
    String language;
    private boolean isEng;
    lang_insert_book(String lang){
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
        ISBN ="ISBN";
        Title="Title";
        Publisher="Publisher";
        Edition="Edition";
        Tags="Tags";
        Rating="Condition";
        Author="Author";
        Submit ="Submit";
        Back ="Back";
        language ="Italiano";
        barcode = "BarCode";
    }
    private void lang_it(){
        ISBN = "ISBN";
        Title = "Titolo";
        Publisher ="Publicatore";
        Edition = "Edizione";
        Tags = "Tags";
        Rating = "Condizione";
        Author = "Autore";
        Submit = "Avanti";
        Back = "Torna indietro";
        language = "English";
        barcode = "Codici a barra";
    }
}
