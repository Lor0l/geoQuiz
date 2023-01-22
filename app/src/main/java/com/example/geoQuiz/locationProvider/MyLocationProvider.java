package com.example.geoQuiz.locationProvider;

import com.example.geoQuiz.geoQuizEngine.LocationListener;

/**
 * engine - location provider connection
 */
public interface MyLocationProvider {

    /**
     * get current location from device, if successful notify observer otherwise inform user
     */
    void getCurrentLocation();

    /**
     * register observer that want to be notified when location is available
     * @param observer
     */
    void registerObserver(LocationListener observer);
}
