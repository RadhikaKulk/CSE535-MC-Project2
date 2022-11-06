package com.example.cameraapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;

public class CategorySelection extends AppCompatActivity {

    private static String categorySelected = "Landscape";
    private MultipartBody body;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_selection);

        ImageView img=  findViewById(R.id.imageView);
        img.setRotation(90);
        img.setImageBitmap(BitmapFactory.decodeFile(MainActivity.pathName));

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Button uploadButton = findViewById(R.id.uploadImage);
        uploadButton.setOnClickListener(v->{
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            String uri = getIntent().getExtras().getString("IMAGE_URI");

            String[] parts = uri.split("/");
            String file_name = uri.split("/")[parts.length - 1];


            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("files", file_name,
                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                    new File(MainActivity.pathName)))
                    .addFormDataPart("category", categorySelected);
            body = builder.build();

            Request request = new Request.Builder()
                    .url("http://192.168.0.235:10001/uploader")
                    .method("POST", body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response.toString());
                Toast.makeText(this, "Upload Successful!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        });
    }
}
