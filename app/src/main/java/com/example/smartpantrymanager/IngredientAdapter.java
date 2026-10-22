package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Connects the list of Ingredient objects to the RecyclerView.
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onItemClick(Ingredient ingredient);
        void onItemLongClick(Ingredient ingredient);
    }

    private List<Ingredient> items;
    private final OnItemActionListener listener;

    public IngredientAdapter(List<Ingredient> items, OnItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setItems(List<Ingredient> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient item = items.get(position);
        holder.tvName.setText(item.getName());

        String details = item.getQuantity() + " " + item.getUnit();
        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            details += "  |  Expires: " + item.getExpiryDate();
        }

        // Read the setting each time a row is drawn
        Context context = holder.itemView.getContext();
        SharedPreferences prefs = context.getSharedPreferences(
                SettingsActivity.PREFS, Context.MODE_PRIVATE);
        boolean alertsOn = prefs.getBoolean(SettingsActivity.KEY_ALERTS, true);

        if (alertsOn && isExpiringSoon(item.getExpiryDate())) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFE0E0")); // light red
            details += "  (expiring soon!)";
        } else {
            // Must reset, because RecyclerView reuses row views
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
        holder.tvDetails.setText(details);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item);
            return true;
        });
    }

    /** It is True if the date is within the next 3 days (or already past).
     * */
    private boolean isExpiringSoon(String expiry) {
        if (expiry == null || expiry.isEmpty()) return false;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            format.setLenient(false);
            Date date = format.parse(expiry);
            if (date == null) return false;
            long daysLeft = (date.getTime() - System.currentTimeMillis()) / (24L * 60 * 60 * 1000);
            return daysLeft <= 3;
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDetails = itemView.findViewById(R.id.tvDetails);
        }
    }
}