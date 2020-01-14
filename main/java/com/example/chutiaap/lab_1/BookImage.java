package com.example.chutiaap.lab_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chutiaap.lab_1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Stefania Titone on 26/04/2018.
 */

public class BookImage extends AppCompatActivity {

    ImageView img;
    Button sub,gallery,camera;
    Uri uri_src,online;
    String ISBN,userID;
    StorageReference ref;
    boolean success;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_select);
        initial();
    }

    private void initial() {
        camera = findViewById(R.id.button6);
        sub = findViewById(R.id.button5);
        gallery = findViewById(R.id.button4);
        img = findViewById(R.id.imageView);
        ISBN = getIntent().getStringExtra("ISBN");
        userID = FirebaseAuth.getInstance().getUid().toString();
        ref = FirebaseStorage.getInstance().getReference("books/" + userID +"/"+ISBN);

        camera.setText("Camera");
        gallery.setText("Gallery");
        sub.setText("Submit");
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //Intent gallery = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(gallery,10);
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent gallery = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(gallery,11);
            }
        });
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(success){
                    Intent in = new Intent();
                    in.putExtra("pic_uri",online.toString());
                    setResult(1,in);
                    finish();
                }
                else{
                    setResult(0);
                    finish();
                }
            }
        });
    }
    private void putImage() {
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                online = uri;
                Glide.with(getApplicationContext()).load(uri.toString()).error(R.drawable.ic_launcher_background).into(img);
                Toast.makeText(BookImage.this, "Downloaded", Toast.LENGTH_SHORT).show();
                success = true;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BookImage.this, "Download Failure", Toast.LENGTH_SHORT).show();
                success=false;

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if (requestCode == 10) {
                uri_src = data.getData();
            /*try {
                Bitmap pic = MediaStore.Images.Media.getBitmap(getContentResolver(), uri_src);
                img_v.setImageBitmap(pic);
                isPresent = true;
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            } else if (requestCode == 11) {
                Bitmap p = (Bitmap) data.getExtras().get("data");
                uri_src = saveImage(p, "Book"+".jpeg");
            }
        ref.putFile(uri_src).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                putImage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BookImage.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
        }
    }
    private Uri saveImage(Bitmap pic,String filename){
        String storagePath = Environment.getExternalStorageDirectory()+ "/BookSharing/";
        File dir = new File(storagePath);
        dir.mkdirs();
        String filepath = dir.toString() + filename;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filepath);
            BufferedOutputStream bos = new BufferedOutputStream(out);
            pic.compress(Bitmap.CompressFormat.JPEG,100,bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(new File(filepath));
    }
}
