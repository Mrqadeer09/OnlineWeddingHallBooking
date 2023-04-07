package com.example.onlineweddinghallbooking;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.errorprone.annotations.Var;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class TotalCostBreakdownActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TotalCostMenuAdapter totalCostMenuAdapter;

    private Booking booking;
    private Venue venue;
    private Stage stage;
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
    private TextView cateringQuantityTV;
    private Button nextButton;
    private ImageButton backButton;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public static String cateringQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_cost_breakdown);

        initializeProperties();
        initializeFirebase();

        getVenueDetails();
        getStageDetails();
        getMenuDetails();
        setPricing();
        
        nextButtonOnClickListener();
        backButtonOnClickListener();
    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TotalCostBreakdownActivity.super.onBackPressed();
                finish();
            }
        });

    }

    private void nextButtonOnClickListener() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Gson gson = new Gson();
                String bookingJson = gson.toJson(booking);

                Bundle args = new Bundle();
                args.putSerializable("menuArrayList", (Serializable) menuArrayList);

                Intent i = new Intent(TotalCostBreakdownActivity.this, PaymentMethodSelectionActivity.class);
                i.putExtra("bookingJson", bookingJson);
                i.putExtra("bundle", args);

                startActivity(i);

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

        if (menuArrayList != null)
        {

            totalCostMenuAdapter = new TotalCostMenuAdapter(this, menuArrayList);
            recyclerView.setAdapter(totalCostMenuAdapter);
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

                Toast.makeText(TotalCostBreakdownActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setStageDetails(Stage stage) {

        stageNameTV.setText(stage.getStageName());
        stagePriceTV.setText("Rs. " + stage.getStagePrice());
        Picasso.get().load(stage.getStageImgUrl()).into(stageImageIV);

    }

    private void getVenueDetails() {

        firebaseDatabase.getReference("Venues").child(booking.getVenueUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                venue = snapshot.getValue(Venue.class);

                setVenueDetails(venue);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(TotalCostBreakdownActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setVenueDetails(Venue venue) {

        venueNameTV.setText(venue.getName());
        venuePriceTV.setText("Rs. " + venue.getBasePrice());
        Picasso.get().load(venue.getThumbnailImage()).into(venueImageIV);

    }

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void initializeProperties() {

        Gson gson = new Gson();
        booking = gson.fromJson(getIntent().getStringExtra("bookingJson"), Booking.class);
        booking.setTax("0");

        Bundle args = new Bundle();
        args = getIntent().getBundleExtra("bundle");

        if (args != null)
        {
            menuArrayList = (ArrayList<Menu>) args.getSerializable("menuArrayList");
        }

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

        cateringQty = booking.getNoOfGuests();

        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        recyclerView = findViewById(R.id.menuList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}