package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class DecorationPricingActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Venue venue;

    private TextInputLayout maximumVIPTablesTIL;
    private TextInputLayout pricePerVipTableTIL;
    private TextInputLayout pricePerChairTIL;
    private TextInputLayout soundSystemPriceTIL;
    private Button nextButton;
    private ProgressDialog progressDialog;

    private String venueUid;
    private String maximumVIPTables;
    private String pricePerVipTable;
    private String pricePerChair;
    private String soundSystemPrice;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoration_pricing);

        initializeProperties();
        initializeFirebase();

        nextButtonOnCLickListener();
        maximumVIPTablesTextChangeListener();
        pricePerVipTableTextChangeListener();
        pricePerChairTextChangeListener();
        soundSystemPriceTextChangeListener();
    }

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void soundSystemPriceTextChangeListener() {

        soundSystemPriceTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                soundSystemPriceTIL.setError(null);
            }
        });

    }

    private void pricePerChairTextChangeListener() {

        pricePerChairTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                pricePerChairTIL.setError(null);
            }
        });

    }

    private void pricePerVipTableTextChangeListener() {

        pricePerVipTableTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                pricePerVipTableTIL.setError(null);
            }
        });

    }

    private void maximumVIPTablesTextChangeListener() {

        maximumVIPTablesTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                maximumVIPTablesTIL.setError(null);
            }
        });

    }

    private void nextButtonOnCLickListener() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                maximumVIPTables = maximumVIPTablesTIL.getEditText().getText().toString();
                pricePerVipTable = pricePerVipTableTIL.getEditText().getText().toString();
                pricePerChair = pricePerChairTIL.getEditText().getText().toString();
                soundSystemPrice = soundSystemPriceTIL.getEditText().getText().toString();

                boolean valid = validateProperties();

                if (valid)
                {
                    addDetailsToFirebase();
                }

            }
        });

    }

    private void addDetailsToFirebase() {

        progressDialog.show();

        Map<String, Object> map = new HashMap<>();
        map.put("maximumVIPTables", maximumVIPTables);
        map.put("pricePerVipTable", pricePerVipTable);
        map.put("pricePerChair", pricePerChair);
        map.put("soundSystemPrice", soundSystemPrice);

        firebaseDatabase.getReference("Venues").child(venueUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                firebaseDatabase.getReference("Venues").child(venueUid).updateChildren(map);

                progressDialog.dismiss();

                Gson gson = new Gson();
                String venueJson = gson.toJson(venue);

                Intent i = new Intent(DecorationPricingActivity.this, StagePricingActivity.class);
                i.putExtra("venueUid", venueUid);
                i.putExtra("venue", venueJson);
                startActivity(i);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(DecorationPricingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validateProperties() {

        flag = true;

        if (TextUtils.isEmpty(maximumVIPTables))
        {
            maximumVIPTablesTIL.setError("Required");
            flag = false;
        }
        else if (Integer.valueOf(maximumVIPTables) <= 0)
        {
            maximumVIPTablesTIL.setError("Value Should Be Greater Than 0");
            flag = false;
        }

        if (TextUtils.isEmpty(pricePerVipTable))
        {
            pricePerVipTableTIL.setError("Required");
            flag = false;
        }
        else if (Integer.valueOf(pricePerVipTable) <= 0)
        {
            pricePerVipTableTIL.setError("Value Should Be Greater Than 0");
            flag = false;
        }

        if (TextUtils.isEmpty(pricePerChair))
        {
            pricePerChairTIL.setError("Required");
            flag = false;
        }
        else if (Integer.valueOf(pricePerChair) <= 0)
        {
            pricePerChairTIL.setError("Value Should Be Greater Than 0");
            flag = false;
        }

        if (TextUtils.isEmpty(soundSystemPrice))
        {
            soundSystemPriceTIL.setError("Required");
            flag = false;
        }
        else if (Integer.valueOf(soundSystemPrice) <= 0)
        {
            soundSystemPriceTIL.setError("Value Should Be Greater Than 0");
            flag = false;
        }

        return flag;
    }

    private void initializeProperties() {

        maximumVIPTablesTIL = findViewById(R.id.maximumVIPTablesTIL);
        pricePerVipTableTIL = findViewById(R.id.pricePerVipTableTIL);
        pricePerChairTIL = findViewById(R.id.pricePerChairTIL);
        soundSystemPriceTIL = findViewById(R.id.soundSystemPriceTIL);
        nextButton = findViewById(R.id.nextButton);

        progressDialog = FancyProgressDialog.createProgressDialog(this);
        progressDialog.dismiss();

        Gson gson = new Gson();
        venue = gson.fromJson(getIntent().getStringExtra("venue"), Venue.class);

        venueUid = getIntent().getStringExtra("venueUid");

    }
}