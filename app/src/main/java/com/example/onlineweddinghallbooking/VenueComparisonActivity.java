package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import kotlin.text.UStringsKt;

public class VenueComparisonActivity extends AppCompatActivity {

    public static String venueOneUid;
    public static String venueTwoUid;

    private ProgressDialog progressDialog;
    private ImageView venueOneIV;
    private ImageView venueTwoIV;
    private TextView venueOneNameTV;
    private TextView venueTwoNameTV;
    private TextView venueOneAddressTV;
    private TextView venueTwoAddressTV;
    private TextView venueOneBasePriceTV;
    private TextView venueTwoBasePriceTV;
    private TextView venueOneSeatingCapacityTV;
    private TextView venueTwoSeatingCapacityTV;
    private TextView venueOneParkingCapacityTV;
    private TextView venueTwoParkingCapacityTV;
    private TextView venueOnePartitionTV;
    private TextView venueTwoPartitionTV;
    private TextView venueOneInternalCateringTV;
    private TextView venueTwoInternalCateringTV;
    private TextView venueOneVIPTablesTV;
    private TextView venueTwoVIPTablesTV;
    private TextView venueOneVIPTablePriceTV;
    private TextView venueTwoVIPTablePriceTV;
    private TextView venueOneSoundSystemPriceTV;
    private TextView venueTwoSoundSystemPriceTV;
    private ImageButton backButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_comparison);

        initializeFirebase();
        initializeProperties();

        setVenueOneDetails();
        setVenueTwoDetails();
        
        backButtonOnClickListener();
    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(VenueComparisonActivity.this, CustomerHomePageActivity.class));
            }
        });

    }

    private void setVenueTwoDetails() {

        firebaseDatabase.getReference("Venues").child(venueTwoUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Venue venue = snapshot.getValue(Venue.class);

                Picasso.get().load(venue.getThumbnailImage()).into(venueTwoIV);
                venueTwoNameTV.setText(venue.getName());
                venueTwoAddressTV.setText(venue.getAddress());
                venueTwoBasePriceTV.setText("Rs. " + venue.getBasePrice());
                venueTwoSeatingCapacityTV.setText(venue.getMaximumSeatingCapacity());
                venueTwoParkingCapacityTV.setText(venue.getMaximumParkingCapacity());
                venueTwoVIPTablesTV.setText(venue.getMaximumVIPTables());
                venueTwoVIPTablePriceTV.setText("Rs. " + venue.getPricePerVipTable());
                venueTwoSoundSystemPriceTV.setText("Rs. " + venue.getSoundSystemPrice());

                if (venue.getPartitionAvailable().equalsIgnoreCase("Yes"))
                {
                    venueTwoPartitionTV.setText("Available");
                }
                else
                {
                    venueTwoPartitionTV.setText("Not Available");
                }

                if (venue.getCateringAvailable().equalsIgnoreCase("Available"))
                {
                    venueTwoInternalCateringTV.setText("Available");
                }
                else
                {
                    venueTwoInternalCateringTV.setText("Not Available");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(VenueComparisonActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setVenueOneDetails() {

        progressDialog = FancyProgressDialog.createProgressDialog(this);

        firebaseDatabase.getReference("Venues").child(venueOneUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Venue venue = snapshot.getValue(Venue.class);

                Picasso.get().load(venue.getThumbnailImage()).into(venueOneIV);
                venueOneNameTV.setText(venue.getName());
                venueOneAddressTV.setText(venue.getAddress());
                venueOneBasePriceTV.setText("Rs. " + venue.getBasePrice());
                venueOneSeatingCapacityTV.setText(venue.getMaximumSeatingCapacity());
                venueOneParkingCapacityTV.setText(venue.getMaximumParkingCapacity());
                venueOneVIPTablesTV.setText(venue.getMaximumVIPTables());
                venueOneVIPTablePriceTV.setText("Rs. " + venue.getPricePerVipTable());
                venueOneSoundSystemPriceTV.setText("Rs. " + venue.getSoundSystemPrice());

                if (venue.getPartitionAvailable().equalsIgnoreCase("Yes"))
                {
                    venueOnePartitionTV.setText("Available");
                }
                else
                {
                    venueOnePartitionTV.setText("Not Available");
                }

                if (venue.getCateringAvailable().equalsIgnoreCase("Available"))
                {
                    venueOneInternalCateringTV.setText("Available");
                }
                else
                {
                    venueOneInternalCateringTV.setText("Not Available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(VenueComparisonActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void initializeProperties() {

        venueOneIV = findViewById(R.id.venueOneIV);
        venueTwoIV = findViewById(R.id.venueTwoIV);
        venueOneNameTV = findViewById(R.id.venueOneNameTV);
        venueTwoNameTV = findViewById(R.id.venueTwoNameTV);
        venueOneAddressTV = findViewById(R.id.venueOneAddressTV);
        venueTwoAddressTV = findViewById(R.id.venueTwoAddressTV);
        venueOneBasePriceTV = findViewById(R.id.venueOneBasePriceTV);
        venueTwoBasePriceTV = findViewById(R.id.venueTwoBasePriceTV);
        venueOneSeatingCapacityTV = findViewById(R.id.venueOneSeatingCapacityTV);
        venueTwoSeatingCapacityTV = findViewById(R.id.venueTwoSeatingCapacityTV);
        venueOneParkingCapacityTV = findViewById(R.id.venueOneParkingCapacityTV);
        venueTwoParkingCapacityTV = findViewById(R.id.venueTwoParkingCapacityTV);
        venueOnePartitionTV = findViewById(R.id.venueOnePartitionTV);
        venueTwoPartitionTV = findViewById(R.id.venueTwoPartitionTV);
        venueOneInternalCateringTV = findViewById(R.id.venueOneInternalCateringTV);
        venueTwoInternalCateringTV = findViewById(R.id.venueTwoInternalCateringTV);
        venueOneVIPTablesTV = findViewById(R.id.venueOneVIPTablesTV);
        venueTwoVIPTablesTV = findViewById(R.id.venueTwoVIPTablesTV);
        venueOneVIPTablePriceTV = findViewById(R.id.venueOneVIPTablePriceTV);
        venueTwoVIPTablePriceTV = findViewById(R.id.venueTwoVIPTablePriceTV);
        venueOneSoundSystemPriceTV = findViewById(R.id.venueOneSoundSystemPriceTV);
        venueTwoSoundSystemPriceTV = findViewById(R.id.venueTwoSoundSystemPriceTV);
        backButton = findViewById(R.id.backButton);
    }
}