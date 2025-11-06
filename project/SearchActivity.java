package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private EditText searchBar;
    private RecyclerView searchResultsRecycler;
    private List<SearchEvent> eventList;
    private SearchEventAdapter eventAdapter;
    private RequestQueue requestQueue;
    private static final String SEARCH_URL = Constants.URL_search_events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageView ivBack = findViewById(R.id.ivBack);

        searchBar = findViewById(R.id.search_bar);
        searchResultsRecycler = findViewById(R.id.search_results_recycler);
        eventList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        eventAdapter = new SearchEventAdapter(this, eventList);
        searchResultsRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecycler.setAdapter(eventAdapter);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        // Listen for text changes
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    searchEvents(s.toString().trim());
                } else {
                    eventList.clear();
                    eventAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void searchEvents(String query) {
        StringRequest request = new StringRequest(Request.Method.POST, SEARCH_URL,
                response -> {
                    Log.d("SearchResponse", response);
                    eventList.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            SearchEvent event = new SearchEvent(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    obj.getString("description"),
                                    obj.getString("date"),
                                    obj.getString("location"),
                                    obj.getString("category"),
                                    obj.getDouble("price")
                            );
                            eventList.add(event);

                        }
                        eventAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("query", query);
                return params;
            }
        };


        requestQueue.add(request);
    }
}
