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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {


    TextInputLayout emailLayout;
    TextInputEditText emailInput;

    TextInputLayout passwordLayout;
    TextInputEditText passwordInput;

    TextView forgotPasswordTextView;
    TextView signupTextView;
    Button signInButton;

    String email;
    String password;

    boolean validationFlag;

    ProgressDialog progressDialog;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;


    Logs logs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLayout = findViewById(R.id.emailTextInputLayout);
        emailInput = findViewById(R.id.emailTextInput);

        passwordLayout = findViewById(R.id.passwordTextInputLayout);
        passwordInput = findViewById(R.id.passwordTextInput);

        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        signInButton = findViewById(R.id.loginButton);

        signupTextView = findViewById(R.id.signupTextView);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Logging In");
        progressDialog.setCanceledOnTouchOutside(false);


        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();




        checkUser();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validationFlag = true;

                email = emailLayout.getEditText().getText().toString();
                password = passwordLayout.getEditText().getText().toString();

                validateCredentials();
            }
        });


        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, Registration1Activity.class));
            }
        });



        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });


        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                email = emailLayout.getEditText().getText().toString();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                    validationFlag = false;
                    emailLayout.setError("Incorrect Email Format");
                }
                else
                {
                    emailLayout.setError(null);
                }
            }
        });

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                passwordLayout.setError(null);
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(true);
            return true;
        }

        return false;
    }

    private void checkUser() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {

            redirect();

            finish();
        }
    }

    private void redirect() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();

        databaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if (user.getRole().equals("Customer"))
                {

                    startActivity(new Intent(LoginActivity.this, CustomerHomePageActivity.class));

                }
                else
                {
                    ServiceProvider serviceProvider = snapshot.getValue(ServiceProvider.class);

                    if (serviceProvider.getVerified().equals("false"))
                    {
                        logs = new Logs();

                        databaseReference.child("Users").child(uid).child("Logs").child("Creation Log").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                logs = snapshot.getValue(Logs.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        Gson gson = new Gson();
                        String serviceProviderJson = gson.toJson(serviceProvider);

                        Gson gson2 = new Gson();
                        String logsJson = gson.toJson(logs);

                        Intent i = new Intent(LoginActivity.this, ServiceProviderVerificationActivity.class);
                        i.putExtra("log", logsJson);
                        i.putExtra("user", serviceProviderJson);
                        i.putExtra("uid", uid);
                        startActivity(i);

                    }
                    else
                    {
                        startActivity(new Intent(LoginActivity.this, ServiceProviderHomePageActivity.class));

                    }
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        
    }

    private void validateCredentials() {

        if(TextUtils.isEmpty(email))
        {
            emailLayout.setError("Email Is Required");
            validationFlag = false;

        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            emailLayout.setError("Incorrect Email Format");
            validationFlag = false;
        }

        if(TextUtils.isEmpty(password))
        {
            passwordLayout.setError("Password Is Required");
            validationFlag = false;
        }

        if(validationFlag == true)
        {
            validateUserFromDatabase();
        }

    }

    private void validateUserFromDatabase() {

        progressDialog.show();


        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        progressDialog.dismiss();

                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        redirect();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Invalid Email Or Password", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}