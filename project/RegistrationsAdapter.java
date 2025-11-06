package com.example.project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationsAdapter extends RecyclerView.Adapter<RegistrationsAdapter.ViewHolder> {

    private Context context;
    private List<Regevent> reventList;

    public RegistrationsAdapter(Context context, List<Regevent> reventList) {
        this.context = context;
        this.reventList = reventList;
    }

    @NonNull
    @Override
    public RegistrationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_registrations, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrationsAdapter.ViewHolder holder, int position) {
        Regevent event = reventList.get(position);

        // Set values using getters
        holder.name.setText(event.getName());
        holder.date.setText("Date: " + event.getDate());
        holder.location.setText("Location: " + event.getLocation());
        holder.category.setText("Category: " + event.getCategory());
        holder.ticketPrice.setText("Price: Rs. " + event.getTicket_price());

        // Cancel button logic
        holder.btnCancel.setVisibility(View.VISIBLE);
        holder.btnCancel.setOnClickListener(v -> {
            int userId = SharedPrefManager.getInstance(context).getUser().getId();
            int eventId = event.getEventId();
            cancelRegistration(userId, eventId, position);
        });
    }

    @Override
    public int getItemCount() {
        return reventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, location, category, ticketPrice;
        Button btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textEventName);
            date = itemView.findViewById(R.id.textEventDate);
            location = itemView.findViewById(R.id.textEventLocation);
            category = itemView.findViewById(R.id.textEventCategory);
            ticketPrice = itemView.findViewById(R.id.textEventPrice);
            btnCancel = itemView.findViewById(R.id.btnCancel); // Make sure this exists in XML
        }
    }

    private void cancelRegistration(int userId, int eventId, int position) {
        String url = Constants.URL_cancel_registrations;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("CancelResponse", response); // Log for debugging
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(context, "Registration canceled", Toast.LENGTH_SHORT).show();
                            reventList.remove(position);
                            notifyItemRemoved(position);
                        } else {
                            Toast.makeText(context, "Failed to cancel", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Error cancelling registration", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("id", String.valueOf(eventId));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }
}
