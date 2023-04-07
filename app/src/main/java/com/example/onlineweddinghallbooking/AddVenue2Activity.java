package com.example.onlineweddinghallbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class AddVenue2Activity extends AppCompatActivity {

    private TextInputLayout minimumSeatingCapacityTIL;
    private TextInputLayout maximumSeatingCapacityTIL;
    private TextInputLayout minimumParkingCapacityTIL;
    private TextInputLayout maximumParkingCapacityTIL;
    private TextInputLayout partitionAvailableTIL;
    private AutoCompleteTextView partitionAvailableItems;
    private TextInputLayout internalCateringAvailableTIL;
    private AutoCompleteTextView internalCateringAvailableItems;
    private ImageButton backButton;
    private Button nextButton;

    private String minimumSeatingCapacity;
    private String maximumSeatingCapacity;
    private String minimumParkingCapacity;
    private String maximumParkingCapacity;
    private String partitionAvailable;
    private String internalCateringAvailable;

    private boolean validation;

    private String[] partitionAvailableOptions;
    private String[] internalCateringAvailableOptions;

    private Venue venue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venue2);

        initializeProperties();

        nextButtonOnClickListener();
        backButtonOnClickListener();

        minimumSeatingCapacityTextChangeListener();
        maximumSeatingCapacityTextChangeListener();
        minimumParkingCapacityTextChangeListener();
        maximumParkingCapacityTextChangeListener();
        partitionAvailableTextChangeListener();
        internalCateringAvailableTextChangeListener();


    }

    private void internalCateringAvailableTextChangeListener() {

        internalCateringAvailableItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                internalCateringAvailableTIL.setError(null);

            }
        });

    }

    private void partitionAvailableTextChangeListener() {

        partitionAvailableItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                partitionAvailableTIL.setError(null);

            }
        });

    }

    private void maximumParkingCapacityTextChangeListener() {

        maximumParkingCapacityTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                maximumParkingCapacityTIL.setError(null);

            }
        });

    }

    private void minimumParkingCapacityTextChangeListener() {

        minimumParkingCapacityTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                minimumParkingCapacityTIL.setError(null);

            }
        });

    }

    private void maximumSeatingCapacityTextChangeListener() {

        maximumSeatingCapacityTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                maximumSeatingCapacityTIL.setError(null);

            }
        });

    }

    private void minimumSeatingCapacityTextChangeListener() {

        minimumSeatingCapacityTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                minimumSeatingCapacityTIL.setError(null);

            }
        });

    }


    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddVenue2Activity.super.onBackPressed();
                finish();
            }
        });

    }

    private void nextButtonOnClickListener() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setProperties();
                boolean checkValidation  = validateProperties();

                if (checkValidation)
                {
                    venue.setMinimumSeatingCapacity(minimumSeatingCapacity);
                    venue.setMaximumSeatingCapacity(maximumSeatingCapacity);
                    venue.setMinimumParkingCapacity(minimumParkingCapacity);
                    venue.setMaximumParkingCapacity(maximumParkingCapacity);
                    venue.setPartitionAvailable(partitionAvailable);
                    venue.setCateringAvailable(internalCateringAvailable);
                    venue.setAvgRating("0");

                    Gson gson = new Gson();
                    String venueJson = gson.toJson(venue);

                    Intent i = new Intent(AddVenue2Activity.this, AddVenue3Activity.class);
                    i.putExtra("venue", venueJson);
                    startActivity(i);
                }

            }
        });

    }

    private boolean validateProperties() {

        validation = true;

        if (TextUtils.isEmpty(minimumSeatingCapacity))
        {
            minimumSeatingCapacityTIL.setError("Required");
            validation = false;
        }
        else if (Integer.valueOf(minimumSeatingCapacity) <= 0)
        {
            minimumSeatingCapacityTIL.setError("Value Should Be Greater Than 0");
            validation = false;
        }

        if (TextUtils.isEmpty(maximumSeatingCapacity))
        {
            maximumSeatingCapacityTIL.setError("Required");
            validation = false;
        }
        else if (Integer.valueOf(maximumSeatingCapacity) <= 0)
        {
            maximumSeatingCapacityTIL.setError("Value Should Be Greater Than 0");
            validation = false;
        }
        else if (Integer.valueOf(maximumSeatingCapacity) <= Integer.valueOf(minimumSeatingCapacity))
        {
            maximumSeatingCapacityTIL.setError("Max Capacity Should Be Greater Than Min Capacity");
            validation = false;
        }

        if (TextUtils.isEmpty(minimumParkingCapacity))
        {
            minimumParkingCapacityTIL.setError("Required");
            validation = false;
        }
        else if (Integer.valueOf(minimumParkingCapacity) <= 0)
        {
            minimumParkingCapacityTIL.setError("Value Should Be Greater Than 0");
            validation = false;
        }

        if (TextUtils.isEmpty(maximumParkingCapacity))
        {
            maximumParkingCapacityTIL.setError("Required");
            validation = false;
        }
        else if (Integer.valueOf(maximumParkingCapacity) <= 0)
        {
            maximumParkingCapacityTIL.setError("Value Should Be Greater Than 0");
            validation = false;
        }
        else if (Integer.valueOf(maximumParkingCapacity) <= Integer.valueOf(minimumParkingCapacity))
        {
            maximumParkingCapacityTIL.setError("Max Capacity Should Be Greater Than Min Capacity");
            validation = false;
        }

        if (TextUtils.isEmpty(partitionAvailable))
        {
            partitionAvailableTIL.setError("Required");
            validation = false;
        }

        if (TextUtils.isEmpty(internalCateringAvailable))
        {
            internalCateringAvailableTIL.setError("Required");
            validation = false;
        }

        return validation;

    }

    private void setProperties() {

        minimumSeatingCapacity = minimumSeatingCapacityTIL.getEditText().getText().toString();
        maximumSeatingCapacity = maximumSeatingCapacityTIL.getEditText().getText().toString();
        minimumParkingCapacity = minimumParkingCapacityTIL.getEditText().getText().toString();
        maximumParkingCapacity = maximumParkingCapacityTIL.getEditText().getText().toString();
        partitionAvailable = partitionAvailableItems.getText().toString();
        internalCateringAvailable = internalCateringAvailableItems.getText().toString();

    }

    private void initializeProperties() {

        minimumSeatingCapacityTIL = findViewById(R.id.minimumSeatingCapacityTIL);
        maximumSeatingCapacityTIL = findViewById(R.id.maximumSeatingCapacityTIL);
        minimumParkingCapacityTIL = findViewById(R.id.minimumParkingCapacityTIL);
        maximumParkingCapacityTIL = findViewById(R.id.maximumParkingCapacityTIL);
        partitionAvailableTIL = findViewById(R.id.partitionAvailableDropDown);
        partitionAvailableItems = findViewById(R.id.partitionAvailableDropDownInput);
        internalCateringAvailableTIL = findViewById(R.id.internalCateringAvailableDropDown);
        internalCateringAvailableItems = findViewById(R.id.internalCateringAvailableDropDownInput);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        partitionAvailableOptions = getResources().getStringArray(R.array.partition_available_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, partitionAvailableOptions);
        partitionAvailableItems.setAdapter(adapter);

        internalCateringAvailableOptions = getResources().getStringArray(R.array.internal_catering_options);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.dropdown_item, internalCateringAvailableOptions);
        internalCateringAvailableItems.setAdapter(adapter2);

        Gson gson = new Gson();
        venue = gson.fromJson(getIntent().getStringExtra("venue"), Venue.class);
    }
}