package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

public class VenueViewActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    RecyclerView recyclerView;
    RatingAndReviewAdapter ratingAndReviewAdapter;
    ArrayList<RatingAndReview> arrayList;

    TextView venueName;
    TextView venueAddress;
    TextView venueRating;
    TextView venueBasePrice;
    TextView venueDescription;
    TextView venueSeatingCapacity;
    TextView venueParkingCapacity;
    TextView venuePartitionAvailable;

    DatePickerDialog datePickerDialog ;
    int Year;
    int Month;
    int Day;

    ImageView locationViewButton;


    ImageSlider imageSlider;

    RelativeLayout chatRL;
    RelativeLayout contactRL;
    RelativeLayout comparisonRL;
    RelativeLayout proceedRL;

    ArrayList<SliderImages> sliderImagesArrayList = new ArrayList<>();
    ArrayList<Booking> bookingReferences = new ArrayList<>();
    ArrayList<Booking> bookings = new ArrayList<>();
    Calendar[] disabledDates;
    ArrayList<Calendar> peakDays;
    Calendar[] peakDaysArray;

    String uid;
    String chatReferenceID;
    float avgRating;
    Venue venueObj;
    int counter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_view);

        initializeProperties();
        getDatePickerAvailableDates();

        progressDialog.show();
        getSliderImagesFromFireBase();
        getVenueDetails();
        getRatingAndReviewsFromFireBase();
        progressDialog.dismiss();

        mapButtonOnClickListener();

        chatRLOnClickListener();
        contactRLOnClickListener();
        comparisonRLOnClickListener();
        proceedRLOnClickListener();

    }

    private void comparisonRLOnClickListener() {

        comparisonRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VenueComparisonActivity.venueOneUid = venueObj.getUid();

                Intent i = new Intent(VenueViewActivity.this, CustomerHomePageActivity.class);
                i.putExtra("comparisonRequired", "true");
                startActivity(i);
            }
        });
    }


    private void getDatePickerAvailableDates() {

        firebaseDatabase.getReference("Venues").child(uid).child("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                disabledDates = new Calendar[((int) snapshot.getChildrenCount())];

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Booking bookingRef = dataSnapshot.getValue(Booking.class);
                    bookingReferences.add(bookingRef);
                }

                getBooking(bookingReferences);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(VenueViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getBooking(ArrayList<Booking> bookingReferences) {

        for (int i = 0; i < bookingReferences.size(); i++)
        {
            int finalI = i;

            firebaseDatabase.getReference("Bookings").child(bookingReferences.get(i).getBookingUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Booking booking = snapshot.getValue(Booking.class);
                    bookings.add(booking);

                    DateFormat formatter ;
                    Date date ;
                    formatter = new SimpleDateFormat("dd-MMM-yy");
                    try
                    {
                        date = (Date)formatter.parse(booking.getDateTime());
                        Calendar cal=Calendar.getInstance();
                        cal.setTime(date);
                        disabledDates[finalI] = cal;
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(VenueViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }


    }

    private void proceedRLOnClickListener() {

        proceedRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = Calendar.getInstance().get(Calendar.YEAR);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                Calendar cal = Calendar.getInstance();
                cal.set(year, month, today);

                int count = 0;

                for (int i = 0; i < 60; i++)
                {
                    int day = cal.get(Calendar.DAY_OF_WEEK);

                    if (day == Calendar.SATURDAY || day == Calendar.SUNDAY)
                    {
                        count++;
                    }

                    cal.add(Calendar.DAY_OF_MONTH, 1);
                }

                peakDaysArray = new Calendar[count];

                count = 0;

                cal.set(year, month, today);

                for (int i = 0; i < 60; i++)
                {
                    int day = cal.get(Calendar.DAY_OF_WEEK);

                    if (day == Calendar.SATURDAY || day == Calendar.SUNDAY)
                    {
                        Calendar ca = (Calendar) cal.clone();

                        peakDaysArray[count] = ca;

                        count++;
                    }
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                }


                Year = Calendar.getInstance().get(Calendar.YEAR);
                Month = Calendar.getInstance().get(Calendar.MONTH);
                Day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                Calendar c = Calendar.getInstance();
                c.set(Year, Month, Day+1);

                datePickerDialog = DatePickerDialog.newInstance(VenueViewActivity.this, Year, Month, Day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("Date Picker");

                datePickerDialog.setMinDate(c);

                c.set(Year, Month + 2, Day);
                datePickerDialog.setMaxDate(c);

                datePickerDialog.setDisabledDays(disabledDates);
                datePickerDialog.setHighlightedDays(peakDaysArray);
                datePickerDialog.setTitle(null);
                datePickerDialog.setOkText("Proceed");
                datePickerDialog.setAccentColor("#FF954CFB");

                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

            }
        });

    }

    private void setVenueRating() {

        if (venueObj !=  null)
        {
            if (Integer.parseInt(venueObj.getAvgRating()) > 0)
            {
                firebaseDatabase.getReference("Venues").child(venueObj.getUid()).child("Rating And Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int totalRatings = Integer.parseInt(venueObj.getAvgRating());
                        long numOfRatings = snapshot.getChildrenCount();
                        float avgRating = ((float)totalRatings/(float) numOfRatings);

                        if (String.valueOf(avgRating).length() > 3)
                        {
                            String avgRatingWithOneDecimalPoint = String.valueOf(avgRating).substring(0, 3);
                            avgRating = Float.valueOf(avgRatingWithOneDecimalPoint);
                        }

                        venueRating.setText(String.valueOf(avgRating));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(VenueViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                venueRating.setText("0.0");
            }
        }
        else
        {
            venueRating.setText("0.0");
        }


    }

    private void contactRLOnClickListener() {

        contactRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+venueObj.getContact()));
                startActivity(intent);

            }
        });

    }


    private void chatRLOnClickListener() {

        chatRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chatReferenceID = FirebaseAuth.getInstance().getCurrentUser().getUid() + ":" + uid;

                Intent i = new Intent(VenueViewActivity.this, ChatActivity.class);
                i.putExtra("receiverUid", venueObj.getOwnerUid());
                i.putExtra("chatReferenceID", chatReferenceID);
                i.putExtra("receiverType", "serviceProvider");
                i.putExtra("venueUid", uid);
                startActivity(i);

            }
        });

    }

    private void mapButtonOnClickListener() {

        locationViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String geoUri = "http://maps.google.com/maps?q=loc:" + venueObj.getLatitude() + "," + venueObj.getLongitude() + " (" + venueObj.getName() + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(intent);
            }
        });



    }

    private void getRatingAndReviewsFromFireBase() {

        firebaseDatabase.getReference("Venues").child(uid).child("Rating And Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    RatingAndReview ratingAndReview = dataSnapshot.getValue(RatingAndReview.class);
                    arrayList.add(ratingAndReview);
                }

                ratingAndReviewAdapter.notifyDataSetChanged();
                //setVenueRating();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(VenueViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getVenueDetails() {



        firebaseDatabase.getReference("Venues").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Venue venue = snapshot.getValue(Venue.class);
                setVenueDetails(venue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(VenueViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setVenueDetails(Venue venue) {

        venueObj = venue;

        venueName.setText(venue.getName());
        venueAddress.setText(venue.getAddress());
        venueBasePrice.setText("Rs. " + venue.getBasePrice());
        venueDescription.setText(venue.getDescription());
        venueParkingCapacity.setText(venue.getMinimumParkingCapacity() + "-" + venue.getMaximumParkingCapacity());
        venueSeatingCapacity.setText(venue.getMinimumSeatingCapacity() + "-" + venue.getMaximumSeatingCapacity());

        if (venue.getPartitionAvailable().equalsIgnoreCase("Yes"))
        {
            venuePartitionAvailable.setText("Available");
        }
        else
        {
            venuePartitionAvailable.setText("Not Available");
        }

        setVenueRating();

    }

    private void addImagesToSlider() {

        ArrayList<SlideModel> images = new ArrayList<>();

        try {

            for (int i = 0; i < sliderImagesArrayList.size(); i++)
            {
                String imgUrl = sliderImagesArrayList.get(i).getImg();
                images.add(new SlideModel(imgUrl, null));
            }

            imageSlider.setImageList(images, ScaleTypes.CENTER_CROP);

        }
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void getSliderImagesFromFireBase() {

        firebaseDatabase.getReference("Venues").child(uid).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    try
                    {
                        SliderImages sliderImage = dataSnapshot.getValue(SliderImages.class);
                        sliderImagesArrayList.add(sliderImage);
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(VenueViewActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                addImagesToSlider();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(VenueViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeProperties() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        uid = getIntent().getStringExtra("venueUid");

        imageSlider = findViewById(R.id.venueImageSlider);

        venueName = findViewById(R.id.venueNameTextView);
        venueAddress = findViewById(R.id.venueAddressTextView);
        venueRating = findViewById(R.id.venueRatingTextView);
        venueBasePrice = findViewById(R.id.basePriceTextView);
        venueDescription = findViewById(R.id.descriptionTextView);
        venueSeatingCapacity = findViewById(R.id.seatingCapacityTextView);
        venueParkingCapacity = findViewById(R.id.parkingCapacityTextView);
        venuePartitionAvailable = findViewById(R.id.partitionAvailableTextView);
        locationViewButton = findViewById(R.id.mapLocationButton);


        chatRL = findViewById(R.id.chatRL);
        contactRL = findViewById(R.id.contactRL);
        comparisonRL = findViewById(R.id.comparisonRL);
        proceedRL = findViewById(R.id.proceedRL);

        progressDialog = FancyProgressDialog.createProgressDialog(VenueViewActivity.this);

        recyclerView = findViewById(R.id.ratingAndReviewList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();
        peakDays = new ArrayList<>();

        ratingAndReviewAdapter = new RatingAndReviewAdapter(this, arrayList);

        recyclerView.setAdapter(ratingAndReviewAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        java.time.Month month = java.time.Month.of(monthOfYear+1);

        String date = dayOfMonth+"-"+month.toString()+"-"+year;

        Gson gson = new Gson();
        String venueJson = gson.toJson(venueObj);

        Intent i = new Intent(VenueViewActivity.this, Booking1Activity.class);
        i.putExtra("venueUid", uid);
        i.putExtra("venue", venueJson);
        i.putExtra("bookingDate", date);
        startActivity(i);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}