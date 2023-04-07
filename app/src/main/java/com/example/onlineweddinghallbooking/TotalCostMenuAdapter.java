package com.example.onlineweddinghallbooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TotalCostMenuAdapter extends RecyclerView.Adapter<TotalCostMenuAdapter.TotalCostMenuViewHolder> {

    Context context;
    private ArrayList<Menu> arrayList;

    public TotalCostMenuAdapter(Context context, ArrayList<Menu> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public TotalCostMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.total_cost_breakdown_layout, parent, false);
        return new TotalCostMenuViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull TotalCostMenuViewHolder holder, int position) {

        Menu menu = arrayList.get(position);

        holder.dishNameTextView.setText(menu.getDishName());
        holder.dishPriceTextView.setText("Rs. " + menu.getDishPrice());
        Picasso.get().load(menu.getDishImgUrl()).into(holder.dishImage);
        holder.dishQty.setText(TotalCostBreakdownActivity.cateringQty + "x");

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class TotalCostMenuViewHolder extends RecyclerView.ViewHolder
    {

        ImageView dishImage;
        TextView dishNameTextView;
        TextView dishPriceTextView;
        TextView dishQty;

        public TotalCostMenuViewHolder(@NonNull View itemView) {
            super(itemView);

            dishImage = itemView.findViewById(R.id.dishIV);
            dishNameTextView = itemView.findViewById(R.id.dishNameTV);
            dishPriceTextView = itemView.findViewById(R.id.dishPriceTV);
            dishQty = itemView.findViewById(R.id.cateringQuantityTV);
        }
    }

}
