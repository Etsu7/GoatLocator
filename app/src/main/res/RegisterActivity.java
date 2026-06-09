package com.example.goatlocator; // ← adapte

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivGoatPhoto;
    private Uri photoUri;
    private File photoFile;

    // Launcher pour l'appareil photo
    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && photoFile != null) {
                    // Affiche la photo prise dans l'ImageView
                    Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    ivGoatPhoto.setImageBitmap(bitmap);
                    Toast.makeText(this, "Photo enregistrée !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Photo annulée", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ivGoatPhoto = findViewById(R.id.ivGoatPhoto);

        // Bouton localiser → ouvre MapActivity
        Button btnLocate = findViewById(R.id.btnLocateGoat);
        btnLocate.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        });

        // Bouton photo → ouvre l'appareil photo
        Button btnPhoto = findViewById(R.id.btnTakePhoto);
        btnPhoto.setOnClickListener(v -> takePhoto());
    }

    private void takePhoto() {
        try {
            // Crée un fichier image avec un nom unique basé sur la date
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            String fileName = "CHEVRE_" + timestamp + ".jpg";

            // Dossier de stockage : Pictures de l'app (stockage interne privé)
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            photoFile = new File(storageDir, fileName);

            // Convertit le fichier en URI sécurisé via FileProvider
            photoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    photoFile
            );

            // Lance l'appareil photo
            takePictureLauncher.launch(photoUri);

        } catch (Exception e) {
            Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}