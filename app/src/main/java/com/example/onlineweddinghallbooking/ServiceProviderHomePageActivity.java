package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ServiceProviderHomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    Toolbar toolbar;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    TextView navHeaderNameTextView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String uid;
    String name;

    ServiceProvider serviceProvider;

    CardView addVenueCV;
    CardView viewAllChatsCV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home_page);

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();


        checkUser();

        uid = firebaseAuth.getCurrentUser().getUid();

        addVenueCV = findViewById(R.id.addVenueCV);
        viewAllChatsCV = findViewById(R.id.viewAllChatsCV);

        navHeaderNameTextView = findViewById(R.id.navHeaderNameTextView);

        toolbar = findViewById(R.id.mainToolBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);

        navigationView = findViewById(R.id.navView);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        setProfileNameInNavigationBar();


        addVenueCVOnClickListener();
        viewAllChatsCVOnClickListener();

    }

    private void viewAllChatsCVOnClickListener() {

        viewAllChatsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ServiceProviderHomePageActivity.this, AllChatsViewActivity.class));

            }
        });

    }

    private void addVenueCVOnClickListener() {

        addVenueCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ServiceProviderHomePageActivity.this, AddVenue1Activity.class));

            }
        });

    }

    private void setProfileNameInNavigationBar() {

        databaseReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                serviceProvider = snapshot.getValue(ServiceProvider.class);

                View hView =  navigationView.inflateHeaderView(R.layout.nav_header_layout);
                TextView tv = (TextView)hView.findViewById(R.id.navHeaderNameTextView);

                try {
                    tv.setText(serviceProvider.getName());
                }
                catch (Exception exception) {

                    Toast.makeText(ServiceProviderHomePageActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ServiceProviderHomePageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        String menuItem = getResources().getResourceEntryName(item.getItemId());

        if (menuItem.equals("logout"))
        {
            FirebaseAuth.getInstance().signOut();
            checkUser();
        }
        else if (menuItem.equals("about"))
        {
            drawerLayout .closeDrawer(GravityCompat.START);
            Intent i = new Intent(this, AboutActivity.class);
            i.putExtra("Role", "serviceProvider");
            startActivity(i);
        }

        return false;
    }

    private void checkUser() {

        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null)
        {
            startActivity(new Intent(ServiceProviderHomePageActivity.this, LoginActivity.class));
            finish();
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}