package com.example.project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecentRegistrationsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Regevent> eventList;
    RegistrationsAdapter eventAdapter;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_registrations);

        recyclerView = findViewById(R.id.recyclerViewRecentRegistrations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new RegistrationsAdapter(this, eventList);
        recyclerView.setAdapter(eventAdapter);

        // Get user_id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);

        loadRegisteredEvents();
    }

    private void loadRegisteredEvents() {
        String url = Constants.URL_get_user_registrations;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONArray array = obj.getJSONArray("events");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject event = array.getJSONObject(i);
                                eventList.add(new Regevent(
                                        event.getInt("id"),
                                        event.getString("name"),
                                        event.getString("date"),
                                        event.getString("location"),
                                        event.getString("category"),
                                        event.getDouble("ticket_price")
                                ));
                            }
                            eventAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No registrations found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error loading events", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
