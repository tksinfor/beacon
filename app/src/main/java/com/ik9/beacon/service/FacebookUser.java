package com.ik9.beacon.service;

import android.os.Bundle;
import android.util.Log;

import com.ik9.beacon.activity.Ik9BeaconApplication;
import com.ik9.beacon.repository.ProfileEntity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookUser {

    public static void filler(AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());

                        // Application code


                        Ik9BeaconApplication.profileEntity = new ProfileEntity();

                        Ik9BeaconApplication.profileEntity.setEmail(object.optString("email"));
                        //Ik9BeaconApplication.profileEntity.setPhone();
                        //Ik9BeaconApplication.profileEntity.setState();
                        Ik9BeaconApplication.profileEntity.setName(object.optString("name"));
                        //Ik9BeaconApplication.profileEntity.setCity();
                        Ik9BeaconApplication.profileEntity.setBirth(object.optString("user_birthday"));
                        Ik9BeaconApplication.profileEntity.setGender(object.optString("gender"));
                        Ik9BeaconApplication.profileEntity.setDevice_id(object.optString("id"));
                        //Ik9BeaconApplication.profileEntity.setUriPhoto();

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,user_birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

}
