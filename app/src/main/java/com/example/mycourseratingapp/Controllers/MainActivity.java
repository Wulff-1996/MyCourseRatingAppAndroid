package com.example.mycourseratingapp.Controllers;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mycourseratingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "MainActivity";
    private EditText emailEditText;
    private EditText passwordEditText;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null)
                {
                    Log.d(TAG, "user signed in: " + user.getEmail());
                }
                else
                {
                    Log.d(TAG, "user signed out");
                }
            }
        };

        init();
    }



    private void init()
    {
        this.emailEditText = findViewById(R.id.emailEditText);
        this.passwordEditText = findViewById(R.id.passwordEditText);

        Button signInButton = findViewById(R.id.signInButton);
        Button signUpButton = findViewById(R.id.signUpButton);

        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
               mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void updateUI(FirebaseUser user)
    {
        if (user != null)
        {
            Intent intent = new Intent(this, CoursesOverviewActivity.class);
            startActivity(intent);
        }

    }

    private void signUp(String email, String password)
    {
        if(emailEditText != null && passwordEditText != null &&
                !emailEditText.getText().toString().isEmpty() &&
                !passwordEditText.getText().toString().isEmpty())
        {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());

                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Could not create Account")
                                        .setMessage("Check for valid email and password, or do you already have an account")

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setPositiveButton(android.R.string.yes, null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();

                                updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }

    private void signIn(String email, String password)
    {
        if(emailEditText != null && passwordEditText != null &&
            !emailEditText.getText().toString().isEmpty() &&
            !passwordEditText.getText().toString().isEmpty())
        {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());

                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Could not Sign In")
                                        .setMessage("Do you have an Account? Check for email and password is correct.")

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setPositiveButton(android.R.string.yes, null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();

                                updateUI(null);
                            }
                        }
                    });
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.signInButton)
        {
            signIn(emailEditText.getText().toString(), passwordEditText.getText().toString());
        }
        else if (i == R.id.signUpButton)
        {
            signUp(emailEditText.getText().toString(), passwordEditText.getText().toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSavedInstance()");

        outState.putString("EMAIL", emailEditText.getText().toString());
        outState.putString("PASSWORD", passwordEditText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState()");

        emailEditText.setText(savedInstanceState.getString("EMAIL"));
        passwordEditText.setText(savedInstanceState.getString("PASSWORD"));
    }
}
