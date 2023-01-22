package com.example.geoQuiz;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.geoQuiz.geoQuizUI.MainMenuActivity;

import org.junit.Rule;
import org.junit.Test;

public class TestUIMain {
    @Rule
    public ActivityScenarioRule<MainMenuActivity> activityRule =
            new ActivityScenarioRule<>(MainMenuActivity.class);
    @Test
    public void testmain() {

        onView(withId(R.id.button_main_menu_editor)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.button_main_menu_editor)).check(matches(isDisplayed()));

    }

    @Test
    public void saveLoad() {

        String projectName = "TestProject2";

        onView(withId(R.id.button_main_menu_editor)).perform(click());
//        onView(withId(R.id.addNew)).perform(click());
//        onView(withId(R.id.newGameName)).perform(typeText(projectName));
//        onView(withText("OK")).perform(click());
        onView(withId(R.id.buttonEdit)).perform(click());
    }
}
