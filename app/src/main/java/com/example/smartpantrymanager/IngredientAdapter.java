package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter that connects the list of Ingredient objects to the RecyclerView.
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    // Lets the Activity decide what happens when a row is tapped or long-pressed
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

    // Replace the data and redraw the list (used after add/edit/delete)
    public void setItems(List<Ingredient> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // Called when the RecyclerView needs a new row view: inflate our XML row
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    // Called to fill a row with the data at the given position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient item = items.get(position);
        holder.tvName.setText(item.getName());

        String details = item.getQuantity() + " " + item.getUnit();
        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            details += "  |  Expires: " + item.getExpiryDate();
        }
        holder.tvDetails.setText(details);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Holds references to the views in one row so findViewById runs only once per row
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDetails = itemView.findViewById(R.id.tvDetails);
        }
    }
}