package com.example.geoQuiz.geoQuizUI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.geoQuiz.R;
import com.example.geoQuiz.geoQuizEngine.Commandable;
import com.example.geoQuiz.geoQuizEngine.GeoQuizEngine;
import com.example.geoQuiz.mapData.MapData;
import com.example.geoQuiz.mapData.Providable;

import java.util.ArrayList;

/**
 * play menu activity
 * shows all existing games in a scroll view
 * with options to play and delete games
 */
public class PlayMenuActivity extends AppCompatActivity {

    Providable mapData;                     // connection to business logic
    Commandable geoQuizEngine;              // connection to engine

    LinearLayout layoutCardContainer;       // contains cards with game name and buttons for actions on that game

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_menu);

        // set up MVC connections
        // ctx for shared preferences to load persistent data
        Context ctx = this.getApplicationContext();
        this.mapData = MapData.getMapDataInstance(ctx);
        this.geoQuizEngine = GeoQuizEngine.makeCommandableInstance(MapData.getMapDataInstance(ctx));

        layoutCardContainer = findViewById(R.id.containerGame);
        // add cards with available games to scroll view
        setUpCards();
    }

    private void setUpCards(){
        ArrayList<String> availableGames = this.mapData.getAvailableGameNames();
        for (int i = 0; i < availableGames.size(); i++) {
            addCard(availableGames.get(i));
        }
    }

    private void addCard(String gameName) {
        View view = getLayoutInflater().inflate(R.layout.card_play_menu, null);

        TextView nameView = view.findViewById(R.id.play_menu_card_game);
        Button play = view.findViewById(R.id.play_menu_card_button_play);
        Button delete = view.findViewById(R.id.play_menu_card_button_delete);

        nameView.setText(gameName);

        play.setOnClickListener(v -> {
            // load business logic so the program operates on correct game data
            geoQuizEngine.loadData(gameName);
            // entrypoint to play activity
            openPlayActivity(gameName);
        });

        delete.setOnClickListener(v -> {
            geoQuizEngine.deleteProjectGame(gameName);
            layoutCardContainer.removeView(view);
        });

        layoutCardContainer.addView(view);

    }

    private void openPlayActivity(String gameName) {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

}

