package com.example.geoQuiz.geoQuizEngine;

import com.example.geoQuiz.locationProvider.MyLocationProvider;
import com.example.geoQuiz.geoQuizUI.Showable;

/**
 * view to control connection
 * EDITOR and GAME indicate the context in which the method supposed to be called
 */

public interface Commandable {

    /**
     * EDITOR/GAME
     * register current activity
     * sets state and invokes GUI to initialize game or editor
     * @param currentActivity
     * @param state           EDITOR = 1
     *                        GAME = 2
     */
    void setCurrentActivity(Showable currentActivity, int state);

    /**
     * EDITOR
     * set connection to devices GPS location provider
     * @param LocationProvider
     */
    void setLocationProvider(MyLocationProvider LocationProvider);

    /**
     * EDITOR
     * crate a new project
     * @param projectID project name
     * @throws NameAlreadyExistsException when this name is already taken by an existing project or game
     */
    void createProject(String projectID) throws NameAlreadyExistsException;

    /**
     * EDITOR
     * deletes existing project or game
     * @param projectGameID project/game name
     */
    void deleteProjectGame(String projectGameID);

    /**
     * EDITOR
     * convert project into game
     * @param projectID project name
     * @throws EmptyProjectException if project has no marker
     */
    void publishProject(String projectID) throws EmptyProjectException;

    /**
     * EDITOR/GAME
     * load project/game data
     * @param name project/game name
     */
    void loadData(String name);

    /**
     * EDITOR
     * save project attributes permanently
     */
    void save();

    /**
     * EDITOR
     * create new marker in currently open project on given coordinates
     * @param latitude
     * @param longitude
     */
    void placeMarkerManually(double latitude, double longitude);

    /**
     * EDITOR
     * create marker on GPS location in currently open project
     */
    void placeMarkerOnLocation() ;

    /**
     * EDITOR
     * set question and answer belonging to a specific marker in open project
     * @param markerID
     * @param question
     * @param answer
     */
    void putQuestionAnswer(int markerID, String question, String answer);

    /**
     * EDITOR
     * delete a marker with related coordinates, question and answer in open project
     * @param markerID
     */
    void deleteMarker(int markerID);

    /**
     * GAME
     * check if given answer matches saved answer (case insensitive)
     * @param answer   answer given by user
     * @param markerID marker where answer belongs to
     */
    void answer(String answer, int markerID);

}
