package com.kenjin.shareloc;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.kenjin.shareloc.Helper.LoggingInterceptor;
import com.kenjin.shareloc.Helper.MyConstant;
import com.kenjin.shareloc.Helper.RestAPI;
import com.kenjin.shareloc.model.Result;
import com.kenjin.shareloc.model.mLokasi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnMyLocationClickListener {

    public static String sessionUser = "seesionUser";
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private LocationRequest mLocationRequest;
    private ArrayList<mLokasi> newLocation = new ArrayList<>();
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 secs */
    private final static String KEY_LOCATION = "location";
    boolean ceklokasi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView text1 = findViewById(R.id.text1);
        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_LONG).show();
        }
        if (getIntent().getStringExtra(sessionUser) != null)
            text1.setText(getIntent().getStringExtra(sessionUser).concat(" Share Your Location!"));

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lihat hasil data
                Intent intent = new Intent(MainActivity.this, LokasiActivity.class);
                intent.putExtra("darimain", "Check Lokasi");
                startActivity(intent);

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display the connection status

        if (mCurrentLocation != null) {
            animateCamera(mCurrentLocation);
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        PermisionDispacther.startLocationUpdatesWithPermissionCheck(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        MenuItem item = menu.findItem(R.id.menuSubmit);
        item.setIcon(android.R.drawable.ic_menu_save);
        item.setTitle("Save");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menuSubmit) {//save customer
            //simpan ke server
            if (newLocation != null)
                for (mLokasi loc : newLocation) {
                    SendMapsToServer(loc);
                }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit",
                Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            Toast.makeText(this, "Map Fragment was loaded properly!",
                    Toast.LENGTH_SHORT).show();
            PermisionDispacther.getMyLocationWithPermissionCheck(this);
            PermisionDispacther.startLocationUpdatesWithPermissionCheck(this);
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                ceklokasi = true;
                return false;
            }
        });
        /*mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);*/
    }

    @Override
    public boolean onMyLocationButtonClick() {
        ceklokasi = true;
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermisionDispacther.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    void getMyLocation() {
        //noinspection MissingPermission

        //jangan lupa implement ini setelah map ready
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        //noinspection MissingPermission
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ShareLoc", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }


    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        if (mCurrentLocation != null) {
            if (mCurrentLocation.getLatitude() != location.getLatitude() && mCurrentLocation.getLongitude() != location.getLongitude()) {
                animateCamera(mCurrentLocation);
            }
        } else {
            animateCamera(location);
        }
        mCurrentLocation = location;

        /*String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();*/
    }

    private void animateCamera(Location location) {

        Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position\n" + latLng.latitude + "," + latLng.longitude)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.you_are_here));
        if (mMap != null) {
            mMap.addMarker(markerOptions);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f);
            mMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (!ceklokasi) {
            addLocation(latLng);
        } else {
            ceklokasi = false;
        }

    }

    private void addLocationMarker(LatLng latLng, String locationName) {
        newLocation.add(new mLokasi(latLng.latitude, latLng.longitude, locationName, ""));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(locationName + ":" + latLng.latitude + "," + latLng.longitude);
        mMap.addMarker(markerOptions);
        ceklokasi = false;
    }

    //custom popup
    public void addLocation(final LatLng latLng) {
        Context context = MainActivity.this;
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.preview_add_map, null);
        final TextInputEditText namalokasi = (TextInputEditText) dialogView.findViewById(R.id.editText);
        final Button btnSave = dialogView.findViewById(R.id.btnSave);

        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        //ad.setTitle("Input Location Name");
        ad.setMessage("Input Location Name");
        ad.setView(dialogView);
        final AlertDialog dialog = ad.create();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cancel = false;
                View focusView = null;
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                namalokasi.setError(null);
                if (TextUtils.isEmpty(namalokasi.getEditableText())) {
                    namalokasi.setError(getString(R.string.error_field_required));
                    focusView = namalokasi;
                    cancel = true;
                }
                if (cancel) {
                    focusView.requestFocus();
                } else {

                    addLocationMarker(latLng, namalokasi.getEditableText().toString());

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    //upload lokasi
    private void SendMapsToServer(mLokasi lokasi) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending Data...");
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor("ShareLock")).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConstant.URL_SEKOLAH) //ke url java MyCOnstant
                .addConverterFactory(GsonConverterFactory.create())//diconvert
                .client(client)
                .build();//dibuild
        RestAPI api = retrofit.create(RestAPI.class); //buat object untuk restAPI
        Call<Result> getGambar = api.sendMarking(lokasi); // buat object call untuk ambil data dari db
        getGambar.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    //Log.e("isi respion", response.body().toString());
                    Result gambarSlide = response.body();
                    if (gambarSlide != null) {
                        // Log.e("error", response.message() + "," + response.code() + ". ada data "+ gambarSlide.getEmailAddress());
                        if (gambarSlide.isStatus()) {
                            Toast.makeText(MainActivity.this, "Location Save In Server " + gambarSlide.getPesan(), Toast.LENGTH_LONG).show();
                            newLocation.clear();
                            mMap.clear();
                            animateCamera(mCurrentLocation);
                        } else {
                            Toast.makeText(MainActivity.this, "Error Save Data in Server " + gambarSlide.getPesan(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error On Save", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("error tidak keluar", response.message() + "," + response.code());
                    Toast.makeText(MainActivity.this, "Error On Connection", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal Send Data  " + t, Toast.LENGTH_SHORT).show();
                Log.e("error", t + ",");
                progressDialog.dismiss();

            }
        });
    }
}
