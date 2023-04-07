package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyBookingDetailsActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private ImageButton cancelPopupButton;
    private TextInputLayout reviewTIL;
    private RatingBar ratingBar;
    private Button saveRRButton;

    private RecyclerView recyclerView;
    private TotalCostMenuAdapter totalCostMenuAdapter;
    private ArrayList<Menu> menuArrayList;

    private TextView venueNameTV;
    private TextView venuePriceTV;
    private TextView venuePriceTV2;
    private ImageView venueImageIV;
    private TextView stageNameTV;
    private TextView stagePriceTV;
    private TextView stagePriceTV2;
    private ImageView stageImageIV;
    private TextView cateringPriceTV;
    private TextView totalBookingCost;
    private TextView taxPrice;
    private TextView vipTablesQty;
    private TextView vipTablesPrice;
    private TextView vipTablesPrice2;
    private TextView seatsQty;
    private TextView seatsPrice;
    private TextView seatsPrice2;
    private LinearLayout soundSystemLL;
    private TextView soundSystemPriceTV;
    private TextView soundSystemPriceTV2;
    private Button rateAndReviewButton;

    private Booking booking;
    private Stage stage;
    private ArrayList<String> menuReferences;
    private ProgressDialog progressDialog;
    private ImageButton backButton;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking_details);

        initializeFirebase();
        initializeProperties();

        setVenueDetails();
        getStageDetails();
        getMenuDetails();
        setPricing();

        rateAndReviewButtonOnClickListener();

        backButtonOnClickListener();
    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyBookingDetailsActivity.super.onBackPressed();
                finish();
            }
        });

    }

    private void rateAndReviewButtonOnClickListener() {

        firebaseDatabase.getReference("Bookings").child(booking.getBookingUid()).child("Rating And Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long reviewAmount = snapshot.getChildrenCount();

                if (reviewAmount <= 0)
                {
                    rateAndReviewButton.setOnClickListener(new View.OnClickListener() {

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(View v) {

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
                            try
                            {
                                Date bookingDate = sdf.parse(booking.getDateTime());
                                Date currentDate = sdf.parse(sdf.format(new Date()));

                                if (currentDate.after(bookingDate))
                                {
                                    dialogBuilder = new AlertDialog.Builder(MyBookingDetailsActivity.this);
                                    final  View ratingAndReviewPopup = getLayoutInflater().inflate(R.layout.rating_and_review_popup, null);

                                    cancelPopupButton = ratingAndReviewPopup.findViewById(R.id.cancelPopupButton);
                                    ratingBar = ratingAndReviewPopup.findViewById(R.id.ratingBar);
                                    reviewTIL = ratingAndReviewPopup.findViewById(R.id.reviewTextInputLayout);
                                    saveRRButton = ratingAndReviewPopup.findViewById(R.id.saveRRButton);

                                    dialogBuilder.setView(ratingAndReviewPopup);
                                    dialog = dialogBuilder.create();
                                    dialog.show();

                                    cancelPopupButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            dialog.dismiss();

                                        }
                                    });

                                    saveRRButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            int rating = (int) ratingBar.getRating();
                                            String review = reviewTIL.getEditText().getText().toString();
                                            boolean validate = true;

                                            if (rating <= 0)
                                            {
                                                Toast.makeText(MyBookingDetailsActivity.this, "Please Rate", Toast.LENGTH_SHORT).show();
                                                validate = false;
                                            }

                                            if (TextUtils.isEmpty(review))
                                            {
                                                Toast.makeText(MyBookingDetailsActivity.this, "Please Write A Review", Toast.LENGTH_SHORT).show();
                                                validate = false;
                                            }

                                            if (validate)
                                            {
                                                progressDialog.show();

                                                String myUid = firebaseAuth.getCurrentUser().getUid();

                                                firebaseDatabase.getReference("Users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        User user = snapshot.getValue(User.class);

                                                        firebaseDatabase.getReference("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                String key = firebaseDatabase.getReference("Bookings")
                                                                        .child(booking.getBookingUid()).child("Rating And Reviews").push().getKey();

                                                                Map<String, Object> map = new HashMap<>();
                                                                map.put("customerName", user.getName());
                                                                map.put("customerUid", myUid);
                                                                map.put("dateAndTime", sdf.format(currentDate));
                                                                map.put("rating", rating);
                                                                map.put("review", review);
                                                                map.put("uid", key);

                                                                firebaseDatabase.getReference("Bookings").child(booking.getBookingUid())
                                                                        .child("Rating And Reviews").child(key).updateChildren(map);


                                                                firebaseDatabase.getReference("Venues").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                        String key = firebaseDatabase.getReference("Venues")
                                                                                .child(booking.getVenueUid()).child("Rating And Reviews").push().getKey();

                                                                        Map<String, Object> map = new HashMap<>();
                                                                        map.put("customerName", user.getName());
                                                                        map.put("customerUid", myUid);
                                                                        map.put("dateAndTime", sdf.format(currentDate));
                                                                        map.put("rating", rating);
                                                                        map.put("review", review);
                                                                        map.put("uid", key);

                                                                        firebaseDatabase.getReference("Venues").child(booking.getVenueUid())
                                                                                .child("Rating And Reviews").child(key).updateChildren(map);

                                                                        Venue venue = snapshot.child(booking.getVenueUid()).getValue(Venue.class);

                                                                        int totalRatings = Integer.parseInt(venue.getAvgRating());
                                                                        totalRatings += rating;

                                                                        Map<String, Object> map2 = new HashMap<>();
                                                                        map2.put("avgRating", String.valueOf(totalRatings));

                                                                        firebaseDatabase.getReference("Venues").child(booking.getVenueUid()).updateChildren(map2);

                                                                        dialog.dismiss();
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(MyBookingDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();

                                                                        Gson gson = new Gson();
                                                                        String bookingJson = gson.toJson(booking);

                                                                        Intent i = new Intent(MyBookingDetailsActivity.this, MyBookingDetailsActivity.class);
                                                                        i.putExtra("bookingGson", bookingJson);
                                                                        startActivity(i);

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                        Toast.makeText(MyBookingDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                Toast.makeText(MyBookingDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        });

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                        Toast.makeText(MyBookingDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(MyBookingDetailsActivity.this, "You Cannot Rate And View Before " + sdf.format(bookingDate), Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (ParseException e)
                            {
                                e.printStackTrace();
                            }

                        }
                    });
                }
                else
                {
                    rateAndReviewButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(MyBookingDetailsActivity.this, "Already Rated", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MyBookingDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void setPricing() {

        venuePriceTV2.setText("Rs. " + booking.getVenuePrice());
        stagePriceTV2.setText("Rs. " + booking.getStagePrice());
        cateringPriceTV.setText("Rs. " + booking.getCateringPrice());
        taxPrice.setText("Rs. " + booking.getTax());
        totalBookingCost.setText("Rs. " + booking.getTotalBill());

        vipTablesQty.setText(booking.getNoOfVipTablesRequired() + "x");
        final int singleVIPTablePrice = Integer.parseInt(booking.getVipTablesPrice())/Integer.parseInt(booking.getNoOfVipTablesRequired());
        vipTablesPrice.setText("Rs. " + String.valueOf(singleVIPTablePrice));
        vipTablesPrice2.setText("Rs. " + booking.getVipTablesPrice());

        seatsQty.setText(booking.getNoOfGuests() + "x");
        final int singleSeatPrice = Integer.parseInt(booking.getSeatingPrice())/Integer.parseInt(booking.getNoOfGuests());
        seatsPrice.setText("Rs. " + String.valueOf(singleSeatPrice));
        seatsPrice2.setText("Rs. " + booking.getSeatingPrice());

        if (booking.getSoundSystemRequired().equalsIgnoreCase("Yes"))
        {
            soundSystemLL.setVisibility(View.VISIBLE);
            soundSystemPriceTV.setText("Rs. " + booking.getSoundSystemPrice());
        }

        soundSystemPriceTV2.setText("Rs. " + booking.getSoundSystemPrice());

    }

    private void getMenuDetails() {

        if (booking.getInternalCateringRequired() != null || !booking.getInternalCateringRequired().equalsIgnoreCase("Yes"))
        {
            firebaseDatabase.getReference("Bookings").child(booking.getBookingUid()).child("Menu").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.getChildrenCount() > 0)
                    {
                        long count = snapshot.getChildrenCount();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            String dishUid = dataSnapshot.child("dishUid").getValue(String.class);
                            menuReferences.add(dishUid);
                            count--;
                        }

                        if (count == 0)
                        {
                            getMenuFromReferences(menuReferences);
                        }

                    }
                    else
                    {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(MyBookingDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void getMenuFromReferences(ArrayList<String> menuReferences) {

        for (int i = 0; i < menuReferences.size(); i++)
        {
            int finalI = i;

            firebaseDatabase.getReference("Venues").child(booking.getVenueUid()).child("Menu").child(menuReferences.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Menu menu = snapshot.getValue(Menu.class);

                    menuArrayList.add(menu);

                    totalCostMenuAdapter.notifyDataSetChanged();


                    if (finalI == menuReferences.size() - 1)
                    {
                        progressDialog.dismiss();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(MyBookingDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }


    private void getStageDetails() {

        firebaseDatabase.getReference("Venues").child(booking.getVenueUid()).child("Stages").child(booking.getStageUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                stage = snapshot.getValue(Stage.class);

                setStageDetails(stage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MyBookingDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setStageDetails(Stage stage) {

        stageNameTV.setText(stage.getStageName());
        stagePriceTV.setText("Rs. " + stage.getStagePrice());
        Picasso.get().load(stage.getStageImgUrl()).into(stageImageIV);

    }

    private void setVenueDetails() {

        venueNameTV.setText(booking.getVenueName());
        venuePriceTV.setText("Rs. " + booking.getVenuePrice());
        Picasso.get().load(booking.getVenueImgURL()).into(venueImageIV);

    }

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void initializeProperties() {

        Gson gson = new Gson();
        booking = gson.fromJson(getIntent().getStringExtra("bookingGson"), Booking.class);

        venueNameTV = findViewById(R.id.venueNameTV);
        venuePriceTV = findViewById(R.id.venuePriceTV);
        venuePriceTV2 = findViewById(R.id.venuePriceTV2);
        venueImageIV = findViewById(R.id.venueIV);

        stageNameTV = findViewById(R.id.stageNameTV);
        stagePriceTV = findViewById(R.id.stagePriceTV);
        stagePriceTV2 = findViewById(R.id.stagePriceTV2);
        stageImageIV = findViewById(R.id.stageIV);

        vipTablesQty = findViewById(R.id.vipTablesQty);
        vipTablesPrice = findViewById(R.id.vipTablesPriceTV);
        vipTablesPrice2 = findViewById(R.id.vipTablesPriceTV2);

        seatsQty = findViewById(R.id.seatsQty);
        seatsPrice = findViewById(R.id.seatsPriceTV);
        seatsPrice2 = findViewById(R.id.seatsPriceTV2);

        cateringPriceTV = findViewById(R.id.cateringPriceTV);
        taxPrice = findViewById(R.id.taxTV);
        totalBookingCost = findViewById(R.id.totalBookingBudgetTV);

        soundSystemLL = findViewById(R.id.soundSystemLL);
        soundSystemPriceTV = findViewById(R.id.soundSystemPriceTV);
        soundSystemPriceTV2 = findViewById(R.id.soundSystemPriceTV2);

        TotalCostBreakdownActivity.cateringQty = booking.getNoOfGuests();

        menuReferences = new ArrayList<>();

        rateAndReviewButton = findViewById(R.id.rateAndReviewButton);

        progressDialog = FancyProgressDialog.createProgressDialog(this);

        recyclerView = findViewById(R.id.menuList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuArrayList = new ArrayList<>();
        totalCostMenuAdapter = new TotalCostMenuAdapter(this, menuArrayList);
        recyclerView.setAdapter(totalCostMenuAdapter);

        backButton = findViewById(R.id.backButton);
    }
}