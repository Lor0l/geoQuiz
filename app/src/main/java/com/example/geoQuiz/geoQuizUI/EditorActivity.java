package com.example.geoQuiz.geoQuizUI;


import static com.example.geoQuiz.geoQuizEngine.GeoQuizEngine.makeCommandableInstance;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.geoQuiz.R;
import com.example.geoQuiz.geoQuizEngine.Commandable;
import com.example.geoQuiz.locationProvider.MyLocationProviderImpl;
import com.example.geoQuiz.mapData.MapData;
import com.example.geoQuiz.mapData.Providable;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

/**
 * editor activity
 * contains map to set marker with question and answer
 * marker can be set, changed(Q/A) and deleted
 *
 * implementing MapEventReceiver from the OSM framework allows to register clicks on map and get
 * corresponding coordinates
 *
 * code partially adopted by osmdoid on github
 * https://osmdroid.github.io/osmdroid/How-to-use-the-osmdroid-library.html
 */
public class EditorActivity extends AppCompatActivity implements Showable, MapEventsReceiver {

    Providable mapData;                                     // connection to business logic component
    Commandable geoQuizEngine;                              // connection to engine

    private AlertDialog dialogQA;                           // dialog to enter question and answer
    private int currentMarker;                              // allows to refer to marker in dialog (dialog is built before marker exist)

    private MapView map;

    private NotificationToaster toaster;                    // for user notifications

    private static final int EDITOR = 1;
    private static final int LATITUDE = 0;
    private static final int LONGITUDE = 1;
    private static final double START_ZOOM = 15.0;
    private static final GeoPoint START_HTW =
            new GeoPoint(52.457413879917695,
                    13.525632283884557);




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // set up MVC connections
        // ctx for shared preferences to load persistent data
        Context ctx = this.getApplicationContext();
        this.mapData = MapData.getMapDataInstance(ctx);
        this.geoQuizEngine = makeCommandableInstance(MapData.getMapDataInstance(ctx));
        // set connection between engine and location provider here since it is needed only in editor
        // engine registers as observer itself
        this.geoQuizEngine.setLocationProvider(new MyLocationProviderImpl(ctx));


        buildAndShowDialogQA();
        this.toaster = new NotificationToaster(ctx);

        // set up map view
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_editor);
        map = findViewById(R.id.editor_map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        IMapController mapController = map.getController();
        mapController.setZoom(START_ZOOM);
        mapController.setCenter(START_HTW);

        // register GUI as editor
        // invokes initialisation of map with existing markers
        this.geoQuizEngine.setCurrentActivity(this, EDITOR);

        // necessary to delegate events to the right place
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(0, mapEventsOverlay);

        // print current project name to head line
        TextView editorHead = findViewById(R.id.editor_head_project_name);
        editorHead.setText(this.mapData.getCurrentProjectGameName());

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
    public void onDestroy(){
        super.onDestroy();
        // save data permanently when leaving editor to be able to continue working on this project
        this.geoQuizEngine.save();
    }



    private void buildAndShowDialogQA(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View viewDialogQA = getLayoutInflater().inflate(R.layout.dialog_qa_editor, null);

        EditText viewQuestion = viewDialogQA.findViewById(R.id.editor_qa_dialog_question);
        EditText viewAnswer = viewDialogQA.findViewById(R.id.editor_qa_dialog_answer);

        // set hint so user knows what to enter
        viewQuestion.setHint("your question");
        viewAnswer.setHint("expected answer");

        // build dialog with on click listener for OK, CANCEL and DELETE buttons
        builder.setView(viewDialogQA);
        builder.setTitle("enter Q/A")
                .setPositiveButton("ok", (dialog, which) -> {
                    // check input for empty strings prevent marker without question or answer
                    String question = viewQuestion.getText().toString();
                    String answer = viewAnswer.getText().toString();
                    if (question.equals("") || answer.equals("")) {
                        this.toaster.notifyUserToast("Q/A missing");
                        deleteCurrentMarker();
                        // reset text view so it doesn't show previous input
                        // when dialog pops up next time
                        viewQuestion.setText("");
                        viewAnswer.setText("");
                    } else {
                        geoQuizEngine.putQuestionAnswer(currentMarker, question, answer);
                        viewQuestion.setText("");
                        viewAnswer.setText("");
                    }
                })
                .setNegativeButton("cancel", (dialog, which) -> {
                    // in case cancel is clicked when marker is just created so no Q/A exists remove marker
                    // if Q/A exist (dialog openes on repeated click) do nothing
                    String existingQuestion = mapData.getQuestion(currentMarker);
                    String existingAnswer = mapData.getAnswer(currentMarker);
                    if (existingAnswer == null || existingQuestion == null) {
                        deleteCurrentMarker();
                    }
                    viewQuestion.setText("");
                    viewAnswer.setText("");
                })
                .setNeutralButton("delete", (dialog, which) -> {
                    deleteCurrentMarker();
                    viewQuestion.setText("");
                    viewAnswer.setText("");
                });

        this.dialogQA = builder.create();

        // set on show listener to load previously entered text when repeatedly clicked on already set marker
        dialogQA.setOnShowListener(dialog -> {
            String existingQuestion = mapData.getQuestion(currentMarker);
            String existingAnswer = mapData.getAnswer(currentMarker);
            if (existingQuestion != null) {
                viewQuestion.setText(existingQuestion);
            }
            if (existingAnswer != null) {
                viewAnswer.setText(existingAnswer);
            }
        });
    }

    private void deleteCurrentMarker() {
        for (int i = 0; i < map.getOverlays().size(); i++) {
            // delete business objects
            geoQuizEngine.deleteMarker(currentMarker);
            // go threw map overlays and delete marker with matching id
            Overlay marker = map.getOverlays().get(i);
            if (marker instanceof Marker &&
                    Integer.parseInt(((Marker) marker).getId()) == currentMarker) {
                map.getOverlays().remove(marker);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // MAP EVENT RECEIVER

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint touchedPosition) {
        this.geoQuizEngine.placeMarkerManually(touchedPosition.getLatitude(),
                touchedPosition.getLongitude());
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        // on first long click location permission will be requested
        // if granted following long clicks will invoke engine to place marker on location
        // if not granted location permission will be requested again
//        if (hasLocationpermission()) {
//            this.geoQuizEngine.placeMarkerOnLocation();
//        } else {
//            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
//        }
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PERMISSION HANDLING FOR LOCATION ACCESS

    private boolean hasLocationpermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean isGranted) {
                            if (isGranted) {
                                toaster.notifyUserToast("Location permission granted");
                            } else {
                                toaster.notifyUserToast("Location permission not granted");
                            }
                        }
                    });

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // SHOWABLE IMPL

    @Override
    public void showOneMarker(int markerID) {
        double[] coordinates = this.mapData.getCoordinates(markerID);
        GeoPoint gp = new GeoPoint(coordinates[LATITUDE], coordinates[LONGITUDE]);
        placeMarkerOnMap(gp, markerID, true);
    }

    @Override
    public void showAllMarkers(int markerAmount) {
        for (int i = 0; i < markerAmount; i++) {
            // mind null slots of deleted markers
            // this is necessary for ID's to match list index
            // (there may be better solutions but this one does its job)
            double[] coordinates = this.mapData.getCoordinates(i);
            if (coordinates != null) {
                GeoPoint gp = new GeoPoint(coordinates[LATITUDE], coordinates[LONGITUDE]);
                placeMarkerOnMap(gp , i, false);
            }
        }
    }

    @Override
    public void wrongAnswer() {
        // does nothing in Editor
    }

    private void placeMarkerOnMap(GeoPoint geoPoint, int markerID, boolean byHand) {
        Marker newMarker = new Marker(map);
        newMarker.setId(String.valueOf(markerID));
        newMarker.setPosition(geoPoint);
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // set on click listener with connection to Q/A dialog so input can be edited
        newMarker.setOnMarkerClickListener((marker, mapView) -> {
            // set marker id temp globally when clicked on to be able to refer to it in dialog
            currentMarker = Integer.parseInt(marker.getId());
            dialogQA.show();
            return true;
        });

        // set marker id temp globally to be able to refer to it in dialog
        currentMarker = markerID;
        map.getOverlays().add(newMarker);

        // if marker set in context of setting up a already existing project no dialog for putting Q/A
        // is needed, when add new marker on click it is
        if (byHand) {
            dialogQA.show();
        }

    }

}