package com.example.onlineweddinghallbooking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class FilterVenueActivity extends AppCompatActivity {

    RadioGroup sortByRadioGroup;
    RadioGroup radiusRadioGroup;

    RadioButton defaultSortByRadioButton;
    RadioButton mostBookedRadioButton;
    RadioButton defaultRadiusRadioButton;
    RadioButton km5RadioButton;
    RadioButton km10RadioButton;
    RadioButton km15RadioButton;

    TextInputLayout minimumPrice;
    TextInputLayout maximumPrice;
    TextInputLayout minimumSeatingCapacity;
    TextInputLayout maximumSeatingCapacity;
    TextInputLayout minimumParkingCapacity;
    TextInputLayout maximumParkingCapacity;

    DatePicker availabilityDatePicker;

    Button applyButton;

    TextView sortByDefaultTV;
    TextView mostBookedTV;
    TextView defaultRadiusTV;
    TextView km5TV;
    TextView km10TV;
    TextView km15TV;
    ImageView backButton;

    DatabaseHelper databaseHelper;

    int sortByRadioGroupId;
    int radiusRadioGroupId;
    String minPrice;
    String maxPrice;
    String minSeatingCapacity;
    String maxSeatingCapacity;
    String minParkingCapacity;
    String maxParkingCapacity;
    String availability;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_venue);


        initializeWidgets();
        setMaxAvailableBookingDate();
        getFilterValues();

        backButtonOnClickListener();
        applyButtonOnClickListener();
        sortByDefaultTVOnClickListener();
        mostBookedTVOnClickListener();
        defaultRadiusTVOnClickListener();
        km5TVOnClickListener();
        km10TVOnClickListener();
        km15TVOnClickListener();

    }

    private void km10TVOnClickListener() {

        km10TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                km10RadioButton.setChecked(true);
            }
        });

    }

    private void km5TVOnClickListener() {

        km5TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                km5RadioButton.setChecked(true);
            }
        });

    }

    private void km15TVOnClickListener() {

        km15TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                km15RadioButton.setChecked(true);
            }
        });

    }

    private void defaultRadiusTVOnClickListener() {

        defaultRadiusTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                defaultRadiusRadioButton.setChecked(true);
            }
        });

    }

    private void mostBookedTVOnClickListener() {

        mostBookedTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mostBookedRadioButton.setChecked(true);
            }
        });

    }

    private void sortByDefaultTVOnClickListener() {

        sortByDefaultTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                defaultSortByRadioButton.setChecked(true);
            }
        });

    }

    private void getFilterValues() {

        databaseHelper = new DatabaseHelper(FilterVenueActivity.this);

        try {
            Cursor data = databaseHelper.getData();

            if (data != null)
            {
                while (data.moveToNext())
                {
                    final int sortByRBIndex = Integer.valueOf(data.getString(1));
                    final RadioButton sortByRB = (RadioButton) sortByRadioGroup.getChildAt(sortByRBIndex);
                    sortByRB.setChecked(true);

                    final int radiusRBIndex = Integer.valueOf(data.getString(8));
                    final RadioButton radiusRB = (RadioButton) radiusRadioGroup.getChildAt(radiusRBIndex);
                    radiusRB.setChecked(true);

                    minimumPrice.getEditText().setText(data.getString(2));
                    maximumPrice.getEditText().setText(data.getString(3));
                    minimumSeatingCapacity.getEditText().setText(data.getString(4));
                    maximumSeatingCapacity.getEditText().setText(data.getString(5));
                    minimumParkingCapacity.getEditText().setText(data.getString(6));
                    maximumParkingCapacity.getEditText().setText(data.getString(7));
                }
            }
            else
            {
                setDefaultRadioButtons();
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    private void applyButtonOnClickListener() {

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseHelper = new DatabaseHelper(FilterVenueActivity.this);
                databaseHelper.truncateTable();

                setProperties();
            }
        });

    }

    private void setProperties() {

        final int sortByRGID = sortByRadioGroup.getCheckedRadioButtonId();
        final View sortByRB = sortByRadioGroup.findViewById(sortByRGID);
        sortByRadioGroupId  = sortByRadioGroup.indexOfChild(sortByRB);

        final int radiusRGID = radiusRadioGroup.getCheckedRadioButtonId();
        final View radiusRB = radiusRadioGroup.findViewById(radiusRGID);
        radiusRadioGroupId = radiusRadioGroup.indexOfChild(radiusRB);


       minPrice = minimumPrice.getEditText().getText().toString();
       maxPrice = maximumPrice.getEditText().getText().toString();
       minSeatingCapacity = minimumSeatingCapacity.getEditText().getText().toString();
       maxSeatingCapacity = maximumSeatingCapacity.getEditText().getText().toString();
       minParkingCapacity = minimumParkingCapacity.getEditText().getText().toString();
       maxParkingCapacity = maximumParkingCapacity.getEditText().getText().toString();
       availability = null;

       if (radiusRadioGroupId != 0 && (CustomerHomePageActivity.locationAccessPermission == false || CustomerHomePageActivity.locationEnabled == false))
       {
           Toast.makeText(this, "Enable Location Access", Toast.LENGTH_SHORT).show();
           return;
       }

       saveFilterValuesInLocalDB();
    }

    private void saveFilterValuesInLocalDB() {

        try
        {
            boolean insertData = databaseHelper.addData(String.valueOf(sortByRadioGroupId), minPrice, maxPrice, minSeatingCapacity,
                    maxSeatingCapacity, minParkingCapacity, maxParkingCapacity, String.valueOf(radiusRadioGroupId), availability);

            if (insertData)
            {
                Intent i = new Intent(FilterVenueActivity.this, CustomerHomePageActivity.class);
                i.putExtra("filtrationRequired", "true");

                startActivity(i);
            }
            else
            {
                Toast.makeText(FilterVenueActivity.this, "Error Inserting Data", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(FilterVenueActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void showToast(String msg)
    {
        Toast.makeText(FilterVenueActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FilterVenueActivity.super.onBackPressed();
                //startActivity(new Intent(FilterVenueActivity.this, CustomerHomePageActivity.class));
            }
        });

    }

    private void setDefaultRadioButtons() {

        sortByRadioGroup.check(sortByRadioGroup.getChildAt(0).getId());
        radiusRadioGroup.check(radiusRadioGroup.getChildAt(0).getId());
    }

    private void setMaxAvailableBookingDate() {

//        int year = Calendar.getInstance().get(Calendar.YEAR);
//        int month = Calendar.getInstance().get(Calendar.MONTH);
//        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//
//        Calendar c = Calendar.getInstance();
//        c.set(year, month + 3, day);
//        availabilityDatePicker.setMinDate(System.currentTimeMillis() - 1000);
//        availabilityDatePicker.setMaxDate(c.getTimeInMillis());

    }

    private void initializeWidgets() {

        sortByRadioGroup = findViewById(R.id.sortByRadioGroup);
        radiusRadioGroup = findViewById(R.id.radiusRadioGroup);

        defaultSortByRadioButton = (RadioButton) findViewById(R.id.sortByDefaultRadioButton);
        mostBookedRadioButton = (RadioButton) findViewById(R.id.mostBookedRadioButton);
        defaultRadiusRadioButton = (RadioButton) findViewById(R.id.defaultRadiusRadioButton);
        km5RadioButton = (RadioButton) findViewById(R.id.km5RadioButton);
        km10RadioButton = (RadioButton) findViewById(R.id.km10RadioButton);
        km15RadioButton = (RadioButton) findViewById(R.id.km15RadioButton);

        minimumPrice = findViewById(R.id.minPriceTextInputLayout);
        maximumPrice = findViewById(R.id.maxPriceTextInputLayout);
        minimumSeatingCapacity = findViewById(R.id.minSeatingCapacityTextInputLayout);
        maximumSeatingCapacity = findViewById(R.id.maxSeatingCapacityTextInputLayout);
        minimumParkingCapacity = findViewById(R.id.minParkingCapacityTextInputLayout);
        maximumParkingCapacity = findViewById(R.id.maxParkingCapacityTextInputLayout);

        //availabilityDatePicker = findViewById(R.id.availabilityDatePicker);

        applyButton = findViewById(R.id.applyButton);

        backButton = findViewById(R.id.backButton);

        sortByDefaultTV = findViewById(R.id.sortByDefaultTV);
        mostBookedTV = findViewById(R.id.mostBookedTV);
        defaultRadiusTV = findViewById(R.id.defaultRadiusTV);
        km5TV = findViewById(R.id.km5TV);
        km10TV = findViewById(R.id.km10TV);
        km15TV = findViewById(R.id.km15TV);
    }

}