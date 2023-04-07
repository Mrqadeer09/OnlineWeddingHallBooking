package com.example.onlineweddinghallbooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class VenuesAdapter extends RecyclerView.Adapter<VenuesAdapter.VenueViewHolder> {

    Context context;
    private ArrayList<Venue> arrayList;
    private OnNoteListener mOnNoteListener;



    public VenuesAdapter(Context context, ArrayList<Venue> arrayList, OnNoteListener onNoteListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.venue_card, parent, false);
        return new VenueViewHolder(v, mOnNoteListener);

    }

    @Override
    public void onBindViewHolder(@NonNull VenueViewHolder holder, int position) {

        Venue venue = arrayList.get(position);
        holder.venueNameTextView.setText(venue.getName());
        holder.venuePriceTextView.setText("Rs. " + venue.getBasePrice());
        holder.addressTextView.setText(venue.getAddress());
        holder.venueRatingTextView.setText(venue.getAvgRating());
        holder.maximumSeatingCapacityTextView.setText(venue.getMaximumSeatingCapacity());
        holder.maximumParkingCapacityTextView.setText(venue.maximumParkingCapacity);
        Picasso.get().load(venue.getThumbnailImage()).into(holder.venueImage);


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class VenueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ImageView venueImage;
        TextView venueNameTextView;
        TextView venuePriceTextView;
        TextView addressTextView;
        TextView venueRatingTextView;
        TextView maximumSeatingCapacityTextView;
        TextView maximumParkingCapacityTextView;

        OnNoteListener onNoteListener;

        public VenueViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            venueImage = itemView.findViewById(R.id.venueImageView);
            venueNameTextView = itemView.findViewById(R.id.venueNameTextView);
            venuePriceTextView = itemView.findViewById(R.id.venuePriceTextView);
            addressTextView = itemView.findViewById(R.id.venueAddressTextView);
            venueRatingTextView = itemView.findViewById(R.id.ratingTextView);
            maximumSeatingCapacityTextView = itemView.findViewById(R.id.seatingCapacityTextView);
            maximumParkingCapacityTextView = itemView.findViewById(R.id.parkingCapacityTextView);
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
