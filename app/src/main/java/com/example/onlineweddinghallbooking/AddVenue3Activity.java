package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddVenue3Activity extends AppCompatActivity {


    private Venue venue;
    private ServiceProvider serviceProvider;

    private int imgNo;
    private int counter = 0;

    private ImageView thumbnailIV;
    private ImageView extraImageOneIV;
    private ImageView extraImageTwoIV;
    private ImageView extraImageThreeIV;
    private Button thumbnailImageSelectBtn;
    private Button moreImageSelectBtnOne;
    private Button moreImageSelectBtnTwo;
    private Button moreImageSelectBtnThree;
    private Button saveBtn;
    private ImageButton backBtn;

    private Uri[] filePaths = new Uri[4];
    private String[] imageUrls = new String[4];

    private StorageReference storageReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venue3);


        initializeFirebase();
        initializeProperties();


        thumbnailImageSelectBtnOnClickListener();
        moreImageSelectBtnOneOnClickListener();
        moreImageSelectBtnTwoOnClickListener();
        moreImageSelectBtnThreeOnClickListener();
        saveBtnOnClickListener();
        backButtonOnClickListener();

    }

    private void backButtonOnClickListener() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddVenue3Activity.super.onBackPressed();
                finish();
            }
        });

    }

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void saveBtnOnClickListener() {

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (filePaths[0] == null || filePaths[1] == null || filePaths[2] == null || filePaths[3] == null)
                {
                    Toast.makeText(AddVenue3Activity.this, "All Images Are Required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    addVenueToDatabase();
                }

            }
        });
    }

    private void addVenueToDatabase() {

        progressDialog.show();

        firebaseDatabase.getReference("Venues").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String key = firebaseDatabase.getReference("Venues").push().getKey();

                venue.setUid(key);

                firebaseDatabase.getReference("Venues").child(key).setValue(venue);

                addVenueReferenceToServiceProviderCollection(key);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AddVenue3Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addVenueReferenceToServiceProviderCollection(String key) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseDatabase.getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String venueRefKey = firebaseDatabase.getReference("Users").child(uid).child("Venues").push().getKey();

                Map<String, Object> map = new HashMap<>();
                map.put("VenueRef", key);

                databaseReference.child("Users").child(uid).child("Venues").child(venueRefKey).updateChildren(map);

                addImagesToFirebaseStorage(key);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AddVenue3Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addImagesToFirebaseStorage(String key) {

        for (int i = 0; i < filePaths.length; i++)
        {
            if (filePaths[i] != null)
            {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
                Date now = new Date();
                String fileName = formatter.format(now);


                storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName+i);

                int finalI = i;

                storageReference.putFile(filePaths[i])
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                try
                                {
                                    Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();

                                    downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            String gFP = downloadUri.getResult().toString();

                                            imageUrls[finalI] = gFP;

                                            addImagesToVenue(key, finalI);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(AddVenue3Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                catch (Exception ex)
                                {
                                    Toast.makeText(AddVenue3Activity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(AddVenue3Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
            }

        }

    }

    private void addImagesToVenue(String key, int i) {

            if (i == 0)
            {
                int finalI = i;

                firebaseDatabase.getReference("Venues").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Map<String, Object> map = new HashMap<>();
                        map.put("thumbnailImage", imageUrls[finalI]);

                        databaseReference.child("Venues").child(key).updateChildren(map);

                        updateCounter(key);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(AddVenue3Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                if (filePaths[i] != null)
                {
                    int finalI1 = i;

                    firebaseDatabase.getReference("Venues").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String imgKey = firebaseDatabase.getReference("Venues").child(key).child("images").push().getKey();

                            Map<String, Object> map = new HashMap<>();
                            map.put("img", imageUrls[finalI1]);

                            firebaseDatabase.getReference("Venues").child(key).child("images").child(imgKey).updateChildren(map);

                            updateCounter(key);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(AddVenue3Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }



    }

    private void updateCounter(String key) {

        counter++;

        if (counter == 4)
        {
            updateVenueLogs(key);
        }

    }

    private void updateVenueLogs(String key) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Date c =  Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        firebaseDatabase.getReference("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                serviceProvider = snapshot.getValue(ServiceProvider.class);

                Logs log = new Logs();
                log.setCreatedBy(serviceProvider.getName());
                log.setCreationDate(formattedDate);
                log.setStatus("Active");
                log.setLockVersion("0");

                Map<String, Object> map = new HashMap<>();
                map.put("Created By", log.getCreatedBy());
                map.put("Creation Date", log.getCreationDate());
                databaseReference.child("Venues").child(key).child("Logs").child("Creation Log").updateChildren(map);

                Map<String, Object> map2 = new HashMap<>();
                map2.put("Status", log.getStatus());
                databaseReference.child("Venues").child(key).child("Logs").child("Status Log").updateChildren(map2);


                Map<String, Object> map3 = new HashMap<>();
                map3.put("Lock Version", log.getLockVersion());
                databaseReference.child("Venues").child(key).child("Logs").child("Lock Version Log").updateChildren(map3);

                progressDialog.dismiss();


                Gson gson = new Gson();
                String venueJson = gson.toJson(venue);

                Intent i = new Intent(AddVenue3Activity.this, DecorationPricingActivity.class);
                i.putExtra("venue", venueJson);
                i.putExtra("venueUid", key);
                startActivity(i);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AddVenue3Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void moreImageSelectBtnThreeOnClickListener() {

        moreImageSelectBtnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgNo = 3;
                selectImage();

            }
        });

    }

    private void moreImageSelectBtnTwoOnClickListener() {

        moreImageSelectBtnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgNo = 2;
                selectImage();

            }
        });

    }

    private void moreImageSelectBtnOneOnClickListener() {

        moreImageSelectBtnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgNo = 1;
                selectImage();

            }
        });

    }

    private void thumbnailImageSelectBtnOnClickListener() {

        thumbnailImageSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgNo = 0;
                selectImage();

            }
        });

    }

    private void initializeProperties() {

        thumbnailIV = findViewById(R.id.thumbnailImageView);
        extraImageOneIV = findViewById(R.id.imageViewOne);
        extraImageTwoIV = findViewById(R.id.imageViewTwo);
        extraImageThreeIV = findViewById(R.id.imageViewThree);
        thumbnailImageSelectBtn = findViewById(R.id.thumbnailChooseBtn);
        moreImageSelectBtnOne = findViewById(R.id.btnChooseOne);
        moreImageSelectBtnTwo = findViewById(R.id.btnChooseTwo);
        moreImageSelectBtnThree = findViewById(R.id.btnChooseThree);
        saveBtn = findViewById(R.id.btnSave);
        backBtn = findViewById(R.id.backButton);

        Gson gson = new Gson();
        venue = gson.fromJson(getIntent().getStringExtra("venue"), Venue.class);
        venue.setOwnerUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        venue.setThumbnailImage("");

//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Please Wait");
//        progressDialog.setMessage("Creating Venue");
//        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog = FancyProgressDialog.createProgressDialog(this);
        progressDialog.dismiss();

    }


    private void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null)
        {
            filePaths[imgNo] = data.getData();

            if (imgNo == 0)
            {
                thumbnailIV.setImageURI(filePaths[imgNo]);
            }
            else if (imgNo == 1)
            {
                extraImageOneIV.setImageURI(filePaths[imgNo]);
            }
            else if (imgNo == 2)
            {
                extraImageTwoIV.setImageURI(filePaths[imgNo]);
            }
            else if (imgNo == 3)
            {
                extraImageThreeIV.setImageURI(filePaths[imgNo]);
            }
        }
    }

}