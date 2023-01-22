package com.example.geoQuiz.geoQuizUI;

/**
 * information for view about changes on business logic so it can update the GUI accordingly
 * EDITOR and GAME indicate the context in which the method supposed to be called
 */
public interface Showable {

    /**
     * EDITOR/GAME
     * add marker to mapview
     * @param markerID
     */
     void showOneMarker(int markerID);

    /**
     * EDITOR
     * adds all markers to map when opening project
     * @param markerAmount
     */
     void showAllMarkers(int markerAmount);

    /**
     * GAME
     * notify user that last answer was wrong
     */
     void wrongAnswer();

}
