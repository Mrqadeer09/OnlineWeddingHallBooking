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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class ServiceProviderVerificationActivity extends AppCompatActivity {

    TextInputLayout cnicLayout;
    TextInputEditText cnicInput;

    TextInputLayout addressLayout;
    TextInputEditText addressInput;

    Button verifyButton;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ServiceProvider serviceProvider;

    Nadra nadraPerson;

    Logs log;

    String uid;
    String CNIC;
    String address;
    String nameOfUser;

    boolean validate;
    boolean verify;
    boolean nadraVerification;



    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_verification);



        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }



        Gson gson = new Gson();
        serviceProvider = gson.fromJson(getIntent().getStringExtra("user"), ServiceProvider.class);
        log = gson.fromJson(getIntent().getStringExtra("log"), Logs.class);
        uid = getIntent().getStringExtra("uid");





        cnicLayout = findViewById(R.id.cnicTextInputLayout);
        cnicInput = findViewById(R.id.cnicTextInput);

        addressLayout = findViewById(R.id.addressTextInputLayout);
        addressInput = findViewById(R.id.addressTextInput);

        verifyButton = findViewById(R.id.verifyButton);




        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();



        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Verifying");
        progressDialog.setCanceledOnTouchOutside(false);



        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CNIC = cnicLayout.getEditText().getText().toString();
                address = addressLayout.getEditText().getText().toString();

                validate = true;

                validateData();
            }
        });





        cnicInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cnicLayout.setError(null);
            }
        });

        addressInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                addressLayout.setError(null);
            }
        });
    }





    private void validateData() {

        if (CNIC.length() < 13){

            cnicLayout.setError("Enter 13 Digit CNIC Number");
            validate = false;
        }
        else if (TextUtils.isEmpty(CNIC)){

            cnicLayout.setError("Required");
            validate = false;
        }

        if (TextUtils.isEmpty(address)){

            addressLayout.setError("Required");
            validate = false;
        }

        if (validate){

             verifyInfo();

        }
    }


    private void addDataToDatabase() {

        progressDialog.show();

        serviceProvider.setCnic(CNIC);
        serviceProvider.setAddress(address);
        serviceProvider.setVerified("true");


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                databaseReference.child("Users").child(uid).setValue(serviceProvider);

                Map<String, Object> map = new HashMap<>();
                map.put("Created By", log.getCreatedBy());
                map.put("Creation Date", log.getCreationDate());
                databaseReference.child("Users").child(uid).child("Logs").child("Creation Log").updateChildren(map);


                progressDialog.dismiss();
                Toast.makeText(ServiceProviderVerificationActivity.this, "Account Verified Successfully", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(ServiceProviderVerificationActivity.this, ServiceProviderHomePageActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


                Toast.makeText(ServiceProviderVerificationActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void verifyInfo() {


        try
        {
            firebaseDatabase.getReference("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User user = snapshot.getValue(User.class);

                    firebaseDatabase.getReference("Nadra").child(CNIC).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.getChildrenCount() > 0)
                            {
                                nadraPerson = snapshot.getValue(Nadra.class);

                                if (user.getName().equalsIgnoreCase(nadraPerson.getName()))
                                {
                                    addDataToDatabase();
                                }
                                else
                                {
                                    cnicLayout.setError("Registered Name Does Not Match With Nadra");
                                }
                            }
                            else
                            {
                                cnicLayout.setError("Invalid CNIC Number");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(ServiceProviderVerificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(ServiceProviderVerificationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}