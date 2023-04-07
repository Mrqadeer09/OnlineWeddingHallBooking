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

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {


    private Context context;
    private ArrayList<Menu> arrayList;

    public MenuAdapter(Context context, ArrayList<Menu> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.menu_recommendation_card_view, parent, false);
        return new MenuViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {

        Menu menu = arrayList.get(position);

        Picasso.get().load(menu.getDishImgUrl()).into(holder.dishImage);
        holder.dishNameTextView.setText(menu.getDishName());
        holder.dishPriceTextView.setText("Rs. " + menu.getDishPrice());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {

        ImageView dishImage;
        TextView dishNameTextView;
        TextView dishPriceTextView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            dishNameTextView = itemView.findViewById(R.id.dishNameTextView);
            dishPriceTextView = itemView.findViewById(R.id.dishPriceTextView);
            dishImage = itemView.findViewById(R.id.dishImageView);
        }
    }

}
