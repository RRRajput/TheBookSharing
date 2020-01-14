package com.example.chutiaap.lab_1;

import android.location.Address;
import android.location.Geocoder;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String word;
    private int i;
    DatabaseReference dbref;
    DatabaseReference users;
    String title;
    String subtitle;
    lang_search lang ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        word = getIntent().getStringExtra("keyword");
        i = getIntent().getIntExtra("i",0);
        dbref = FirebaseDatabase.getInstance().getReference("books");
        users = FirebaseDatabase.getInstance().getReference("users");
        lang = new lang_search("English");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final ArrayList<String> keywords = new ArrayList<>();
        keywords.add(word);
        if(i>3){
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        RegisteredUser ru = ds.getValue(RegisteredUser.class);
                        if(ru.getName().toUpperCase().indexOf(word.toUpperCase()) != -1 || ru.getName().toUpperCase().indexOf(word.toUpperCase()) != -1){
                            keywords.add(ru.getUserid());
                        }
                    }
                    ShowList(i,keywords);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            ShowList(i,keywords);
        }
        /*Geocoder geocoder = new Geocoder(MapsActivity.this);
        try {
            List<Address> fromLocationName = geocoder.getFromLocationName("Piazza Cavour 5 Torino", 1);
            LatLng rehan = new LatLng(fromLocationName.get(0).getLatitude(),fromLocationName.get(0).getLongitude());
            MarkerOptions mo = new MarkerOptions()
                    .position(rehan)
                    .title("Rehan lives here")
                    .snippet("subtitle goes here");
            mMap.addMarker(mo);
            CameraPosition build = CameraPosition.builder().target(rehan).zoom(12).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(build));
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    private void ShowList(final int i, final ArrayList<String> keyword) {
        users.child(FirebaseAuth.getInstance().getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RegisteredUser ru = dataSnapshot.getValue(RegisteredUser.class);
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                try{
                    if(ru != null) {
                        List<Address> fromLocationName = geocoder.getFromLocationName(ru.getAddress(),1);
                        if(!fromLocationName.isEmpty()){
                            LatLng latLng = new LatLng(fromLocationName.get(0).getLatitude(),fromLocationName.get(0).getLongitude());
                            CameraPosition build = CameraPosition.builder().target(latLng).zoom(12).build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(build));
                        }
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren() ){
                        final Book b = ds.getValue(Book.class);
                        boolean value = false;
                        for(String k : keyword){
                            value = value || isValid(i,k,b);
                            if(value){
                                break;
                            }
                        }
                        if (value){
                            users.child(b.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    RegisteredUser ru = dataSnapshot.getValue(RegisteredUser.class);
                                    title = b.getTitle() + " By: " + b.getAuthor();
                                    subtitle = lang.getUser() + " : " + ru.getName()  +
                                                "Rating : " + b.getRating();
                                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                                    try {
                                        List<Address> fromLocationName = geocoder.getFromLocationName(ru.getAddress(), 1);
                                        if (!fromLocationName.isEmpty()){
                                            LatLng latLng = new LatLng(fromLocationName.get(0).getLatitude(),fromLocationName.get(0).getLongitude());
                                            MarkerOptions m = new MarkerOptions().position(latLng)
                                                    .title(title).snippet(subtitle);
                                            mMap.addMarker(m);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
                /*for(MarkerOptions m : markerList){
                    mMap.addMarker(m);
                    Toast.makeText(MapsActivity.this, "Marker Added", Toast.LENGTH_SHORT).show();
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        /*} else if(i==4){
            RegisteredUser u = UserFromID(b.getUserID());
            user = u.getName().contains(keyword) || u.getSurname().contains(keyword);
            return user;*/
        }else{
            title = b.getTitle().toUpperCase().indexOf(keyword.toUpperCase()) != -1;
            author = b.getAuthor().toUpperCase().indexOf(keyword.toUpperCase()) != -1;
            publisher = b.getPublisher().toUpperCase().indexOf(keyword.toUpperCase()) != -1;
            ISBN = b.getPublisher().toUpperCase().indexOf(keyword.toUpperCase()) != -1;
            return title || author || publisher || ISBN;
        }
    }
}
