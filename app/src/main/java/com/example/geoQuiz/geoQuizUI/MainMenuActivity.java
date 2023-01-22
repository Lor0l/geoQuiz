package com.example.geoQuiz.geoQuizUI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.geoQuiz.R;

/**
 * main menu activity - program entry point
 * with buttons for navigation to the editor menu and game menu
 */
public class MainMenuActivity extends AppCompatActivity {

    private Button editor;
    private Button play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        editor = findViewById(R.id.button_main_menu_editor);
        play = findViewById(R.id.button_main_menu_play);

        editor.setOnClickListener(view -> openEditorMenuActivity());
        play.setOnClickListener(view -> openPlayMenuActivity());
    }

    private void openPlayMenuActivity() {
        Intent intent = new Intent(this, PlayMenuActivity.class);
        startActivity(intent);
    }

    private void openEditorMenuActivity() {
        Intent intent = new Intent(this, EditorMenuActivity.class);
        startActivity(intent);
    }

}
