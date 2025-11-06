package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Ticket> tickets;

    public TicketAdapter(Context context, ArrayList<Ticket> tickets) {
        this.context = context;
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public TicketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketAdapter.ViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);

        holder.btnViewTicket.setOnClickListener(v -> {
            Intent intent = new Intent(context, TicketDetailActivity.class);
            intent.putExtra("name", ticket.getName());
            intent.putExtra("date", ticket.getDate());
            intent.putExtra("location", ticket.getLocation());
            intent.putExtra("category", ticket.getCategory());
            intent.putExtra("price", ticket.getPrice());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button btnViewTicket;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnViewTicket = itemView.findViewById(R.id.btnViewTicket);
        }
    }
}
