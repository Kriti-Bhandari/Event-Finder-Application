package com.example.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final Context context;
    private final List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName,eventTime, eventDescription, eventDate, getEventTime, eventLocation, eventCategory, ticketPrice;
        Button btnRegister;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.textEventName);
            eventDescription = itemView.findViewById(R.id.textEventDescription);
            eventDate = itemView.findViewById(R.id.textEventDate);
            eventTime = itemView.findViewById(R.id.textEventTime);
            eventLocation = itemView.findViewById(R.id.textEventLocation);
            eventCategory = itemView.findViewById(R.id.textEventCategory);
            ticketPrice = itemView.findViewById(R.id.textTicketPrice);
            btnRegister = itemView.findViewById(R.id.btnRegister);

        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.eventName.setText("Name:"+ event.getEventName());
        holder.eventDescription.setText("Description"+ event.getEventDescription());
        holder.eventDate.setText("Date: " + event.getEventDate());
        holder.eventTime.setText("Time: " + event.getEventTime());
        holder.eventLocation.setText("Location: " + event.getEventLocation());
        holder.eventCategory.setText("Category: " + event.getEventCategory());
        holder.ticketPrice.setText("Price: Rs." + event.getTicketPrice());

        holder.btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventRegistrationActivity.class);
            intent.putExtra("event_id", String.valueOf(event.getEventId()));
            intent.putExtra("event_name", event.getEventName());
            intent.putExtra("event_date", event.getEventDate());
            intent.putExtra("event_time", event.getEventTime());
            intent.putExtra("event_category", event.getEventCategory());
            intent.putExtra("event_location", event.getEventLocation());
            intent.putExtra("event_price", String.valueOf(event.getTicketPrice()));
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
