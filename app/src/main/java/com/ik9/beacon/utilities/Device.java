package com.ik9.beacon.utilities;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class Device {

    public static String GetMAC(Context context)
    {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connInfo = mWifiManager.getConnectionInfo();

        return connInfo.getMacAddress();
    }

}
