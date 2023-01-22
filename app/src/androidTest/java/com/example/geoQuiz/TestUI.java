package com.example.geoQuiz;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.Matchers.*;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.geoQuiz.geoQuizUI.EditorMenuActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestUI {

    /**
     * entrypoint for following tests
     */
    @Rule
    public ActivityScenarioRule<EditorMenuActivity> activityRule =
            new ActivityScenarioRule<>(EditorMenuActivity.class);
    /**
     * create new project with unique name
     * then check if layout with this name exists
     */
    @Test
    public void testNewProject(){
        String projectName = "TestProject1";
        //create card
        onView(withId(R.id.editor_menu_button_new)).perform(click());
        onView(withId(R.id.editor_menu_dialog_project_name)).perform(typeText(projectName));
        onView(withText("OK")).perform(click());
        //check if exists
        onView(withId(R.id.editor_menu_card_game)).check(matches(withText(projectName)));

        //clean cards for next test
        onView(withId(R.id.editor_menu_card_button_delete)).perform(click());
    }

    /**
     * creates and deletes a project with unique name then checks if new project with same name can
     * be created
     */
    @Test
    public void testDeleteProject(){
        //doesnt work with more projects in list
        String projectName = "TestProject2";
        //create card
        onView(withId(R.id.editor_menu_button_new)).perform(click());
        onView(withId(R.id.editor_menu_dialog_project_name)).perform(typeText(projectName));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.editor_menu_card_game)).check(matches(withText(projectName)));
        //delete card
        onView(withId(R.id.editor_menu_card_button_delete)).perform(click());
        onView(withId(R.id.editor_menu_card_game)).check(doesNotExist());
        //add card with same name
        onView(withId(R.id.editor_menu_button_new)).perform(click());
        onView(withId(R.id.editor_menu_dialog_project_name)).perform(clearText());
        onView(withId(R.id.editor_menu_dialog_project_name)).perform(typeText(projectName));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.editor_menu_card_game)).check(matches(withText(projectName)));



        //clean cards for next test
        onView(withId(R.id.editor_menu_card_button_delete)).perform(click());
    }






}
