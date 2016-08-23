package com.ik9.beacon.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ik9.beacon.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SettingsActivity extends AppCompatActivity {

    private ToggleButton tgbBluetooth;
    private ToggleButton tgbWifi;
    private BluetoothAdapter btaAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = this;

        tgbBluetooth = (ToggleButton) findViewById(R.id.tgbBluetooth);
        getBluetoothStatus();
        tgbBluetooth.setOnCheckedChangeListener(new BluetoothSetting());

        tgbWifi = (ToggleButton) findViewById(R.id.tgbWiFi);
        getWiFiStatus();
        tgbWifi.setOnCheckedChangeListener(new WiFiSetting());
    }

    private void getBluetoothStatus() {
        btaAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btaAdapter.equals(null)) {
            tgbBluetooth.setChecked(false);
        } else if (btaAdapter.isEnabled()) {
            tgbBluetooth.setChecked(true);
        }
    }

    private void getWiFiStatus() {
        ConnectivityManager cnmWiFi = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo ntiWiFi = cnmWiFi.getActiveNetworkInfo();

        boolean status = false;

        if (ntiWiFi != null){
            status = ntiWiFi.isConnectedOrConnecting();
        }

        tgbWifi.setChecked(status);
    }

    class BluetoothSetting implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (btaAdapter.equals(null)) {
                    tgbBluetooth.setChecked(false);
                    Toast.makeText(context, R.string.bluetooth_indisponivel, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                if (!btaAdapter.isEnabled()) {
                    btaAdapter.enable();
                }

                return;
            }

            if (!(btaAdapter.equals(null))) {
                if (btaAdapter.isEnabled()) {
                    btaAdapter.disable();
                }
            }
        }
    }

    class WiFiSetting implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                setWifiStatus(true);
                return;
            }

            setWifiStatus(false);
        }

        private void setWifiStatus(boolean status) {
            WifiManager wfmInternet = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            wfmInternet.setWifiEnabled(status);

//            try {
//                Method method = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
//                method.setAccessible(status);
//                method.invoke(cnmInternet, status);
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }

            tgbWifi.setChecked(status);
        }
    }
}
