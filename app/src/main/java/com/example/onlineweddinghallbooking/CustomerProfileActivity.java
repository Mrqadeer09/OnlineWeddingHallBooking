package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class CustomerProfileActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextView nameTextView;
    TextView emailTextView;
    TextView passwordTextView;
    TextView phoneNoTextView;

    ImageView nameEditButton;
    ImageView emailEditButton;
    ImageView phoneNoEditButton;
    ImageView passwordEditButton;
    ImageView backButton;

    String uid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        initializeFirebase();
        initializeWidgets();
        initializeUser();

        nameEditButtonListener();
        emailEditButtonListener();
        phoneNoEditButtonListener();
        passwordEditButtonListener();
        backButtonListener();

    }

    private void backButtonListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent i = new Intent(CustomerProfileActivity.this, CustomerHomePageActivity.class);
//                startActivity(i);

                CustomerProfileActivity.super.onBackPressed();
            }
        });
    }

    private void passwordEditButtonListener() {

        passwordEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CustomerProfileActivity.this, CustomerPasswordEditActivity.class));
            }
        });

    }

    private void phoneNoEditButtonListener() {

        phoneNoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CustomerProfileActivity.this, CustomerPhoneEditActivity.class));
            }
        });

    }

    private void emailEditButtonListener() {

        emailEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CustomerProfileActivity.this, CustomerEmailEditActivity.class));
            }
        });

    }

    private void nameEditButtonListener() {

        nameEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CustomerProfileActivity.this, CustomerNameEditActivity.class));
            }
        });

    }

    private void setProperties(Customer customer) {

        try {
            nameTextView.setText(customer.getName());
            emailTextView.setText(customer.getEmail());
            phoneNoTextView.setText(customer.getPhoneNo());
            passwordTextView.setText("******");
        }
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeUser() {

        uid = firebaseUser.getUid();

        databaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Customer c = snapshot.getValue(Customer.class);

                setProperties(c);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CustomerProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeWidgets() {

        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        phoneNoTextView = findViewById(R.id.phoneNoTextView);

        nameEditButton = findViewById(R.id.nameEditImageView);
        emailEditButton = findViewById(R.id.emailEditImageView);
        phoneNoEditButton = findViewById(R.id.phoneNoEditImageView);
        passwordEditButton = findViewById(R.id.passwordEditImageView);
        backButton = findViewById(R.id.backButton);

    }

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


    }


}