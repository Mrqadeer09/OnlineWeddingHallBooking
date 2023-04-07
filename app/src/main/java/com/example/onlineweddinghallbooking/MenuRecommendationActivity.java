package com.example.onlineweddinghallbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class MenuRecommendationActivity extends AppCompatActivity {

    private Booking booking;

    private TextInputLayout totalCateringBudgetTIL;
    private TextView totalPriceTV;
    private Button nextButton;
    private ImageButton backButton;

    private String totalCateringBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_recommendation);

        initializeProperties();

        nextButtonOnClickListener();

        totalCateringBudgetTextChangeListener();

        backButtonOnClickListener();
    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MenuRecommendationActivity.super.onBackPressed();
                finish();
            }
        });

    }

    private void totalCateringBudgetTextChangeListener() {

        totalCateringBudgetTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                totalCateringBudgetTIL.setError(null);
            }
        });

    }

    private void nextButtonOnClickListener() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                totalCateringBudget = totalCateringBudgetTIL.getEditText().getText().toString();

                if (TextUtils.isEmpty(totalCateringBudget))
                {
                    totalCateringBudgetTIL.setError("Required");
                }
                else if (Integer.parseInt(totalCateringBudget) <= 0)
                {
                    totalCateringBudgetTIL.setError("Value Should Be Greater Than 0");
                }
                else
                {
                    Gson gson = new Gson();
                    String bookingJson = gson.toJson(booking);

                    Intent i = new Intent(MenuRecommendationActivity.this, MenuRecommendation2Activity.class);
                    i.putExtra("bookingJson", bookingJson);
                    i.putExtra("totalCateringBudget", totalCateringBudget);
                    startActivity(i);
                }

            }
        });

    }

    private void initializeProperties() {

        totalCateringBudgetTIL = findViewById(R.id.totalCateringBudgetTIL);
        totalPriceTV = findViewById(R.id.totalPriceTV);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        Gson gson = new Gson();
        booking = gson.fromJson(getIntent().getStringExtra("bookingJson"), Booking.class);
        totalPriceTV.setText("Rs. " + booking.getTotalBill());
    }
}