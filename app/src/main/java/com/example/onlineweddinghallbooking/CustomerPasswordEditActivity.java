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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CustomerPasswordEditActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextInputLayout currentPasswordTextInputLayout;
    TextInputLayout newPasswordTextInputLayout;
    Button saveButton;
    ImageView backButton;

    String currentPassword;
    String newPassword;
    String uid;

    User user;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_password_edit);

        initializeFirebase();
        initializeWidgets();

        getCustomerDetails();

        saveButtonListener();
        currentPasswordTextChangeListener();
        newPasswordTextChangeListener();
        backButtonListener();

    }





    private void newPasswordTextChangeListener() {

        newPasswordTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                newPasswordTextInputLayout.setError(null);
            }
        });

    }





    private void backButtonListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startActivity(new Intent(CustomerPasswordEditActivity.this, CustomerProfileActivity.class));
                CustomerPasswordEditActivity.super.onBackPressed();
                finish();
            }
        });

    }






    private void currentPasswordTextChangeListener() {

        currentPasswordTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                currentPasswordTextInputLayout.setError(null);
            }
        });

    }






    private void saveButtonListener() {

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPassword = currentPasswordTextInputLayout.getEditText().getText().toString();
                newPassword = newPasswordTextInputLayout.getEditText().getText().toString();

                if (dataValidation() == true)
                {
                    updateCredentialsInFirebaseAuth();
                }
            }
        });
    }







    private void updateCredentialsInFirebaseAuth() {

        progressDialog.show();

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (!task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Toast.makeText(CustomerPasswordEditActivity.this, "Error Updating Password", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                updateDataInRealTimeDatabase();
                            }
                        }
                    });
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(CustomerPasswordEditActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void updateDataInRealTimeDatabase() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String newPasswordEncrypted = encryptPassword(newPassword);

                HashMap<String, Object> updatedData = new HashMap<>();
                updatedData.put("password", newPasswordEncrypted);
                databaseReference.child("Users").child(uid).updateChildren(updatedData);

                updateLogs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressDialog.dismiss();
                Toast.makeText(CustomerPasswordEditActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateLogs() {

        Date c =  Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        Logs log = new Logs();

        log.modifiedBy = user.getName();
        log.modificationDate = formattedDate;


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String key = databaseReference.child("Users").child(uid).child("Logs").child("Modification Log").push().getKey();

                Map<String, Object> map = new HashMap<>();
                map.put("Modified By", log.getModifiedBy());
                map.put("Modification Date", log.getModificationDate());
                map.put("Old Password", encryptPassword(currentPassword));
                map.put("New Password", encryptPassword(newPassword));
                databaseReference.child("Users").child(uid).child("Logs").child("Modification Log").child(key).updateChildren(map);

                progressDialog.dismiss();

                Toast.makeText(CustomerPasswordEditActivity.this, "Success", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(CustomerPasswordEditActivity.this, CustomerHomePageActivity.class));
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressDialog.dismiss();
                Toast.makeText(CustomerPasswordEditActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    }


    private String encryptPassword(String pass) {

        String  encryptedPassword = "";

        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(pass.getBytes());
            byte[] bytes = m.digest();
            StringBuilder s = new StringBuilder();

            for(int i = 0; i < bytes.length ;i++)
            {
                s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            encryptedPassword = s.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            Toast.makeText(CustomerPasswordEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return encryptedPassword;

    }






    private boolean dataValidation() {

        if (TextUtils.isEmpty(currentPassword) && TextUtils.isEmpty(newPassword))
        {
            currentPasswordTextInputLayout.setError("Required");
            newPasswordTextInputLayout.setError("Required");
            return false;
        }
        else if (TextUtils.isEmpty(currentPassword))
        {
            currentPasswordTextInputLayout.setError("Required");
            return false;
        }
        else if (TextUtils.isEmpty(newPassword))
        {
            newPasswordTextInputLayout.setError("Required");
            return false;
        }
        else
        {
            String encryptedEnteredPassword = encryptPassword(currentPassword);

            if (!user.getPassword().equals(encryptedEnteredPassword))
            {
                currentPasswordTextInputLayout.setError("Invalid Current Password");
                return false;
            }
            else
            {
                if (newPassword.length() < 6)
                {
                    newPasswordTextInputLayout.setError("Password Must Be Greater Than 6 characters");
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
    }





    private void getCustomerDetails() {

        uid = firebaseUser.getUid();

        databaseReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CustomerPasswordEditActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }






    private void initializeWidgets() {

        currentPasswordTextInputLayout = findViewById(R.id.currentPasswordTextInputLayout);
        newPasswordTextInputLayout = findViewById(R.id.newPasswordTextInputLayout);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);

        progressDialog = FancyProgressDialog.createProgressDialog(this);
        progressDialog.dismiss();
    }




    private void initializeFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

    }
}