package com.example.chutiaap.lab_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.Result;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class bar_code extends AppCompatActivity {
    private ZXingScannerView scannerView;
    private String resultCode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultCode="";
        scannerView = new ZXingScannerView(this);
        scannerView.setResultHandler(new ResultHandler());
        setContentView(scannerView);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    class ResultHandler implements ZXingScannerView.ResultHandler{

        @Override
        public void handleResult(Result result) {
            resultCode = result.getText().toString();
            scannerView.stopCamera();
            ReturnResults();
        }
    }

    void ReturnResults(){
        Intent end = new Intent();
        end.putExtra("ISBN",resultCode);
        setResult(1,end);
        finish();
    }

    @Override
    public void onBackPressed() {
        ReturnResults();
    }
}
