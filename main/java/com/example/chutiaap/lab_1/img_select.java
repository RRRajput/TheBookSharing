package com.example.chutiaap.lab_1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.ImageVideoModelLoader;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class img_select extends AppCompatActivity {
    private Button img_but,img_sav,img_cap;
    private ImageView img_v;
    private Uri uri_src;
    private boolean isPresent;
    private StorageReference ref;
    private String user_id;
    private void initial(){
        img_but = (Button) findViewById(R.id.button4);
        img_sav = (Button) findViewById(R.id.button5);
        img_cap = (Button) findViewById(R.id.button6);

        img_v = (ImageView) findViewById(R.id.imageView);
        img_but.setText("Gallery");
        img_sav.setText("Done");
        img_cap.setText("Selfie");
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        ref = FirebaseStorage.getInstance().getReference().child("images/"+user_id+".jpeg");
        putImage();

    }

    private void putImage() {
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri.toString()).error(R.drawable.ic_launcher_background).into(img_v);
                isPresent= true;
                Toast.makeText(img_select.this, "Downloaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isPresent = false;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_select);
        initial();

        img_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //Intent gallery = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(gallery,10);
            }
        });
        img_sav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent res = new Intent();
                res.setData(uri_src);
                setResult(10,res);
                finish();
            }
        });
        img_cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent gallery = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(gallery,11);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isPresent",isPresent);
        outState.putString("uri",uri_src == null ? null : uri_src.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isPresent = savedInstanceState.getBoolean("isPresent");
        if(isPresent){
            uri_src = Uri.parse(savedInstanceState.getString("uri"));
            Bitmap pic = null;
            try {
                pic = MediaStore.Images.Media.getBitmap(getContentResolver(),uri_src);
                img_v.setImageBitmap(pic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Activity.RESULT_OK) {
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
                uri_src = saveImage(p, "selfie"+".jpeg");
            }
            //StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();

            ref.putFile(uri_src).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    putImage();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(img_select.this, "Image Upload/Download Failed", Toast.LENGTH_SHORT).show();
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
