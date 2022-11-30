package com.example.year_book_photo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.year_book_photo.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Fetching image...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String first = binding.typeFirst.getText().toString();
                String last = binding.typeLast.getText().toString();
                String year = binding.typeYear.getText().toString();
                String imageId = first + "_" + last + "_" + year;

                storageReference = FirebaseStorage.getInstance().getReference("Images/"+imageId + ".png");
                try {
                    File localfile = File.createTempFile("tempfile", ".png");
                    storageReference.getFile(localfile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                   if(progressDialog.isShowing()){
                                       progressDialog.dismiss();
                                       Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                       binding.imageView.setImageBitmap(bitmap);
                                   }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if(progressDialog.isShowing()){
                                        progressDialog.dismiss();
                                    }
                                    Toast.makeText(MainActivity.this, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }
}