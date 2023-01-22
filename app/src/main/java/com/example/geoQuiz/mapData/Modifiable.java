package com.example.geoQuiz.mapData;

import java.util.ArrayList;

/**
 * instructions for the business logic to work on the data
 * EDITOR and GAME indicate the context in which the method supposed to be called
 */
public interface Modifiable {

    /**
     * EDITOR
     * crate a new empty project
     * @param projectName project ID
     */
    void createProject(String projectName);

    /**
     * EDITOR
     * delete existing project or game
     * @param projectGameName project/game ID
     */
    void deleteProjectGame(String projectGameName);

    /**
     * EDITOR
     * convert project into game
     * @param projectName project ID
     */
    void publishProject(String projectName) ;

    /**
     * EDITOR/GAME
     * load project/game data from shared preferences
     * @param name name
     */
    void loadData(String name);

    /**
     * EDITOR
     * create marker in open project
     * @param latitude
     * @param longitude
     * @return marker ID
     */
    int createMarker(double latitude, double longitude);

    /**
     * EDITOR
     * set question and answer belonging to a specific marker
     * @param markerID
     * @param question
     * @param answer
     */
    void setQuestionAnswer(int markerID, String question, String answer);

    /**
     * EDITOR
     * delete a marker and related coordinates, question and answer
     * @param markerID
     */
    void deleteMarker(int markerID);

    /**
     * GAME
     * return answer related to a marker
     * @param markerID
     * @return answer
     */
    String getAnswer(int markerID);

    /**
     * EDITOR/GAME
     * write values of open project or game into local file
     */
    void save();

    /**
     * EDITOR/GAME
     * return a list of available projects
     * @return available projects
     */
    ArrayList<String> getAvailableProjectNames();

    /**
     * EDITOR/GAME
     * return a list of available games
     * @return available games
     */
    ArrayList<String> getAvailableGameNames();


    /**
     * EDITOR
     * get IDs of existing markers (ID of last Marker == amount)
     * @return amount
     */
    int getExistingMarkerAmount();

}




