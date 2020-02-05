package com.ttb.bcp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.MapboxConstants;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class accomodationdetails extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback, Style.OnStyleLoaded, MapboxMap.OnMapClickListener, MapboxMap.OnMarkerClickListener {

    TextView title_toolbar;
    ImageView logout;
    GoogleApiClient googleApiClient;
    Location location;
    LocationRequest locationRequest;
    final static int REQUEST_LOCATION = 199;
    private MapboxMap mapboxMap;
    private MapView mapView;
    CameraPosition cameraPosition;
    Marker myCurrentLocationMarker;
    Style style;
    boolean firstLocation = false;
    private static final LatLng BOUND_CORNER_NW = new LatLng(23.2380, 39.8539);
    private static final LatLng BOUND_CORNER_SE = new LatLng(22.6873, 40.8434);
    NavigationMapRoute navigationMapRoute;
    String TAG = "HomeActivity";
    DirectionsRoute currentRoute;
    Button categoryclicked;
    ArrayList<Marker> markersList = new ArrayList<>();
    ProgressDialog pd = null;
    Marker lastClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(true);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_accomodationdetails);

        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        logout = (ImageView) findViewById(R.id.toolbar_signout);
//        logout.setVisibility(View.VISIBLE);

//        logout.setImageResource(R.drawable.plus);

        title_toolbar.setText("ACCOMODATION");

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

//        tryAndEnableGPS();
//        buildGoogleApiClient();
//        createLocationRequest();


//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAddFamilyDialog();
//            }
//        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openMakkah();
            }
        },1500);

        ((Button) findViewById(R.id.makkahbtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMakkah();
            }
        });

        ((Button) findViewById(R.id.madinabtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMadina();
            }
        });

        ((Button) findViewById(R.id.minabtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMina();
            }
        });


    }

    void openMakkah(){
        mapboxMap.clear();
        LatLng location = new LatLng(21.420093,39.824533);
        Marker temp = mapboxMap.addMarker(new MarkerOptions()
                .setTitle("Pullman Zamzam Makkah\nFloor # 1\nRoom # 101")
                .setPosition(location)
        );
        moveCameraToLocation(location);
        mapboxMap.selectMarker(temp);
    }

    void openMadina(){
        mapboxMap.clear();
        LatLng location = new LatLng(24.464462,39.611512);
        Marker temp = mapboxMap.addMarker(new MarkerOptions()
                .setTitle("Pullman Zamzam Madina\nFloor # 2\nRoom # 201")
                .setPosition(location)
        );
        moveCameraToLocation(location);
        mapboxMap.selectMarker(temp);
    }

    void openMina(){
        mapboxMap.clear();
        LatLng location = new LatLng(21.414459,39.884067);
        Marker temp = mapboxMap.addMarker(new MarkerOptions()
                .setTitle("Maktab 009\nCamp 7/56")
                .setPosition(location)
        );
        moveCameraToLocation(location);
        mapboxMap.selectMarker(temp);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        startLocationUpdates();
//        Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show();
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            if (googleApiClient.isConnected() && mapboxMap != null)
                updateCameraPosition(location);
//            Toast.makeText(this, "connected" + location.getLatitude() + "_" + location.getLongitude(), Toast.LENGTH_SHORT).show();
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        if (mapboxMap != null && location != null) {
            updateCameraPosition(this.location);
//            Toast.makeText(this, "loc_changed " + this.location.getLatitude() + "_" + this.location.getLongitude(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return true;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
//        mapboxMap.selectMarker(marker);

        moveCameraToLocation(marker.getPosition());
        return false;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        this.mapboxMap.setAllowConcurrentMultipleOpenInfoWindows(true);

        this.mapboxMap.setStyle(Style.OUTDOORS, this);
        this.mapboxMap.getUiSettings().setCompassEnabled(true);
        this.mapboxMap.getUiSettings().setAllGesturesEnabled(true);
        this.mapboxMap.setOnMarkerClickListener(this);
        this.mapboxMap.addOnMapClickListener(this);
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {
        this.style = style;
    }


    void moveCameraToLocation(LatLng location) {
        cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(16)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 101:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Toast.makeText(accomodationdetails.this, states.isLocationPresent() + "", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(accomodationdetails.this, "Canceled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    private void tryAndEnableGPS() {

        areBothProvidersEnabled();
        String le = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(le);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "High Accuracy Done", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not Done High Accuracy", Toast.LENGTH_SHORT).show();
            enableLoc();
        }

        //        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && hasGPSDevice(this)) {
        //            Toast.makeText(this,"High Accuracy already enabled",Toast.LENGTH_SHORT).show();
        //
        //            return;
        //        }

        //        if(!hasGPSDevice(this)){
        //            Toast.makeText(this,"GPS not Supported",Toast.LENGTH_SHORT).show();
        //        }
        //
        //        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&  !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)  && hasGPSDevice(this)) {
        //            Log.e("hajjease","High Accuracy not enabled");
        //            Toast.makeText(this,"High Accuracy not enabled",Toast.LENGTH_SHORT).show();
        //            enableLoc();
        //        }else{
        //            Log.e("keshav","High Accuracy already enabled");
        //            Toast.makeText(this,"High Accuracy already enabled",Toast.LENGTH_SHORT).show();
        //        }
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    void updateCameraPosition(Location location) {
        if (location != null && firstLocation == false) {
            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(17)
                    .tilt(30)
                    .bearing(0)
                    .build();

            if (myCurrentLocationMarker != null)
                mapboxMap.removeMarker(myCurrentLocationMarker);
            myCurrentLocationMarker = mapboxMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .setTitle("Me")
                            .setIcon(getUserIcon())
            );

            mapboxMap.selectMarker(myCurrentLocationMarker);

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 500);
            firstLocation = true;

//            registerUserLocation(location.getLatitude()+","+location.getLongitude());

        } else {

            if (myCurrentLocationMarker != null)
                myCurrentLocationMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
//
//            if(myCurrentLocationMarker!=null)
//                mapboxMap.removeMarker(myCurrentLocationMarker);
//            myCurrentLocationMarker = mapboxMap.addMarker(
//                    new MarkerOptions()
//                            .position(new LatLng(location.getLatitude(),location.getLongitude()))
//                            .setTitle("My Location")
//                            .setIcon(getUserIcon())
//            );
//
//            mapboxMap.selectMarker(myCurrentLocationMarker);
        }
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            //                           Toast.makeText(Home.this, "Google Api Connected after location changed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(500);
            locationRequest.setSmallestDisplacement(0);
            locationRequest.setFastestInterval(500);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);


            Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

            task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                    } catch (ApiException exception) {
                        switch (exception.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the
                                // user a dialog.
                                try {
                                    // Cast to a resolvable exception.
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    resolvable.startResolutionForResult(
                                            accomodationdetails.this,
                                            101);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                } catch (ClassCastException e) {
                                    // Ignore, should be an impossible error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't show the dialog.
                                break;
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, "googleapiclient was null in tryandongps", Toast.LENGTH_SHORT).show();
            buildGoogleApiClient();
        }
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(0);
    }

    @SuppressWarnings("MissingPermission")
    void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    void stopLocationUpdates() {
        if (googleApiClient != null || locationRequest != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
//        if (googleApiClient != null && !googleApiClient.isConnected())
//            googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        if (firstLocation)
            firstLocation = !firstLocation;
        mapView.onResume();
//        if (googleApiClient != null && locationRequest != null && googleApiClient.isConnected()) {
//            startLocationUpdates();
//        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
//        if (googleApiClient != null && googleApiClient.isConnected())
//            stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
//        if (googleApiClient != null && googleApiClient.isConnected())
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//            googleApiClient.disconnect();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    Bitmap bitmap=null;
    Icon getUserIcon() {
        IconFactory iconFactory = IconFactory.getInstance(accomodationdetails.this);
        Icon icon = iconFactory.fromResource(R.drawable.user);


        return icon;

//
//        int height = 100;
//        int width = 100;
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.mecca);
//        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
////        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
//        return smallMarker;
    }

    Icon getSmallFoodIcon() {
        IconFactory iconFactory = IconFactory.getInstance(accomodationdetails.this);
        Icon icon = iconFactory.fromResource(R.drawable.pin_128);



        return icon;

//
//        int height = 100;
//        int width = 100;
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.mecca);
//        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
////        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
//        return smallMarker;
    }

    Icon getLargePicIcon() {
        IconFactory iconFactory = IconFactory.getInstance(accomodationdetails.this);
        Icon icon = iconFactory.fromResource(R.drawable.pin_180);


        return icon;

//
//        int height = 100;
//        int width = 100;
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.mecca);
//        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
////        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
//        return smallMarker;
    }

    boolean areBothProvidersEnabled() {
        String le = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(le);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(this, "High Accuracy Done", Toast.LENGTH_SHORT).show();
            return true;
        } else {
//            Toast.makeText(this, "Not High Accuracy", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    ArrayList<Marker> familyMembers = new ArrayList<>();
    Bitmap a,b;
    int i = 0;
    JSONArray array = new JSONArray();
    void loadFamilyMembers(){

        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait ...");
        pd.setCancelable(false);
        pd.show();



        try {
            array.put(new JSONObject().put("title","Ahmad\n(1 mins ago)").put("position","21.559017,39.149812"));
            array.put(new JSONObject().put("title","Saad\n(2 mins ago) ").put("position","21.557543,39.151011"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (i=0; i< array.length(); i++){
            if(mapboxMap != null){
                try {
                    String [] arr = array.getJSONObject(i).getString("position").split(",");

//                    Picasso.get().load("https://icon-library.net/images/small-user-icon/small-user-icon-19.jpg").into(new Target() {
//                        @Override
//                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                            try {
//                                Toast.makeText(accomodationdetails.this, "ICON LOADED", Toast.LENGTH_SHORT).show();
//                                Marker temp = mapboxMap.addMarker
//                                        (new MarkerOptions().
//                                                setTitle(array.getJSONObject(i).getString("title")).
//                                                setPosition(new LatLng(Double.parseDouble(arr[0]),Double.parseDouble(arr[1]))).
//                                                setIcon(IconFactory.getInstance(accomodationdetails.this).fromBitmap(bitmap))
//
//                                        );
//                                Toast.makeText(accomodationdetails.this, IconFactory.getInstance(accomodationdetails.this).fromBitmap(bitmap).getScale()+"", Toast.LENGTH_SHORT).show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//                        }
//
//                        @Override
//                        public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                        }
//                    });

                    Marker temp = mapboxMap.addMarker(new MarkerOptions()
                            .setTitle(array.getJSONObject(i).getString("title"))
                            .setPosition(new LatLng(Double.parseDouble(arr[0]),Double.parseDouble(arr[1])))
                    );
                    mapboxMap.selectMarker(temp);
                    familyMembers.add(temp);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        pd.dismiss();


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                pd.cancel();
//            }
//        },1000);
    }

    void testMarkerRemoteIcon(){
        Picasso.get().load("https://icon-library.net/images/small-user-icon/small-user-icon-19.jpg").into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    Toast.makeText(accomodationdetails.this, "ICON LOADED", Toast.LENGTH_SHORT).show();
                    Marker temp = mapboxMap.addMarker
                            (new MarkerOptions().
                                    setTitle("Test Marker").
                                    setPosition(new LatLng(Double.parseDouble("21.5587018"),Double.parseDouble("39.1504557"))).
                                    setIcon(IconFactory.getInstance(accomodationdetails.this).fromBitmap(bitmap))

                            );
                    Toast.makeText(accomodationdetails.this, IconFactory.getInstance(accomodationdetails.this).fromBitmap(bitmap).getScale()+"", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    void showAddFamilyDialog(){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_add_family_tracking_dialog,null,false);
        EditText ed = view.findViewById(R.id.passportNumberDialog);
        adb.setView(view);
        adb.setPositiveButton("Track This Person", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    array.put(new JSONObject().put("title","Ibrahim"+"\n(5 mins ago)").put("position","21.558566,39.151255"));
                    loadFamilyMembers();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        AlertDialog ad = adb.create();
        ad.show();

    }

}
