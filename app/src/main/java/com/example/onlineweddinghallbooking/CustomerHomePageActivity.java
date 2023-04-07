package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class CustomerHomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, VenuesAdapter.OnNoteListener {

    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    boolean resumeRequired = false;
    public static boolean locationAccessPermission = false;
    public static boolean locationEnabled = false;
    double currentLatitude = 0;
    double currentLongitude = 0;

    RecyclerView recyclerView;
    VenuesAdapter venueAdapter;
    ArrayList<Venue> venueArrayList;
    ArrayList<Venue> venuesToRemove;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView navHeaderNameTextView;
    EditText venueSearchEditText;
    ImageView filterImageView;
    TextView currentCityTV;
    TextView shortAddressTV;

    Pattern pattern;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseHelper databaseHelper;

    String uid;
    String name;
    String filtrationRequired;
    String minimumPrice;
    String maximumPrice;
    String minimumSeatingCapacity;
    String maximumSeatingCapacity;
    String minimumParkingCapacity;
    String maximumParkingCapacity;
    int sortByRBID;
    int radiusRBID;
    String comparisonRequired;

    ProgressDialog progressDialog;

    Customer customer;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button openSettingsButton;
    private ImageButton cancelPopupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();

        uid = firebaseAuth.getCurrentUser().getUid();

        filtrationRequired = getIntent().getStringExtra("filtrationRequired");

        navHeaderNameTextView = findViewById(R.id.navHeaderNameTextView);
        venueSearchEditText = findViewById(R.id.venueSearchEditText);
        filterImageView = findViewById(R.id.filterImageView);
        currentCityTV = findViewById(R.id.currentCityTV);
        shortAddressTV = findViewById(R.id.shortAddressTV);

        currentCityTVOnClickListener();
        shortAddressTVOnClickListener();

        pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

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

        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        setProfileNameInNavigationBar();



        recyclerView = findViewById(R.id.venueList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        venueArrayList = new ArrayList<>();
        venueAdapter = new VenuesAdapter(this, venueArrayList, this);
        recyclerView.setAdapter(venueAdapter);

        getAllVenues();

        comparisonRequired = getIntent().getStringExtra("comparisonRequired");

        filterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CustomerHomePageActivity.this, FilterVenueActivity.class));
            }
        });


        venueSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                venueArrayList.clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                venueArrayList.clear();
            }

            @Override
            public void afterTextChanged(Editable s) {

                venueArrayList.clear();
                String venueName = venueSearchEditText.getText().toString();

                if (TextUtils.isEmpty(venueName))
                {
                    getAllVenues();
                }
                else
                {
                    getVenueBySearchText(venueName);
                }

            }
        });


        venueSearchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE); imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    return true;
                }

                return false;
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();


    }

    private void shortAddressTVOnClickListener() {

        shortAddressTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermissions() == false)
                {
                    dialogBuilder = new AlertDialog.Builder(CustomerHomePageActivity.this);
                    final  View locationPermissionPopup = getLayoutInflater().inflate(R.layout.location_permission_popup, null);

                    cancelPopupButton = locationPermissionPopup.findViewById(R.id.cancelPopupButton);
                    openSettingsButton = locationPermissionPopup.findViewById(R.id.openSettingsButton);

                    dialogBuilder.setView(locationPermissionPopup);
                    dialog = dialogBuilder.create();
                    dialog.show();

                    cancelPopupButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();

                        }
                    });

                    openSettingsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                        }
                    });
                }
                else
                {
                    if (locationEnabled == false)
                    {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(CustomerHomePageActivity.this);
                        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        resumeRequired = true;
                                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        dialog.cancel();
                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        });

    }

    private void currentCityTVOnClickListener() {

        currentCityTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermissions() == false)
                {
                    dialogBuilder = new AlertDialog.Builder(CustomerHomePageActivity.this);
                    final  View locationPermissionPopup = getLayoutInflater().inflate(R.layout.location_permission_popup, null);

                    cancelPopupButton = locationPermissionPopup.findViewById(R.id.cancelPopupButton);
                    openSettingsButton = locationPermissionPopup.findViewById(R.id.openSettingsButton);

                    dialogBuilder.setView(locationPermissionPopup);
                    dialog = dialogBuilder.create();
                    dialog.show();

                    cancelPopupButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();

                        }
                    });

                    openSettingsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                        }
                    });
                }
                else
                {
                    if (locationEnabled == false)
                    {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(CustomerHomePageActivity.this);
                        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        resumeRequired = true;
                                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        dialog.cancel();
                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        if (checkPermissions()) {

            locationAccessPermission = true;

            if (isLocationEnabled()) {

                locationEnabled = true;

                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        Location location = task.getResult();

                        if (location == null)
                        {
                            progressDialog = FancyProgressDialog.createProgressDialog(CustomerHomePageActivity.this);

                            requestNewLocationData();
                        }
                        else
                        {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();

                            try
                            {
                                setCurrentAddress(currentLatitude, currentLongitude);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }
                });
            }
            else
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                resumeRequired = true;
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        }
        else
        {
            requestPermissions();
        }

    }

    private void requestPermissions() {

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getLastLocation();
            }
            else
            {
                //Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (resumeRequired)
        {
            if (checkPermissions())
            {
                getLastLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            currentLatitude = mLastLocation.getLatitude();
            currentLongitude = mLastLocation.getLongitude();

            try
            {
                progressDialog.dismiss();
                setCurrentAddress(currentLatitude, currentLongitude);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    };

    private boolean isLocationEnabled() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkPermissions() {

        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setCurrentAddress(double latitude, double longitude) throws IOException {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

        String shortAddress = addresses.get(0).getAddressLine(0);
        String cityName = addresses.get(0).getLocality();

        if (shortAddress.length() > 32)
        {
            shortAddress = shortAddress.substring(0, 31);
            shortAddress += "...";
        }

        currentCityTV.setText(cityName);
        shortAddressTV.setText(shortAddress);
    }


    private void getVenueBySearchText(String searchText) {

        firebaseDatabase.getReference("Venues").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                venueArrayList.clear();
                venueAdapter.notifyDataSetChanged();

                long numOfRatings = snapshot.getChildrenCount() - 1;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    try
                    {
                        Venue venue = dataSnapshot.getValue(Venue.class);

                        int totalRatings = Integer.parseInt(venue.getAvgRating());

                        float avgRating = ((float)totalRatings/(float) numOfRatings);

                        if (String.valueOf(avgRating).length() > 3)
                        {
                            String avgRatingWithOneDecimalPoint = String.valueOf(avgRating).substring(0, 3);
                            avgRating = Float.valueOf(avgRatingWithOneDecimalPoint);
                        }

                       venue.setAvgRating(String.valueOf(avgRating));

                        if (venue.getName().toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT))
                         || venue.getAddress().toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT)))
                        {
                            venueArrayList.add(venue);
                        }
                        else if (pattern.matcher(searchText).matches())
                        {
                            if (Integer.valueOf(searchText) >= Integer.valueOf(venue.getBasePrice()))
                            {
                                venueArrayList.add(venue);
                            }
                        }
                        venueAdapter.notifyDataSetChanged();
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(CustomerHomePageActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                //checkIfFiltrationRequired(venueArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CustomerHomePageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getAllVenues() {

        firebaseDatabase.getReference("Venues").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                venueArrayList.clear();
                venueAdapter.notifyDataSetChanged();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    try
                    {
                        Venue venue = dataSnapshot.getValue(Venue.class);
                        venueArrayList.add(venue);
                        //venueAdapter.notifyDataSetChanged();
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(CustomerHomePageActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                checkIfFiltrationRequired(venueArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CustomerHomePageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void checkIfFiltrationRequired(ArrayList<Venue> venueArrayList) {

        if (filtrationRequired != null)
        {
            getFilteredVenues(venueArrayList);
        }
        else
        {
            for (int i = 0; i < venueArrayList.size(); i++)
            {
                int finalI = i;

                if (Integer.parseInt(venueArrayList.get(i).getAvgRating()) > 0)
                {
                    firebaseDatabase.getReference("Venues").child(venueArrayList.get(i).getUid()).child("Rating And Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            int totalRatings = Integer.parseInt(venueArrayList.get(finalI).getAvgRating());
                            long numOfRatings = snapshot.getChildrenCount();

                            float avgRating = ((float)totalRatings/(float) numOfRatings);

                            if (String.valueOf(avgRating).length() > 3)
                            {
                                String avgRatingWithOneDecimalPoint = String.valueOf(avgRating).substring(0, 3);
                                avgRating = Float.valueOf(avgRatingWithOneDecimalPoint);
                            }

                            venueArrayList.get(finalI).setAvgRating(String.valueOf(avgRating));

                            venueAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(CustomerHomePageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    venueAdapter.notifyDataSetChanged();
                }

            }


        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
            {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            else
            {
                moveTaskToBack(true);
            }

            return true;
        }

        return false;

    }

    private void setProfileNameInNavigationBar() {

        databaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                customer = snapshot.getValue(Customer.class);

                View hView =  navigationView.inflateHeaderView(R.layout.nav_header_layout);
                TextView tv = (TextView)hView.findViewById(R.id.navHeaderNameTextView);

                try
                {
                    tv.setText(customer.getName());
                }
                catch (Exception exception) {

                    Toast.makeText(CustomerHomePageActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CustomerHomePageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkUser() {

        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null)
        {
            startActivity(new Intent(CustomerHomePageActivity.this, LoginActivity.class));
            finish();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        String menuItem = getResources().getResourceEntryName(item.getItemId());

        if (menuItem.equals("logout"))
        {
            FirebaseAuth.getInstance().signOut();
            checkUser();
        }
        else if (menuItem.equals("profile"))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(CustomerHomePageActivity.this, CustomerProfileActivity.class));
        }
        else if (menuItem.equals("about"))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent i = new Intent(this, AboutActivity.class);
            i.putExtra("Role", "customer");
            startActivity(i);
        }
        else if (menuItem.equals("bookings"))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent i = new Intent(this, MyBookingsActivity.class);
            startActivity(i);
        }

        return false;
    }

    public void getFilteredVenues(ArrayList<Venue> venueArrayList)
    {

        getFiltrationValues();

        if (!TextUtils.isEmpty(minimumPrice))
        {
            Iterator<Venue> itr = venueArrayList.iterator();

            while (itr.hasNext())
            {
                Venue venue = itr.next();
                if (Integer.parseInt(venue.getBasePrice()) < Integer.parseInt(minimumPrice))
                {
                    itr.remove();
                }
            }
        }

        if (!TextUtils.isEmpty(maximumPrice))
        {
            Iterator<Venue> itr = venueArrayList.iterator();

            while (itr.hasNext())
            {
                Venue venue = itr.next();
                if (Integer.parseInt(venue.getBasePrice()) > Integer.parseInt(maximumPrice))
                {
                    itr.remove();
                }
            }
        }

        if (!TextUtils.isEmpty(minimumSeatingCapacity))
        {
            Iterator<Venue> itr = venueArrayList.iterator();

            while (itr.hasNext())
            {
                Venue venue = itr.next();
                if (Integer.parseInt(venue.getMaximumSeatingCapacity()) < Integer.parseInt(minimumSeatingCapacity))
                {
                    itr.remove();
                }
            }
        }

        if (!TextUtils.isEmpty(maximumSeatingCapacity))
        {
            Iterator<Venue> itr = venueArrayList.iterator();

            while (itr.hasNext())
            {
                Venue venue = itr.next();
                if (Integer.parseInt(venue.getMaximumSeatingCapacity()) > Integer.parseInt(maximumSeatingCapacity))
                {
                    itr.remove();
                }
            }
        }

        if (!TextUtils.isEmpty(minimumParkingCapacity))
        {
            Iterator<Venue> itr = venueArrayList.iterator();

            while (itr.hasNext())
            {
                Venue venue = itr.next();
                if (Integer.parseInt(venue.getMaximumParkingCapacity()) < Integer.parseInt(minimumParkingCapacity))
                {
                    itr.remove();
                }
            }
        }

        if (!TextUtils.isEmpty(maximumParkingCapacity))
        {
            Iterator<Venue> itr = venueArrayList.iterator();

            while (itr.hasNext())
            {
                Venue venue = itr.next();
                if (Integer.parseInt(venue.getMaximumParkingCapacity()) > Integer.parseInt(maximumParkingCapacity))
                {
                    itr.remove();
                }
            }
        }

        if (radiusRBID != 0 && locationAccessPermission == true && locationEnabled == true)
        {
            double radius = 0;

            if (radiusRBID == 1)
            {
                radius = 5;
            }
            else if (radiusRBID == 2)
            {
                radius = 10;
            }
            else
            {
                radius = 15;
            }

            Iterator<Venue> itr = venueArrayList.iterator();

            while (itr.hasNext())
            {
                Venue venue = itr.next();

                if (distance(currentLatitude, currentLongitude, (double)Float.valueOf(venue.getLatitude()),
                        (double)Float.valueOf(venue.getLongitude())) > radius)
                {
                    itr.remove();
                }
            }
        }

        for (int i = 0; i < venueArrayList.size(); i++) {

            int finalI = i;

            if (Integer.parseInt(venueArrayList.get(finalI).getAvgRating()) > 0)
            {
                firebaseDatabase.getReference("Venues").child(venueArrayList.get(finalI).getUid()).child("Rating And Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int totalRatings = Integer.parseInt(venueArrayList.get(finalI).getAvgRating());
                        long numOfRatings = snapshot.getChildrenCount();
                        float avgRating = ((float)totalRatings/(float) numOfRatings);

                        if (String.valueOf(avgRating).length() > 3)
                        {
                            String avgRatingWithOneDecimalPoint = String.valueOf(avgRating).substring(0, 3);
                            avgRating = Float.valueOf(avgRatingWithOneDecimalPoint);
                        }

                        venueArrayList.get(finalI).setAvgRating(String.valueOf(avgRating));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(CustomerHomePageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }


        if (sortByRBID == 1)
        {
            for (int i = 0; i < venueArrayList.size(); i++) {

                int finalI = i;

                firebaseDatabase.getReference("Venues").child(venueArrayList.get(i).getUid()).child("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        venueArrayList.get(finalI).setNoOfBookings(((int) snapshot.getChildrenCount()));
                        Sort sort = new Sort();
                        sort.selectionSort(venueArrayList);

                        venueAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(CustomerHomePageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else
        {
            venueAdapter.notifyDataSetChanged();
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        double distanceInKM = dist/0.62137;

        return (distanceInKM);
    }

    private double deg2rad(double deg) {

        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {

        return (rad * 180.0 / Math.PI);
    }

    private void getFiltrationValues() {

        databaseHelper = new DatabaseHelper(CustomerHomePageActivity.this);

        try
        {
            Cursor data = databaseHelper.getData();

            while (data.moveToNext())
            {
                sortByRBID = Integer.valueOf(data.getString(1));
                minimumPrice = data.getString(2);
                maximumPrice = data.getString(3);
                minimumSeatingCapacity = data.getString(4);
                maximumSeatingCapacity = data.getString(5);
                minimumParkingCapacity = data.getString(6);
                maximumParkingCapacity = data.getString(7);
                radiusRBID = Integer.valueOf(data.getString(8));
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onNoteClick(int position) {

        if (comparisonRequired != null)
        {
            VenueComparisonActivity.venueTwoUid = venueArrayList.get(position).getUid();
            comparisonRequired = null;
            startActivity(new Intent(CustomerHomePageActivity.this, VenueComparisonActivity.class));
        }
        else
        {
            Intent i = new Intent(CustomerHomePageActivity.this, VenueViewActivity.class);
            i.putExtra("venueUid", venueArrayList.get(position).getUid());
            startActivity(i);
        }


    }
}