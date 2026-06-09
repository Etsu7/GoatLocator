package com.massinond.menon.goatlocator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivGoatPhoto;
    private TextView tvLocation;
    private Uri photoUri;
    private File photoFile;

    private double savedLatitude  = 0;
    private double savedLongitude = 0;

    // Launcher pour récupérer la position depuis MapActivity
    private final ActivityResultLauncher<Intent> mapLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    (ActivityResult result) -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            double lat = result.getData().getDoubleExtra("latitude", 0);
                            double lon = result.getData().getDoubleExtra("longitude", 0);

                            tvLocation.setText(getString(R.string.col_lat) + " : " + String.format(Locale.getDefault(), "%.6f", lat)
                                    + "\n" + getString(R.string.col_lon) + " : " + String.format(Locale.getDefault(), "%.6f", lon));

                            savedLatitude  = lat;
                            savedLongitude = lon;

                            Toast.makeText(this, "Position enregistrée !", Toast.LENGTH_SHORT).show();
                        }
                    });

    // Launcher pour l'appareil photo
    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && photoFile != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    ivGoatPhoto.setImageBitmap(bitmap);
                    Toast.makeText(this, R.string.photo_saved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.photo_cancelled, Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ivGoatPhoto = findViewById(R.id.ivGoatPhoto);
        tvLocation = findViewById(R.id.tvLocation);

        // Bouton localiser → ouvre MapActivity et attend le résultat
        Button btnLocate = findViewById(R.id.btnLocateGoat);
        btnLocate.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            mapLauncher.launch(intent);
        });

        // Bouton photo → ouvre l'appareil photo
        Button btnPhoto = findViewById(R.id.btnTakePhoto);
        btnPhoto.setOnClickListener(v -> takePhoto());

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {

            if (savedLatitude == 0 && savedLongitude == 0) {
                Toast.makeText(this, R.string.locate_first, Toast.LENGTH_SHORT).show();
                return;
            }

            // Chemin photo : chaîne vide si pas de photo prise
            String path = (photoFile != null && photoFile.exists())
                    ? photoFile.getAbsolutePath()
                    : "";

            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(new Date());

            GoatDatabaseHelper db = new GoatDatabaseHelper(this);
            long id = db.insertGoat(path, savedLatitude, savedLongitude, date);

            if (id != -1) {
                Toast.makeText(this, R.string.saved_ok, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.save_error, Toast.LENGTH_SHORT).show();
            }
        });

        // Bouton retour → retourne à MainActivity
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // évite d'empiler les activités
            startActivity(intent);
        });
    }

    private void takePhoto() {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            String fileName = "CHEVRE_" + timestamp + ".jpg";

            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            photoFile = new File(storageDir, fileName);

            photoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    photoFile
            );

            takePictureLauncher.launch(photoUri);

        } catch (Exception e) {
            Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}