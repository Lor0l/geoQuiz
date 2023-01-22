package com.example.geoQuiz.geoQuizUI;

import static com.example.geoQuiz.geoQuizEngine.GeoQuizEngine.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.geoQuiz.R;
import com.example.geoQuiz.geoQuizEngine.Commandable;
import com.example.geoQuiz.geoQuizEngine.EmptyProjectException;
import com.example.geoQuiz.geoQuizEngine.NameAlreadyExistsException;
import com.example.geoQuiz.mapData.MapData;
import com.example.geoQuiz.mapData.Providable;

import java.util.ArrayList;

/**
 * editor menu activity
 * shows all saved projects in a scroll view
 * with options to create, edit, publish and delete projects
 */
public class EditorMenuActivity extends AppCompatActivity {

    private Providable mapData;                 // connection to business logic component
    private Commandable geoQuizEngine;          // connection to engine

    private Button addNew;                      // button leading to newProjectDialog
    private AlertDialog newProjectDialog;       // dialog to enter new project name

    private LinearLayout layoutCardContainer;   // contains cards with project name and buttons for actions on that project
    private NotificationToaster toaster;        // for user notifications


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_menu);

        // set up MVC connections
        // ctx for shared preferences to load persistent data
        Context ctx = this.getApplicationContext();
        // mapData instance to get available projects for cards
        this.mapData = MapData.getMapDataInstance(ctx);
        // geoQuizEngine to load selected project before starting editor
        this.geoQuizEngine = makeCommandableInstance(MapData.getMapDataInstance(ctx));

        addNew = findViewById(R.id.editor_menu_button_new);
        layoutCardContainer = findViewById(R.id.editor_menu_card_container);
        buildDialogNewProject();

        // connect dialog with addNew button
        addNew.setOnClickListener(view -> newProjectDialog.show());

        // add cards with available projects to scroll view
        setUpCards();

        this.toaster = new NotificationToaster(ctx);
    }

    private void setUpCards(){
        ArrayList<String> availableProjects = this.mapData.getAvailableProjectNames();
        for (int i = 0; i < availableProjects.size(); i++) {
            addCard(availableProjects.get(i));
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // SCROLL VIEW WITH ADD CARD DIALOG

    private void buildDialogNewProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View viewDialog = getLayoutInflater().inflate(R.layout.dialog_editor_menu, null);

        EditText viewName = viewDialog.findViewById(R.id.editor_menu_dialog_project_name);

        builder.setView(viewDialog);
        // set on click listener for OK and CANCEL button in dialog
        builder.setTitle("Enter name")
                .setPositiveButton("OK", (dialogInterface, which) -> {
                    // try to create project with entered name
                    // may throw exception in case name is already taken
                    // if successful add card to scroll view
                    try {
                        geoQuizEngine.createProject(viewName.getText().toString());
                        addCard(viewName.getText().toString());
                    } catch (NameAlreadyExistsException e) {
                        this.toaster.notifyUserToast("name already exists");
                    }
                    // reset text view so it doesn't show previous input
                    // when dialog pops up next time
                    viewName.setText("");
                })
                .setNegativeButton("Cancel", (dialogInterface, which) -> {
                    viewName.setText("");
                });

        newProjectDialog = builder.create();

    }

    private void addCard(String projectName) {
        View viewCard = getLayoutInflater().inflate(R.layout.card_editor_menu, null);

        TextView viewName = viewCard.findViewById(R.id.editor_menu_card_game);

        Button edit = viewCard.findViewById(R.id.buttonEdit);
        Button delete = viewCard.findViewById(R.id.editor_menu_card_button_delete);
        Button publish = viewCard.findViewById(R.id.editor_menu_card_button_publish);

        viewName.setText(projectName);

        edit.setOnClickListener(v -> {
            // load business logic so the editor operates on correct project data
            geoQuizEngine.loadData(projectName);
            // entry point to EditorActivity
            openEditorActivity();
        });

        delete.setOnClickListener(v -> {
            geoQuizEngine.deleteProjectGame(projectName);
            layoutCardContainer.removeView(viewCard);
        });

        publish.setOnClickListener(v -> {
            try {
                geoQuizEngine.publishProject(projectName);
                layoutCardContainer.removeView(viewCard);
            } catch (EmptyProjectException e) {
               this.toaster.notifyUserToast("can't publish without marker");
            }
        });

        layoutCardContainer.addView(viewCard);

    }

    private void openEditorActivity() {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivity(intent);
    }

}