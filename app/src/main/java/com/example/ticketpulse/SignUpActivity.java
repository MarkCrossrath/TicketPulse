package com.example.ticketpulse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextSportPreference;
    private FirebaseAuth mAuth;
    String ticket;


    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressbar);
        editTextSportPreference = findViewById(R.id.editTextSportPreference);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();


        findViewById(R.id.buttonSignUp).setOnClickListener(this);
       findViewById(R.id.textViewLogin).setOnClickListener(this);



    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() !=null){
            Toast.makeText(getApplicationContext(),"you are already registered", Toast.LENGTH_LONG).show();
        }



    }

    private void registerUser() {
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String sportPreference = editTextSportPreference.getText().toString().trim();


        if (email.isEmpty()) {
            editTextEmail.setError("Email is Required ");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is Required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be greater than 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if (sportPreference.isEmpty()) {
            editTextSportPreference.setError("please enter your sport of preference");
            editTextSportPreference.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            //stored fields
                            User user = new User(
                                    email,sportPreference,ticket
                            );
                            FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String userID = user.getUid();
                                        mRef.child("Users").child(userID).child("role").setValue(2);
                                        Toast.makeText(getApplicationContext()," User registered Successfully", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                        }
                        else {
                            Toast.makeText(SignUpActivity.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSignUp:

               registerUser();
               // break;
            case R.id.textViewLogin:
                startActivity(new Intent(this, MainActivity.class));
               break;
        }
    }
}
