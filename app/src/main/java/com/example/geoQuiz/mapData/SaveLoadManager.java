package com.example.geoQuiz.mapData;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * deals with persisting data using shared Preferences
 */
public class SaveLoadManager {

    // key parts for identifying shared prefs
    private static final String SHARED_PREF = "geoquiz";
    private static final String SAVE_PROJ = "geoquiz_projects";
    private static final String SAVE_GAMES = "geoquiz_games";
    private static final String MARKER_AMOUNT = "marker_amount";

    private SharedPreferences sp;

    /**
     * creates an instance of the save load manager and initializes the connection to shared preferences
     * @param ctx activity context
     */
    public SaveLoadManager(Context ctx){
        this.sp = ctx.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
    }

    /**
     * @return loadet set of all available project names
     */
    public Set<String> loadProjects() {
        return sp.getStringSet(SAVE_PROJ, new HashSet<>());
    }

    /**
     * @return loadet set of all available game names
     */
    public Set<String> loadGames() {
        return sp.getStringSet(SAVE_GAMES, new HashSet<>());
    }

    /**
     * update the set of saved project names
     * @param availableProjects
     */
    public void saveProjects(List<String> availableProjects) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putStringSet(SAVE_PROJ, new HashSet<>(availableProjects));
        editor.apply();
    }

    /**
     * update the set of all saved names
     * @param availableGames
     */
    public void saveGames(List<String> availableGames) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putStringSet(SAVE_GAMES, new HashSet<>(availableGames));
        editor.apply();
    }

    /**
     * deletes all shared preferences of one project/game
     * @param currentProjectGame
     */
    public void removeSharedPrefs(String currentProjectGame) {
        SharedPreferences.Editor editor = this.sp.edit();
        int markerAmount = sp.getInt(currentProjectGame + "-" + MARKER_AMOUNT, 0);
        for (int i = 0; i < markerAmount; i++) {
            editor.remove(currentProjectGame + "-" + i + "-" + "id");
            editor.remove(currentProjectGame + "-" + i + "-" + "lat");
            editor.remove(currentProjectGame + "-" + i + "-" + "lon");
            editor.remove(currentProjectGame + "-" + i + "-" + "question");
            editor.remove(currentProjectGame + "-" + i + "-" + "answer");
            editor.remove(currentProjectGame + "-" + MARKER_AMOUNT);
            editor.apply();
        }
    }

    /**
     * load all attribute values of all markers related to one project/game from shared preferences
     * and build markers
     * puts null values to the index of a deleted marker
     * @param currentProjectGame project/game name for unique key
     * @return list with all loaded markers
     */
    public ArrayList<MapMarker> loadMarkers(String currentProjectGame) {
        ArrayList<MapMarker> loadedMarkers = new ArrayList<>();
        int markerAmount = sp.getInt(currentProjectGame + "-" + MARKER_AMOUNT, 0);
        for (int i = 0; i < markerAmount; i++) {
            int id = sp.getInt(currentProjectGame + "-" + i + "-" + "id", -1);
            if (id >= 0) {
                String lat = sp.getString(currentProjectGame + "-" + i + "-" + "lat", "latLeer");
                String lon = sp.getString(currentProjectGame + "-" + i + "-" + "lon", "lonLeer");
                String question = sp.getString(currentProjectGame + "-" + i + "-" + "question", "leer");
                String answer = sp.getString(currentProjectGame + "-" + i + "-" + "answer", "leer");
                MapMarker marker = new MapMarker(id, Double.parseDouble(lat), Double.parseDouble(lon));
                marker.setQuestion(question);
                marker.setAnswer(answer);
                loadedMarkers.add(marker);
            } else {
                loadedMarkers.add(null);
            }
        }
        return loadedMarkers;
    }

    /**
     * save all attribute values of all markers of one project/game to shared preferences
     * @param markers2Save marker objetcs to persit
     * @param Project2Save project/game name for unique key
     */
    public void saveMarkers(ArrayList<MapMarker> markers2Save, String Project2Save) {
        SharedPreferences.Editor editor = this.sp.edit();
        editor.putInt(Project2Save + "-" + MARKER_AMOUNT, markers2Save.size());
        for (int i = 0; i < markers2Save.size(); i++) {
            MapMarker marker = markers2Save.get(i);
            if (!(marker == null)) {
                // ID
                editor.putInt(Project2Save + "-" + i + "-" + "id", i);
                // LAT
                String lat = String.valueOf(marker.getLatitude());
                editor.putString(Project2Save + "-" + i + "-" + "lat", lat);
                // LON
                String lon = String.valueOf(marker.getLongitude());
                editor.putString(Project2Save + "-" + i + "-" + "lon", lon);
                // QUESTION
                String question = marker.getQuestion();
                editor.putString(Project2Save + "-" + i + "-" + "question", question);
                // ANSWER
                String answer = marker.getAnswer();
                editor.putString(Project2Save + "-" + i + "-" + "answer", answer);
            } else {
                // if save data for null(deleted marker) exists remove them
                editor.remove(Project2Save + "-" + i + "-" + "id");
                editor.remove(Project2Save + "-" + i + "-" + "lat");
                editor.remove(Project2Save + "-" + i + "-" + "lon");
                editor.remove(Project2Save + "-" + i + "-" + "question");
                editor.remove(Project2Save + "-" + i + "-" + "answer");
            }
        }
        editor.apply();
    }
}
