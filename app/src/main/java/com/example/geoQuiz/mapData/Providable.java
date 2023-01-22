package com.example.geoQuiz.mapData;

import java.util.ArrayList;

/**
 * view to model connection
 */
public interface Providable {

    /**
     * EDITOR/GAME
     * returns coordinates related to a marker
     * @param markerID
     * @return double[] [0] = latitude
     *                  [1] = longitude
     *         null if marker with this id has been deleted to keep marker ID's matching list index
     * @throws IndexOutOfBoundsException when markerID is larger than marker amount
     */
    double[] getCoordinates(int markerID);

    /**
     * EDITOR/GAME
     * returns the question beloning to a specific Marker
     * @param markerID
     * @return question
     */
    String getQuestion(int markerID);

    /**
     * EDITOR
     * returns the answer belonging to a specific Marker
     * @param markerID
     * @return answer
     */
    String getAnswer(int markerID);

    /**
     * EDITOR
     * returns a list of available projects
     * @return available projects
     */
    ArrayList<String> getAvailableProjectNames();

    /**
     * GAME
     * returns a list of available games
     * @return available games
     */
    ArrayList<String> getAvailableGameNames();

    /**
     * EDITOR/GAME
     * returns the name of the loadet project/game
     * @return
     */
    String getCurrentProjectGameName();

}
