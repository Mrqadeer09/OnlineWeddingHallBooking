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

public class StageAdapter extends RecyclerView.Adapter<StageAdapter.StageViewHolder> {

    Context context;
    private ArrayList<Stage> arrayList;
    private OnNoteListener mOnNoteListener;

    public StageAdapter(Context context, ArrayList<Stage> arrayList, OnNoteListener onNoteListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public StageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.stage_card_view, parent, false);
        return new StageViewHolder(v, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StageViewHolder holder, int position) {

        Stage stage = arrayList.get(position);

        Picasso.get().load(stage.getStageImgUrl()).into(holder.stageImageView);
        holder.stageNameTextView.setText(stage.getStageName());
        holder.stagePriceTextView.setText("Rs. " + stage.getStagePrice());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class StageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView stageImageView;
        TextView stageNameTextView;
        TextView stagePriceTextView;

        OnNoteListener onNoteListener;

        public StageViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            stageImageView = itemView.findViewById(R.id.stageImageView);
            stageNameTextView = itemView.findViewById(R.id.stageNameTextView);
            stagePriceTextView = itemView.findViewById(R.id.stagePriceTextView);
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
