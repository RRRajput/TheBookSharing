package com.example.chutiaap.lab_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button show,insert_book,sch,chat;
    private ImageButton logout;
    private ImageView flagView;
    private lang_main ling;
    private boolean canLogOut;

    @Override
    protected void onStart() {
        super.onStart();
        canLogOut = false;
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            Intent cr_prof = new Intent(MainActivity.this,create_profile.class);
                            startActivity(cr_prof);
                        }
                        canLogOut = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        canLogOut = true;
        show = (Button) findViewById(R.id.button1);
        insert_book = (Button) findViewById(R.id.button2);
        sch = (Button) findViewById(R.id.button3);
        logout = findViewById(R.id.imageButton);
        flagView = findViewById(R.id.flagView);
        chat = findViewById(R.id.button4);
        change_language();
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(MainActivity.this,AllChats.class);
                startActivity(a);
                finish();
            }
        });

        flagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_language();
            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sh_prof = new Intent(MainActivity.this, user_rep.class);
                sh_prof.putExtra("user_id",FirebaseAuth.getInstance().getUid());
                startActivity(sh_prof);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        insert_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in_book = new Intent(MainActivity.this,InsertBook.class);
                startActivity(in_book);
                finish();
            }
        });
        sch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent s = new Intent(MainActivity.this, search.class);
                startActivity(s);
                finish();
            }
        });
        Intent serve = new Intent(MainActivity.this,com.example.chutiaap.lab_1.services.service.class);
        startService(serve);

    }

    @Override
    public void onBackPressed() {
        LogOut();
    }

    private void LogOut() {
        if(canLogOut) {
            //setResult(100);
            //finish();
            Intent logout = new Intent(MainActivity.this,Login.class);
            logout.putExtra("LogOut" , true);
            startActivity(logout);
            finish();
        }
        else{
            Toast.makeText(this, "Please wait..", Toast.LENGTH_SHORT).show();
        }
    }


    private void change_language(){
        if(ling==null){
            ling = new lang_main("English");
        }
        else{
            ling.changeLang();
        }
        insert_book.setText(ling.getInsert());
        show.setText(ling.getShow());
        sch.setText(ling.getSearch());
        chat.setText(ling.getChat());
        if(ling.isEng()){
            flagView.setImageResource(R.drawable.uk);
        }
        else{
            flagView.setImageResource(R.drawable.it);
        }
    }
}
