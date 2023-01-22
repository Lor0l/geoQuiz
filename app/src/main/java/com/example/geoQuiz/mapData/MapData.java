package com.example.geoQuiz.mapData;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * contains the business logic
 * loads and saves data permanently
 * holds all marker objects of the currently loaded project/game (only one project/game at the same time)
 */
public class MapData implements Modifiable, Providable {


    private static MapData instance;                // saves instance as part of the singleton pattern

    private int nextMarkerID;                       // keeps track of taken markerIDs

    private List<String> availableProjects;         // contains all saved project names
    private List<String> availableGames;            // contains all saved game names
    private String currentProjectGame;              // name of the currently loaded project/game
    private ArrayList<MapMarker> loadedMarkers;     // contains all marker objects of the loaded project/game

    private SaveLoadManager saveLoadManager;        // instance that deals with shared preferences




    // private constructor as part of singleton pattern
    // sets up shared preference connection and loads all saved project/game names
    private MapData(Context ctx) {
        this.saveLoadManager = new SaveLoadManager(ctx);
        Set<String> loadetProjects = this.saveLoadManager.loadProjects();
        Set<String> loadetGames = this.saveLoadManager.loadGames();
        this.availableProjects = new ArrayList<>(loadetProjects);
        this.availableGames = new ArrayList<>(loadetGames);
    }

    /**
     * obtain runtime-unique map data instance as part of singleton pattern
     * @param ctx acitity context for shared preferences
     * @return runtime-unique map data instance
     */
    public static MapData getMapDataInstance(Context ctx) {
        if (MapData.instance == null) {
            MapData.instance = new MapData(ctx);
        }
        return MapData.instance;
    }

    @Override
    public ArrayList<String> getAvailableProjectNames() {
        return new ArrayList<>(this.availableProjects);
    }

    @Override
    public ArrayList<String> getAvailableGameNames() {
        return new ArrayList<>(this.availableGames);
    }

    @Override
    public String getCurrentProjectGameName() {
        return this.currentProjectGame;
    }

    @Override
    public void createProject(String projectName) {
        availableProjects.add(projectName);
        // overwrite shared preferences with updated list
        this.saveLoadManager.saveProjects(this.availableProjects);

    }

    @Override
    public void deleteProjectGame(String projectGameName) {
        this.currentProjectGame = projectGameName;

        // remove shared prefs so they don't collide with a new project with the same name
        this.saveLoadManager.removeSharedPrefs(currentProjectGame);

        // go threw lists and remove name
        this.availableProjects = this.availableProjects
                .stream()
                .filter(name -> !name.equals(projectGameName))
                .collect(Collectors.toList());
        this.availableGames = this.availableGames
                .stream()
                .filter(name -> !name.equals(projectGameName))
                .collect(Collectors.toList());

        // overwrite shared preferences with updated list
        this.saveLoadManager.saveProjects(this.availableProjects);
        this.saveLoadManager.saveGames(this.availableGames);
    }

    @Override
    public void publishProject(String projectName) {
        // move project name to game list
        for (int i = 0; i < this.availableProjects.size(); i++) {
            String tmp = this.availableProjects.get(i);
            if (tmp.equals(projectName)) {
                this.availableProjects.remove(i);
                break;
            }
        }
        this.availableGames.add(projectName);
        // overwrite shared preferences with updated list
        this.saveLoadManager.saveProjects(this.availableProjects);
        this.saveLoadManager.saveGames(this.availableGames);
    }

    @Override
    public void loadData(String name) {
        // save loaded project/game globally so we know everywhere what we are working on
        this.currentProjectGame = name;
        this.loadedMarkers =  this.saveLoadManager.loadMarkers(currentProjectGame);
        this.nextMarkerID = this.loadedMarkers.size();
    }

    @Override
    public int createMarker(double latitude, double longitude) {
//        this.nextMarkerID = this.loadedMarkers.size();
        MapMarker nextMarker = new MapMarker(this.nextMarkerID, latitude, longitude);
        loadedMarkers.add(nextMarker);
        this.nextMarkerID++;
        return this.nextMarkerID - 1;
    }

    @Override
    public void setQuestionAnswer(int markerID, String question, String answer) {
        MapMarker marker2Modify = this.loadedMarkers.get(markerID);
        marker2Modify.setQuestion(question);
        marker2Modify.setAnswer(answer);
    }

    @Override
    public void deleteMarker(int markerID) {
        // overwrite marker with null to keep id equally to array index
        this.loadedMarkers.set(markerID, null);
    }

    @Override
    public void save() {
        this.saveLoadManager.saveMarkers(this.loadedMarkers, this.currentProjectGame);
    }

    @Override
    public int getExistingMarkerAmount() {
        return this.loadedMarkers.size();
    }

    @Override
    public String getAnswer(int markerID) {
        return this.loadedMarkers.get(markerID).getAnswer();
    }


    @Override
    public String getQuestion(int markerID) {
        return this.loadedMarkers.get(markerID).getQuestion();
    }

    @Override
    public double[] getCoordinates(int markerID) {
        // mind null slots of deleted markers
        MapMarker marker = this.loadedMarkers.get(markerID);
        if (marker == null) {
            return null;
        }
        double[] latLon = {marker.getLatitude(), marker.getLongitude()};
        return latLon;
    }

}


