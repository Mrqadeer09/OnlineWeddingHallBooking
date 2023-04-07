package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentMethodSelectionActivity extends AppCompatActivity {

    private int paymentOptionRGID;
    private Booking booking;
    private ArrayList<Menu> menuArrayList;

    private TextView totalPriceTV;
    private TextView onlinePaymentTV;
    private TextView paymentByCashTV;
    private Button nextButton;
    private RadioGroup paymentOptionRG;
    private RadioButton cashRB;
    private RadioButton onlinePaymentRB;
    private ProgressDialog progressDialog;
    private ImageButton backButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method_selection);

        initializeProperties();
        initializeFirebase();

        paymentByCashTVOnClickListener();
        onlinePaymentTVOnClickListener();
        nextButtonOnClickListener();
        backButtonOnClickListener();
    }

    private void paymentByCashTVOnClickListener() {

        paymentByCashTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cashRB.setChecked(true);
            }
        });

    }

    private void onlinePaymentTVOnClickListener() {

        onlinePaymentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onlinePaymentRB.setChecked(true);
            }
        });

    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PaymentMethodSelectionActivity.super.onBackPressed();
            }
        });

    }

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void nextButtonOnClickListener() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paymentOptionRGID = paymentOptionRG.getCheckedRadioButtonId();
                View radioButton = paymentOptionRG.findViewById(paymentOptionRGID);
                int radioButtonIndex = paymentOptionRG.indexOfChild(radioButton);

                if (radioButtonIndex == -1)
                {
                    Toast.makeText(PaymentMethodSelectionActivity.this, "Please Select From Available Options", Toast.LENGTH_SHORT).show();
                }
                else if (radioButtonIndex == 0)
                {
                    progressDialog.show();

                    Date c =  Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    String formattedDate = df.format(c);

                    booking.setStatus("Pending");

                    addBookingToDatabase(formattedDate);
                }
                else if (radioButtonIndex == 1)
                {

                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addBookingToDatabase(String formattedDate) {

        String userUid = firebaseAuth.getCurrentUser().getUid();

        firebaseDatabase.getReference("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try
                {
                    String bookingUid = firebaseDatabase.getReference("Bookings").push().getKey();
                    booking.setBookingUid(bookingUid);

                    firebaseDatabase.getReference("Bookings").child(bookingUid).setValue(booking);

                    firebaseDatabase.getReference("Users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            final User user = snapshot.getValue(User.class);

                            Logs log = new Logs();
                            log.setCreatedBy(user.getName());
                            log.setCreationDate(formattedDate);
                            log.setStatus("Active");
                            log.setLockVersion("0");

                            Map<String, Object> creationLogMap = new HashMap<>();
                            creationLogMap.put("Created By", log.getCreatedBy());
                            creationLogMap.put("Creation Date", log.getCreationDate());
                            databaseReference.child("Bookings").child(bookingUid).child("Logs").child("Creation Log").updateChildren(creationLogMap);

                            Map<String, Object> statusLogMap = new HashMap<>();
                            statusLogMap.put("Status", log.getStatus());
                            databaseReference.child("Bookings").child(bookingUid).child("Logs").child("Status Log").updateChildren(statusLogMap);


                            Map<String, Object> lockVersionLogMap = new HashMap<>();
                            lockVersionLogMap.put("Lock Version", log.getLockVersion());
                            databaseReference.child("Bookings").child(bookingUid).child("Logs").child("Lock Version Log").updateChildren(lockVersionLogMap);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(PaymentMethodSelectionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    firebaseDatabase.getReference("Venues").child(booking.getVenueUid()).child("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            HashMap<String, Object> map2 = new HashMap<String, Object>();
                            map2.put("bookingUid", booking.getBookingUid());

                            firebaseDatabase.getReference("Venues").child(booking.getVenueUid()).child("Bookings").child(bookingUid).updateChildren(map2);

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(PaymentMethodSelectionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    firebaseDatabase.getReference("Users").child(userUid).child("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            HashMap<String, Object> map3 = new HashMap<String, Object>();
                            map3.put("bookingUid", booking.getBookingUid());

                            firebaseDatabase.getReference("Users").child(userUid).child("Bookings").child(bookingUid).updateChildren(map3);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(PaymentMethodSelectionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (menuArrayList != null)
                    {
                        firebaseDatabase.getReference("Bookings").child(bookingUid).child("Menu").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (int i = 0; i < menuArrayList.size(); i++)
                                {
                                    String menuKey = firebaseDatabase.getReference("Bookings").child(bookingUid).push().getKey();

                                    HashMap<String, Object> map4 = new HashMap<String, Object>();

                                    final String dishUid = menuArrayList.get(i).getDishUid().toString();

                                    map4.put("dishUid", dishUid);

                                    firebaseDatabase.getReference("Bookings").child(bookingUid).child("Menu").child(menuKey).updateChildren(map4);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Toast.makeText(PaymentMethodSelectionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
                catch (Exception ex)
                {
                    Toast.makeText(PaymentMethodSelectionActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(PaymentMethodSelectionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();

        Toast.makeText(this, "Booking Confirmed", Toast.LENGTH_SHORT).show();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("one", "notificationChannel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Gson gson = new Gson();
        String bookingGson = gson.toJson(booking);

        Intent intent = new Intent(PaymentMethodSelectionActivity.this, MyBookingDetailsActivity.class);
        intent.putExtra("bookingGson", bookingGson);

        PendingIntent pendingIntent = PendingIntent.getActivity(PaymentMethodSelectionActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(PaymentMethodSelectionActivity.this, "one")
                .setSmallIcon(R.drawable.city_hall)
                .setContentTitle("Booking")
                .setContentText("Booking Request Placed")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, builder.build());

        startActivity(new Intent(PaymentMethodSelectionActivity.this, CustomerHomePageActivity.class));
        finish();
    }

    private void initializeProperties() {

        totalPriceTV = findViewById(R.id.totalPriceTV);
        paymentByCashTV = findViewById(R.id.paymentByCashTV);
        onlinePaymentTV = findViewById(R.id.onlinePaymentTV);
        nextButton = findViewById(R.id.nextButton);
        paymentOptionRG = findViewById(R.id.paymentOptionsRadioGroup);
        cashRB = findViewById(R.id.cashRadioButton);
        onlinePaymentRB = findViewById(R.id.onlinePaymentRadioButton);
        progressDialog = FancyProgressDialog.createProgressDialog(this);
        progressDialog.dismiss();
        backButton = findViewById(R.id.backButton);

        Gson gson = new Gson();
        booking = gson.fromJson(getIntent().getStringExtra("bookingJson"), Booking.class);
        totalPriceTV.setText("Rs. " + booking.getTotalBill());

        Bundle args = new Bundle();
        args = getIntent().getBundleExtra("bundle");

        if (args != null)
        {
            menuArrayList = (ArrayList<Menu>) args.getSerializable("menuArrayList");
        }
    }

}
