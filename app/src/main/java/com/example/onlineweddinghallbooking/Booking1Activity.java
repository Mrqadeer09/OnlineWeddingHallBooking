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
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class Booking1Activity extends AppCompatActivity {

    private TextInputLayout timeSlotTIL;
    private AutoCompleteTextView timeSlotACTV;
    private TextInputLayout partitionRequiredTIL;
    private AutoCompleteTextView partitionRequiredACTV;
    private TextInputLayout soundSystemRequiredTIL;
    private AutoCompleteTextView soundSystemRequiredACTV;
    private TextInputLayout internalCateringRequiredATIL;
    private AutoCompleteTextView internalCateringRequiredACTV;
    private TextInputLayout noOfGuestsTIL;
    private TextInputLayout noOfVipTablesRequiredTIL;
    private TextView totalPriceTV;
    private Button nextButton;
    private ImageButton backButton;


    private String timeSlot;
    private String partitionRequired;
    private String soundSystemRequired;
    private String internalCateringRequired;
    private String noOfGuests;
    private String noOfVipTablesRequired;
    private String totalPrice;
    private boolean validate;
    private int totalDecorationPrice;
    private int soundSystemPrice;
    private int seatingPrice;
    private int vipTablesPrice;
    private String dateOfBooking;

    private String[] timeSlots;
    private String[] availableOptions;


    private Venue venue;
    private String venueUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking1);

        initializeProperties();
        finalizeViews();

        timeSlotTextChangeListener();
        partitionRequiredTextChangeListener();
        soundSystemRequiredTextChangeListener();
        internalCateringRequiredTextChangeListener();
        noOfGuestsTextChangeListener();
        noOfVipTablesRequiredTextChangeListener();

        nextButtonOnClickListener();
        backButtonOnClickListener();
    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Booking1Activity.super.onBackPressed();
                finish();
            }
        });

    }

    private void noOfVipTablesRequiredTextChangeListener() {

        noOfVipTablesRequiredTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                noOfVipTablesRequired = noOfVipTablesRequiredTIL.getEditText().getText().toString();

                totalDecorationPrice -= vipTablesPrice;
                vipTablesPrice = 0;
                totalPriceTV.setText("Rs. " + totalDecorationPrice);

                if (!TextUtils.isEmpty(noOfVipTablesRequired) && !(Integer.parseInt(noOfVipTablesRequired) < 1)
                        && !(Integer.parseInt(noOfVipTablesRequired) > Integer.parseInt(venue.getMaximumVIPTables())))
                {
                    vipTablesPrice = Integer.parseInt(noOfVipTablesRequired) * Integer.parseInt(venue.getPricePerVipTable());
                    totalDecorationPrice += vipTablesPrice;
                    totalPriceTV.setText("Rs. " + totalDecorationPrice);
                    noOfVipTablesRequiredTIL.setError(null);
                }
                else
                {
                    noOfVipTablesRequiredTIL.setError("1-" + venue.getMaximumVIPTables() + " VIP Tables Allowed");
                }

            }
        });

    }

    private void noOfGuestsTextChangeListener() {

        noOfGuestsTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                noOfGuests = noOfGuestsTIL.getEditText().getText().toString();

                totalDecorationPrice -= seatingPrice;
                seatingPrice = 0;
                totalPriceTV.setText("Rs. " + totalDecorationPrice);

                if (!TextUtils.isEmpty(noOfGuests) && !(Integer.parseInt(noOfGuests) < Integer.parseInt(venue.getMinimumSeatingCapacity()))
                        && !(Integer.parseInt(noOfGuests) > Integer.parseInt(venue.getMaximumSeatingCapacity())))
                {
                    seatingPrice = Integer.parseInt(noOfGuests) * Integer.parseInt(venue.getPricePerChair());
                    totalDecorationPrice += seatingPrice;
                    totalPriceTV.setText("Rs. " + totalDecorationPrice);
                    noOfGuestsTIL.setError(null);
                }
                else
                {
                    noOfGuestsTIL.setError(venue.getMinimumSeatingCapacity() + "-" + venue.getMaximumSeatingCapacity() + " Guests Allowed");
                }



            }
        });

    }

    private void internalCateringRequiredTextChangeListener() {

        internalCateringRequiredACTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                internalCateringRequiredATIL.setError(null);

            }
        });

    }

    private void soundSystemRequiredTextChangeListener() {

        soundSystemRequiredACTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                soundSystemRequired = soundSystemRequiredACTV.getText().toString();

                soundSystemRequiredTIL.setError(null);

                if (soundSystemRequired.equalsIgnoreCase("Yes"))
                {
                    if (soundSystemPrice == 0)
                    {
                        totalDecorationPrice += Integer.parseInt(venue.getSoundSystemPrice());
                        totalPriceTV.setText("Rs. " + totalDecorationPrice);
                        soundSystemPrice = Integer.parseInt(venue.getSoundSystemPrice());
                    }
                }
                else
                {
                    if (soundSystemPrice != 0)
                    {
                        totalDecorationPrice -= Integer.parseInt(venue.getSoundSystemPrice());
                        totalPriceTV.setText("Rs. " + totalDecorationPrice);
                        soundSystemPrice = 0;
                    }
                }
            }
        });

    }

    private void partitionRequiredTextChangeListener() {

        partitionRequiredACTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                partitionRequiredTIL.setError(null);

            }
        });

    }

    private void timeSlotTextChangeListener() {

        timeSlotACTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                timeSlotTIL.setError(null);

            }
        });

    }

    private void nextButtonOnClickListener() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPropertyValues();
                boolean isValid = validateProperties();

                if (isValid)
                {
                    Booking booking = new Booking();
                    booking.setTimeSlot(timeSlot);
                    booking.setPartitionRequired(partitionRequired);
                    booking.setSoundSystemRequired(soundSystemRequired);
                    booking.setSoundSystemPrice(String.valueOf(soundSystemPrice));
                    booking.setInternalCateringRequired(internalCateringRequired);
                    booking.setNoOfGuests(noOfGuests);
                    booking.setNoOfVipTablesRequired(noOfVipTablesRequired);
                    booking.setVipTablesPrice(String.valueOf(vipTablesPrice));
                    booking.setSeatingPrice(String.valueOf(seatingPrice));
                    booking.setTotalBill(String.valueOf(totalDecorationPrice));
                    booking.setVenueUid(venueUid);
                    booking.setVenuePrice(venue.getBasePrice());
                    booking.setDateTime(dateOfBooking);

                    if (!booking.getInternalCateringRequired().equalsIgnoreCase("Yes") || booking.getInternalCateringRequired() == null)
                    {
                        booking.setCateringPrice("0");
                    }

                    if (!booking.getSoundSystemRequired().equalsIgnoreCase("Yes"))
                    {
                        booking.setSoundSystemPrice("0");
                    }

                    Gson gson = new Gson();
                    String bookingJson = gson.toJson(booking);

                    Intent i = new Intent(Booking1Activity.this, StageSelectionActivity.class);
                    i.putExtra("venueUid", venueUid);
                    i.putExtra("bookingJson", bookingJson);
                    startActivity(i);
                }
            }
        });
    }

    private void getPropertyValues() {

        timeSlot = timeSlotACTV.getText().toString();
        partitionRequired = partitionRequiredACTV.getText().toString();
        soundSystemRequired = soundSystemRequiredACTV.getText().toString();
        internalCateringRequired = internalCateringRequiredACTV.getText().toString();
        noOfGuests = noOfGuestsTIL.getEditText().getText().toString();
        noOfVipTablesRequired = noOfVipTablesRequiredTIL.getEditText().getText().toString();

    }

    private boolean validateProperties() {

        validate = true;

        if (TextUtils.isEmpty(timeSlot))
        {
            timeSlotTIL.setError("Required");
            validate = false;
        }

        if (venue.getPartitionAvailable().equalsIgnoreCase("Yes") && TextUtils.isEmpty(partitionRequired))
        {
            partitionRequiredTIL.setError("Required");
            validate = false;
        }

        if (TextUtils.isEmpty(soundSystemRequired))
        {
            soundSystemRequiredTIL.setError("Required");
            validate = false;
        }

        if (venue.getCateringAvailable().equalsIgnoreCase("Available") && TextUtils.isEmpty(internalCateringRequired))
        {
            internalCateringRequiredATIL.setError("Required");
            validate = false;
        }

        if (TextUtils.isEmpty(noOfGuests))
        {
            noOfGuestsTIL.setError("Required");
            validate = false;
        }
        else if (Integer.parseInt(noOfGuests) < Integer.parseInt(venue.getMinimumSeatingCapacity()) || Integer.parseInt(noOfGuests) > Integer.parseInt(venue.getMaximumSeatingCapacity()))
        {
            noOfGuestsTIL.setError(venue.getMinimumSeatingCapacity() + "-" + venue.getMaximumSeatingCapacity() + " Guests Allowed");
            validate = false;
        }

        if (TextUtils.isEmpty(noOfVipTablesRequired))
        {
            noOfVipTablesRequiredTIL.setError("Required");
            validate = false;
        }
        else if (Integer.parseInt(noOfVipTablesRequired) < 1 || Integer.parseInt(noOfVipTablesRequired) > Integer.parseInt(venue.getMaximumVIPTables()))
        {
            noOfVipTablesRequiredTIL.setError("1-" + venue.getMaximumVIPTables() + " VIP Tables Allowed");
            validate = false;
        }

        return  validate;
    }

    private void finalizeViews() {

        if (!venue.getCateringAvailable().equalsIgnoreCase("Available"))
        {
            internalCateringRequiredATIL.setVisibility(View.GONE);
            internalCateringRequiredACTV.setVisibility(View.GONE);
        }

        if (!venue.getPartitionAvailable().equalsIgnoreCase("Yes"))
        {

            partitionRequiredTIL.setVisibility(View.GONE);
            partitionRequiredACTV.setVisibility(View.GONE);
        }

        soundSystemRequiredTIL.setHelperText("Rs. " + venue.getSoundSystemPrice());
        noOfGuestsTIL.setHelperText("Rs. " + venue.getPricePerChair() + " Per Person");
        noOfVipTablesRequiredTIL.setHelperText("Rs. " + venue.getPricePerVipTable() + " Per Table");
        totalPriceTV.setText("Rs. " + venue.getBasePrice());

    }

    private void initializeProperties() {

        Gson gson = new Gson();
        venue = gson.fromJson(getIntent().getStringExtra("venue"), Venue.class);
        venueUid = getIntent().getStringExtra("venueUid");
        dateOfBooking = getIntent().getStringExtra("bookingDate");

        timeSlotTIL = findViewById(R.id.timeSlotDropDown);
        timeSlotACTV = findViewById(R.id.timeSlotDropDownInput);
        partitionRequiredTIL = findViewById(R.id.partitionRequiredDropDown);
        partitionRequiredACTV = findViewById(R.id.partitionRequiredDropDownInput);
        soundSystemRequiredTIL = findViewById(R.id.soundSystemRequiredDropDown);
        soundSystemRequiredACTV = findViewById(R.id.soundSystemRequiredDropDownInput);
        internalCateringRequiredATIL = findViewById(R.id.internalCateringRequiredDropDown);
        internalCateringRequiredACTV = findViewById(R.id.internalCateringRequiredDropDownInput);
        noOfGuestsTIL = findViewById(R.id.noOfGuestsTIL);
        noOfVipTablesRequiredTIL = findViewById(R.id.noOfVIPTablesRequiredTIL);
        totalPriceTV = findViewById(R.id.totalPriceTV);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        totalDecorationPrice = Integer.parseInt(venue.getBasePrice());
        soundSystemPrice = 0;
        seatingPrice = 0;
        vipTablesPrice = 0;

        timeSlots = getResources().getStringArray(R.array.time_slots);
        availableOptions = getResources().getStringArray(R.array.yes_no);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.dropdown_item, timeSlots);
        timeSlotACTV.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.dropdown_item, availableOptions);
        partitionRequiredACTV.setAdapter(adapter2);
        soundSystemRequiredACTV.setAdapter(adapter2);
        internalCateringRequiredACTV.setAdapter(adapter2);
    }
}