package com.ttb.bcp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class locationservice extends Service implements 
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;



    @Override
    public void onCreate() {
        buildGoogleApiClient();
        createLocationRequest();

//        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
        
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        buildGoogleApiClient();
//        createLocationRequest();

//        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
//        Toast.makeText(this, "API connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
//        Toast.makeText(this, "Connection Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        //broadcast this location and get it on home
        Intent intent = new Intent("new-location-published");
        intent.putExtra("location",location.getLatitude()+","+location.getLongitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.e("locationSent",location.getLatitude()+","+location.getLongitude());
//        Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(0);

//        startLocationUpdates();
    }

    void startLocationUpdates() {
        if (googleApiClient!=null && googleApiClient.isConnected() && locationRequest!=null) {
//            Toast.makeText(this, "Starting location updates", Toast.LENGTH_SHORT).show();
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }else{
//            Toast.makeText(this, "apiclient not found so trying a new one", Toast.LENGTH_SHORT).show();
            buildGoogleApiClient();
        }
    }

    void stopLocationUpdates() {
        if (googleApiClient != null || locationRequest != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }
    
}
