package com.learning.one_pc.easypark;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //widgets
    private Button signin, signup;
    private EditText password, email;
    private String blank = "", sEmail, sPassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        Toast.makeText(SigninActivity.this, "signing in..", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SigninActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(SigninActivity.this, "Verify your email and try again", Toast.LENGTH_SHORT).show();
                        user.sendEmailVerification();
                        mAuth.signOut();
                    }
                }
            }
        };
        signin.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View view) {
                    sEmail = email.getText().toString();
                    sPassword = password.getText().toString();

                    if (!sEmail.equals("") && !sPassword.equals("")) {
                        mAuth.signInWithEmailAndPassword(sEmail, sPassword)
                                .addOnCompleteListener(SigninActivity.this,
                                        new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (!task.isSuccessful()) {

                                                }
                                            }
                                        });
                    }
                    else if (sEmail.equals(blank)){
                        AlertDialog.Builder mybuilder = new AlertDialog.Builder(SigninActivity.this);
                        mybuilder.setMessage("Email field is empty")
                                .setTitle("Login Error")
                                .setPositiveButton("ok",null);
                        AlertDialog dialog = mybuilder.create();
                        dialog.show();
                    }

                    else if (sPassword.equals(blank)){
                        AlertDialog.Builder mybuilder = new AlertDialog.Builder(SigninActivity.this);
                        mybuilder.setMessage("Password field is empty")
                                .setTitle("Login Error")
                                .setPositiveButton("ok",null);
                        AlertDialog dialog = mybuilder.create();
                        dialog.show();
                    }

                }
            });
        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       DialogBox mydialog = new DialogBox();
                       mydialog.show(getSupportFragmentManager(), "Dialog Signup");
                    }
                }
        );

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
