package com.example.geoQuiz.geoQuizUI;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.geoQuiz.R;
import com.example.geoQuiz.geoQuizEngine.Commandable;
import com.example.geoQuiz.geoQuizEngine.GeoQuizEngine;
import com.example.geoQuiz.mapData.MapData;
import com.example.geoQuiz.mapData.Providable;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;

/**
 * play activity
 * contains map where one marker at a time will be placed
 * a click on the marker will open a dialog to answer the related question
 * <p>
 * code partially adopted by osmdoid on github
 * https://osmdroid.github.io/osmdroid/How-to-use-the-osmdroid-library.html
 */
public class PlayActivity extends AppCompatActivity implements Showable {

    Providable mapData;                                     // connection to business logic
    Commandable geoQuizEngine;                              // connection to engine

    private AlertDialog dialogQA;                           // dialog displays question and requests answer
    private int currentMarker;                              // allows to refer to marker in dialog (dialog is built before marker exist)

    private MapView map;
    private IMapController mapController;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private NotificationToaster toaster;

    private static final int GAME = 2;
    private static final int LATITUDE = 0;
    private static final int LONGITUDE = 1;
    private static final double START_ZOOM = 15.0;
    private static final GeoPoint START_HTW =
            new GeoPoint(52.457413879917695,
                    13.525632283884557);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestPermissionsIfNecessary(
//                // if you need to show the current location, uncomment the line below
//                // Manifest.permission.ACCESS_FINE_LOCATION,
//                // WRITE_EXTERNAL_STORAGE is required in order to show the map
//                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,}
//        );

        // set up MVC connections
        // ctx for shared preferences to load persistent data
        Context ctx = this.getApplicationContext();
        this.mapData = MapData.getMapDataInstance(ctx);
        this.geoQuizEngine = GeoQuizEngine.makeCommandableInstance(MapData.getMapDataInstance(ctx));

        buildDialogQA();
        this.toaster = new NotificationToaster(ctx);

        // initialize map view
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //inflate and create the map
        setContentView(R.layout.activity_play);
        map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        this.mapController = map.getController();
        this.mapController.setZoom(START_ZOOM);
        this.mapController.setCenter(START_HTW);

        // register GUI as game
        // invokes initialisation of map with first marker only
        this.geoQuizEngine.setCurrentActivity(this, GAME);

        // print current project name in head line
        TextView playHead = findViewById(R.id.play_head_project_name);
        playHead.setText(this.mapData.getCurrentProjectGameName());

    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void buildDialogQA() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_qa_play, null);

        TextView viewQuestion = view.findViewById(R.id.play_qa_dialog_question);
        EditText viewAnswer = view.findViewById(R.id.play_qa_dialog_answer);

        // build dialog with on click listener for OK button to confirm input
        builder.setView(view);
        builder.setTitle("enter answer")
                .setPositiveButton("OK", (dialog, which) -> {
                    String answerString = viewAnswer.getText().toString();
                    // reset text view so it doesn't show previous input
                    // when dialog pops up next time
                    viewAnswer.setText("");
                    geoQuizEngine.answer(answerString, currentMarker);
                });

        dialogQA = builder.create();

        // set on show listener to put hint for user and
        // set related question
        dialogQA.setOnShowListener(dialog -> {
            viewAnswer.setHint("put answer here");
            viewQuestion.setText(mapData.getQuestion(currentMarker));
        });

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // SHOWABLE IMPL

    @Override
    public void showOneMarker(int markerID) {
        // remove previous marker if current marker is not the first
        if (markerID > 0) {
            for (int i = 0; i < map.getOverlays().size(); i++) {
                Overlay marker = map.getOverlays().get(i);
                if (marker instanceof Marker &&
                        Integer.parseInt(((Marker) marker).getId()) == currentMarker) {
                    map.getOverlays().remove(marker);
                    break;
                }
            }
        }

        // get coordinates of next marker
        // mind null slots of deleted markers
        // this is necessary for ID's to keep matching list index
        // (there may be better solutions but this one does its job)
        try {
            int nextID = markerID;
            double[] coordinates = null;
            // jump ID's of deleted markers
            while (coordinates == null) {
                coordinates = this.mapData.getCoordinates(nextID);
                nextID++;
            }
            placeMarkerOnMap(new GeoPoint(coordinates[LATITUDE], coordinates[LONGITUDE]),
                    nextID - 1);

            // when solved all markers of the game, the next call invokes a out of bounds exception
            // which is caught to notify user about and of game and close this activity
        } catch (IndexOutOfBoundsException e) {
            this.toaster.notifyUserToast("YOU WON");
            this.finish();
        }

    }

    @Override
    public void showAllMarkers(int markerAmount) {
        // not needed in PlayActivity
    }

    @Override
    public void wrongAnswer() {
        // when engine calls wrong answer an GUI the user will be notified and the same Q/A dialog
        // will be shown again
        this.toaster.notifyUserToast("wrong answer, try again");
        dialogQA.show();
    }

    private void placeMarkerOnMap(GeoPoint geoPoint, int markerID) {
        Marker newMarker = new Marker(map);
        newMarker.setId(String.valueOf(markerID));
        newMarker.setPosition(geoPoint);
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // set on click listener with connection to Q/A dialog so user can open dialog to answer the
        // question
        newMarker.setOnMarkerClickListener((marker, mapView) -> {
            // set marker id temp globally when clicked on to be able to refer to it in dialog
            currentMarker = Integer.parseInt(marker.getId());
            dialogQA.show();
            return false;
        });

        // set marker id temp globally to be able to refer to it in dialog
        this.currentMarker = markerID;
        map.getOverlays().add(newMarker);
        // set map center to new marker location
        this.mapController.setCenter(geoPoint);

    }

}