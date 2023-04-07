package com.example.onlineweddinghallbooking;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {

                DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                databaseHelper.truncateTable();

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);

            }
        }, 1000);


    }
}