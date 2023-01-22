package com.example.geoQuiz.geoQuizEngine;

import com.example.geoQuiz.mapData.Modifiable;
import com.example.geoQuiz.locationProvider.MyLocationProvider;
import com.example.geoQuiz.geoQuizUI.Showable;



import java.util.ArrayList;

/**
 * game engine representing the controller
 * receives input from GUI and initialize changes on business logic
 */
public class GeoQuizEngine implements Commandable, LocationListener {

    private static Commandable instance;        // saves instance as part of the singleton pattern
    private static final int LATITUDE = 0;
    private static final int LONGITUDE = 1;
    private static final int EDITOR = 1;
    private static final int GAME = 2;

    private Showable currentActivity;           // connection to view component for update calls on running activity
    private Modifiable mapData;                 // connection to business logic component
    private MyLocationProvider locationProvider;  // connection to location provider of device for obtaining GPS location




    // private constructor as part of singleton pattern
    private GeoQuizEngine(Modifiable mapData) {
        this.mapData = mapData;
    }

    /**
     * obtain runtime-unique engine instance as part of singleton pattern
     * first call sets up program and connects GUI, engine and business logic instances
     * @param mapData business logic instance
     * @return runtime-unique engine instance
     */
    public static Commandable makeCommandableInstance(Modifiable mapData) {
        if (GeoQuizEngine.instance == null) {
            GeoQuizEngine.instance = new GeoQuizEngine(mapData);
        }
        return GeoQuizEngine.instance;
    }

    @Override
    public void setCurrentActivity(Showable currentActivity, int state) {
        this.currentActivity = currentActivity;
        // different update calls on GUI depending on activity
        if (state == EDITOR) {
            // call GUI to show all existing markers on editor start
            currentActivity.showAllMarkers(this.mapData.getExistingMarkerAmount());
        }
        if (state == GAME) {
            // call GUI to show first marker on game start
            currentActivity.showOneMarker(0);
        }
    }

    @Override
    public void setLocationProvider(MyLocationProvider locationProvider) {
        this.locationProvider = locationProvider;
        this.locationProvider.registerObserver(this);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // EDITOR

    @Override
    public void createProject(String projectID) throws NameAlreadyExistsException {
        // add all taken names to a list
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> projectNames = mapData.getAvailableProjectNames();
        ArrayList<String> gameNames = mapData.getAvailableGameNames();
        if (!projectNames.isEmpty()) {
            names.addAll(projectNames);
        }
        if (!gameNames.isEmpty()) {
            names.addAll(gameNames);
        }
        // check if requested name is unique
        boolean existsAlready = names.stream().anyMatch(project -> project.equals(projectID));
        if (existsAlready) {
            throw new NameAlreadyExistsException();
        }
        // create project if requested name is unique
        mapData.createProject(projectID);
    }

    @Override
    public void deleteProjectGame(String projectGameID) {
        this.mapData.deleteProjectGame(projectGameID);
    }

    @Override
    public void publishProject(String projectID) throws EmptyProjectException {
        this.mapData.loadData(projectID);
        if(this.mapData.getExistingMarkerAmount() < 1) {
            throw new EmptyProjectException();
        }
        this.mapData.publishProject(projectID);
    }

    @Override
    public void loadData(String name) {
        mapData.loadData(name);
    }

    @Override
    public void placeMarkerManually(double latitude, double longitude) {
        // call business logic to create new marker object
        // and call GUI to show it on map
        int id = mapData.createMarker(latitude, longitude);
        currentActivity.showOneMarker(id);
    }

    @Override
    public void placeMarkerOnLocation() {
        locationProvider.getCurrentLocation();
        // when location accessible, locationAvailableListener will be called
    }

    // LocationObserver method
    @Override
    public void locationAvailableListener(double[] coordinates) {
        int id = mapData.createMarker(coordinates[LATITUDE], coordinates[LONGITUDE]);
        currentActivity.showOneMarker(id);
    }


    @Override
    public void putQuestionAnswer(int markerID, String question, String answer) {
        mapData.setQuestionAnswer(markerID, question, answer);
    }

    @Override
    public void deleteMarker(int markerID) {
        mapData.deleteMarker(markerID);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // GAME

    @Override
    public void answer(String answer, int markerID) {
        String expectedAnswer = mapData.getAnswer(markerID);
        // update call on GUI depending on weather users answer is:
        // correct      (unlock next marker)
        // not correct  (continue with same marker)
        if (expectedAnswer.equalsIgnoreCase(answer)) {
            currentActivity.showOneMarker(markerID + 1);
        } else {
            currentActivity.wrongAnswer();
        }
    }

    @Override
    public void save() {
        this.mapData.save();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // FOR UNIT TESTING PURPOSES

    /**
     * reset engine instance to null for set up different test scenarios
     */
    public static void resetSingletonForTest() {
        GeoQuizEngine.instance = null;
    }


}

