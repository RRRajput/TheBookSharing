package com.example.chutiaap.lab_1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class InsertBook extends AppCompatActivity {
    private EditText ISBN,title,author,publisher,edition,tags;
    private Button forward,back,barcode;
    private TextView textView;
    private CheckBox check;

    private ImageView flagView;
    private RatingBar rating;
    private lang_insert_book lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_book);
        ISBN = findViewById(R.id.editText1);
        title = findViewById(R.id.editText2);
        author = findViewById(R.id.editText3);
        publisher = findViewById(R.id.editText4);
        edition = findViewById(R.id.editText5);
        tags = findViewById(R.id.editText6);
        flagView = findViewById(R.id.flagView);
        rating = findViewById(R.id.ratingBar);
        forward = findViewById(R.id.button2);
        back = findViewById(R.id.button);
        check = findViewById(R.id.checkBox2);
        barcode = findViewById(R.id.button3);
        textView = findViewById(R.id.textView);
        lang=null;
        change_language();
        flagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_language();
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forward_button();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        check.setText("Auto-complete");
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(InsertBook.this, "Getting Data", Toast.LENGTH_SHORT).show();
                new JSONParser(new TextView[]{title,author,publisher,edition,tags}).execute(ISBN.getText().toString());
            }
        });
        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent barcode = new Intent(InsertBook.this,bar_code.class);
                startActivityForResult(barcode,20);
            }
        });

    }

    private void change_language() {
        if(lang == null){
            lang = new lang_insert_book("English");
        }else{
            lang.changeLang();
        }
        ISBN.setHint(lang.getISBN());
        author.setHint(lang.getAuthor());
        title.setHint(lang.getTitle());
        publisher.setHint(lang.getPublisher());
        edition.setHint(lang.getEdition());
        tags.setHint(lang.getTags());
        forward.setText(lang.getSubmit());
        back.setText(lang.getBack());
        barcode.setText(lang.getBarcode());
        textView.setText(lang.getRating());
        if(lang.isEng()){
            flagView.setImageResource(R.drawable.uk);
        }
        else{
            flagView.setImageResource(R.drawable.it);
        }
    }

    private void forward_button() {
        if(ISBN.getText().toString().equals("")){
            Toast.makeText(this, "Put an ISBN number", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent photo = new Intent(InsertBook.this, BookImage.class);
            photo.putExtra("ISBN", ISBN.getText().toString());
            startActivityForResult(photo, 1);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ISBN",ISBN.getText().toString());
        outState.putString("author",author.getText().toString());
        outState.putString("title",title.getText().toString());
        outState.putString("edition",edition.getText().toString());
        outState.putString("publisher",publisher.getText().toString());
        outState.putString("tags",tags.getText().toString());
        outState.putString("rating",String.valueOf(rating.getRating()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ISBN.setText(savedInstanceState.getString("ISBN"));
        author.setText(savedInstanceState.getString("author"));
        title.setText(savedInstanceState.getString("title"));
        edition.setText(savedInstanceState.getString("edition"));
        publisher.setText(savedInstanceState.getString("publisher"));
        tags.setText(savedInstanceState.getString("tags"));
        rating.setRating(Float.valueOf(savedInstanceState.getString("rating")));
        change_language();
        change_language();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            DatabaseReference datref = FirebaseDatabase.getInstance().getReference("books");
            Book book = new Book();
            book.setIsbn(ISBN.getText().toString());
            book.setTitle(title.getText().toString());
            book.setAuthor(author.getText().toString());
            book.setEdition(edition.getText().toString());
            book.setPublisher(publisher.getText().toString());
            book.setRating(String.valueOf(rating.getRating()));
            book.setTags(tags.getText().toString());
            book.setUserID(FirebaseAuth.getInstance().getUid().toString());
            if(resultCode==1){
                book.setPic(data.getStringExtra("pic_uri"));
            }else{
                Toast.makeText(this, "No picture found", Toast.LENGTH_SHORT).show();
            }
            datref.push().setValue(book);
            onBackPressed();
        }
        else if (requestCode == 20 && resultCode==1){
            ISBN.setText(data.getStringExtra("ISBN"));
            Toast.makeText(InsertBook.this, "Getting Data", Toast.LENGTH_SHORT).show();
            new JSONParser(new TextView[]{title,author,publisher,edition,tags}).execute(ISBN.getText().toString());
        }
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(InsertBook.this,MainActivity.class);
        startActivity(main);
        finish();
    }
}
