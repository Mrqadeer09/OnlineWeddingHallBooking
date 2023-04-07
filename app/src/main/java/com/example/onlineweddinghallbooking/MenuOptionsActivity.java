package com.example.onlineweddinghallbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MenuOptionsActivity extends AppCompatActivity {

    private int menuOptionRGID;
    private Booking booking;

    private TextView totalPriceTV;
    private TextView selectMenuTV;
    private TextView getRecommendationTV;
    private Button nextButton;
    private RadioGroup menuOptionRG;
    private RadioButton selectMenuRB;
    private RadioButton menuRecommendationRB;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_options);

        initializeProperties();

        nextButtonOnClickListener();
        backButtonOnClickListener();
        selectMenuTVOnClickListener();
        getRecommendationTVOnClickListener();
    }

    private void getRecommendationTVOnClickListener() {

        getRecommendationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menuRecommendationRB.setChecked(true);
            }
        });

    }

    private void selectMenuTVOnClickListener() {

        selectMenuTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectMenuRB.setChecked(true);
            }
        });

    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MenuOptionsActivity.super.onBackPressed();
                finish();
            }
        });

    }

    private void nextButtonOnClickListener() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menuOptionRGID = menuOptionRG.getCheckedRadioButtonId();
                View radioButton = menuOptionRG.findViewById(menuOptionRGID);
                int radioButtonIndex = menuOptionRG.indexOfChild(radioButton);

                try
                {
                    if (radioButtonIndex == -1)
                    {
                        Toast.makeText(MenuOptionsActivity.this, "Please Select From Available Options", Toast.LENGTH_SHORT).show();
                    }
                    else if (radioButtonIndex == 0)
                    {
                        Gson gson = new Gson();
                        String bookingJson = gson.toJson(booking);

                        Intent i = new Intent(MenuOptionsActivity.this, MenuSelectionActivity.class);
                        i.putExtra("bookingJson", bookingJson);
                        startActivity(i);
                    }
                    else if (radioButtonIndex == 1)
                    {
                        Gson gson = new Gson();
                        String bookingJson = gson.toJson(booking);

                        Intent i = new Intent(MenuOptionsActivity.this, MenuRecommendationActivity.class);
                        i.putExtra("bookingJson", bookingJson);
                        startActivity(i);
                    }
                }
                catch (Exception ex)
                {
                    Toast.makeText(MenuOptionsActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void initializeProperties() {

        totalPriceTV = findViewById(R.id.totalPriceTV);
        selectMenuTV = findViewById(R.id.selectMenuTV);
        getRecommendationTV = findViewById(R.id.getRecommendationTV);
        nextButton = findViewById(R.id.nextButton);
        menuOptionRG = findViewById(R.id.menuOptionsRadioGroup);
        selectMenuRB = findViewById(R.id.selectMenuRadioButton);
        menuRecommendationRB = findViewById(R.id.menuRecommendationRadioButton);
        backButton = findViewById(R.id.backButton);

        Gson gson = new Gson();
        booking = gson.fromJson(getIntent().getStringExtra("bookingJson"), Booking.class);
        totalPriceTV.setText("Rs. " + booking.getTotalBill());

    }
}