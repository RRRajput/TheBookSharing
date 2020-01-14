package com.example.chutiaap.lab_1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Rehan Rajput on 28/05/2018.
 */

public class UserLibrary extends AppCompatActivity{

    ArrayList<SearchItem> bookArrayList;
    ListView listView;
    TextView name;
    ImageView flagView;
    String user_ID;
    DatabaseReference booksDB,usersDB;
    customAdapter adapter;
    lang_search lang;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_library);
        name = findViewById(R.id.username);
        listView = findViewById(R.id.booklist);
        flagView = findViewById(R.id.flagView);
        user_ID = getIntent().getStringExtra("user");
        name.setText(user_ID);
        bookArrayList = new ArrayList<>();
        booksDB = FirebaseDatabase.getInstance().getReference("books");
        adapter = new customAdapter();
        changeLanguage();

        flagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguage();
            }
        });
        usersDB = FirebaseDatabase.getInstance().getReference("users").child(user_ID);
        usersDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final RegisteredUser ru = dataSnapshot.getValue(RegisteredUser.class);
                name.setText(ru.getName());
                booksDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            Book b = ds.getValue(Book.class);
                            String key = ds.getKey();
                            if(b.getUserID().equals(user_ID)){
                                SearchItem s = new SearchItem(b,ru);
                                s.setPicID(key);
                                bookArrayList.add(s);
                            }
                        }
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchItem s = (SearchItem) adapterView.getAdapter().getItem(i);
                if(s.getUserID().equals(FirebaseAuth.getInstance().getUid().toString())){
                    Toast.makeText(getApplicationContext(), "You already have this book", Toast.LENGTH_SHORT).show();
                }
                else if(!s.isAvailable()){
                    Toast.makeText(getApplicationContext(), "This book is not available", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent chat = new Intent(getApplicationContext(),ChatMessage.class);
                    chat.putExtra("from",FirebaseAuth.getInstance().getUid().toString());
                    chat.putExtra("to",s.getUserID());
                    chat.putExtra("bookID",s.getPicID());
                    startActivity(chat);
                    onBackPressed();
                }
            }
        });
    }

    private void changeLanguage() {
        if(lang == null){
            lang = new lang_search("English");
        }else{
            lang.changeLang();
        }
        if(lang.isEng()){
            flagView.setImageResource(R.drawable.uk);
        }
        else{
            flagView.setImageResource(R.drawable.it);
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        listView.setAdapter(null);
        bookArrayList.clear();
        finish();
    }

    class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return bookArrayList.size();
        }

        @Override
        public SearchItem getItem(int i) {
            return bookArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View v, ViewGroup viewGroup) {
            v = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView lang_title = v.findViewById(R.id.lang_title);
            TextView lang_author = v.findViewById(R.id.lang_author);
            TextView lang_user = v.findViewById(R.id.lang_user);
            TextView lang_address = v.findViewById(R.id.lang_address);
            TextView title = v.findViewById(R.id.titolo);
            TextView author = v.findViewById(R.id.author);
            TextView user = v.findViewById(R.id.user);
            TextView address = v.findViewById(R.id.address);
            RatingBar rating = v.findViewById(R.id.rating);
            ImageView bookView = v.findViewById(R.id.bookView);

            lang_title.setText(lang.getTitle());
            lang_author.setText(lang.getAuthor());
            lang_user.setText(lang.getUser());
            lang_address.setText(lang.getAddress());
            SearchItem s = bookArrayList.get(i);
            title.setText(s.getTitle());
            author.setText(s.getAuthor());
            user.setText(s.getUser());
            address.setText(s.getAddress());
            rating.setRating(s.getRating().equals("") ? 0 : Float.parseFloat(s.getRating()));
            rating.setIsIndicator(true);
            Glide.with(getApplicationContext()).load(s.getPic()).error(R.mipmap.ic_launcher_background).into(bookView);
            return v;
        }
    }
}
