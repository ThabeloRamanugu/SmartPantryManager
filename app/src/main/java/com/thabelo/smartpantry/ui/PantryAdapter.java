package com.thabelo.smartpantry.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thabelo.smartpantry.R;
import com.thabelo.smartpantry.data.PantryItem;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// My adapter. It takes the pantry items and shows each one as a card in the list.
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    // I use this to tell the fragment when a card is tapped
    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private List<PantryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    // 3 days in milliseconds, items closer than this count as "expiring soon"
    private static final long SOON = 3L * 24 * 60 * 60 * 1000;

    public PantryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    // I call this when the database gives me a new list
    public void setItems(List<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // The fragment needs this to know which item was swiped
    public PantryItem getItem(int position) {
        return items.get(position);
    }

    // I make one empty card from my item_pantry layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    // I fill a card with the data of one item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.name.setText(item.name);

        // show 200 instead of 200.0 when it is a whole number
        String qty = (item.quantity == Math.floor(item.quantity))
                ? String.valueOf((long) item.quantity)
                : String.valueOf(item.quantity);
        holder.amount.setText(qty + " " + item.unit);

        if (item.expiryDate == null) {
            holder.expiry.setText(R.string.no_expiry);
            holder.expiry.setTextColor(Color.GRAY);
        } else {
            String date = DateFormat.getDateInstance().format(new Date(item.expiryDate));
            holder.expiry.setText(holder.itemView.getContext()
                    .getString(R.string.expires_on, date));

            // red if it is expired or expires within 3 days, grey if not
            boolean soon = item.expiryDate - System.currentTimeMillis() < SOON;
            holder.expiry.setTextColor(soon ? Color.parseColor("#C62828") : Color.GRAY);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // This holds the views of one card so I don't look them up again and again
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, amount, expiry;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            amount = itemView.findViewById(R.id.text_amount);
            expiry = itemView.findViewById(R.id.text_expiry);
        }
    }
}