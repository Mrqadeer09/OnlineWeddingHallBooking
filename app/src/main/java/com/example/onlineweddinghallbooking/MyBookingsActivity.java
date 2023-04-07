package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MyBookingsActivity extends AppCompatActivity implements MyBookingsAdapter.OnNoteListener{

    private RecyclerView recyclerView;
    private MyBookingsAdapter myBookingsAdapter;
    private ArrayList<Booking> bookingArrayList;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    private ProgressDialog progressDialog;
    private long bookingCount;
    private LinearLayout noBookingLL;
    private ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);


        initializeFirebase();
        initializeProperties();

        getMyBookings();

        backButtonOnClickListener();
    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyBookingsActivity.super.onBackPressed();
                finish();
            }
        });

    }

    private void getMyBookings() {

        String myUid = firebaseAuth.getCurrentUser().getUid();

        firebaseDatabase.getReference("Users").child(myUid).child("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getChildrenCount() > 0)
                {
                    bookingCount = snapshot.getChildrenCount();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Booking booking = dataSnapshot.getValue(Booking.class);

                        firebaseDatabase.getReference("Bookings").child(booking.getBookingUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Booking booking1 = snapshot.getValue(Booking.class);

                                if (booking1 != null)
                                {
                                    firebaseDatabase.getReference("Venues").child(booking1.getVenueUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            Venue venue = snapshot.getValue(Venue.class);

                                            booking1.setVenueName(venue.getName());
                                            booking1.setVenueImgURL(venue.getThumbnailImage());

                                            bookingArrayList.add(booking1);
                                            myBookingsAdapter.notifyDataSetChanged();

                                            bookingCount--;

                                            if (bookingCount == 0)
                                            {
                                                progressDialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                            Toast.makeText(MyBookingsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Toast.makeText(MyBookingsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    noBookingLL.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MyBookingsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeProperties() {

        recyclerView = findViewById(R.id.myBookingsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookingArrayList = new ArrayList<>();
        myBookingsAdapter = new MyBookingsAdapter(this, bookingArrayList, this);
        recyclerView.setAdapter(myBookingsAdapter);

        progressDialog = FancyProgressDialog.createProgressDialog(this);

        noBookingLL = findViewById(R.id.noBookingsLL);

        backButton = findViewById(R.id.backButton);
    }

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onNoteClick(int position) {

        Gson gson = new Gson();
        String bookingGson = gson.toJson(bookingArrayList.get(position));

        Intent i = new Intent(this, MyBookingDetailsActivity.class);
        i.putExtra("bookingGson", bookingGson);
        startActivity(i);
    }
}