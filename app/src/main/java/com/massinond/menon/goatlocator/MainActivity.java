package com.massinond.menon.goatlocator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRegister = findViewById(R.id.btnRegisterGoat);
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGoatsTable();
    }

    private void loadGoatsTable() {
        LinearLayout tableBody = findViewById(R.id.tableBody);
        tableBody.removeAllViews();

        GoatDatabaseHelper db = new GoatDatabaseHelper(this);
        List<Goat> goats = db.getAllGoats();

        if (goats.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText(getString(R.string.no_goats));
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, 32, 0, 0);
            empty.setTextColor(Color.parseColor("#9E9E9E"));
            tableBody.addView(empty);
            return;
        }

        for (int i = 0; i < goats.size(); i++) {
            Goat goat = goats.get(i);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(8, 8, 8, 8);
            row.setBackgroundColor(i % 2 == 0
                    ? Color.parseColor("#FFF8F0")
                    : Color.parseColor("#EFE5D8"));

            row.addView(makeCell(String.valueOf(goat.id), 0, 30));
            row.addView(makeCell(goat.date, 2, 0));
            row.addView(makeCell(String.format("%.4f", goat.latitude), 2, 0));
            row.addView(makeCell(String.format("%.4f", goat.longitude), 2, 0));
            if (!goat.photoPath.isEmpty()) {
                ImageView thumb = new ImageView(this);
                Bitmap bmp = BitmapFactory.decodeFile(goat.photoPath);
                thumb.setImageBitmap(bmp);
                thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
                int size = (int)(48 * getResources().getDisplayMetrics().density);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, size, 1f);
                p.setMargins(4, 4, 4, 4);
                thumb.setLayoutParams(p);
                row.addView(thumb);
            } else {
                row.addView(makeCell("—", 1, 0));
            }

            tableBody.addView(row);
        }
    }

    private TextView makeCell(String text, float weight, int fixedWidthDp) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#3E2723"));
        tv.setTextSize(11);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(6, 6, 6, 6);

        LinearLayout.LayoutParams params;
        if (fixedWidthDp > 0) {
            int px = (int) (fixedWidthDp * getResources().getDisplayMetrics().density);
            params = new LinearLayout.LayoutParams(px, LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        }
        tv.setLayoutParams(params);
        return tv;
    }
}