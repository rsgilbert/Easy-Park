package com.learning.one_pc.easypark;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import static android.content.ContentValues.TAG;

public class DialogBox extends AppCompatDialogFragment {
    EditText email_box, password_box,confirm_box;
    public static int ERROR_CODE = 0;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        email_box = view.findViewById(R.id.email);
        password_box = view.findViewById(R.id.password1);
        confirm_box = view.findViewById(R.id.password2);

        builder.setView(view)
                .setTitle("Register with EasyPark")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("signup ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addData();
                    }
                });


        return builder.create();
    }
        public void addData(){

          String sEmail = email_box.getText().toString();
          String sPassword1 = password_box.getText().toString();
          String sPassword2 = confirm_box.getText().toString();
                if (!sEmail.isEmpty() && !sPassword1.isEmpty() && !sPassword2.isEmpty()){
                    if (sPassword1.equals(sPassword2)){
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(sEmail, sPassword1);
                        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                            FirebaseAuth.getInstance().signOut();
                }

            }
        }else {
                  Log.d(TAG, "Invalid");
                }

    }
}

