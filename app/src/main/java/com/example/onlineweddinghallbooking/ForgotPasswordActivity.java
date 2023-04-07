package com.example.onlineweddinghallbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputLayout emailTextInputLayout;
    TextInputEditText emailTextInputEditText;

    TextView noteTextView;

    Button resetButton;

    String email;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        emailTextInputEditText = findViewById(R.id.emailTextInput);

        noteTextView = findViewById(R.id.noteTextView);

        resetButton = findViewById(R.id.passwordResetButton);


        firebaseAuth = FirebaseAuth.getInstance();



        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = emailTextInputLayout.getEditText().getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    emailTextInputLayout.setError("Email Is Required");
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    emailTextInputLayout.setError("Incorrect Email Format");
                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    noteTextView.setText("We've sent a password reset link to the entered Email. If you don't get the link in 30 seconds, kindly check the spam folder.");
                                }
                            });
                }

            }
        });
    }
}