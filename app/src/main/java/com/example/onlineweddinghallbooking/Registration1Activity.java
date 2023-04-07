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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.regex.Pattern;

public class Registration1Activity extends AppCompatActivity {

    TextInputLayout usernameLayout;
    TextInputEditText usernameInput;

    TextInputLayout emailLayout;
    TextInputEditText emailInput;

    TextInputLayout passwordLayout;
    TextInputEditText passwordInput;

    TextInputLayout rePasswordLayout;
    TextInputEditText rePasswordInput;

    Button submitButton;

    String username;
    String email;
    String password;
    String rePassword;

    boolean validateSignup;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }



        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();



        usernameLayout = findViewById(R.id.usernameTextInputLayout);
        usernameInput = findViewById(R.id.usernameTextInput);

        emailLayout = findViewById(R.id.emailTextInputLayout);
        emailInput = findViewById(R.id.emailTextInput);

        passwordLayout = findViewById(R.id.passwordTextInputLayout);
        passwordInput = findViewById(R.id.passwordTextInput);

        rePasswordLayout = findViewById(R.id.rePasswordTextInputLayout);
        rePasswordInput = findViewById(R.id.rePasswordTextInput);

        submitButton = findViewById(R.id.submitButton);




        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Creating Account");
        progressDialog.setCanceledOnTouchOutside(false);



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateSignup = true;

                username = usernameLayout.getEditText().getText().toString();
                email = emailLayout.getEditText().getText().toString();
                password = passwordLayout.getEditText().getText().toString();
                rePassword = rePasswordLayout.getEditText().getText().toString();

                validateData();

            }
        });


        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                usernameLayout.setError(null);
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

                    validateSignup = false;
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

        rePasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                rePasswordLayout.setError(null);
            }
        });

    }

    private void validateData() {

        String usernameValidationRegex = "^[A-Za-z]\\w{0,29}$";

        if (TextUtils.isEmpty(username)){

            validateSignup = false;
            usernameLayout.setError("Required");
        }
        else if (!Pattern.compile(usernameValidationRegex).matcher(username).matches())
        {
            validateSignup = false;
            usernameLayout.setError("Invalid Username");
        }
        else if (username.length() < 6)
        {
            validateSignup = false;
            usernameLayout.setError("Username Should Contain At Least 6 Characters");
        }


        if (TextUtils.isEmpty(email)){

            validateSignup = false;
            emailLayout.setError("Required");
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            validateSignup = false;
            emailLayout.setError("Incorrect Email Format");
        }


        if (TextUtils.isEmpty(password) && TextUtils.isEmpty(rePassword)){

            validateSignup = false;
            passwordLayout.setError("Required");
            rePasswordLayout.setError("Required");
        }
        else if (TextUtils.isEmpty(password)){

            validateSignup = false;
            passwordLayout.setError("Required");
        }
        else if (TextUtils.isEmpty(rePassword)){

            validateSignup = false;
            rePasswordLayout.setError("Required");
        }
        else {

            if (password.equals(rePassword)){

                if (password.length() < 6)
                {
                    validateSignup = false;
                    Toast.makeText(this, "Password Should Be At Least 6 Characters Long", Toast.LENGTH_SHORT).show();
                }

                if (validateSignup == true)
                {
                    checkIfEmailAlreadyExists();
                }

            }
            else{

                validateSignup = false;
                passwordLayout.setError("Passwords Don't Match");
                rePasswordLayout.setError("Passwords Don't Match");
            }
        }
    }

    private void checkIfEmailAlreadyExists() {

        progressDialog.show();

        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean doesEmailExist = !task.getResult().getSignInMethods().isEmpty();

                        progressDialog.dismiss();

                        if (doesEmailExist)
                        {
                            emailLayout.setError("Email Already Exists");
                        }
                        else
                        {
                            User user = new User();
                            user.setUsername(username);
                            user.setEmail(email);
                            user.setPassword(password);

                            Gson gson = new Gson();
                            String serviceProviderJson = gson.toJson(user);

                            Intent i = new Intent(Registration1Activity.this, Registration2Activity.class);
                            i.putExtra("user", serviceProviderJson);
                            startActivity(i);

                            finish();
                        }

                    }
                });

    }
}