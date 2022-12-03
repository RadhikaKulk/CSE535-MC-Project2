package com.example.cameraapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static int counter = 1;
    public static int IMAGE_CAPTURED = 200;
    public static Uri uriForFile;
    public static String pathName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, PackageManager.GET_PERMISSIONS);
        StrictMode.VmPolicy.Builder policyBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(policyBuilder.build());

    }

    public void clickPicture(View view) throws IOException {
        String nameOfThePic =  counter + ".jpeg";
        counter+=1;

        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File picFile = new File(getExternalFilesDir(Environment.getExternalStorageDirectory().getAbsolutePath()), nameOfThePic);
        pathName = picFile.getAbsolutePath();
        uriForFile = FileProvider.getUriForFile(this.getApplicationContext(), this.getApplicationContext().getPackageName() + ".fileprovider", picFile);
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        startActivityForResult(pictureIntent, IMAGE_CAPTURED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_CAPTURED && resultCode == RESULT_OK) {
            System.out.println("Image captured");

            Toast.makeText(this, "Picture Clicked!", Toast.LENGTH_LONG).show();
            Intent categorySelection = new Intent(this, CategorySelection.class);
            categorySelection.putExtra("IMAGE_URI", uriForFile.toString());
            startActivity(categorySelection);
        }
    }



}