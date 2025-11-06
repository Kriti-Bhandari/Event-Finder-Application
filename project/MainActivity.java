package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageButton btnHome, btnCategory, btnSearch, btnRegister,btnTicketing, logoutBtn;
    Spinner categorySpinner;
    Spinner sortSpinner;
    RecyclerView recyclerEvents;
    ArrayList<Event> eventList;
    ArrayList<Ticket> ticketlist;
    ArrayList<Regevent> reventList;
    TicketAdapter ticketAdapter;
    RegistrationsAdapter registrationsAdapter;
    EventAdapter adapter;



    String[] categories = {
            "Arts & Culture", "Music & Entertainment", "Education & Career",
            "Sports & Fitness", "Health & Wellness", "Business & Networking",
            "Social & Volunteering", "Food & Drinks", "Tech & Innovation",
            "Parties & Nightlife", "Family & Kids"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHome = findViewById(R.id.btnHome);
        btnCategory = findViewById(R.id.btnCategory);
        btnSearch = findViewById(R.id.SearchBtn);
        btnRegister = findViewById(R.id.btnRegister);
        btnTicketing= findViewById(R.id.btnTicketing);
        logoutBtn = findViewById(R.id.LogoutBtn);
        categorySpinner = findViewById(R.id.categorySpinner);
        sortSpinner = findViewById(R.id.sortSpinner);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(categoryAdapter);
        String[] sortOptions = {
                "Sort by Date (Newest)",
                "Sort by Date (Oldest)",
                "Sort by Price (Low to High)",
                "Sort by Price (High to Low)",
                "Sort by Name (A–Z)",
                "Sort by Name (Z–A)"
        };
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, sortOptions);
        sortSpinner.setAdapter(sortAdapter);

        recyclerEvents = findViewById(R.id.recyclerEvents);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        adapter = new EventAdapter(this, eventList);

        reventList = new ArrayList<>();
        registrationsAdapter = new RegistrationsAdapter(this, reventList);

        ticketlist = new ArrayList<>();
        ticketAdapter = new TicketAdapter(this, ticketlist);

// Set initial adapter to event list
        recyclerEvents.setAdapter(adapter);


        btnHome.setOnClickListener(v -> {
            categorySpinner.setVisibility(View.GONE);
            recyclerEvents.setAdapter(adapter); // Switch back to event adapter
            loadEventData(null); // Load all events
        });


        btnCategory.setOnClickListener(v -> {
            categorySpinner.setVisibility(View.VISIBLE);
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = categories[pos];
                loadEventData(selected); // Load only selected category
                sortSpinner.setVisibility(View.VISIBLE); // show sort options

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (pos) {
                    case 0: // Newest
                        Collections.sort(eventList, (e1, e2) -> e2.getEventDate().compareTo(e1.getEventDate()));
                        break;
                    case 1: // Oldest
                        Collections.sort(eventList, Comparator.comparing(Event::getEventDate));
                        break;
                    case 2: // Price low to high
                        Collections.sort(eventList, Comparator.comparingDouble(Event::getTicketPrice));
                        break;
                    case 3: // Price high to low
                        Collections.sort(eventList, (e1, e2) -> Double.compare(e2.getTicketPrice(), e1.getTicketPrice()));
                        break;
                    case 4: // Name A–Z
                        Collections.sort(eventList, Comparator.comparing(Event::getEventName));
                        break;
                    case 5: // Name Z–A
                        Collections.sort(eventList, (e1, e2) -> e2.getEventName().compareTo(e1.getEventName()));
                        break;
                }
                adapter.notifyDataSetChanged(); // update the RecyclerView
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        btnSearch.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });

        btnRegister.setOnClickListener(v -> {
            categorySpinner.setVisibility(View.GONE);
            sortSpinner.setVisibility(View.GONE);

            reventList.clear(); // already handled safely
            recyclerEvents.setAdapter(registrationsAdapter);
            loadUserRegistrations();

            Toast.makeText(this, "Loading your registered events...", Toast.LENGTH_SHORT).show();
        });

        btnTicketing.setOnClickListener(v -> {
            categorySpinner.setVisibility(View.GONE);
            sortSpinner.setVisibility(View.GONE);

            ticketlist.clear();
            recyclerEvents.setAdapter(ticketAdapter);
            loadUserTickets();
        });


        logoutBtn.setOnClickListener(v -> {
            SharedPrefManager.getInstance(getApplicationContext()).logout();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Load all events on app start
        loadEventData(null);
    }

    private void loadEventData(String categoryFilter) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                Constants.URL_Fetch_events, // Changed here
                null,
                response -> {
                    eventList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String category = obj.getString("event_category");
                            if (categoryFilter == null || category.equalsIgnoreCase(categoryFilter.trim())) {
                                Event event = new Event(
                                        obj.getInt("event_id"),
                                        obj.getInt("total_seats"),
                                        obj.getString("event_name"),
                                        obj.getString("event_description"),
                                        obj.getString("event_date"),
                                        obj.getString("event_time"),
                                        obj.getString("event_location"),
                                        category,
                                        obj.getDouble("ticket_price")
                                        // image URL
                                );
                                eventList.add(event);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }

    private void loadUserRegistrations() {
        int userId = SharedPrefManager.getInstance(this).getUser().getId();
        Log.d("USER_ID", String.valueOf(userId));

        String registrationUrl = Constants.URL_get_user_registrations + "?user_id=" + userId;
        Log.d("REG_URL", registrationUrl);

        StringRequest request = new StringRequest(Request.Method.POST, registrationUrl,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            JSONArray eventsArray = jsonResponse.getJSONArray("events");
                            reventList.clear();
                            for (int i = 0; i < ((org.json.JSONArray) eventsArray).length(); i++) {
                                JSONObject obj = eventsArray.getJSONObject(i);
                                Regevent event = new Regevent(
                                        obj.getInt("id"),
                                        obj.getString("name"),
                                        obj.getString("date"),
                                        obj.getString("location"),
                                        obj.getString("category"),
                                        obj.getDouble("price")  // match PHP key exactly
                                );
                                reventList.add(event);
                            }
                            registrationsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No registrations found.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", String.valueOf(userId)); // replace with your actual user ID
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);


    }
    private void loadUserTickets() {
        int userId = SharedPrefManager.getInstance(this).getUser().getId();
        Log.d("USER_ID", String.valueOf(userId));

        String ticketUrl = Constants.URL_get_user_tickets + "?user_id=" + userId;
        StringRequest request = new StringRequest(Request.Method.POST, ticketUrl,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray array = jsonResponse.getJSONArray("tickets");
                            ticketlist.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Ticket ticket = new Ticket(
                                        obj.getInt("id"),
                                        obj.getString("name"),
                                        obj.getString("date"),
                                        obj.getString("location"),
                                        obj.getString("category"),
                                        obj.getDouble("price")
                                );
                                ticketlist.add(ticket);
                            }
                            ticketAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No tickets found. You may not have registered.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to load Ticket.", Toast.LENGTH_SHORT).show();
                })   {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", String.valueOf(userId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
