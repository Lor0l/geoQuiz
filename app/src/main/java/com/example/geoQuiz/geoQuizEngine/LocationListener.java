package com.example.geoQuiz.geoQuizEngine;

/**
 * LocationListener objects will be notified once a MyLocationProvider object obtained a location
 */
public interface LocationListener {

    /**
     * send coordinates to subscribed observer
     * @param coordinates
     */
    void locationAvailableListener(double[] coordinates);
}
