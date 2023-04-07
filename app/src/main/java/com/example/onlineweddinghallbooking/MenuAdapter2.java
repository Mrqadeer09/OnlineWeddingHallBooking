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

public class MenuAdapter2 extends RecyclerView.Adapter<MenuAdapter2.Menu2ViewHolder>{

    private Context context;
    private ArrayList<Menu> arrayList;
    private OnNoteListener mOnNoteListener;

    public MenuAdapter2(Context context, ArrayList<Menu> arrayList, OnNoteListener onNoteListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public Menu2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.menu_recommendation_card_view, parent, false);
        return new Menu2ViewHolder(v, mOnNoteListener);

    }

    @Override
    public void onBindViewHolder(@NonNull Menu2ViewHolder holder, int position) {

        Menu menu = arrayList.get(position);

        Picasso.get().load(menu.getDishImgUrl()).into(holder.dishImage);
        holder.dishNameTextView.setText(menu.getDishName());
        holder.dishPriceTextView.setText("Rs. " + menu.getDishPrice());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class Menu2ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView dishImage;
        TextView dishNameTextView;
        TextView dishPriceTextView;

        OnNoteListener onNoteListener;

        public Menu2ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            dishNameTextView = itemView.findViewById(R.id.dishNameTextView);
            dishPriceTextView = itemView.findViewById(R.id.dishPriceTextView);
            dishImage = itemView.findViewById(R.id.dishImageView);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener{

        void onNoteClick(int position);

    }

}
