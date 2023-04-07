package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.media.audiofx.DynamicsProcessing;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StagePricingActivity extends AppCompatActivity {

    private Venue venue;

    private LinearLayout stageListLL;
    private ImageButton addStageButton;
    private Button nextButton;
    private ImageButton backButton;

    private ImageView selectedIV;

    private ProgressDialog progressDialog;

    private ArrayList<ImageView> allImageViews;
    private ArrayList<ImageView> allImageViewsWithUri;
    private ArrayList<Uri> allUris;
    private ArrayList<String> stageNames;
    private ArrayList<String> stagePrices;
    private ArrayList<String> allUrls;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String venueUid;
    private int counter;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_pricing);

        initializeProperties();
        initializeFirebase();

        addStageButtonOnCLickListener();
        nextButtonOnClickListener();
        backButtonOnClickListener();
    }

    private void backButtonOnClickListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StagePricingActivity.super.onBackPressed();
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

    private void nextButtonOnClickListener() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validate = true;

                if (stageListLL.getChildCount() <= 0)
                {
                    Toast.makeText(StagePricingActivity.this, "Add At Least 1 Stage", Toast.LENGTH_SHORT).show();
                    validate = false;
                    return;
                }

                for (int i = 0; i < stageListLL.getChildCount(); i++)
                {
                    if (stageListLL.getChildAt(i) == null)
                    {
                        continue;
                    }
                    else
                    {
                        View addStageView = stageListLL.getChildAt(i);
                        TextInputLayout stageNameTIL = addStageView.findViewById((R.id.stageNameTIL));
                        TextInputLayout stagePriceTIL = addStageView.findViewById(R.id.stagePriceTIL);

                        String stageName = stageNameTIL.getEditText().getText().toString();
                        String stagePrice = stagePriceTIL.getEditText().getText().toString();

                        stageNames.remove(i);
                        stageNames.add(i, stageName);

                        stagePrices.remove(i);
                        stagePrices.add(i, stagePrice);

                        if (allImageViewsWithUri.get(i) == null || stageName.equals("") || stagePrice.equals(""))
                        {
                            Toast.makeText(StagePricingActivity.this, "All Information Is Required ", Toast.LENGTH_SHORT).show();
                            validate = false;
                            break;
                        }
                    }
                }

                if (validate == true)
                {
                    progressDialog.show();

                    for (int i = 0; i < stageListLL.getChildCount(); i++)
                    {
                        if (stageListLL.getChildAt(i) != null)
                        {
                            addImagesInFirebaseStorage(i);
                        }
                    }
                }

            }
        });

    }

    private void addImagesInFirebaseStorage(int index) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);


        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName+index);

        storageReference.putFile(allUris.get(index)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        try
                        {
                            Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();

                            downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String imageURL = downloadUri.getResult().toString();
                                    allUrls.remove(index);
                                    allUrls.add(index, imageURL);

                                    addStageToRealTimeDatabase(index);

                                }
                            });
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(StagePricingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(StagePricingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void addStageToRealTimeDatabase(int index) {

        firebaseDatabase.getReference("Venues").child(venueUid).child("Stages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String key = firebaseDatabase.getReference("Venues").child(venueUid).child("Stages").push().getKey();

                Map<String, Object> map = new HashMap<>();
                map.put("stageImgUrl", allUrls.get(index));
                map.put("stageName", stageNames.get(index));
                map.put("stagePrice", stagePrices.get(index));

                firebaseDatabase.getReference("Venues").child(venueUid).child("Stages").child(key).updateChildren(map);

                counter++;

                if (counter == stageListLL.getChildCount())
                {
                    progressDialog.dismiss();

                    Intent i;

                    if (venue.getCateringAvailable().equalsIgnoreCase("Available"))
                    {
                        i = new Intent(StagePricingActivity.this, SetMenuAndPricingActivity.class);
                        i.putExtra("venueUid", venueUid);
                    }
                    else
                    {
                        Toast.makeText(StagePricingActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        i = new Intent(StagePricingActivity.this, ServiceProviderHomePageActivity.class);
                    }

                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(StagePricingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addStageButtonOnCLickListener() {

        addStageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView();

            }
        });

    }

    private void addView() {

        View addStageView = getLayoutInflater().inflate(R.layout.add_stage_card_view, null, false);

        ImageView stageImage = addStageView.findViewById(R.id.stageImageView);
        Button chooseImageButton = addStageView.findViewById(R.id.thumbnailChooseBtn);
        ImageButton closeButton = addStageView.findViewById(R.id.closeButton);

        stageListLL.addView(addStageView);
        allImageViews.add(stageImage);
        allImageViewsWithUri.add(null);
        allUris.add(null);
        stageNames.add(null);
        stagePrices.add(null);
        allUrls.add(null);


        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedIV = stageImage;
                selectImage();

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeView(addStageView, stageImage);

            }
        });

    }

    private void removeView(View stageView, ImageView stageImage) {

        int index = stageListLL.indexOfChild(stageView);
        stageListLL.removeView(stageView);

        allImageViews.remove(index);
        allImageViewsWithUri.remove(index);
        stageNames.remove(index);
        stagePrices.remove(index);
        allUrls.remove(index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null)
        {
            int index = allImageViews.indexOf(selectedIV);

            Uri imageUri = data.getData();
            selectedIV.setImageURI(imageUri);

            allImageViewsWithUri.remove(index);
            allImageViewsWithUri.add(index, selectedIV);

            allUris.remove(index);
            allUris.add(index, imageUri);
        }

    }

    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);


    }


    private void initializeProperties() {

        allImageViews = new ArrayList<>();
        allImageViewsWithUri = new ArrayList<>();
        allUris = new ArrayList<>();
        stageNames = new ArrayList<>();
        stagePrices = new ArrayList<>();
        allUrls = new ArrayList<>();

        stageListLL = findViewById(R.id.stageListLL);
        addStageButton = findViewById(R.id.addStageIB);
        nextButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);

        progressDialog = FancyProgressDialog.createProgressDialog(this);
        progressDialog.dismiss();

        Gson gson = new Gson();
        venue = gson.fromJson(getIntent().getStringExtra("venue"), Venue.class);

        venueUid = getIntent().getStringExtra("venueUid");

        counter = 0;
    }
}