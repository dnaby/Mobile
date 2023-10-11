package sn.ept.git.mobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sn.ept.git.mobile.api.ClientAPI;
import sn.ept.git.mobile.api.interfaces.ArticleCommandeCall;
import sn.ept.git.mobile.api.interfaces.LocationCall;
import sn.ept.git.mobile.api.models.article_commande.ArticleCommande;
import sn.ept.git.mobile.api.models.location.LocationResponse;
import sn.ept.git.mobile.databinding.ActivityMainBinding;
import sn.ept.git.mobile.location.Constants;
import sn.ept.git.mobile.location.LocationService;

public class MainActivity extends AppCompatActivity implements LocationListener {
    LocationManager locationManager;
    private static final int GPS_TIME_INTERVAL = 1000 * 60 * 10; // get gps location every 10 min
    private static final int GPS_DISTANCE = 1000; // set the distance value in meter
    private static final int HANDLER_DELAY = 1000 * 60 * 10;
    private static final int START_HANDLER_DELAY = 0;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    final static int PERMISSION_ALL = 1;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FloatingActionButton fab;
    String imei;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                requestLocation();
                handler.postDelayed(this, HANDLER_DELAY);
            }
        }, START_HANDLER_DELAY);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fab = binding.appBarMain.fab;

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_category, R.id.nav_marque,
                R.id.nav_produit, R.id.nav_stock, R.id.nav_article_commande,
                R.id.nav_client, R.id.nav_commande, R.id.nav_employe, R.id.nav_magasin)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        NotificationChannel channel = new NotificationChannel("channel_id", "Canal", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Description");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        notificationManager.createNotificationChannel(channel);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                Toast.makeText(getApplicationContext(), "NEWORK AVAILABLE. SYNC DATA", Toast.LENGTH_LONG).show();
                /*
                StockCall stockCall = ClientAPI.getStockCall();
                SyncStock syncStock = new SyncStock();
                Handler dbHandler = new Handler(getApplicationContext());
                syncStock.setStocks(dbHandler.getAllStocks());
                dbHandler.close();
                Call<Object> call = stockCall.syncData(syncStock);
                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sync successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error on syncing", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                        Log.e("ERROR", t.getMessage());
                        Toast.makeText(getApplicationContext(), "Error on syncing", Toast.LENGTH_LONG).show();
                    }
                });
                 */
            }

            @Override
            public void onLost(@NonNull Network network) {
                Toast.makeText(getApplicationContext(), "NETWORK LOST", Toast.LENGTH_LONG).show();
            }
        };

        connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        locationManager.removeUpdates(this);

        @SuppressLint("HardwareIds") String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        sn.ept.git.mobile.api.models.location.Location locationToPost = new sn.ept.git.mobile.api.models.location.Location();
        locationToPost.setAndroidID(androidID);
        locationToPost.setLatitude(latitude);
        locationToPost.setLongitude(longitude);
        locationToPost.setTimestamp((new Date()).toString());

        LocationCall locationCall = ClientAPI.getLocationCall();
        Call<LocationResponse> call = locationCall.postLocation(locationToPost);
        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(@NonNull Call<LocationResponse> call, @NonNull Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Post location " + locationToPost + "successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error on Post location = " + locationToPost + " !", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocationResponse> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                Toast.makeText(MainActivity.this, "Error on Post location !", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void requestLocation() {
        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        (long) GPS_TIME_INTERVAL, (float) GPS_DISTANCE, this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    requestLocation();
                    handler.postDelayed(this, HANDLER_DELAY);
                }
            }, HANDLER_DELAY);
        } else {
            finish();
        }
    }
}