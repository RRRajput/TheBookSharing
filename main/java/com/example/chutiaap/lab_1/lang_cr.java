package com.example.chutiaap.lab_1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rehan Rajput on 17/03/2018.
 */

public class lang_cr {

    String name;
    String surname;

    public String getAddress() {
        return address;
    }

    private String address;

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public String getDob() {
        return dob;
    }

    public String getPhone() {
        return phone;
    }

    public String getNext() {
        return next;
    }

    public String getBack() {
        return back;
    }

    public String getSave() {
        return save;
    }

    public String getLanguage() {
        return language;
    }

    public String getError() {
        return error;
    }

    String description;
    String email;
    String dob;
    String phone;
    String next;
    String back;
    String save;
    String language;
    String error;

    public boolean isEng() {
        return isEng;
    }

    boolean isEng;
    lang_cr(String language){
        if(language.equals("English")){
            lang_en();
            isEng=true;
        }
        else{
            lang_it();
            isEng= false;
        }
    }
    public void changeLanguage(){
        if(isEng){
            isEng = false;
            lang_it();
        }
        else{
            isEng=true;
            lang_en();
        }
    }
    private void lang_it(){
        name = "Nome";
        surname = "Cognome";
        description = "Descrizione";
        email = "Email";
        dob = "Data di nascita";
        phone = "Numero di telefono";
        next = "Prossimo";
        back = "Torna indietro";
        save = "Salva";
        language = "English";
        error = "Informazione Mancante";
        address="Indirizzo";
    }
    private void lang_en(){

        name = "Name";
        surname = "Surname";
        description="Description";
        email= "Email";
        dob="Date of Birth";
        phone= "Phone number";
        next= "Next";
        back ="Back";
        save = "Submit";
        language = "Italiano";
        error = "Missing Information";
        address = "Address";

    }
}
