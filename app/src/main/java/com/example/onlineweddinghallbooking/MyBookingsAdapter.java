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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyBookingsAdapter extends RecyclerView.Adapter<MyBookingsAdapter.MyBookingsViewHolder> {

    private Context context;
    private ArrayList<Booking> arrayList;
    private OnNoteListener mOnNoteListener;

    public MyBookingsAdapter(Context context, ArrayList<Booking> arrayList, OnNoteListener onNoteListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public MyBookingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.my_bookings_view, parent, false);
        return new MyBookingsViewHolder(v, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyBookingsViewHolder holder, int position) {

        Booking booking = arrayList.get(position);

        holder.venueNameTV.setText(booking.getVenueName());
        holder.bookingStatusTV.setText("Status: " + booking.getStatus());
        holder.bookingAmountTV.setText("Rs. " + booking.getTotalBill());
        holder.bookingDateTV.setText(booking.getDateTime());
        Picasso.get().load(booking.getVenueImgURL()).into(holder.venueImageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyBookingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView venueNameTV;
        TextView bookingStatusTV;
        TextView bookingAmountTV;
        TextView bookingDateTV;
        ImageView venueImageView;

        OnNoteListener onNoteListener;

        public MyBookingsViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            venueNameTV = itemView.findViewById(R.id.venueNameTV);
            bookingStatusTV = itemView.findViewById(R.id.bookingStatusTV);
            bookingAmountTV = itemView.findViewById(R.id.bookingAmountTV);
            bookingDateTV = itemView.findViewById(R.id.bookingDateTv);
            venueImageView = itemView.findViewById(R.id.venueIV);
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
