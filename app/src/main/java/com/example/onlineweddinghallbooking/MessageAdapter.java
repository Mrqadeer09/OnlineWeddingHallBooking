package com.example.onlineweddinghallbooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    Context context;
    ArrayList<MessageModel> arrayList;

    public MessageAdapter(Context context, ArrayList<MessageModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.chat_message_layout, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        MessageModel messageModel = arrayList.get(position);

        if (messageModel.getSenderUid().equals(ChatActivity.userUid))
        {
             holder.senderMessageTv.setText(messageModel.getMsg());
             holder.receiverLayout.setVisibility(View.GONE);
        }
        else
        {
            holder.receiverMessageTv.setText(messageModel.getMsg());
            holder.senderLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {

        return arrayList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView senderMessageTv;
        TextView receiverMessageTv;
        LinearLayout senderLayout;
        LinearLayout receiverLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageTv = itemView.findViewById(R.id.senderMessageTV);
            receiverMessageTv = itemView.findViewById(R.id.receiverMessageTV);
            senderLayout = itemView.findViewById(R.id.senderLayout);
            receiverLayout = itemView.findViewById(R.id.receiverLayout);
        }
    }

}
