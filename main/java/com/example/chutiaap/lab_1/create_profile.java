package com.example.chutiaap.lab_1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class create_profile extends AppCompatActivity {
    EditText name_edit;
    EditText surname_edit;
    EditText birthday_edit;
    EditText email_edit;
    EditText description_edit;
    EditText tel_edit;
    EditText address_edit;

    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref;
    String userID;
    RegisteredUser ru;

    Button sub,bck;
    TextView tv;
    ImageView flagView;
    lang_cr ling;
    Bitmap pic;
    int i;
    private void initialize(){
        name_edit = (EditText) findViewById(R.id.editText);
        surname_edit = (EditText) findViewById(R.id.editText2);
        birthday_edit = (EditText) findViewById(R.id.editText3);
        email_edit = (EditText) findViewById(R.id.editText4);
        tel_edit = (EditText) findViewById(R.id.editText5);
        description_edit = (EditText) findViewById(R.id.editText6);
        address_edit = (EditText) findViewById(R.id.editText7);

        flagView = findViewById(R.id.flagView);
        sub = (Button) findViewById(R.id.button);
        bck = (Button) findViewById(R.id.button1);

        tv = (TextView) findViewById(R.id.textView);
        ling = null;
        pic = null;
        i=0;

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        userID = user.getUid();
        button_type();
        tv.setText("");
    }

    private void fillVals(DataSnapshot dataSnapshot){
        ru = null;
        for(DataSnapshot ds:dataSnapshot.getChildren()){
            ru = ds.getValue(RegisteredUser.class);
            if(ru.getUserid().equals(userID)){
                break;
            }
        }
        if(ru!=null) {
            if (!ru.getUserid().equals(userID)) {
                ru = new RegisteredUser(userID);
            }
        }else{
            ru = new RegisteredUser(userID);
        }
        name_edit.setText(ru.getName());
        surname_edit.setText(ru.getSurname());
        email_edit.setText(ru.getEmail());
        description_edit.setText(ru.getDescription());
        birthday_edit.setText(ru.getDob());
        tel_edit.setText(ru.getPhone());
        address_edit.setText(ru.getAddress());
    }

    private void changeLanguage(){
        if(ling == null){
            ling = new lang_cr("English");
        }
        else{
            ling.changeLanguage();
        }
        name_edit.setHint(ling.getName());
        surname_edit.setHint(ling.getSurname());
        birthday_edit.setHint(ling.getDob());
        email_edit.setHint(ling.getEmail());
        tel_edit.setHint(ling.getPhone());
        description_edit.setHint(ling.getDescription());
        address_edit.setHint(ling.getAddress());
        sub.setText(ling.getNext());

        bck.setText(ling.getBack());
        if(ling.isEng()){
            flagView.setImageResource(R.drawable.uk);
        }
        else{
            flagView.setImageResource(R.drawable.it);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        initialize();
        ref.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fillVals(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        changeLanguage();
        flagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguage();
            }
        });
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForwardButton();
            }
        });
        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name",name_edit.getText().toString());
        outState.putString("surname",surname_edit.getText().toString());
        outState.putString("birthday",birthday_edit.getText().toString());
        outState.putString("email",email_edit.getText().toString());
        outState.putString("tel",tel_edit.getText().toString());
        outState.putString("description",description_edit.getText().toString());
        outState.putString("address",address_edit.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        i = savedInstanceState.getInt("i");
        name_edit.setText(savedInstanceState.getString("name"));
        surname_edit.setText(savedInstanceState.getString("surname"));
        birthday_edit.setText(savedInstanceState.getString("birthday"));
        email_edit.setText(savedInstanceState.getString("email"));
        tel_edit.setText(savedInstanceState.getString("tel"));
        description_edit.setText(savedInstanceState.getString("description"));
        address_edit.setText(savedInstanceState.getString("address"));

        changeLanguage();
        changeLanguage();
    }

    private void ForwardButton(){
        tv.setText("");

        if (name_edit.getText().toString().equals("") || surname_edit.getText().toString().equals("") || email_edit.getText().toString().equals("")) {
            tv.setText(ling.getError());
            return;
        }
        ru.setName(name_edit.getText().toString());
        ru.setSurname(surname_edit.getText().toString());
        ru.setDob(birthday_edit.getText().toString());
        ru.setDescription(description_edit.getText().toString());
        ru.setEmail(email_edit.getText().toString());
        ru.setPhone(tel_edit.getText().toString());
        ru.setAddress(address_edit.getText().toString());

        ref.child("users").child(userID).setValue(ru);

        Intent pic = new Intent(create_profile.this,img_select.class);

        startActivityForResult(pic,10);
    }

    private void button_type(){
        email_edit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        birthday_edit.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        tel_edit.setInputType(InputType.TYPE_CLASS_PHONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==resultCode){
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(create_profile.this,user_rep.class);
        main.putExtra("user_id",FirebaseAuth.getInstance().getUid());
        startActivity(main);
        finish();
    }
}
