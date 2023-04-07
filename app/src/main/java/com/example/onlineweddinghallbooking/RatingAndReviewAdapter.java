package com.example.onlineweddinghallbooking;

import android.content.Context;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RatingAndReviewAdapter extends RecyclerView.Adapter<RatingAndReviewAdapter.RatingAndReviewViewHolder> {

    Context context;
    ArrayList<RatingAndReview> arrayList;

    public RatingAndReviewAdapter(Context context, ArrayList<RatingAndReview> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RatingAndReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.rating_and_review_layout, parent, false);
        return new RatingAndReviewViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull RatingAndReviewViewHolder holder, int position) {


        RatingAndReview ratingAndReview = arrayList.get(position);

        holder.customerNameTV.setText(ratingAndReview.getCustomerName());
        holder.ratingBar.setRating(ratingAndReview.getRating());
        holder.reviewTV.setText(ratingAndReview.getReview());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RatingAndReviewViewHolder extends RecyclerView.ViewHolder{

        TextView customerNameTV;
        RatingBar ratingBar;
        TextView reviewTV;

        public RatingAndReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            customerNameTV = itemView.findViewById(R.id.customerNameTV);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            reviewTV = itemView.findViewById(R.id.reviewTV);
        }
    }

}
