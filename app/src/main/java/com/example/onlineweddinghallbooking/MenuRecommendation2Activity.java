package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
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

import java.io.Serializable;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Collections;

public class MenuRecommendation2Activity extends AppCompatActivity {

    private Booking booking;
    private float totalCateringBudget;
    private float drinkBudget;
    private int usedDrinkBudget;
    private float starterBudget;
    private int usedStarterBudget;
    private float mainCourseBudget;
    private int usedMainCourseBudget;
    private float dessertBudget;
    private int usedDessertBudget;
    private int totalCateringPrice;
    private int counter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private RecyclerView drinksRV;
    private RecyclerView startersRV;
    private RecyclerView mainCourseRV;
    private RecyclerView dessertRV;
    private MenuAdapter menuAdapter1;
    private MenuAdapter menuAdapter2;
    private MenuAdapter menuAdapter3;
    private MenuAdapter menuAdapter4;
    private ArrayList<Menu> menusArrayList;
    private ArrayList<Menu> drinksArrayList;
    private ArrayList<Menu> startersArrayList;
    private ArrayList<Menu> mainCourseArrayList;
    private ArrayList<Menu> dessertArrayList;

    private TextView totalPriceTV;
    private TextView drinksTV;
    private TextView startersTV;
    private TextView mainCourseTV;
    private TextView dessertTV;
    private Button nextButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_recommendation2);

        initializeProperties();
        initializeFirebase();

        getMenuFromDatabase();

        nextButtonOnClickListener();
        backButtonOnClickListener();
    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MenuRecommendation2Activity.super.onBackPressed();
                finish();
            }
        });

    }

    private void nextButtonOnClickListener() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < drinksArrayList.size(); i++)
                {
                    menusArrayList.add(drinksArrayList.get(i));
                }

                for (int i = 0; i < startersArrayList.size(); i++)
                {
                    menusArrayList.add(startersArrayList.get(i));
                }

                for (int i = 0; i < mainCourseArrayList.size(); i++)
                {
                    menusArrayList.add(mainCourseArrayList.get(i));
                }

                for (int i = 0; i < dessertArrayList.size(); i++)
                {
                    menusArrayList.add(dessertArrayList.get(i));
                }

                Gson gson = new Gson();
                String bookingJson = gson.toJson(booking);

                Bundle args = new Bundle();
                args.putSerializable("menuArrayList", (Serializable) menusArrayList);

                Intent i = new Intent(MenuRecommendation2Activity.this, TotalCostBreakdownActivity.class);
                i.putExtra("bookingJson", bookingJson);
                i.putExtra("bundle", args);

                startActivity(i);

            }
        });

    }

    private void getMenuFromDatabase() {

        final String venueUid = booking.getVenueUid();

        firebaseDatabase.getReference("Venues").child(venueUid).child("Menu").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Menu menu = dataSnapshot.getValue(Menu.class);
                    menu.setDishUid(dataSnapshot.getKey());

                    if (menu.getDishType().equalsIgnoreCase("Drinks"))
                    {
                        drinksArrayList.add(menu);
                        Collections.sort(drinksArrayList, Menu.menuDishPrice);
                    }
                    else if (menu.getDishType().equalsIgnoreCase("Starters"))
                    {
                        startersArrayList.add(menu);
                        Collections.sort(startersArrayList, Menu.menuDishPrice);
                    }
                    else if (menu.getDishType().equalsIgnoreCase("Main Course"))
                    {
                        mainCourseArrayList.add(menu);
                        Collections.sort(mainCourseArrayList, Menu.menuDishPrice);
                    }
                    else
                    {
                        dessertArrayList.add(menu);
                        Collections.sort(dessertArrayList, Menu.menuDishPrice);
                    }

                }

                filterMenuAccordingToBudget();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MenuRecommendation2Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void filterMenuAccordingToBudget() {

        final float cateringBudgetPerPerson = totalCateringBudget/Integer.parseInt(booking.getNoOfGuests());
        final float drinkBudgetPercentage = 10;
        final float startersBudgetPercentage = 15;
        final float mainCourseBudgetPercentage = 60;
        final float dessertBudgetPercentage = 15;

        drinkBudget = (cateringBudgetPerPerson * (drinkBudgetPercentage/100));
        starterBudget = (cateringBudgetPerPerson * (startersBudgetPercentage/100));
        mainCourseBudget = (cateringBudgetPerPerson * (mainCourseBudgetPercentage/100));
        dessertBudget = (cateringBudgetPerPerson * (dessertBudgetPercentage/100));

        counter = 0;
        usedDrinkBudget = 0;
        usedStarterBudget = 0;
        usedMainCourseBudget = 0;
        usedDessertBudget = 0;

        for (int i = 0; i < drinksArrayList.size(); i++)
        {
            if (Integer.parseInt(drinksArrayList.get(i).getDishPrice()) <= drinkBudget)
            {
                drinksTV.setVisibility(View.VISIBLE);
                counter = i+1;
                usedDrinkBudget = Integer.parseInt(drinksArrayList.get(i).getDishPrice());
                drinkBudget -= usedDrinkBudget;
            }
            else
            {
                break;
            }
        }

        starterBudget += drinkBudget;

        drinksArrayList.subList(counter, drinksArrayList.size()).clear();

        counter = 0;

        for (int i = 0; i < startersArrayList.size(); i++)
        {
            if (Integer.parseInt(startersArrayList.get(i).getDishPrice()) <= starterBudget)
            {
                startersTV.setVisibility(View.VISIBLE);
                counter = i+1;
                usedStarterBudget = Integer.parseInt(startersArrayList.get(i).getDishPrice());
                starterBudget -= usedStarterBudget;
            }
            else
            {
                break;
            }
        }

        dessertBudget += starterBudget;

        startersArrayList.subList(counter, startersArrayList.size()).clear();

        counter = 0;

        for (int i = 0; i < dessertArrayList.size(); i++)
        {
            if (Integer.parseInt(dessertArrayList.get(i).getDishPrice()) <= dessertBudget)
            {
                dessertTV.setVisibility(View.VISIBLE);
                counter = i+1;
                usedDessertBudget = Integer.parseInt(dessertArrayList.get(i).getDishPrice());
                dessertBudget -= usedDessertBudget;
            }
            else
            {
                break;
            }
        }

        mainCourseBudget += dessertBudget;


        dessertArrayList.subList(counter, dessertArrayList.size()).clear();

        counter = 0;

        for (int i = 0; i < mainCourseArrayList.size(); i++)
        {
            if (Integer.parseInt(mainCourseArrayList.get(i).getDishPrice()) <= mainCourseBudget)
            {
                mainCourseTV.setVisibility(View.VISIBLE);
                counter = i+1;
                usedMainCourseBudget = Integer.parseInt(mainCourseArrayList.get(i).getDishPrice());
                mainCourseBudget -= usedMainCourseBudget;
            }
            else
            {
                break;
            }
        }

        mainCourseArrayList.subList(counter, mainCourseArrayList.size()).clear();

        menuAdapter1.notifyDataSetChanged();
        menuAdapter2.notifyDataSetChanged();
        menuAdapter3.notifyDataSetChanged();
        menuAdapter4.notifyDataSetChanged();

        recalculateTotalBill();
    }

    private void recalculateTotalBill() {

        totalCateringPrice = 0;

        for (int i = 0; i < drinksArrayList.size(); i++)
        {
            totalCateringPrice += Integer.parseInt(drinksArrayList.get(i).getDishPrice()) * Integer.parseInt(booking.getNoOfGuests());
        }

        for (int i = 0; i < startersArrayList.size(); i++)
        {
            totalCateringPrice += Integer.parseInt(startersArrayList.get(i).getDishPrice()) * Integer.parseInt(booking.getNoOfGuests());
        }

        for (int i = 0; i < mainCourseArrayList.size(); i++)
        {
            totalCateringPrice += Integer.parseInt(mainCourseArrayList.get(i).getDishPrice()) * Integer.parseInt(booking.getNoOfGuests());
        }

        for (int i = 0; i < dessertArrayList.size(); i++)
        {
            totalCateringPrice += Integer.parseInt(dessertArrayList.get(i).getDishPrice()) * Integer.parseInt(booking.getNoOfGuests());
        }

        booking.setTotalBill(String.valueOf(Integer.parseInt(booking.getTotalBill()) + totalCateringPrice));
        booking.setCateringPrice(String.valueOf(totalCateringPrice));
        totalPriceTV.setText("Rs. " + booking.getTotalBill());

    }


    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void initializeProperties() {

        totalPriceTV = findViewById(R.id.totalPriceTV);
        drinksTV = findViewById(R.id.drinksTV);
        startersTV = findViewById(R.id.startersTV);
        mainCourseTV = findViewById(R.id.mainCourseTV);
        dessertTV = findViewById(R.id.dessertTV);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        Gson gson = new Gson();
        booking = gson.fromJson(getIntent().getStringExtra("bookingJson"), Booking.class);
        totalCateringBudget = Integer.parseInt(getIntent().getStringExtra("totalCateringBudget"));

        drinksRV = findViewById(R.id.drinksRV);
        drinksRV.setHasFixedSize(true);
        drinksRV.setLayoutManager(new LinearLayoutManager(this));

        startersRV = findViewById(R.id.startersRV);
        startersRV.setHasFixedSize(true);
        startersRV.setLayoutManager(new LinearLayoutManager(this));

        mainCourseRV = findViewById(R.id.mainCourseRV);
        mainCourseRV.setHasFixedSize(true);
        mainCourseRV.setLayoutManager(new LinearLayoutManager(this));

        dessertRV = findViewById(R.id.dessertRV);
        dessertRV.setHasFixedSize(true);
        dessertRV.setLayoutManager(new LinearLayoutManager(this));

        menusArrayList = new ArrayList<>();
        drinksArrayList = new ArrayList<>();
        startersArrayList = new ArrayList<>();
        mainCourseArrayList = new ArrayList<>();
        dessertArrayList = new ArrayList<>();


        menuAdapter1 = new MenuAdapter(this, drinksArrayList);
        menuAdapter2 = new MenuAdapter(this, startersArrayList);
        menuAdapter3 = new MenuAdapter(this, mainCourseArrayList);
        menuAdapter4 = new MenuAdapter(this, dessertArrayList);

        drinksRV.setAdapter(menuAdapter1);
        startersRV.setAdapter(menuAdapter2);
        mainCourseRV.setAdapter(menuAdapter3);
        dessertRV.setAdapter(menuAdapter4);

    }
}