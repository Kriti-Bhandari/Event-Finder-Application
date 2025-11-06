package com.example.project;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class SearchEventAdapter extends RecyclerView.Adapter<SearchEventAdapter.SearchEventViewHolder> {

    private Context context;
    private List<SearchEvent> eventList;

    public SearchEventAdapter(Context context, List<SearchEvent> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public SearchEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_event, parent, false);
        return new SearchEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchEventViewHolder holder, int position) {
        SearchEvent event = eventList.get(position);
        holder.name.setText(event.getName());
        holder.description.setText(event.getDescription());
        holder.date.setText("Date: " + event.getDate());
        holder.location.setText("Location: " + event.getLocation());
        holder.category.setText("Category: " + event.getCategory());
        holder.ticketPrice.setText("Rs. " + event.getTicketPrice());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class SearchEventViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, date, location, category, ticketPrice;

        public SearchEventViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.event_name);
            description = itemView.findViewById(R.id.event_description);
            date = itemView.findViewById(R.id.event_date);
            location = itemView.findViewById(R.id.event_location);
            category = itemView.findViewById(R.id.event_category);
            ticketPrice = itemView.findViewById(R.id.event_ticket_price);
        }
    }
}
