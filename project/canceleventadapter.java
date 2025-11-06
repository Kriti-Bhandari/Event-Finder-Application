package com.example.project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class canceleventadapter extends RecyclerView.Adapter<canceleventadapter.EventViewHolder> {

    private final Context context;
    private final ArrayList<Event> CancelEventList;

    public canceleventadapter(Context context, ArrayList<Event> CancelEventList) {
        this.context = context;
        this.CancelEventList = CancelEventList;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDescription, eventDate, eventLocation, eventCategory, ticketPrice;
        Button btnUpdate, btnCancel;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.textEventName);
            eventDescription = itemView.findViewById(R.id.textEventDescription);
            eventDate = itemView.findViewById(R.id.textEventDate);
            eventLocation = itemView.findViewById(R.id.textEventLocation);
            eventCategory = itemView.findViewById(R.id.textEventCategory);
            ticketPrice = itemView.findViewById(R.id.textTicketPrice);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cancelevent, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = CancelEventList.get(position);

        holder.eventName.setText("Name: " + event.getEventName());
        holder.eventDescription.setText("Description: " + event.getEventDescription());
        holder.eventDate.setText("Date: " + event.getEventDate());
        holder.eventLocation.setText("Location: " + event.getEventLocation());
        holder.eventCategory.setText("Category: " + event.getEventCategory());
        holder.ticketPrice.setText("Price: Rs." + event.getTicketPrice());

        holder.btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateEventActivity.class);
            intent.putExtra("event_id", event.getEventId());
            intent.putExtra("event_name", event.getEventName());
            intent.putExtra("event_description", event.getEventDescription());
            intent.putExtra("event_date", event.getEventDate());
            intent.putExtra("event_location", event.getEventLocation());
            intent.putExtra("event_category", event.getEventCategory());
            intent.putExtra("event_price", String.valueOf(event.getTicketPrice()));
            intent.putExtra("event_seats", String.valueOf(event.getTotalSeats()));
            context.startActivity(intent);

        });

        holder.btnCancel.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Cancel Event")
                    .setMessage("Are you sure you want to cancel this event?")
                    .setPositiveButton("Yes", (dialogInterface, which) -> {
                        deleteEvent(event.getEventId(), position);
                    })
                    .setNegativeButton("No", null)
                    .create();

            dialog.setOnShowListener(dialogInterface -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(android.R.color.black));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(android.R.color.black));
            });

            dialog.show();

        });
    }

    @Override
    public int getItemCount() {
        return CancelEventList.size();
    }

    private void deleteEvent(int eventId, int position) {
        String deleteUrl = Constants.URL_delete_event + "?event_id=" + eventId;
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, deleteUrl,
                response -> {
                    CancelEventList.remove(position);
                    notifyItemRemoved(position);
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }
}
