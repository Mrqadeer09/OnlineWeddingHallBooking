package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
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
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CustomerEmailEditActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextInputLayout emailTextInputLayout;
    TextInputLayout currentPasswordTextInputLayout;
    Button saveButton;
    ImageView backButton;

    String newEmail;
    String currentPassword;
    String uid;

    User user;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_email_edit);


        initializeFirebase();
        initializeWidgets();

        getCustomerDetails();

        saveButtonListener();
        emailTextChangeListener();
        passwordTextChangeListener();
        backButtonListener();
        
    }

    private void passwordTextChangeListener() {

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


    private void backButtonListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startActivity(new Intent(CustomerEmailEditActivity.this, CustomerProfileActivity.class));
                CustomerEmailEditActivity.super.onBackPressed();
                finish();
            }
        });

    }





    private void emailTextChangeListener() {

        emailTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                emailTextInputLayout.setError(null);
            }
        });

    }




    private void saveButtonListener() {

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newEmail = emailTextInputLayout.getEditText().getText().toString();
                currentPassword = currentPasswordTextInputLayout.getEditText().getText().toString();

                if (dataValidation() == true)
                {
                    updateEmailInFirebaseAuth();
                }
            }
        });
    }




    private void updateEmailInFirebaseAuth() {

        progressDialog.show();

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {

                    firebaseAuth.fetchSignInMethodsForEmail(newEmail)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    boolean doesEmailExist = !task.getResult().getSignInMethods().isEmpty();

                                    if (doesEmailExist)
                                    {
                                        progressDialog.dismiss();
                                        emailTextInputLayout.setError("This Email Is Already Linked With Another Account");
                                    }
                                    else
                                    {

                                        firebaseUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful())
                                                {
                                                    updateDataInDatabase();
                                                }
                                                else
                                                {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(CustomerEmailEditActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }

                                }
                            });

                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(CustomerEmailEditActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }






    private void updateDataInDatabase() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, Object> updatedData = new HashMap<>();
                updatedData.put("email", newEmail);
                databaseReference.child("Users").child(uid).updateChildren(updatedData);

                updateLogs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressDialog.dismiss();
                Toast.makeText(CustomerEmailEditActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                map.put("Old Email", user.getEmail());
                map.put("Updated Email", newEmail);
                databaseReference.child("Users").child(uid).child("Logs").child("Modification Log").child(key).updateChildren(map);

                progressDialog.dismiss();

                Toast.makeText(CustomerEmailEditActivity.this, "Success", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(CustomerEmailEditActivity.this, CustomerHomePageActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressDialog.dismiss();
                Toast.makeText(CustomerEmailEditActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private boolean dataValidation() {

        if (TextUtils.isEmpty(this.newEmail))
        {
            emailTextInputLayout.setError("Required");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches())
        {
            emailTextInputLayout.setError("Incorrect Email Format");
            return false;
        }
        else if (newEmail.equals(user.getEmail()))
        {
            return false;
        }

        if (TextUtils.isEmpty(this.currentPassword))
        {
            currentPasswordTextInputLayout.setError("Required");
            return false;
        }

        return true;
    }






    private void setCustomerDetails(String email) {

        emailTextInputLayout.getEditText().setText(email);
    }





    private void getCustomerDetails() {

        uid = firebaseUser.getUid();

        databaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user = snapshot.getValue(User.class);
                setCustomerDetails(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CustomerEmailEditActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }






    private void initializeWidgets() {

        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        currentPasswordTextInputLayout = findViewById(R.id.currentPasswordTextInputLayout);
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