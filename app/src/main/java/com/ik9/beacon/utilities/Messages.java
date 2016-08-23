package com.ik9.beacon.utilities;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Messages {
    public static void Toast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
