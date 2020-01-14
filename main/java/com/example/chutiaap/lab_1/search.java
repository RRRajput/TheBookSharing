package com.example.chutiaap.lab_1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class search extends AppCompatActivity {

    Button searchKey, back, forward,Map;
    EditText k_word;
    ImageView flagView;
    Spinner sp;

    ArrayList<SearchItem> searchItemArrayList;
    ArrayAdapter<String> spAdapt;
    DatabaseReference dbref;
    ListView lv;
    lang_search lang;
    int selection;
    DatabaseReference users;
    boolean v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        lv = findViewById(R.id.list);
        k_word = findViewById(R.id.editText8);
        searchKey = findViewById(R.id.button9);
        back = findViewById(R.id.button11);
        forward = findViewById(R.id.button10);
        Map = findViewById(R.id.button13);
        sp = findViewById(R.id.spinner);
        flagView = findViewById(R.id.flagView);
        selection = 0;
        lang=null;
        changeLanguage();
        dbref = FirebaseDatabase.getInstance().getReference("books");
        users = FirebaseDatabase.getInstance().getReference("users");
        searchItemArrayList = new ArrayList<>();
        sp.setSelection(0);
        Map.setText("Map");
        Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent map = new Intent(search.this,MapsActivity.class);
                map.putExtra("keyword",k_word.getText().toString().equals("") ? "" : k_word.getText().toString());
                map.putExtra("i",selection);
                startActivity(map);
            }
        });
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selection = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(search.this, "Nothing Selected", Toast.LENGTH_SHORT).show();
            }
        });
        v = false;
        SearchView(v);

        searchKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v=true;
                SearchView(v);
                final ArrayList<String> keywords = new ArrayList<>();
                final String words = k_word.getText().toString().equals("") ? "" : k_word.getText().toString();
                keywords.add(words);
                if(selection>3){
                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                RegisteredUser ru = ds.getValue(RegisteredUser.class);
                                if(ru.getName().toUpperCase().indexOf(words.toUpperCase()) != -1 || ru.getName().toUpperCase().indexOf(words.toUpperCase()) != -1){
                                    keywords.add(ru.getUserid());
                                }
                            }
                            ShowList(selection,keywords);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    ShowList(selection,keywords);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v=false;SearchView(v);
            }
        });



        flagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguage();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchItem s = (SearchItem) adapterView.getAdapter().getItem(i);
                if(s.getUserID().equals(FirebaseAuth.getInstance().getUid().toString())){
                    Toast.makeText(search.this, "You already have this book", Toast.LENGTH_SHORT).show();
                }
                else if(!s.isAvailable()){
                    Toast.makeText(search.this,"This book is not available",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent chat = new Intent(search.this,ChatMessage.class);
                    chat.putExtra("from",FirebaseAuth.getInstance().getUid().toString());
                    chat.putExtra("to",s.getUserID());
                    chat.putExtra("bookID",s.getPicID());
                    startActivity(chat);
                    finish();
                }
            }
        });
    }

    private void changeLanguage() {
        if(lang==null){
            lang = new lang_search("English");
        }
        else{
            lang.changeLang();
        }
        k_word.setHint(lang.getSearch());
        searchKey.setText(lang.getForward());
        back.setText(lang.getBack());
        forward.setText(lang.getForward());
        spAdapt = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,lang.getLst());
        sp.setAdapter(spAdapt);
        if(lang.isEng()){
            flagView.setImageResource(R.drawable.uk);
        }
        else{
            flagView.setImageResource(R.drawable.it);
        }

    }


    private void SearchView(boolean t) {
        if (t) {
            findViewById(R.id.search).setVisibility(View.GONE);
            findViewById(R.id.buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.ListView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.search).setVisibility(View.VISIBLE);
            findViewById(R.id.buttons).setVisibility(View.GONE);
            findViewById(R.id.ListView).setVisibility(View.GONE);
        }
    }

    private void ShowList(final int i, final ArrayList<String> keyword) {
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    searchItemArrayList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final Book b = ds.getValue(Book.class);
                        final String book_id = ds.getKey();
                        boolean value = false;
                        for(String k : keyword){
                            value = value || isValid(i,k,b);
                            if(value){
                                break;
                            }
                        }
                        if (value) {
                            users.child(b.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    RegisteredUser ru = dataSnapshot.getValue(RegisteredUser.class);
                                    SearchItem s = new SearchItem(b,ru);
                                    s.setPicID(book_id);
                                    searchItemArrayList.add(s);
                                    }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    v=true;
                    SearchView(v);
                    lv.setAdapter(new SearchItemAdapter());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(search.this, "Database error occurred", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        lv.setAdapter(null);
        searchItemArrayList.clear();
        if(!v) {
            Intent main = new Intent(search.this,MainActivity.class);
            startActivity(main);
            finish();
        }
        else{
            v=false;
            SearchView(v);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("v",v);
        outState.putString("keyword",k_word.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        v= savedInstanceState.getBoolean("v");
        k_word.setText(savedInstanceState.getString("keyword"));
        SearchView(v);
        if(v){
            SearchKeyProcess();
        }
    }

    private boolean isValid(int i, String keyword, Book b) {
        boolean title, author, publisher, ISBN,user;
        if (i == 0) {
            title = b.getTitle().toUpperCase().indexOf(keyword.toUpperCase()) != -1;
            return title;
        } else if (i == 1) {
            author = b.getAuthor().toUpperCase().indexOf(keyword.toUpperCase()) != -1;
            return author;
        } else if (i == 2) {
            publisher = b.getPublisher().toUpperCase().indexOf(keyword.toUpperCase()) != -1;
            return publisher;
        } else if (i == 3) {
            ISBN = b.getPublisher().toUpperCase().indexOf(keyword.toUpperCase()) != -1;
            return ISBN;
        } else if(i==4){
            user = b.getUserID().equals(keyword);
            return user;
        }else{
            title = b.getTitle().toUpperCase().indexOf(keyword.toUpperCase()) != -1;
            author = b.getAuthor().toUpperCase().indexOf(keyword.toUpperCase()) != -1;
            publisher = b.getPublisher().toUpperCase().indexOf(keyword.toUpperCase()) != -1;
            ISBN = b.getPublisher().toUpperCase().indexOf(keyword.toUpperCase()) != -1;
            user = b.getUserID().equals(keyword);
            return title || author || publisher || ISBN || user;
        }
    }

    class SearchItemAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return searchItemArrayList.size();
        }

        @Override
        public SearchItem getItem(int i) {
            return searchItemArrayList.get(i);
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
            SearchItem s = searchItemArrayList.get(i);
            title.setText(s.getTitle());
            author.setText(s.getAuthor());
            user.setText(s.getUser());
            address.setText(s.getAddress());
            rating.setRating(s.getRating().equals("") ? 0 : Float.parseFloat(s.getRating()));
            rating.setIsIndicator(true);
            Glide.with(search.this).load(s.getPic()).error(R.mipmap.ic_launcher_background).into(bookView);
            return v;
        }
    }

    void SearchKeyProcess(){
        final ArrayList<String> keywords = new ArrayList<>();
        final String words = k_word.getText().toString().equals("") ? "" : k_word.getText().toString();
        keywords.add(words);
        if(selection>3){
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        RegisteredUser ru = ds.getValue(RegisteredUser.class);
                        if(ru.getName().toUpperCase().indexOf(words.toUpperCase()) != -1 || ru.getName().toUpperCase().indexOf(words.toUpperCase()) != -1){
                            keywords.add(ru.getUserid());
                        }
                    }
                    ShowList(selection,keywords);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            ShowList(selection,keywords);
        }

    }
}

