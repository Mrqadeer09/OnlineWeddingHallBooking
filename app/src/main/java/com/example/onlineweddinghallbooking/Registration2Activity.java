package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Registration2Activity extends AppCompatActivity {


    TextInputLayout nameLayout;
    TextInputEditText nameInput;

    TextInputLayout phoneNoLayout;
    TextInputEditText phoneNoInput;

    TextInputLayout genderDropDownLayout;
    AutoCompleteTextView genderDropDownItems;

    TextInputLayout registerAsDropDownLayout;
    AutoCompleteTextView registerAsDropDownItems;

    TextInputLayout yearOfBirthLayout;
    TextInputEditText yearOfBirthInput;

    TextInputLayout monthOfBirthDropDownLayout;
    AutoCompleteTextView monthOfBirthDropDownItems;

    TextInputLayout dayOfBirthLayout;
    TextInputEditText dayOfBirthInput;

    Button submitButton;

    User user;

    int count = 0;

    String name;
    String gender;
    String phoneNo;
    String registerAs;
    String day;
    String month;
    String year;

    String[] genders;
    String[] roles;
    String[] months;

    Roles[] rolesArray;

    boolean validateSignup;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);


        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        nameLayout = findViewById(R.id.nameTextInputLayout);
        nameInput = findViewById(R.id.nameTextInput);

        genderDropDownItems = findViewById(R.id.genderDropDownInput);
        genderDropDownLayout = findViewById(R.id.genderDropDown);

        phoneNoLayout = findViewById(R.id.phoneNoTextInputLayout);
        phoneNoInput = findViewById(R.id.phoneNoTextInput);

        registerAsDropDownLayout = findViewById(R.id.registerAsDropDown);
        registerAsDropDownItems = findViewById(R.id.registerAsDropDownInput);

        yearOfBirthLayout = findViewById(R.id.yearOfBirthInputLayout);
        yearOfBirthInput = findViewById(R.id.yearOfBirtTextInput);

        monthOfBirthDropDownLayout = findViewById(R.id.monthDropDown);
        monthOfBirthDropDownItems = findViewById(R.id.monthDropDownInput);

        dayOfBirthLayout = findViewById(R.id.dayOfBirthInputLayout);
        dayOfBirthInput = findViewById(R.id.dayOfBirtTextInput);


        submitButton = findViewById(R.id.submitButton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Creating Account");
        progressDialog.setCanceledOnTouchOutside(false);

        roles = getResources().getStringArray(R.array.register_as_array);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.dropdown_item, roles);
        registerAsDropDownItems.setAdapter(adapter2);

        months = getResources().getStringArray(R.array.months);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, R.layout.dropdown_item, months);
        monthOfBirthDropDownItems.setAdapter(adapter3);

        genders = getResources().getStringArray(R.array.gender_names);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, genders);
        genderDropDownItems.setAdapter(adapter);

        Gson gson = new Gson();
        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateSignup = true;

                name = nameLayout.getEditText().getText().toString();
                gender = genderDropDownItems.getText().toString();
                phoneNo = phoneNoLayout.getEditText().getText().toString();
                registerAs = registerAsDropDownItems.getText().toString();
                day = dayOfBirthLayout.getEditText().getText().toString();
                month = monthOfBirthDropDownItems.getText().toString();
                year = yearOfBirthLayout.getEditText().getText().toString();

                validateData();

            }
        });


        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nameLayout.setError(null);
            }
        });

        phoneNoInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phoneNoLayout.setError(null);
            }
        });

        dayOfBirthInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dayOfBirthLayout.setError(null);
            }
        });

        yearOfBirthInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                yearOfBirthLayout.setError(null);
            }
        });

//        try
//        {
//            getRolesFromDatabase();
//        }
//        catch (Exception ex)
//        {
//            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }

    }

    private void getRolesFromDatabase() {

            databaseReference.child("Roles").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        try
                        {
                            Roles role = dataSnapshot.getValue(Roles.class);
                            setRolesInComboBoxArray(role);
                        }
                        catch (Exception ex)
                        {
                            Toast.makeText(Registration2Activity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(Registration2Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void setRolesInComboBoxArray(Roles role) {

        try
        {
            if (!role.getType().equalsIgnoreCase("admin"))
            {
                genders[count] = role.getType();
                rolesArray[count] = role;

                count++;
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        setRolesInComboBox();

    }

    private void setRolesInComboBox() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, genders);
        genderDropDownItems.setAdapter(adapter);
    }

    private void validateData() {



        if (TextUtils.isEmpty(name)){

            validateSignup = false;
            nameLayout.setError("Required");
        }
        else
        {
            char[] nameInChar = name.toCharArray();

            for (char c : nameInChar)
            {
                if (!(Character.isLetter(c) || Character.isSpaceChar(c)))
                {
                    validateSignup = false;
                    nameLayout.setError("Name Cannot Contain Numbers");
                    break;
                }
            }
        }

        if (TextUtils.isEmpty(gender)){

            validateSignup = false;
            genderDropDownLayout.setError("Required");
        }

        if (TextUtils.isEmpty(phoneNo)){

            validateSignup = false;
            phoneNoLayout.setError("Required");
        }
        else if (phoneNo.length() != 11) {

            validateSignup = false;
            phoneNoLayout.setError("Invalid Phone No.");
        }
        else if (!phoneNo.substring(0, 2).equals("03")) {

            validateSignup = false;
            phoneNoLayout.setError("Invalid Phone No.");
        }

        if (TextUtils.isEmpty(registerAs)){

            validateSignup = false;
            registerAsDropDownLayout.setError("Required");
        }

        if (TextUtils.isEmpty(day)){

            validateSignup = false;
            dayOfBirthLayout.setError("Required");
        }
        else if (Integer.valueOf(day) > 31 || Integer.valueOf(day) < 1) {

            validateSignup = false;
            dayOfBirthLayout.setError("Invalid Day");
        }

        if (TextUtils.isEmpty(month)){

            validateSignup = false;
            monthOfBirthDropDownLayout.setError("Required");
        }

        if (TextUtils.isEmpty(year)){

            validateSignup = false;
            yearOfBirthLayout.setError("Required");
        }

        if (Integer.valueOf(day) > 31 || Integer.valueOf(day) < 1)
        {
            dayOfBirthLayout.setError("Invalid Date");
            validateSignup = false;
        }
        else if ((month.equalsIgnoreCase("Jan") || month.equalsIgnoreCase("March") || month.equalsIgnoreCase("May") ||
        month.equalsIgnoreCase("July") || month.equalsIgnoreCase("August") || month.equalsIgnoreCase("Oct") ||
                month.equalsIgnoreCase("Dec")) && (Integer.valueOf(day) > 31))
        {
            dayOfBirthLayout.setError("Invalid Date");
            validateSignup = false;
        }
        else if (month.equalsIgnoreCase("Feb") && Integer.valueOf(day) > 29)
        {
            dayOfBirthLayout.setError("Invalid Date");
            validateSignup = false;
        }
        else if ((month.equalsIgnoreCase("April") || month.equalsIgnoreCase("June") || month.equalsIgnoreCase("Sept") ||
                month.equalsIgnoreCase("Nov")) && (Integer.valueOf(day) > 30))
        {
            dayOfBirthLayout.setError("Invalid Date");
            validateSignup = false;
        }


        if (validateSignup) {

            firebaseSignUp();

        }

    }

    private void firebaseSignUp() {

        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        String dateOfBirth =  day + "/" + month + "/" + year;

                        Date c =  Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        String formattedDate = df.format(c);

                        Logs log = new Logs();

                        if (registerAs.equals(roles[0]))
                        {

                            Customer customer = new Customer();
                            customer.setName(name);
                            customer.setUsername(user.getUsername());
                            customer.setGender(gender);
                            customer.setPhoneNo(phoneNo);
                            customer.setEmail(user.getEmail());
                            customer.setDateOfBirth(dateOfBirth);
                            customer.setRole(registerAs);


                            try {
                                MessageDigest m = MessageDigest.getInstance("MD5");
                                m.update(user.getPassword().getBytes());
                                byte[] bytes = m.digest();
                                StringBuilder s = new StringBuilder();

                                for(int i = 0; i < bytes.length ;i++)
                                {
                                    s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                                }
                                String  encryptedPassword = s.toString();

                                customer.setPassword(encryptedPassword);
                            }
                            catch (NoSuchAlgorithmException e)
                            {
                                Toast.makeText(Registration2Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }



                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = firebaseUser.getUid();

                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    databaseReference.child("Users").child(uid).setValue(customer);


                                    log.setCreatedBy(customer.getName());
                                    log.setCreationDate(formattedDate);
                                    log.setStatus("Active");;
                                    log.setLockVersion("0");

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("Created By", log.getCreatedBy());
                                    map.put("Creation Date", log.getCreationDate());
                                    databaseReference.child("Users").child(uid).child("Logs").child("Creation Log").updateChildren(map);


                                    Map<String, Object> map2 = new HashMap<>();
                                    map2.put("Status", log.getStatus());
                                    databaseReference.child("Users").child(uid).child("Logs").child("Status Log").updateChildren(map2);


                                    Map<String, Object> map3 = new HashMap<>();
                                    map3.put("Lock Version", log.getLockVersion());
                                    databaseReference.child("Users").child(uid).child("Logs").child("Lock Version Log").updateChildren(map3);



                                    Toast.makeText(Registration2Activity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();

                                    Intent i = new Intent(Registration2Activity.this, CustomerHomePageActivity.class);
                                    i.putExtra("uid", uid);
                                    startActivity(i);

                                    progressDialog.dismiss();


                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                    progressDialog.dismiss();
                                    Toast.makeText(Registration2Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                        else {

                            ServiceProvider serviceProvider = new ServiceProvider();
                            serviceProvider.setVerified("false");
                            serviceProvider.setName(name);
                            serviceProvider.setUsername(user.getUsername());
                            serviceProvider.setGender(gender);
                            serviceProvider.setPhoneNo(phoneNo);
                            serviceProvider.setEmail(user.getEmail());
                            serviceProvider.setDateOfBirth(dateOfBirth);
                            serviceProvider.setRole(registerAs);


                            try {
                                MessageDigest m = MessageDigest.getInstance("MD5");
                                m.update(user.getPassword().getBytes());
                                byte[] bytes = m.digest();
                                StringBuilder s = new StringBuilder();

                                for(int i = 0; i < bytes.length ;i++)
                                {
                                    s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                                }
                                String  encryptedPassword = s.toString();

                                serviceProvider.setPassword(encryptedPassword);
                            }
                            catch (NoSuchAlgorithmException e)
                            {
                                Toast.makeText(Registration2Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }



                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = firebaseUser.getUid();

                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    databaseReference.child("Users").child(uid).setValue(serviceProvider);

                                    log.setCreatedBy(serviceProvider.getName());
                                    log.setCreationDate(formattedDate);
                                    log.setStatus("Active");
                                    log.setLockVersion("0");

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("Created By", log.getCreatedBy());
                                    map.put("Creation Date", log.getCreationDate());
                                    databaseReference.child("Users").child(uid).child("Logs").child("Creation Log").updateChildren(map);

                                    Map<String, Object> map2 = new HashMap<>();
                                    map2.put("Status", log.getStatus());
                                    databaseReference.child("Users").child(uid).child("Logs").child("Status Log").updateChildren(map2);


                                    Map<String, Object> map3 = new HashMap<>();
                                    map3.put("Lock Version", log.getLockVersion());
                                    databaseReference.child("Users").child(uid).child("Logs").child("Lock Version Log").updateChildren(map3);


                                    progressDialog.dismiss();
                                    Toast.makeText(Registration2Activity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();

                                    Gson gson = new Gson();
                                    String serviceProviderJson = gson.toJson(serviceProvider);

                                    Gson gson2 = new Gson();
                                    String logsJson = gson.toJson(log);

                                    Intent i = new Intent(Registration2Activity.this, ServiceProviderVerificationActivity.class);
                                    i.putExtra("log", logsJson);
                                    i.putExtra("user", serviceProviderJson);
                                    i.putExtra("uid", uid);
                                    startActivity(i);

                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                    progressDialog.dismiss();
                                    Toast.makeText(Registration2Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Toast.makeText(Registration2Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }
}