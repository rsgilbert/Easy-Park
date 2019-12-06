package com.learning.one_pc.easypark;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText email,password1,password2;
    private String sEmail,sPassword1,sPassword2,sblank;
    private Button signin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        email=findViewById(R.id.email);
        password1=findViewById(R.id.password1);
        password2=findViewById(R.id.password2);
        Button signup=findViewById(R.id.signup);
        signin=findViewById(R.id.signin);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sEmail=email.getText().toString();
                sPassword1=password1.getText().toString();
                sPassword2=password2.getText().toString();

                if(sPassword1.equals(sPassword2)) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(sEmail, sPassword1)
                            .addOnCompleteListener(RegisterActivity.this,
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(!task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "Sign up failed: "+task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                if(task.isSuccessful()){
                                                   final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                    if(user != null) {
                                                              user.sendEmailVerification()
                                                                   .addOnCompleteListener(RegisterActivity.this,
                                                                           new OnCompleteListener<Void>() {
                                                                               @Override
                                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                                   if(task.isSuccessful()) {
                                                                                       Toast.makeText(RegisterActivity.this,
                                                                                               "Verification Email sent to: "
                                                                                                       + user.getEmail(),
                                                                                               Toast.LENGTH_SHORT).show();
                                                                                       FirebaseAuth.getInstance().signOut();
                                                                                       startActivity(new Intent(RegisterActivity.this,
                                                                                               SigninActivity.class));
                                                                                   }
                                                                                   else {
                                                                                       Toast.makeText(RegisterActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
//                                                                                       restart activity
                                                                                       startActivity(getIntent());
                                                                                   }
                                                                               }
                                                                           });
                                                    }
                                                }
                                            }
                                        }
                                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Passwords dont match", Toast.LENGTH_SHORT).show();
                    password1.setText(sblank);
                    password2.setText(sblank);
                }
            }
        });



    }
}
