package com.example.project;

import android.content.Context;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AllEventRegistrationsAdapter extends RecyclerView.Adapter<AllEventRegistrationsAdapter.ViewHolder> {
    private Context context;
    private List<AllEventRegistrations> registrationList;

    public AllEventRegistrationsAdapter(Context context, List<AllEventRegistrations> registrationList) {
        this.context = context;
        this.registrationList = registrationList;
    }

    @NonNull
    @Override
    public AllEventRegistrationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_allregistrations, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllEventRegistrations reg = registrationList.get(position);
        holder.tvEventName.setText("Event: " + reg.getEventName());
        holder.tvUsername.setText("User: " + reg.getUsername());
        holder.tvEmail.setText("Email: " + reg.getEmail());
        holder.tvDate.setText("Date: " + reg.getRegistrationDate());

        holder.btnDelete.setOnClickListener(v -> {
            int registrationId = reg.getRegistrationId();
            deleteRegistration(registrationId, position);
        });
    }

    @Override
    public int getItemCount() {
        return registrationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvUsername, tvEmail, tvDate;
        Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
    private void deleteRegistration(int regId, int position) {
        String deleteUrl = Constants.URL_delete_registration + "?registration_id=" + regId;


        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, deleteUrl,
                response -> {
                    // Remove from list and notify adapter
                    registrationList.remove(position);
                    notifyItemRemoved(position);
                },
                error -> {
                    error.printStackTrace();
                }
        );

        queue.add(request);
    }

}