package com.learning.one_pc.easypark.Models;

import android.content.Context;
import android.widget.Toast;

public class CommonMethods {
    private void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
