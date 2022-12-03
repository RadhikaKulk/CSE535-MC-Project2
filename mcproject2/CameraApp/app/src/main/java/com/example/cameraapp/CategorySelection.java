package com.example.cameraapp;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class CategorySelection extends AppCompatActivity {

    private static String categorySelected = "Landscape";
    private MultipartBody body;

    @SuppressLint("WrongThread")
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


        ArrayList<Bitmap> chunkedImages = splitImage(img, 4);
       System.out.println(chunkedImages);
        for(int i=0; i<4; i++){
            String filename = "img_"+(i+1);
            try {
                String path = Environment.getExternalStorageDirectory().toString();
                OutputStream fOut = null;
                //Integer counter = 0;
                File file = new File(path, filename+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                fOut = new FileOutputStream(file);

                Bitmap pictureBitmap = chunkedImages.get(i); // obtaining the Bitmap
                pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream

                MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            System.out.println("File output written");

            Button discoverButton = findViewById(R.id.discoveryButton);
            discoverButton.setOnClickListener(v -> {
                discoverClient();
            });

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

    private ArrayList<Bitmap> splitImage(ImageView image, int chunkNumbers) {

        //For the number of rows and columns of the grid to be displayed
        int rows,cols;

        //For height and width of the small image chunks
        int chunkHeight,chunkWidth;

        //To store all the small image chunks in bitmap format in this list
        ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>(chunkNumbers);

        //Getting the scaled bitmap of the source image
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        rows = cols = (int) Math.sqrt(chunkNumbers);
        chunkHeight = bitmap.getHeight() / rows;
        chunkWidth = bitmap.getWidth() / cols;

        //xCoord and yCoord are the pixel positions of the image chunks
        int yCoord = 0;
        for(int x = 0; x < rows; x++) {
            int xCoord = 0;
            for(int y = 0; y < cols; y++) {
                chunkedImages.add(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight));
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }

        return chunkedImages;
    }

//    private void setEventListeners() {
//        bMaster.setOnClickListener((view) -> {
//            startClientDiscoveryActivity();
//        });
//
//
//        bWorker.setOnClickListener((view) -> {
//            startWorkAdvertisementActivity();
//        });
//    }

    private void discoverClient() {
        Intent intent = new Intent(getApplicationContext(), DiscoverClientActivity.class);
        startActivity(intent);
    }

//    private void startWorkAdvertisementActivity() {
//        Intent intent = new Intent(getApplicationContext(), WorkerInitiationActivity.class);
//        startActivity(intent);
//    }

}
