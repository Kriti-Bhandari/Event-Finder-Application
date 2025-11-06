package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    EditText etEventName, etDescription, etDate, etTime, etTicketPrice, etSeats;
    Spinner spinnerCategory, spinnerLocation;
    Button btnAddEvent;
    String selectedCategory, selectedLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize views
        ImageView ivBack = findViewById(R.id.ivBack);
        etEventName = findViewById(R.id.etEventName);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etTicketPrice = findViewById(R.id.etTicketPrice);
        etSeats = findViewById(R.id.etSeats); // New field
        spinnerCategory = findViewById(R.id.categorySpinner);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        btnAddEvent = findViewById(R.id.btnAddEvent);

        // Spinner data
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

        // Set up spinners
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedCategory = "";
            }
        });

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, districts);
        spinnerLocation.setAdapter(districtAdapter);
        spinnerLocation.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedLocation = parent.getItemAtPosition(position).toString();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedLocation = "";
            }
        });

        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(CreateEventActivity.this, AdminDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        etDate.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this,
                    (view, hourOfDay, minute1) -> {
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                        etTime.setText(selectedTime);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        btnAddEvent.setOnClickListener(v -> {
            final String eventName = etEventName.getText().toString().trim();
            final String description = etDescription.getText().toString().trim();
            final String date = etDate.getText().toString().trim();
            final String time = etTime.getText().toString().trim();
            final String ticketPrice = etTicketPrice.getText().toString().trim();
            final String seats = etSeats.getText().toString().trim();

            if (eventName.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty()
                    || selectedLocation.equals("Select District") || ticketPrice.isEmpty()
                    || selectedCategory.isEmpty() || seats.isEmpty()) {
                Toast.makeText(CreateEventActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send data to server
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_insert_event,
                    response -> {
                        Toast.makeText(CreateEventActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                    },
                    error -> Toast.makeText(CreateEventActivity.this, "Failed to add event: " + error.getMessage(), Toast.LENGTH_LONG).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", eventName);
                    params.put("description", description);
                    params.put("date", date);
                    params.put("time", time);
                    params.put("location", selectedLocation);
                    params.put("category", selectedCategory);
                    params.put("price", ticketPrice);
                    params.put("total_seats", seats); // Add seats to request
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(CreateEventActivity.this);
            requestQueue.add(stringRequest);
        });
    }

    private void clearFields() {
        etEventName.setText("");
        etDescription.setText("");
        etDate.setText("");
        etTime.setText("");
        etTicketPrice.setText("");
        etSeats.setText(""); // Clear seats input
        spinnerCategory.setSelection(0);
        spinnerLocation.setSelection(0);
    }
}
