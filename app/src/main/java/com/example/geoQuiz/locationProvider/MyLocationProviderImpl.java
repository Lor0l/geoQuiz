package com.example.geoQuiz.locationProvider;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import com.example.geoQuiz.geoQuizEngine.LocationListener;
import com.example.geoQuiz.geoQuizUI.NotificationToaster;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MyLocationProviderImpl implements MyLocationProvider {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private NotificationToaster toaster;
    private LocationListener observer;

    public MyLocationProviderImpl(Context ctx) {
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx);
        this.toaster = new NotificationToaster(ctx);
    }

    
    // this method can't be called without granted permission (dealt with in activity)
    @SuppressLint("MissingPermission")
    @Override
    public void getCurrentLocation() {
        // call location provider on device
        this.fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                double[] coordinates = new double[2];
                Location location = task.getResult();
                if (location != null) {
                    coordinates[0] = location.getLatitude();
                    coordinates[1] = location.getLongitude();
                    observer.locationAvailableListener(coordinates);
                } else {
                    toaster.notifyUserToast("Location not available - GPS might be disabled");
                }
            } else {
                toaster.notifyUserToast("Location not available");
            }
        });
    }

    public void registerObserver(LocationListener observer) {
        this.observer = observer;
    }
}
