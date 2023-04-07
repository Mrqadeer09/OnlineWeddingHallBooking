package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SetMenuAndPricingActivity extends AppCompatActivity {

    private LinearLayout menuListLL;
    private ImageButton addMenuButton;
    private Button saveButton;

    private ImageView selectedIV;

    private ProgressDialog progressDialog;

    private ArrayList<ImageView> allImageViews;
    private ArrayList<ImageView> allImageViewsWithUri;
    private ArrayList<Uri> allUris;
    private ArrayList<String> dishNames;
    private ArrayList<String> dishPrices;
    private ArrayList<String> dishTypes;
    private ArrayList<String> allUrls;
    private String[] menuTypes;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String venueUid;
    private int counter;
    private boolean validate;
    private boolean menuTypeOneValidation;
    private boolean menuTypeTwoValidation;
    private boolean menuTypeThreeValidation;
    private boolean menuTypeFourValidation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_menu_and_pricing);

        initializeProperties();
        initializeFirebase();

        addMenuButtonOnCLickListener();
        saveButtonOnClickListener();
    }

    private void saveButtonOnClickListener() {

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validate = true;
                menuTypeOneValidation = false;
                menuTypeTwoValidation = false;
                menuTypeThreeValidation = false;

                if (menuListLL.getChildCount() <= 0)
                {
                    Toast.makeText(SetMenuAndPricingActivity.this, "Add At Least One Dish In All Types", Toast.LENGTH_SHORT).show();
                    validate = false;
                    return;
                }
                else
                {
                    for (int i = 0; i < menuListLL.getChildCount(); i++)
                    {
                        View addStageView = menuListLL.getChildAt(i);
                        AutoCompleteTextView dishTypeACTV = addStageView.findViewById((R.id.dishTypeDropDownInput));
                        String dishType = dishTypeACTV.getText().toString();

                        if (dishType.equalsIgnoreCase("Drinks"))
                        {
                            menuTypeOneValidation = true;
                        }
                        else if (dishType.equalsIgnoreCase("Starters"))
                        {
                            menuTypeTwoValidation = true;
                        }
                        else if (dishType.equalsIgnoreCase("Main Course"))
                        {
                            menuTypeThreeValidation = true;
                        }
                        else if (dishType.equalsIgnoreCase("Dessert"))
                        {
                            menuTypeFourValidation = true;
                        }
                    }

                    if (menuTypeOneValidation != true || menuTypeTwoValidation != true || menuTypeThreeValidation != true || menuTypeFourValidation != true)
                    {
                        Toast.makeText(SetMenuAndPricingActivity.this, "Add At Least One Item In All Types", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                for (int i = 0; i < menuListLL.getChildCount(); i++)
                {
                    if (menuListLL.getChildAt(i) == null)
                    {
                        continue;
                    }
                    else
                    {
                        View addStageView = menuListLL.getChildAt(i);
                        TextInputLayout stageNameTIL = addStageView.findViewById((R.id.stageNameTIL));
                        TextInputLayout stagePriceTIL = addStageView.findViewById(R.id.stagePriceTIL);
                        AutoCompleteTextView dishTypeACTV = addStageView.findViewById((R.id.dishTypeDropDownInput));

                        String stageName = stageNameTIL.getEditText().getText().toString();
                        String stagePrice = stagePriceTIL.getEditText().getText().toString();
                        String dishType = dishTypeACTV.getText().toString();

                        dishNames.remove(i);
                        dishNames.add(i, stageName);

                        dishPrices.remove(i);
                        dishPrices.add(i, stagePrice);

                        dishTypes.remove(i);
                        dishTypes.add(i, dishType);

                        if (allImageViewsWithUri.get(i) == null || stageName.equals("") || stagePrice.equals("") || dishType.equals(""))
                        {
                            Toast.makeText(SetMenuAndPricingActivity.this, "All Information Is Required ", Toast.LENGTH_SHORT).show();
                            validate = false;
                            break;
                        }
                    }
                }

                if (validate == true)
                {
                    progressDialog.show();

                    for (int i = 0; i < menuListLL.getChildCount(); i++)
                    {
                        if (menuListLL.getChildAt(i) != null)
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

                                    addMenuToRealTimeDatabase(index);

                                }
                            });
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(SetMenuAndPricingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SetMenuAndPricingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void addMenuToRealTimeDatabase(int index) {

        firebaseDatabase.getReference("Venues").child(venueUid).child("Menu").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String key = firebaseDatabase.getReference("Venues").child(venueUid).child("Menu").push().getKey();

                Map<String, Object> map = new HashMap<>();
                map.put("dishImgUrl", allUrls.get(index));
                map.put("dishName", dishNames.get(index));
                map.put("dishPrice", dishPrices.get(index));
                map.put("dishType", dishTypes.get(index));

                firebaseDatabase.getReference("Venues").child(venueUid).child("Menu").child(key).updateChildren(map);

                counter++;

                if (counter == menuListLL.getChildCount())
                {
                    progressDialog.dismiss();
                    Toast.makeText(SetMenuAndPricingActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SetMenuAndPricingActivity.this, ServiceProviderHomePageActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(SetMenuAndPricingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void addMenuButtonOnCLickListener() {

        addMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView();

            }
        });

    }

    private void addView() {

        View addMenuView = getLayoutInflater().inflate(R.layout.add_menu_card_view, null, false);

        ImageView dishImage = addMenuView.findViewById(R.id.menuImageView);
        Button chooseImageButton = addMenuView.findViewById(R.id.imgChooseBtn);
        ImageButton closeButton = addMenuView.findViewById(R.id.closeButton);
        AutoCompleteTextView menuTypeDD = addMenuView.findViewById(R.id.dishTypeDropDownInput);

        menuTypes = getResources().getStringArray(R.array.menu_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, menuTypes);
        menuTypeDD.setAdapter(adapter);

        menuListLL.addView(addMenuView);
        allImageViews.add(dishImage);
        allImageViewsWithUri.add(null);
        allUris.add(null);
        dishNames.add(null);
        dishPrices.add(null);
        dishTypes.add(null);
        allUrls.add(null);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedIV = dishImage;
                selectImage();

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeView(addMenuView, dishImage);

            }
        });

    }

    private void removeView(View stageView, ImageView stageImage) {

        int index = menuListLL.indexOfChild(stageView);
        menuListLL.removeView(stageView);

        allImageViews.remove(index);
        allImageViewsWithUri.remove(index);
        dishNames.remove(index);
        dishPrices.remove(index);
        dishTypes.remove(index);
        allUrls.remove(index);
        allUris.remove(index);

    }

    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);

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

    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void initializeProperties() {

        allImageViews = new ArrayList<>();
        allImageViewsWithUri = new ArrayList<>();
        allUris = new ArrayList<>();
        dishNames = new ArrayList<>();
        dishPrices = new ArrayList<>();
        dishTypes = new ArrayList<>();
        allUrls = new ArrayList<>();

        menuListLL = findViewById(R.id.stageListLL);
        addMenuButton = findViewById(R.id.addMenuIB);
        saveButton = findViewById(R.id.saveButton);

        progressDialog = FancyProgressDialog.createProgressDialog(this);
        progressDialog.dismiss();

        venueUid = getIntent().getStringExtra("venueUid");

        counter = 0;

        validate = false;
        menuTypeOneValidation = false;
        menuTypeTwoValidation = false;
        menuTypeThreeValidation = false;

    }
}