package com.example.chutiaap.lab_1;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class user_rep extends AppCompatActivity {

    String user_id;
    ImageView thisImage;
    TextView thisName,thisAddress,thisDescription,thisPhone,revTitle;
    ImageButton lib,edit;
    RatingBar thisRating;
    ListView revList;
    boolean isEng;
    ImageView flagView;
    DatabaseReference revs;
    ArrayList<Review> reviewArrayList;
    FirebaseListAdapter<Review> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rep);
        user_id = getIntent().getStringExtra("user_id");
        revs = FirebaseDatabase.getInstance().getReference("reviews").child(user_id);
        initialize();
        flagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguage();
            }
        });
        lib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent library = new Intent(user_rep.this,UserLibrary.class);
                library.putExtra("user",user_id);
                startActivity(library);
                finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editprof = new Intent(user_rep.this,create_profile.class);
                startActivity(editprof);
                finish();
            }
        });
        FirebaseDatabase.getInstance().getReference("users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RegisteredUser ru = dataSnapshot.getValue(RegisteredUser.class);
                thisName.setText(ru.getName() + " " + ru.getSurname());
                thisAddress.setText(ru.getAddress());
                thisDescription.setText(ru.getDescription());
                thisPhone.setText(ru.getPhone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        try{
            FirebaseStorage.getInstance().getReference("images/"+user_id+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri.toString()).error(R.drawable.user_id2).into(thisImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(user_rep.this, "no user image found", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
       adapter = new FirebaseListAdapter<Review>(this,Review.class,R.layout.review,revs){

           @Override
           protected void populateView(View v, Review model, int position) {
                final ImageView reviewImage = v.findViewById(R.id.ReviewImage);
                TextView reviewName = v.findViewById(R.id.ReviewName);
                RatingBar rating = v.findViewById(R.id.rating);
                TextView rev = v.findViewById(R.id.rev);

                reviewName.setText(model.getName());
                rating.setRating(model.getRating());
                rev.setText(model.getComment());
                FirebaseStorage.getInstance().getReference("images/"+model.getId()+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri.toString()).error(R.drawable.user_id2).into(reviewImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(mActivity, "Review Image not found", Toast.LENGTH_SHORT).show();
                    }
                });
           }
       };

       revList.setAdapter(adapter);
       revs.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   float sum = 0;
                   int total = 0;
                   for(DataSnapshot ds: dataSnapshot.getChildren()){
                       sum = sum + ds.getValue(Review.class).getRating();
                       total++;
                   }
                   thisRating.setRating(sum/total);
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }

    private void initialize() {
        thisImage = findViewById(R.id.thisImage);
        thisName = findViewById(R.id.thisName);
        thisDescription = findViewById(R.id.thisDescription);
        thisAddress = findViewById(R.id.thisAddress);
        thisPhone = findViewById(R.id.thisPhone);
        thisRating = findViewById(R.id.thisRating);
        revList = findViewById(R.id.reviewList);
        revTitle = findViewById(R.id.revTitleText);
        lib = findViewById(R.id.LibraryImageButton);
        edit = findViewById(R.id.EditImageButton);
        flagView = findViewById(R.id.flagView);
        isEng = false;
        changeLanguage();
        if(!user_id.equals(FirebaseAuth.getInstance().getUid())){
            edit.setVisibility(View.GONE);
        }
    }

    private void changeLanguage() {
        if(isEng){
            isEng = false;
            revTitle.setText("Recensioni");
            flagView.setImageResource(R.drawable.it);
        }else{
            isEng = true;
            revTitle.setText("Reviews");
            flagView.setImageResource(R.drawable.uk);
        }
    }
}
