package com.example.project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventRegistrationActivity extends AppCompatActivity {

    String eventId, eventName, eventDate, eventTime, eventCategory, eventLocation, eventPrice, userId;

    TextView tvEventName,tvDate, tvCategory, tvLocation, tvPrice;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_register);

        // Bind Views
        tvEventName = findViewById(R.id.tvEventName);
        tvCategory = findViewById(R.id.tvEventCategory);
        tvLocation = findViewById(R.id.tvEventLocation);
        tvDate = findViewById(R.id.tvEventDate);
        tvPrice = findViewById(R.id.tvPrice);
        btnRegister = findViewById(R.id.btnRegister);

        // Get Intent Data
        eventId = getIntent().getStringExtra("event_id");
        eventName = getIntent().getStringExtra("event_name");
        eventDate = getIntent().getStringExtra("event_date");
        eventCategory = getIntent().getStringExtra("event_category");
        eventLocation = getIntent().getStringExtra("event_location");
        eventPrice = getIntent().getStringExtra("event_price");
        eventTime = getIntent().getStringExtra("event_time");
        Log.d("ReminderDebug", "Received eventTime: " + eventTime);




        // Get user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);

        // Set TextViews
        tvEventName.setText("Name: " + eventName);
        tvDate.setText("Date: " + eventDate);
        tvCategory.setText("Category: " + eventCategory);
        tvLocation.setText("Location: " + eventLocation);
        tvPrice.setText("Price: Rs. " + eventPrice);

        // On Register Button Click
        btnRegister.setOnClickListener(v -> checkSeatsAndRegister());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

    }

    private void checkSeatsAndRegister() {
        String url = Constants.URL_check_seats;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("SeatCheckResponse", "Raw Response: " + response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean seatsAvailable = obj.getBoolean("seats_available");

                        if (seatsAvailable) {
                            registerEventToServer();
                        } else {
                            Toast.makeText(this, "No seats left for this event!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing seat check response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                        error.printStackTrace();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String body = new String(error.networkResponse.data);
                            Log.e("VolleyError", "Response body: " + body);
                        }
                        Toast.makeText(this, "Error checking seat availability", Toast.LENGTH_SHORT).show();

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("event_id", eventId);
                return params;
            }
        };


        Volley.newRequestQueue(this).add(request);
    }

    private void registerEventToServer() {
        String url = Constants.URL_register_event;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("RegisterResponse", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean success = obj.getBoolean("success");

                        if (success) {
                            Toast.makeText(this, "Event Registered Successfully!", Toast.LENGTH_SHORT).show();
                            setReminderAlarm();
                        } else {
                            String errorMessage = obj.optString("error", "Registration failed");
                            if (errorMessage.equalsIgnoreCase("Already registered")) {
                                Toast.makeText(this, "You have already registered for this event!", Toast.LENGTH_LONG).show();
                            } else if (errorMessage.equalsIgnoreCase("No seats available")) {
                                Toast.makeText(this, "No seats available for this event!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to register event", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("event_id", eventId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setReminderAlarm() {
        try {
            // Combine date and time string
            String eventDateTime = eventDate + " " + eventTime; // e.g., "2025-06-15 15:30"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date eventDateParsed = sdf.parse(eventDateTime);

            if (eventDateParsed == null) {
                Toast.makeText(this, "Invalid event time", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set the alarm to 1 minute before event time
            long triggerTime = eventDateParsed.getTime() - (60 * 1000); // 1 minute before

            if (triggerTime <= System.currentTimeMillis()) {
                Toast.makeText(this, "Event is too close or in the past", Toast.LENGTH_SHORT).show();
                return;
            }
            if (eventTime == null || eventTime.trim().isEmpty()) {
                Toast.makeText(this, "Event time is missing", Toast.LENGTH_SHORT).show();
                Log.e("ReminderDebug", "eventTime is null or empty");
                return;
            }
            // Create Intent and PendingIntent
            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("event_name", eventName);
            intent.putExtra("event_time", eventDateParsed.getTime());


            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, Integer.parseInt(eventId), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                Toast.makeText(this, "Reminder set 1 minute before event", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to set reminder", Toast.LENGTH_SHORT).show();
        }
    }




}
