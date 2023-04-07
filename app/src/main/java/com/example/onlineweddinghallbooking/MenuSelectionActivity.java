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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

public class MenuSelectionActivity extends AppCompatActivity implements MenuAdapter2.OnNoteListener{

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Booking booking;

    private RecyclerView menuRV;
    private MenuAdapter2 menuAdapter;
    private ArrayList<Menu> menuItemsList;
    private ArrayList<Integer> menuItemsPriceList;

    private TextView totalPriceTV;
    private Button nextButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_selection);

        initializeProperties();
        initializeFirebase();

        getAllMenuItems();

        nextButtonOnClickListener();
        backButtonOnClickListener();
    }

    private void nextButtonOnClickListener() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Menu> finalMenuList = new ArrayList<>();
                int counter = 0;
                int totalCateringBill = 0;

                for (int i = 0; i < menuItemsList.size(); i++)
                {
                    if (menuItemsPriceList.get(i) != 0)
                    {
                        finalMenuList.add(menuItemsList.get(i));
                        totalCateringBill += menuItemsPriceList.get(i);
                        counter++;
                    }
                }

                if (counter == 0)
                {
                    Toast.makeText(MenuSelectionActivity.this, "Please Select At Least One Item", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    booking.setCateringPrice(String.valueOf(totalCateringBill));

                    Gson gson = new Gson();
                    String bookingJson = gson.toJson(booking);

                    Bundle args = new Bundle();
                    args.putSerializable("menuArrayList", (Serializable) finalMenuList);

                    Intent i = new Intent(MenuSelectionActivity.this, TotalCostBreakdownActivity.class);
                    i.putExtra("bookingJson", bookingJson);
                    i.putExtra("bundle", args);

                    startActivity(i);
                }
            }
        });

    }

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void getAllMenuItems() {

        firebaseDatabase.getReference("Venues").child(booking.getVenueUid()).child("Menu").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Menu menu = dataSnapshot.getValue(Menu.class);
                    menuItemsList.add(menu);
                    menuItemsPriceList.add(0);
                    menuAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MenuSelectionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MenuSelectionActivity.super.onBackPressed();
                finish();
            }
        });

    }

    private void initializeProperties() {

        totalPriceTV = findViewById(R.id.totalPriceTV);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        menuItemsList = new ArrayList<>();
        menuItemsPriceList = new ArrayList<>();

        menuRV = findViewById(R.id.menuRV);
        menuRV.setHasFixedSize(true);
        menuRV.setLayoutManager(new LinearLayoutManager(this));

        menuAdapter = new MenuAdapter2(this, menuItemsList, this);

        menuRV.setAdapter(menuAdapter);

        Gson gson = new Gson();
        booking = gson.fromJson(getIntent().getStringExtra("bookingJson"), Booking.class);
        totalPriceTV.setText("Rs. " + booking.getTotalBill());
    }

    @Override
    public void onNoteClick(int position) {

        String dishName = menuItemsList.get(position).getDishName();
        int dishPrice = Integer.parseInt(menuItemsList.get(position).getDishPrice());
        int noOfGuests = Integer.parseInt(booking.getNoOfGuests());
        int totalBill = Integer.parseInt(booking.getTotalBill());

        Toast.makeText(this, dishName, Toast.LENGTH_SHORT).show();

        if (menuItemsPriceList.get(position) == 0)
        {
            int totalCostAccordingToGuests = dishPrice * noOfGuests;

            menuItemsPriceList.remove(position);
            menuItemsPriceList.add(position, totalCostAccordingToGuests);

            booking.setTotalBill(String.valueOf(totalBill + totalCostAccordingToGuests));

            updateBookingTotalAmount();
            //Toast.makeText(this, String.valueOf(menuItemsPriceList.size()), Toast.LENGTH_SHORT).show();
        }
        else
        {
            int totalCostAccordingToGuests = dishPrice * noOfGuests;

            menuItemsPriceList.remove(position);
            menuItemsPriceList.add(position, 0);

            booking.setTotalBill(String.valueOf(totalBill - totalCostAccordingToGuests));

            updateBookingTotalAmount();
            //Toast.makeText(this, String.valueOf(menuItemsPriceList.size()), Toast.LENGTH_SHORT).show();
        }

    }

    private void updateBookingTotalAmount() {

        totalPriceTV.setText("Rs. " + booking.getTotalBill());

    }
}