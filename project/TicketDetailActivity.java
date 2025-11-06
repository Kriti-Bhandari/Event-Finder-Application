package com.example.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TicketDetailActivity extends AppCompatActivity {

    TextView tvEventName, tvEventDate, tvEventLocation, tvEventCategory, tvTicketPrice;
    Button downloadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        tvEventName = findViewById(R.id.tvEventName);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvEventLocation = findViewById(R.id.tvEventLocation);
        tvEventCategory = findViewById(R.id.tvEventCategory);
        tvTicketPrice = findViewById(R.id.tvTicketPrice);
        downloadBtn = findViewById(R.id.btnDownload);

        String name = getIntent().getStringExtra("name");
        String date = getIntent().getStringExtra("date");
        String location = getIntent().getStringExtra("location");
        String category = getIntent().getStringExtra("category");
        double price = getIntent().getDoubleExtra("price", 0);

        tvEventName.setText("Event: " + name);
        tvEventDate.setText("Date: " + date);
        tvEventLocation.setText("Location: " + location);
        tvEventCategory.setText("Category: " + category);
        tvTicketPrice.setText("Price: Rs. " + price);

        downloadBtn.setOnClickListener(v -> {
            View ticketLayout = findViewById(R.id.ticketLayout); // your root layout
            saveTicketAsImage(ticketLayout);
        });
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void saveTicketAsImage(View layoutView) {
        // Find the download button
        Button downloadBtn = findViewById(R.id.btnDownload);

        // Hide it before capture
        downloadBtn.setVisibility(View.INVISIBLE);

        // Create the bitmap
        Bitmap bitmap = getBitmapFromView(layoutView);

        // Restore the button visibility
        downloadBtn.setVisibility(View.VISIBLE);

        // Save to public Pictures directory
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String fileName = "ticket_" + System.currentTimeMillis() + ".png";
        File file = new File(picturesDir, fileName);

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            // Notify media scanner
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            Toast.makeText(this, "Ticket saved to Gallery!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save ticket.", Toast.LENGTH_SHORT).show();
        }
    }


}
