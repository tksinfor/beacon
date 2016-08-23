package com.ik9.beacon.activity;

import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ik9.beacon.R;
import com.ik9.beacon.repository.ProfileEntity;
import com.ik9.beacon.service.FacebookUser;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplahScreenFragment extends Fragment {
    private LoginButton loginButton;
    private Button btnSign;
    private SkipLoginCallback skipLoginCallback;
    private CallbackManager callbackManager;


    public interface SkipLoginCallback {
        void onSkipLoginPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splah_screen, container, false);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        btnSign = (Button) view.findViewById(R.id.btnSign);
        btnSign.setOnClickListener(new OnClickListenerMethod());
        setSkipLoginCallback(new SkipLoginCallbackMethod());

        List<String> Permissions = new ArrayList<>();

        Permissions.add("public_profile");
        Permissions.add("email");
        Permissions.add("user_birthday");
        Permissions.add("user_friends");

        loginButton.setReadPermissions(Permissions);
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallbackMethods());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void setSkipLoginCallback(SkipLoginCallback callback) {
        skipLoginCallback = callback;
    }



    class FacebookCallbackMethods implements FacebookCallback<LoginResult> {
        @Override
        public void onSuccess(LoginResult loginResult) {
            //Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();

            Ik9BeaconApplication.accessToken = loginResult.getAccessToken();

            FacebookUser.filler(loginResult.getAccessToken());

            getActivity().finish();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getActivity(), "Login cancelado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException exception) {
            Toast.makeText(getActivity(), "Login falhou", Toast.LENGTH_SHORT).show();
        }
    }

    class OnClickListenerMethod implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (skipLoginCallback != null) {
                skipLoginCallback.onSkipLoginPressed();
            }
        }
    }

    class SkipLoginCallbackMethod implements SkipLoginCallback{
        @Override
        public void onSkipLoginPressed() {
            Ik9BeaconApplication.userSkippedLogin = true;
        }
    }
}