package com.example.chutiaap.lab_1;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class show_profile extends AppCompatActivity {
    private ImageView img;
    private TextView[] val;
    private TextView[] txt;
    private lang_cr ling;

    private DatabaseReference datref;
    private StorageReference storef;
    private RegisteredUser ru;
    private String userID;


    private void initialize(){
        img = (ImageView) findViewById(R.id.imageView);

        val = new TextView[7];
        val[0] = (TextView) findViewById(R.id.textView2);
        val[1]= (TextView) findViewById(R.id.textView3);
        val[2]= (TextView) findViewById(R.id.textView4);
        val[3]= (TextView) findViewById(R.id.textView5);
        val[4]= (TextView) findViewById(R.id.textView6);
        val[5]= (TextView) findViewById(R.id.textView7);
        val[6] = (TextView) findViewById(R.id.textView14);

        txt = new TextView[7];
        txt[0] = (TextView) findViewById(R.id.textView8);
        txt[1]= (TextView) findViewById(R.id.textView9);
        txt[2]= (TextView) findViewById(R.id.textView10);
        txt[3]= (TextView) findViewById(R.id.textView11);
        txt[4]= (TextView) findViewById(R.id.textView12);
        txt[5]= (TextView) findViewById(R.id.textView13);
        txt[6] =(TextView) findViewById(R.id.textView15);

        val[0].setText("");
        val[1].setText("");
        val[2].setText("");
        val[3].setText("");
        val[4].setText("");
        val[5].setText("");
        val[6].setText("");

        ling = new lang_cr("English");
        datref = FirebaseDatabase.getInstance().getReference("users");
        userID = FirebaseAuth.getInstance().getUid().toString();
        storef = FirebaseStorage.getInstance().getReference().child("images/"+userID+".jpeg");
    }

    private void changeLanguage(){
        if(!getIntent().getBooleanExtra("language",true)) {
            ling.changeLanguage();
        }
        for(int i=0;i<7;i++){
            txt[0].setText(ling.getName());
            txt[1].setText(ling.getSurname());
            txt[2].setText(ling.getEmail());
            txt[3].setText(ling.getDob());
            txt[4].setText(ling.getPhone());
            txt[5].setText(ling.getDescription());
            txt[6].setText(ling.getAddress());
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        initialize();
        changeLanguage();
        showData();
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
        val[0].setText(ru.getName());
        val[1].setText(ru.getSurname());
        val[2].setText(ru.getEmail());
        val[3].setText(ru.getDob());
        val[4].setText(ru.getPhone());
        val[5].setText(ru.getDescription());
        val[6].setText(ru.getAddress());
    }

    void showData(){
        datref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fillVals(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(show_profile.this,"Cancelled",Toast.LENGTH_SHORT).show();
            }
        });
        storef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri.toString()).error(R.drawable.ic_launcher_background).into(img);
                Toast.makeText(show_profile.this,"Downloaded Image",Toast.LENGTH_SHORT);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(show_profile.this,"No Image found",Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(show_profile.this,MainActivity.class);
        startActivity(main);
        finish();
    }
}
