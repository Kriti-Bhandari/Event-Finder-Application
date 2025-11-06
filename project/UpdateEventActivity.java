package com.example.project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateEventActivity extends AppCompatActivity {

    EditText etEventName, etDescription, etDate, etTime, etTicketPrice, etSeats;
    Spinner spinnerCategory, spinnerLocation;
    Button btnUpdateEvent;
    String selectedCategory, selectedLocation;

    String updateUrl = Constants.URL_update_event;

    int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        // Initialize views
        etEventName = findViewById(R.id.etEventName);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etTicketPrice = findViewById(R.id.etTicketPrice);
        etSeats = findViewById(R.id.etSeats);
        spinnerCategory = findViewById(R.id.categorySpinner);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        btnUpdateEvent = findViewById(R.id.btnUpdateEvent);

        // Get data from Intent
        Intent intent = getIntent();
        eventId = intent.getIntExtra("event_id", -1);
        String name = intent.getStringExtra("event_name");
        String description = intent.getStringExtra("event_description");
        String date = intent.getStringExtra("event_date");
        String time = intent.getStringExtra("event_time");
        String location = intent.getStringExtra("event_location");
        String category = intent.getStringExtra("event_category");
        String price = intent.getStringExtra("event_price");
        String seats = intent.getStringExtra("event_seats");

        // Set data
        etEventName.setText(name);
        etDescription.setText(description);
        etDate.setText(date);
        etTime.setText(time);
        etTicketPrice.setText(price);
        etSeats.setText(seats);

        // Populate spinners
        String[] districts = {
                "Select District", "Achham", "Arghakhanchi", "Baglung", "Baitadi", "Bajhang", "Bajura",
                "Banke", "Bara", "Bardiya", "Bhaktapur", "Bhojpur", "Chitwan", "Dadeldhura", "Dailekh",
                "Dang", "Darchula", "Dhading", "Dhankuta", "Dhanusha", "Dolakha", "Dolpa", "Doti", "Eastern Rukum",
                "Gorkha", "Gulmi", "Humla", "Ilam", "Jajarkot", "Jhapa", "Jumla", "Kailali", "Kalikot", "Kanchanpur",
                "Kapilvastu", "Kaski", "Kathmandu", "Kavrepalanchok", "Khotang", "Lalitpur", "Lamjung", "Mahottari",
                "Makwanpur", "Manang", "Morang", "Mugu", "Mustang", "Myagdi", "Nawalpur", "Nuwakot", "Okhaldhunga",
                "Palpa", "Panchthar", "Parbat", "Parsa", "Pyuthan", "Ramechhap", "Rasuwa", "Rautahat", "Rolpa",
                "Rupandehi", "Salyan", "Sankhuwasabha", "Saptari", "Sarlahi", "Sindhuli", "Sindhupalchok",
                "Siraha", "Solukhumbu", "Sunsari", "Surkhet", "Syangja", "Tanahun", "Taplejung", "Tehrathum",
                "Udayapur", "Western Rukum"
        };

        String[] categories = {
                "Arts & Culture", "Music & Entertainment", "Education & Career",
                "Sports & Fitness", "Health & Wellness", "Business & Networking",
                "Social & Volunteering", "Food & Drinks", "Tech & Innovation",
                "Parties & Nightlife", "Family & Kids"
        };

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, districts);
        spinnerLocation.setAdapter(locationAdapter);
        spinnerLocation.setSelection(getIndex(spinnerLocation, location));

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setSelection(getIndex(spinnerCategory, category));

        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateEventActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        etDate.setText(selectedDate);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateEventActivity.this,
                    (view, hourOfDay, minute) -> {
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        etTime.setText(selectedTime);
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        btnUpdateEvent.setOnClickListener(v -> {
            final String updatedName = etEventName.getText().toString().trim();
            final String updatedDescription = etDescription.getText().toString().trim();
            final String updatedDate = etDate.getText().toString().trim();
            final String updatedTime = etTime.getText().toString().trim();
            final String updatedPrice = etTicketPrice.getText().toString().trim();
            final String updatedSeats = etSeats.getText().toString().trim();
            selectedCategory = spinnerCategory.getSelectedItem().toString();
            selectedLocation = spinnerLocation.getSelectedItem().toString();

            if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedDate.isEmpty() || updatedTime.isEmpty()
                    || selectedCategory.isEmpty() || selectedLocation.isEmpty() || updatedPrice.isEmpty() || updatedSeats.isEmpty()) {
                Toast.makeText(UpdateEventActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            StringRequest request = new StringRequest(Request.Method.POST, updateUrl,
                    response -> {
                        Toast.makeText(UpdateEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    error -> Toast.makeText(UpdateEventActivity.this, "Update failed: " + error.getMessage(), Toast.LENGTH_LONG).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("event_id", String.valueOf(eventId));
                    params.put("name", updatedName);
                    params.put("description", updatedDescription);
                    params.put("date", updatedDate);
                    params.put("time", updatedTime);
                    params.put("location", selectedLocation);
                    params.put("category", selectedCategory);
                    params.put("price", updatedPrice);
                    params.put("total_seats", updatedSeats);
                    return params;
                }
            };

            Volley.newRequestQueue(UpdateEventActivity.this).add(request);
        });
    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }
}

