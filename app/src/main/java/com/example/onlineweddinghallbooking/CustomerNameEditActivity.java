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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CustomerNameEditActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextInputLayout nameTextInputLayout;
    Button saveButton;
    ImageView backButton;
    ProgressDialog progressDialog;

    String name;
    String uid;
    String oldName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_name_edit);

        initializeFirebase();
        initializeWidgets();

        getCustomerDetails();

        saveButtonListener();
        nameTextChangeListener();
        backButtonListener();
    }

    private void backButtonListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startActivity(new Intent(CustomerNameEditActivity.this, CustomerProfileActivity.class));

                CustomerNameEditActivity.super.onBackPressed();
                finish();
            }
        });

    }


    private void nameTextChangeListener() {

        nameTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                nameTextInputLayout.setError(null);
            }
        });

    }




    private void saveButtonListener() {

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = nameTextInputLayout.getEditText().getText().toString();

                if (dataValidation() == true)
                {
                    updateDataInDatabase();
                }
            }
        });
    }





    private void updateDataInDatabase() {

        progressDialog.show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, Object> updatedData = new HashMap<>();
                updatedData.put("name", name);
                databaseReference.child("Users").child(uid).updateChildren(updatedData);

                updateLogs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CustomerNameEditActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateLogs() {

        Date c =  Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        Logs log = new Logs();

        log.modifiedBy = name;
        log.modificationDate = formattedDate;

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String key = databaseReference.child("Users").child(uid).child("Logs").child("Modification Log").push().getKey();

                Map<String, Object> map = new HashMap<>();
                map.put("Modified By", log.getModifiedBy());
                map.put("Modification Date", log.getModificationDate());
                map.put("Old Name", oldName);
                map.put("New Name", name);
                databaseReference.child("Users").child(uid).child("Logs").child("Modification Log").child(key).updateChildren(map);

                progressDialog.dismiss();

                Toast.makeText(CustomerNameEditActivity.this, "Success", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(CustomerNameEditActivity.this, CustomerHomePageActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CustomerNameEditActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private boolean dataValidation() {

        if (TextUtils.isEmpty(this.name))
        {
            nameTextInputLayout.setError("Required");
            return false;
        }

        return true;
    }




    private void setCustomerDetails(String name) {

        nameTextInputLayout.getEditText().setText(name);
        oldName = name;
    }





    private void getCustomerDetails() {

        uid = firebaseUser.getUid();

        databaseReference.child("Users").child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                name = snapshot.getValue(String.class);
                setCustomerDetails(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CustomerNameEditActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void initializeWidgets() {

        nameTextInputLayout = findViewById(R.id.nameTextInputLayout);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
        progressDialog = FancyProgressDialog.createProgressDialog(this);
        progressDialog.dismiss();
    }




    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

    }
}