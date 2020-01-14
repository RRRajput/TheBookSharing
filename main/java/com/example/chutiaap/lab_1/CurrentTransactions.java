package com.example.chutiaap.lab_1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CurrentTransactions extends AppCompatActivity {

    private String you,them;
    ImageView flagView;
    private String TheirString = "";
    private TextView title,taken,given;
    private ListView taken_list,given_list;
    private DatabaseReference takenref,givenref,generic;
    private ArrayList<SearchItem> searchItemArrayList = new ArrayList<>(),searchItemArrayList2 = new ArrayList<>();
    private ArrayList<String> bookID = new ArrayList<>(),bookID2 = new ArrayList<>();
    private lang_search lang = new lang_search("English");
    private TransLingua ling = null;
    SearchItemAdapter adapter1 = new SearchItemAdapter();
    SearchItemAdapter2 adapter2 = new SearchItemAdapter2();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_transactions);
        you = getIntent().getStringExtra("you");
        them = getIntent().getStringExtra("them");
        ///initialize title,taken,given,takenlist,givenlist
        initialize();
        changeLanguage(TheirString,true);
        takenref = FirebaseDatabase.getInstance().getReference("exchanges").child(them).child(you);
        givenref = FirebaseDatabase.getInstance().getReference("exchanges").child(you).child(them);
        generic = FirebaseDatabase.getInstance().getReference();
        flagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguage(TheirString,true);
                lang.changeLang();
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }
        });
        generic.child("users").child(them).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    RegisteredUser ru = dataSnapshot.getValue(RegisteredUser.class);
                    TheirString = ru.getName();
                    changeLanguage(ru.getName(),false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        takenref.orderByChild("returned").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        final Exchange ex = ds.getValue(Exchange.class);
                        generic.child("users").child(them).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    final RegisteredUser ru = dataSnapshot.getValue(RegisteredUser.class);
                                    Log.d("Transaction",ex.getBookid() + " not inside though");
                                    generic.child("books").child(ex.getBookid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                Book b = dataSnapshot.getValue(Book.class);
                                                SearchItem s = new SearchItem(b,ru);
                                                s.setPicID(dataSnapshot.getKey());
                                                searchItemArrayList.add(s);
                                                bookID.add(ex.getBookid());
                                                Log.d("Transaction", "added the book " + b.getTitle());
                                                Log.d("Transaction","Adapter set");
                                                taken_list.setAdapter(adapter1);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        givenref.orderByChild("returned").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        final Exchange ex = ds.getValue(Exchange.class);
                        generic.child("users").child(you).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    final RegisteredUser ru = dataSnapshot.getValue(RegisteredUser.class);
                                    generic.child("books").child(ex.getBookid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                Book b = dataSnapshot.getValue(Book.class);
                                                SearchItem s = new SearchItem(b,ru);
                                                s.setPicID(dataSnapshot.getKey());
                                                searchItemArrayList2.add(s);
                                                bookID2.add(ex.getBookid());
                                                given_list.setAdapter(adapter2);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        taken_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent chat = new Intent(CurrentTransactions.this,ChatMessage.class);
                chat.putExtra("return",true);
                chat.putExtra("bookID",bookID.get(i));
                chat.putExtra("from",you);
                chat.putExtra("to",them);
                startActivity(chat);
                onBackPressed();
            }
        });

    }

    private void initialize() {
        taken_list = findViewById(R.id.takenlist);
        given_list = findViewById(R.id.givenlist);
        title = findViewById(R.id.mainTitle);
        taken = findViewById(R.id.takentitletext);
        given = findViewById(R.id.giventitletext);
        flagView = findViewById(R.id.flagView);
    }

    void changeLanguage(String them, boolean b){
        if(ling== null){
            ling = new TransLingua("English");
        }else if(b){
            ling.changeLanguage();
        }
        title.setText(ling.title(them));
        taken.setText(ling.taken(them));
        given.setText(ling.given(them));
        if(ling.isEng){
            flagView.setImageResource(R.drawable.uk);
        }else{
            flagView.setImageResource(R.drawable.it);
        }
    }

    @Override
    public void onBackPressed() {
        taken_list.setAdapter(null);
        given_list.setAdapter(null);
        finish();
    }

    class SearchItemAdapter extends BaseAdapter {

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
            Glide.with(getApplicationContext()).load(s.getPic()).error(R.mipmap.ic_launcher_background).into(bookView);
            return v;
        }
    }

    class SearchItemAdapter2 extends BaseAdapter {

        @Override
        public int getCount() {
            return searchItemArrayList2.size();
        }

        @Override
        public SearchItem getItem(int i) {
            return searchItemArrayList2.get(i);
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
            SearchItem s = searchItemArrayList2.get(i);
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

    class TransLingua {
        boolean isEng;

        TransLingua(String ling){
            if(ling.equals("English")){
                isEng = true;
            }
            else{
                isEng = false;
            }
        }

        void changeLanguage(){
            isEng = !isEng;
        }

        String title(String them){
            if(isEng){
                return "Exchange between you and " + them;
            }
            return "Lo scambio tra te e " + them;
        }

        String taken(String them){
            if(isEng){
                return "Books taken from " + them;
            }
            return "I libri presi da " + them;
        }

        String given(String them){
            if(isEng){
                return "Books given to " + them;
            }
            return "Libri prestati a " + them;
        }

    }
}
