package com.example.onlineweddinghallbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class AboutActivity extends AppCompatActivity {

    private String role;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeProperties();

        backButtonOnClickListener();
    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (role.equalsIgnoreCase("customer"))
                {
                    startActivity(new Intent(AboutActivity.this, CustomerHomePageActivity.class));
                }
                else
                {
                    startActivity(new Intent(AboutActivity.this, ServiceProviderHomePageActivity.class));
                }

            }
        });

    }

    private void initializeProperties() {

        backButton = findViewById(R.id.backButton);
        role = getIntent().getStringExtra("Role");
    }
}