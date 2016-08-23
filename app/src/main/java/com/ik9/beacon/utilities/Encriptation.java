package com.ik9.beacon.utilities;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encriptation {
    public static String hash(String text) {
        String result = "";
        try {

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(text.getBytes());
                result = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", result);

        } catch (NoSuchAlgorithmException e) {

        }

        return result;
    }
}
