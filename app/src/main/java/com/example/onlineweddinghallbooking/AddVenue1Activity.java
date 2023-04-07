package com.example.onlineweddinghallbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class AddVenue1Activity extends AppCompatActivity {


    private TextInputLayout venueNameTIL;
    private TextInputLayout basePriceTIL;
    private TextInputLayout venueTypeTIL;
    private AutoCompleteTextView venueTypeItems;
    private TextInputLayout venueAddressTIL;
    private TextInputLayout latitudeTIL;
    private TextInputLayout longitudeTIL;
    private TextInputLayout contactNoTIL;
    private ImageButton backButton;
    private Button nextButton;


    private String venueName;
    private String basePrice;
    private String venueType;
    private String venueAddress;
    private String latitude;
    private String longitude;
    private String contactNo;


    private boolean validation;


    private String[] venueTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venue1);

        initializeProperties();

        nextButtonOnClickListener();
        backButtonOnClickListener();

        venueNameTextChangeListener();
        basePriceTextChangeListener();
        venueTypeTextChangeListener();
        venueAddressTextChangeListener();
        latitudeTextChangeListener();
        longitudeTextChangeListener();
        contactNoTextChangeListener();

    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddVenue1Activity.super.onBackPressed();
                finish();
            }
        });

    }

    private void venueTypeTextChangeListener() {

        venueTypeItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                venueTypeTIL.setError(null);

            }
        });

    }

    private void contactNoTextChangeListener() {

        contactNoTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                contactNoTIL.setError(null);

            }
        });

    }

    private void longitudeTextChangeListener() {

        longitudeTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                longitudeTIL.setError(null);

            }
        });

    }

    private void latitudeTextChangeListener() {

        latitudeTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                latitudeTIL.setError(null);

            }
        });

    }

    private void venueAddressTextChangeListener() {

        venueAddressTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                venueAddressTIL.setError(null);

            }
        });

    }

    private void basePriceTextChangeListener() {

        basePriceTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                basePriceTIL.setError(null);

            }
        });

    }

    private void venueNameTextChangeListener() {

        venueNameTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                venueNameTIL.setError(null);

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
                    Venue venue = new Venue();
                    venue.setName(venueName);
                    venue.setBasePrice(basePrice);
                    venue.setDescription(venueType);
                    venue.setAddress(venueAddress);
                    venue.setLatitude(latitude);
                    venue.setLongitude(longitude);
                    venue.setContact(contactNo);

                    Gson gson = new Gson();
                    String venueJson = gson.toJson(venue);

                    Intent i = new Intent(AddVenue1Activity.this, AddVenue2Activity.class);
                    i.putExtra("venue", venueJson);
                    startActivity(i);

                }

            }
        });

    }

    private void setProperties() {

        venueName = venueNameTIL.getEditText().getText().toString();
        basePrice = basePriceTIL.getEditText().getText().toString();
        venueType = venueTypeItems.getText().toString();
        venueAddress = venueAddressTIL.getEditText().getText().toString();
        latitude = latitudeTIL.getEditText().getText().toString();
        longitude = longitudeTIL.getEditText().getText().toString();
        contactNo = contactNoTIL.getEditText().getText().toString();

    }

    private boolean validateProperties() {

        validation = true;

        if (TextUtils.isEmpty(venueName))
        {
            venueNameTIL.setError("Required");
            validation = false;
        }
        else if (venueName.matches(".*[0-9].*"))
        {
            venueNameTIL.setError("Name cannot Contain Numbers");
            validation = false;
        }
        else if (venueName.length() < 6)
        {
            venueNameTIL.setError("Name Should Be At Least Of 6 Characters");
            validation = false;
        }

        if (TextUtils.isEmpty(basePrice))
        {
            basePriceTIL.setError("Required");
            validation = false;
        }
        else if (Integer.valueOf(basePrice) <= 0)
        {
            basePriceTIL.setError("Price Should Be Greater Than 0");
            validation = false;
        }

        if (TextUtils.isEmpty(venueType))
        {
            venueTypeTIL.setError("Required");
            validation = false;
        }

        if (TextUtils.isEmpty(venueAddress))
        {
            venueAddressTIL.setError("Required");
            validation = false;
        }

        if (TextUtils.isEmpty(latitude))
        {
            latitudeTIL.setError("Required");
            validation = false;
        }

        if (TextUtils.isEmpty(longitude))
        {
            longitudeTIL.setError("Required");
            validation = false;
        }

        if (TextUtils.isEmpty(contactNo))
        {
            contactNoTIL.setError("Required");
            validation = false;
        }
        else if (!(contactNo.substring(0, 2).equals("03") || contactNo.substring(0, 2).equals("92") || contactNo.substring(0, 3).equals("021")))
        {
            contactNoTIL.setError("Invalid Phone No.");
            validation = false;
        }

        return validation;
    }

    private void initializeProperties() {

        venueNameTIL = findViewById(R.id.venueNameTIL);
        basePriceTIL = findViewById(R.id.venueBasePriceTIL);
        venueTypeTIL = findViewById(R.id.venueTypeDropDown);
        venueTypeItems = findViewById(R.id.venueTypeDropDownInput);
        venueAddressTIL = findViewById(R.id.venueAddressTIL);
        latitudeTIL = findViewById(R.id.venueLatitudeTIL);
        longitudeTIL = findViewById(R.id.venueLongitudeTIL);
        contactNoTIL = findViewById(R.id.venueContactTIL);
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);

        venueTypes = getResources().getStringArray(R.array.venue_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, venueTypes);
        venueTypeItems.setAdapter(adapter);

    }
}