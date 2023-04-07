package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class StageSelectionActivity extends AppCompatActivity implements StageAdapter.OnNoteListener{

    private RecyclerView recyclerView;
    private StageAdapter stageAdapter;
    private ArrayList<Stage> stageArrayList;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView totalPriceTV;
    private Button nextButton;
    private ImageButton backButton;

    private String venueUid;
    private Booking booking;
    private String stageUid;
    private String stageName;
    private int stagePrice;
    private int totalDecorationPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_selection);

        initializeProperties();
        initializeFirebase();

        getAllStages();

        nextButtonOnClickListener();
        backButtonOnClickListener();

    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StageSelectionActivity.super.onBackPressed();
                finish();
            }
        });

    }

    private void nextButtonOnClickListener() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stagePrice == 0)
                {
                    Toast.makeText(StageSelectionActivity.this, "Select A Stage", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    booking.setStageUid(stageUid);
                    booking.setStageName(stageName);
                    booking.setStagePrice(String.valueOf(stagePrice));
                    booking.setTotalBill(String.valueOf(totalDecorationPrice));

                    if (booking.getInternalCateringRequired() != null && booking.getInternalCateringRequired().equalsIgnoreCase("Yes"))
                    {
                        Gson gson = new Gson();
                        String bookingJson = gson.toJson(booking);

                        Intent i = new Intent(StageSelectionActivity.this, MenuOptionsActivity.class);
                        i.putExtra("bookingJson", bookingJson);
                        startActivity(i);
                    }
                    else
                    {
                        Gson gson = new Gson();
                        String bookingJson = gson.toJson(booking);

                        Intent i = new Intent(StageSelectionActivity.this, TotalCostBreakdownActivity.class);
                        i.putExtra("bookingJson", bookingJson);
                        startActivity(i);
                    }
                }

            }
        });

    }

    private void getAllStages() {

        firebaseDatabase.getReference("Venues").child(venueUid).child("Stages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Stage stage = dataSnapshot.getValue(Stage.class);
                    stage.setUid(dataSnapshot.getKey());
                    stageArrayList.add(stage);
                    stageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(StageSelectionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

    }

    private void initializeProperties() {

        Gson gson = new Gson();

        venueUid = getIntent().getStringExtra("venueUid");
        booking = gson.fromJson(getIntent().getStringExtra("bookingJson"), Booking.class);
        totalDecorationPrice = Integer.parseInt(booking.getTotalBill());
        stagePrice = 0;

        totalPriceTV = findViewById(R.id.totalPriceTV);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        totalPriceTV.setText("Rs. " + booking.getTotalBill());

        recyclerView = findViewById(R.id.stagesRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        stageArrayList = new ArrayList<>();
        stageAdapter = new StageAdapter(this, stageArrayList, this);
        recyclerView.setAdapter(stageAdapter);

    }

    @Override
    public void onNoteClick(int position) {

        totalDecorationPrice -= stagePrice;

        stageUid = stageArrayList.get(position).getUid();
        stageName = stageArrayList.get(position).getStageName();
        stagePrice = Integer.parseInt(stageArrayList.get(position).getStagePrice());

        Toast.makeText(this, stageName, Toast.LENGTH_SHORT).show();

        totalDecorationPrice += stagePrice;
        totalPriceTV.setText("Rs. " + String.valueOf(totalDecorationPrice));

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}