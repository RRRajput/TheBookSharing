package com.example.chutiaap.lab_1;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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

public class singleReview extends AppCompatActivity {

    ImageView revImage;
    TextView revName;
    RatingBar revRating;
    Button revSubmit;
    EditText revText;
    boolean isEng;
    ImageView flagView;

    String revUsername;
    DatabaseReference generic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_review);
        initialize();
        flagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguage();
            }
        });
        revUsername = getIntent().getStringExtra("user");
        revName.setText(revUsername);
        generic = FirebaseDatabase.getInstance().getReference();
        generic.child("users").child(revUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    RegisteredUser ru = dataSnapshot.getValue(RegisteredUser.class);
                    revName.setText(ru.getName() + " " + ru.getSurname());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseStorage.getInstance().getReference("images/"+revUsername+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri.toString()).error(R.drawable.user_id2).into(revImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("singleReview","User has no picture");
            }
        });
        revSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitButton();
            }
        });
    }

    private void submitButton() {
        if(revRating.getRating() == (float) 0){
            Toast.makeText(getApplicationContext(),"Please enter a rating",Toast.LENGTH_SHORT).show();
            return;
        }
        generic.child("users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final RegisteredUser ru = dataSnapshot.getValue(RegisteredUser.class);
                Review r = new Review(ru.getName()+" "+ru.getSurname(),ru.getUserid(),revRating.getRating());
                r.setComment(revText.getText().toString());
                generic.child("reviews").child(revUsername).push().setValue(r);
                generic.child("perm_review").child(revUsername).child(ru.getUserid()).setValue(false);
                onBackPressed();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initialize() {
        revImage = findViewById(R.id.revImage);
        revName = findViewById(R.id.revUsername);
        revRating = findViewById(R.id.reviewrating);
        revSubmit = findViewById(R.id.reviewsubmit);
        revText = findViewById(R.id.reviewtext);
        flagView = findViewById(R.id.flagView);
        isEng = false;
        changeLanguage();
    }

    void changeLanguage(){
        if(isEng){
            isEng=false;
            flagView.setImageResource(R.drawable.it);
            revText.setHint("Descrivi la tua esperienza...(opzionale)");
            revSubmit.setText("Fatto");
        }
        else{
            isEng = true;
            flagView.setImageResource(R.drawable.uk);
            revText.setHint("Describe your experience...(optional)");
            revSubmit.setText("Submit");
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
