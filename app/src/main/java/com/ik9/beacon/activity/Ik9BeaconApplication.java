package com.ik9.beacon.activity;
import android.app.Application;
import android.graphics.Bitmap;

import com.ik9.beacon.repository.ProfileEntity;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.ProfilePictureView;

public class Ik9BeaconApplication extends Application {

    public static AccessToken accessToken;
    public static Bitmap userPicture;
    public static boolean userSkippedLogin = false;
    public static Profile profileFacebook;
    public static ProfileEntity profileEntity;
    public static ProfilePictureView profilePictureView;
    public static ProfileTracker profileTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
