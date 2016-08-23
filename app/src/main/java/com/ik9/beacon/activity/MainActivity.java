package com.ik9.beacon.activity;
//import com.facebook.FacebookSdk;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ik9.beacon.R;
import com.ik9.beacon.service.FacebookUser;
import com.ik9.beacon.utilities.Constants;
import com.ik9.beacon.utilities.HttpConnection;
import com.ik9.beacon.utilities.Messages;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.ScanMode;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.BeaconRegion;
import com.kontakt.sdk.android.ble.device.EddystoneDevice;
import com.kontakt.sdk.android.ble.discovery.eddystone.EddystoneTLMAdvertisingPacket;
import com.kontakt.sdk.android.ble.discovery.eddystone.EddystoneUIDAdvertisingPacket;
import com.kontakt.sdk.android.ble.filter.eddystone.TLMFilter;
import com.kontakt.sdk.android.ble.filter.eddystone.UIDFilter;
import com.kontakt.sdk.android.ble.filter.ibeacon.IBeaconFilters;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerContract;
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.ik9.beacon.activity.Ik9BeaconApplication.profileEntity;
import static com.ik9.beacon.activity.Ik9BeaconApplication.profileFacebook;
import static com.ik9.beacon.activity.Ik9BeaconApplication.userSkippedLogin;
import static com.ik9.beacon.activity.Ik9BeaconApplication.accessToken;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Context context;
    private static final String JAALEE_BEACON_PROXIMITY_UUID = "ebefd083-70a2-47c8-9837-e7b5634df599";
    private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";
    private static final int REQUEST_ENABLE_BT = 1234;
    private ArrayAdapter<String> adpCampaigns;
    private AccessTokenTracker accessTokenTracker;
    private BluetoothAdapter btaAdapter;
    private boolean isResumed = false;
    private CallbackManager callbackManager;
    private GoogleApiClient client;
    private ProfileTracker profileTracker;
    private ProximityManagerContract proximityManager;

    public MainActivity() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (btaAdapter.isEnabled()) {
            Messages.Toast(this, String.valueOf(R.string.bluetooth_ativado));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;

        super.onCreate(savedInstanceState);

        KontaktSDK.initialize(this);

        proximityManager = new ProximityManager(this);
        configureProximityManager();
        configureListeners();
        configureSpaces();
        configureFilters();


        if (savedInstanceState != null) {
            userSkippedLogin = savedInstanceState.getBoolean(USER_SKIPPED_LOGIN_KEY);
        }
        callbackManager = CallbackManager.Factory.create();

        setupProfileTracker();
        setupTokenTracker();

        Ik9BeaconApplication.profileTracker = new ProfileTrackerMethod();
        accessTokenTracker = new AccessTokenTrackerMethod();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new FloatingClickListener());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        proximityManager.disconnect();
        proximityManager = null;
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            Intent intent = new Intent(this, CustomerActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
//            Intent shareIntent = new Intent();
//            shareIntent.setAction(Intent.ACTION_SEND);
//            shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
//            shareIntent.setType("image/jpeg");
//            startActivity(Intent.createChooser(shareIntent, "Compartilhar com");
        } else if (id == R.id.nav_phone) {
            choosePhoneNumber();
        } else if (id == R.id.nav_email) {
            sendCustomerEmail();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Toast.makeText(this, "settings clicado", Toast.LENGTH_LONG).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
    }

    @Override
    public void onResume() {
        super.onResume();

        isResumed = true;

        if ((AccessToken.getCurrentAccessToken() == null) && !userSkippedLogin) {
            CallFacebookLogin();
            return;
        }

        FacebookUser.filler(AccessToken.getCurrentAccessToken());
        profileFacebook = Profile.getCurrentProfile();

        CallChains();

        checkBluetooth();
        checkWiFi();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
    }

    @Override
    public void onStart() {
        super.onStart();

        startScanning();

        checkBluetooth();
        checkWiFi();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        proximityManager.stopScanning();
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
//        client.disconnect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    private void checkBluetooth() {
//        // Check if device supports Bluetooth Low Energy.
//        if (!beaconManager.hasBluetooth()) {
//            Messages.Toast(this, "Aparelho não possui o recurso Bluetooth Low Energy");
//            return;
//        }

        // If Bluetooth is not enabled, let user enable it.
//        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        } else {
//            connectToService();
//        }
    }

    private void checkWiFi() {
        ConnectivityManager cnmWiFi = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo ntiWiFi = cnmWiFi.getActiveNetworkInfo();

        boolean status = false;

        if (ntiWiFi != null) {
            status = ntiWiFi.isConnectedOrConnecting();
        }

        if (!status) {
//            WifiManager wfmInternet = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
//            wfmInternet.setWifiEnabled(status);

            Intent enableWiFiIntent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
            startActivityForResult(enableWiFiIntent, 0);
        }
    }

    private void choosePhoneNumber() {
        AlertDialog.Builder choosePhoneNumber = new AlertDialog.Builder(this);
        String[] items = new String[]{"Itu", "Salto"};
        final AlertDialog.Builder builder = choosePhoneNumber.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        initiateCustomerCall(getString(R.string.contato_tel_itu));
                        break;
                    case 1:
                        initiateCustomerCall(getString(R.string.contato_tel_salto));
                        break;
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Sair", null);
        builder.setIcon(R.drawable.ic_call_black_18dp);
        builder.setTitle("Escolha a unidade:");
        builder.show();
    }

    private void configureFilters() {
        proximityManager.filters().eddystoneUidFilter(new UIDFilter() {
            @Override
            public boolean apply(EddystoneUIDAdvertisingPacket eddystoneUIDAdvertisingPacket) {
                eddystoneUIDAdvertisingPacket.getNamespaceId();
                return false;
            }
        });

        proximityManager.filters().iBeaconFilter(IBeaconFilters.newDeviceNameFilter("JonSnow"));
    }

    private void configureListeners() {
        proximityManager.setIBeaconListener(createIBeaconListener());
        proximityManager.setEddystoneListener(createEddystoneListener());
        //proximityManager.setScanStatusListener(createScanStatusListener());
    }

    private void configureProximityManager() {
        proximityManager.configuration()
                .scanMode(ScanMode.LOW_LATENCY)
                .scanPeriod(ScanPeriod.create(TimeUnit.SECONDS.toMillis(10), TimeUnit.SECONDS.toMillis(20)))
                .activityCheckConfiguration(ActivityCheckConfiguration.MINIMAL)
                .monitoringEnabled(false);
    }

    private void configureSpaces() {
        IBeaconRegion region = new BeaconRegion.Builder()
                .setIdentifier("All my iBeacons")
                .setProximity(UUID.fromString("123e4567-e89b-12d3-a456-426655440000"))
                .build();

        proximityManager.spaces().iBeaconRegion(region);
    }

    private void connectToService() {
//        beaconManager.connect(new ServiceReadyCallback() {
//            @Override
//            public void onServiceReady() {
//                try {
//                    beaconManager.startRangingAndDiscoverDevice(ALL_BEACONS_REGION);
//                } catch (RemoteException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    private EddystoneListener createEddystoneListener() {
        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                Log.i("Sample", "Eddystone discovered: " + eddystone.getUniqueId());
            }
        };
    }

    private IBeaconListener createIBeaconListener() {
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                Log.i("Sample", "IBeacon discovered: " + ibeacon.getUniqueId());
            }
        };
    }

    private void initiateCustomerCall(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    private void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }
        });
    }

    private void sendCustomerEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:contato@ik9.com.br"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "E-mail do Tag Push");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Escreva aqui sua mensagem para nós.");

        try {
            startActivity(emailIntent.createChooser(emailIntent, "Mandar e-mail usando..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Messages.Toast(this, "Não há cliente de e-mail instalado.");
        }
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void setupTokenTracker() {
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                accessToken = currentAccessToken;
            }
        };
    }

    private void setupProfileTracker() {
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                profileFacebook = currentProfile;
                Profile.setCurrentProfile(currentProfile);

                if (accessToken != null) {
                    profileTracker.stopTracking();
                }
            }
        };
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    private void CallCampaigns(String deviceID) {
        try {

            HttpConnection.bringCampaigns(deviceID);

        } catch (Exception ex) {
            Log.e(Constants.TAG, ex.getMessage());
        }
    }

    private void CallChains() {
        try {
            LinearLayout lltChains = (LinearLayout) findViewById(R.id.lltChains);

            LinearLayout.LayoutParams params = new LinearLayout
                    .LayoutParams(150, 150);

            for (int i = 0; i <= 2; i++) {

                final String chainID = "Image_" + i;

                ImageView iv = new ImageView(this);
                iv.setImageResource(R.drawable.ic_local_mall_black_18dp);
                iv.setLayoutParams(params);
                iv.setTag(chainID);
                final int finalI = i;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Messages.Toast(context, "Image_" + finalI);
                        CallCampaigns(chainID);
                    }
                });
                lltChains.addView(iv);
            }

        } catch (Exception ex) {
            Log.e(Constants.TAG, ex.getMessage());
        }
    }

    private void CallFacebookLogin() {
        Intent intent = new Intent(context, FragmentHolderActivity.class);
        startActivity(intent);
    }

//    private ConnectionCallback createConnectionCallback() {
//        return new ConnectionCallback() {
//            @Override
//            public void onAuthenticated(final BeaconCharacteristics beaconChars) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        StringBuilder sb = new StringBuilder()
//                                .append("UUID: ").append(beaconChars.getBeaconUUID()).append("\n")
//                                .append("Major: ").append(beaconChars.getMajor()).append("\n")
//                                .append("Minor: ").append(beaconChars.getMinor()).append("\n")
//                                .append("Advertising interval: ").append(beaconChars.getBroadcastRate()).append("ms\n")
//                                .append("Broadcasting power: ").append(beaconChars.getBroadcastingPower()).append(" dBm\n")
//                                .append("Device Name: ").append(beaconChars.getBeaconName()).append("\n")
//                                .append("Beacon State: ").append(beaconChars.getBeaconState())
//                                .append("Battery: ").append(beaconChars.getBeaconBatteryLevel());
////                        beaconDetailsView.setText(sb.toString());
////                        afterConnectedView.setVisibility(View.VISIBLE);
//                    }
//                });
//            }
//
//            @Override
//            public void onAuthenticationError() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        statusView.setText("Status: Cannot connect to beacon. Authentication problems.");
//                    }
//                });
//            }
//
//            @Override
//            public void onDisconnected() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        statusView.setText("Status: Disconnected from beacon");
//                    }
//                });
//            }
//        };
//    }
//
//    private List<Beacon> filterBeacons(List<Beacon> beacons) {
//        List<Beacon> filteredBeacons = new ArrayList<Beacon>(beacons.size());
//        for (Beacon beacon : beacons) {
//            //    	only detect the Beacon of Jaalee
//            if (beacon.getProximityUUID().equalsIgnoreCase(JAALEE_BEACON_PROXIMITY_UUID))//                if (beacon.getRssi() > -50) {
//            {
//                Log.i("JAALEE", "JAALEE:" + beacon.getMacAddress());
//                filteredBeacons.add(beacon);
//            }
//        }
//        return filteredBeacons;
//    }

    private ShareActionProvider mShareActionProvider;

    class AccessTokenTrackerMethod extends AccessTokenTracker {

        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (isResumed) {
                if (currentAccessToken == null) {
                    CallFacebookLogin();
                    return;
                }

                accessToken = AccessToken.getCurrentAccessToken();
                Profile.fetchProfileForCurrentAccessToken();
            }
        }
    }

    class FloatingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "TESTE DE COMPARTILHAMENTO VIA ANDROID");
            startActivity(Intent.createChooser(intent, "Compartilhar com"));

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
        }
    }

    private class ProfileTrackerMethod extends ProfileTracker {
        @Override
        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
            profileFacebook = currentProfile;
        }
    }
}
