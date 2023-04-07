package com.example.onlineweddinghallbooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    Context context;
    ArrayList<AllMessageModel> arrayList;
    private OnNoteListener mOnNoteListener;

    public ChatAdapter(Context context, ArrayList<AllMessageModel> arrayList, OnNoteListener onNoteListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false);
        return new ChatViewHolder(v, mOnNoteListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        AllMessageModel allMessageModel = arrayList.get(position);

        holder.nameTV.setText(allMessageModel.getName());
        holder.dateAndTimeTV.setText(allMessageModel.getDate());
        holder.msgTV.setText(allMessageModel.getLastMessage());
    }

    @Override
    public int getItemCount() {

        return arrayList.size();

    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView nameTV;
        TextView dateAndTimeTV;
        TextView msgTV;
        OnNoteListener onNoteListener;

        public ChatViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.receiverNameTV);
            dateAndTimeTV = itemView.findViewById(R.id.msgDateTV);
            msgTV = itemView.findViewById(R.id.latestMsgTV);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener
    {
        void onNoteClick(int position);
    }

}
