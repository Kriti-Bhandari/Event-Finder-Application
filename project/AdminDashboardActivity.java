package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdminDashboardActivity extends AppCompatActivity {

    ImageButton btnHome,btnCreateEvent,btnSearch,btnRegister, btnManageEvents, btnCategory,logoutBtn;

    Spinner categorySpinner;
    Spinner sortSpinner;
    RecyclerView recyclerEvents;
    ArrayList<Event> eventList;
    ArrayList<AllEventRegistrations> registrationList;
    EventAdapter adapter;
    AllEventRegistrationsAdapter registrationAdapter;

    canceleventadapter cancelEventAdapter;
    ArrayList<Event> cancelEventList;

    String[] categories = {
            "Arts & Culture", "Music & Entertainment", "Education & Career",
            "Sports & Fitness", "Health & Wellness", "Business & Networking",
            "Social & Volunteering", "Food & Drinks", "Tech & Innovation",
            "Parties & Nightlife", "Family & Kids"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        btnHome = findViewById(R.id.btnHome);
        categorySpinner = findViewById(R.id.categorySpinner);
        sortSpinner = findViewById(R.id.sortSpinner);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        btnManageEvents = findViewById(R.id.btnCancelUpdate);
        btnCategory = findViewById(R.id.btnCategory);
        btnSearch = findViewById(R.id.SearchBtn);
        btnRegister = findViewById(R.id.btnRegister);
        logoutBtn = findViewById(R.id.LogoutBtn);


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
        recyclerEvents.setAdapter(adapter);

        registrationList = new ArrayList<>();
        registrationAdapter = new AllEventRegistrationsAdapter(this, registrationList);

        cancelEventList = new ArrayList<>();
        cancelEventAdapter = new canceleventadapter(this, cancelEventList);


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
            startActivity(new Intent(AdminDashboardActivity.this, SearchActivity.class));
        });

        btnCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEventActivity.class);
            startActivity(intent);
        });

        btnManageEvents.setOnClickListener(v -> {
            categorySpinner.setVisibility(View.GONE);
            sortSpinner.setVisibility(View.GONE);
            recyclerEvents.setAdapter(cancelEventAdapter); // use cancel/update adapter
            loadCancelEventData(); // method that fills eventList and updates cancelEventAdapter
        });


        btnRegister.setOnClickListener(v -> {
            categorySpinner.setVisibility(View.GONE);
            sortSpinner.setVisibility(View.GONE);
            registrationList.clear(); // already handled safely
            recyclerEvents.setAdapter(registrationAdapter);
            loadEventRegistrations(); // show user registrations instead of events
        });

        logoutBtn.setOnClickListener(v -> {
            SharedPrefManager.getInstance(getApplicationContext()).logout();
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });


        loadEventData(null);
    }

    private void loadEventData(String categoryFilter) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                Constants.URL_Fetch_events,
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

    private void loadEventRegistrations() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                Constants.URL_Fetch_event_registrations, // CHANGED HERE
                null,
                response -> {
                    ArrayList<AllEventRegistrations> registrationList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            AllEventRegistrations reg = new AllEventRegistrations(
                                    obj.getInt("registration_id"),
                                    obj.getString("name"),
                                    obj.getString("username"),
                                    obj.getString("email"),
                                    obj.getString("registered_at") // check your API field name
                            );
                            registrationList.add(reg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    AllEventRegistrationsAdapter adapter = new AllEventRegistrationsAdapter(AdminDashboardActivity.this, registrationList);
                    recyclerEvents.setAdapter(adapter);
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }

    private void loadCancelEventData() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                Constants.URL_Fetch_events, // CHANGED HERE
                null,
                response -> {
                    cancelEventList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Event event = new Event(
                                    obj.getInt("event_id"),
                                    obj.getInt("total_seats"),
                                    obj.getString("event_name"),
                                    obj.getString("event_description"),
                                    obj.getString("event_date"),
                                    obj.getString("event_time"),
                                    obj.getString("event_location"),
                                    obj.getString("event_category"),
                                    obj.getDouble("ticket_price")
                            );
                            cancelEventList.add(event);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    cancelEventAdapter.notifyDataSetChanged();
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }


}
