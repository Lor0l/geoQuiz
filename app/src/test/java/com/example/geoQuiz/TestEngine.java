package com.example.geoQuiz;

import com.example.geoQuiz.geoQuizEngine.Commandable;
import com.example.geoQuiz.geoQuizEngine.*;
import com.example.geoQuiz.geoQuizUI.Showable;
import com.example.geoQuiz.geoQuizUI.UIMock;
import com.example.geoQuiz.mapData.MapDataMock;
import com.example.geoQuiz.mapData.Modifiable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
public class TestEngine {

    @Before
    public void resetSingleton(){
        GeoQuizEngine.resetSingletonForTest();
    }

    @Test
    public void testSetCurrentActivityEditor() {

        int editorState = 1;
        int markerAmount = 3;
        int expectedOneCall = 1;

        Showable sm = new UIMock();
        Modifiable mdm = new MapDataMock();
        ((MapDataMock) mdm).setExistingMarkerAmount(markerAmount);
        Commandable geo = GeoQuizEngine.makeCommandableInstance(mdm);



        geo.setCurrentActivity(sm, editorState);
        Assert.assertEquals(expectedOneCall, ((UIMock) sm).getShowAllMarkersCount());
        Assert.assertEquals(markerAmount, ((UIMock) sm).getGetShowMarkersVal());

    }

    @Test
    public void testSetCurrentActivityGame() {

        int gameState = 2;
        int expectedOneCall = 1;
        int expectedVal = 0;

        Showable sm = new UIMock();
        Modifiable mdm = new MapDataMock();
        Commandable geo = GeoQuizEngine.makeCommandableInstance(mdm);

        geo.setCurrentActivity(sm,gameState);

        Assert.assertEquals(expectedOneCall, ((UIMock) sm).getShowOneMarkerCount());
        Assert.assertEquals(expectedVal, ((UIMock) sm).getShowOneMarkerVal());
    }

    @Test
    public void testCreateProjectNewName() throws NameAlreadyExistsException {

        ArrayList<String> noExistingNames = new ArrayList<>();
        String uniqueName = "TestProject";
        int expectedOneCall = 1;


        Modifiable mdm = new MapDataMock();
        ((MapDataMock) mdm).setAvailableProjectNames(noExistingNames);
        ((MapDataMock) mdm).setAvailableGameNames(noExistingNames);
        Commandable geo = GeoQuizEngine.makeCommandableInstance(mdm);

        geo.createProject(uniqueName);

        Assert.assertEquals(expectedOneCall, ((MapDataMock) mdm).getAvailableProjectNamesCount());
        Assert.assertEquals(expectedOneCall, ((MapDataMock) mdm).getCreateProjectCount());
        Assert.assertEquals(uniqueName, ((MapDataMock) mdm).getCreateProjectVal());

    }

    @Test(expected = NameAlreadyExistsException.class)
    public void testCreateProjectTakenName() throws NameAlreadyExistsException {

        String takenName = "TestProjectTaken";
        ArrayList<String> noExistingGameNames = new ArrayList<>();
        ArrayList<String> existingProjectNames = new ArrayList<>();
        existingProjectNames.add(takenName);
        int expectedOneCall = 1;

        Modifiable mdm = new MapDataMock();
        ((MapDataMock) mdm).setAvailableProjectNames(existingProjectNames);
        ((MapDataMock) mdm).setAvailableGameNames(noExistingGameNames);
        Commandable geo = GeoQuizEngine.makeCommandableInstance(mdm);

        geo.createProject(takenName);
        Assert.assertEquals(expectedOneCall, ((MapDataMock) mdm).getAvailableProjectNamesCount());

    }

    @Test
    public void testPublishProject2Markers() throws EmptyProjectException {

        int existingMarkers = 2;
        String someProject = "SomeProject2M";
        int expectedOneCall = 1;

        Modifiable mdm = new MapDataMock();
        ((MapDataMock) mdm).setExistingMarkerAmount(existingMarkers);
        Commandable geo = GeoQuizEngine.makeCommandableInstance(mdm);

        geo.publishProject(someProject);

        Assert.assertEquals(expectedOneCall, ((MapDataMock) mdm).getLoadDataCount());
        Assert.assertEquals(someProject, ((MapDataMock) mdm).getLoadDataVal());

        Assert.assertEquals(existingMarkers, ((MapDataMock) mdm).getExistingMarkerAmount());

        Assert.assertEquals(expectedOneCall, ((MapDataMock) mdm).getPublishProjetCount());
        Assert.assertEquals(someProject, ((MapDataMock) mdm).getPublishProjectVal());

    }

    @Test
    public void testPublishProject1Marker() throws EmptyProjectException {

        int existingMarkers = 1;
        String someProject = "SomeProject1M";
        int expectedOneCall = 1;

        Modifiable mdm = new MapDataMock();
        ((MapDataMock) mdm).setExistingMarkerAmount(existingMarkers);
        Commandable geo = GeoQuizEngine.makeCommandableInstance(mdm);

        geo.publishProject(someProject);

        Assert.assertEquals(expectedOneCall, ((MapDataMock) mdm).getLoadDataCount());
        Assert.assertEquals(someProject, ((MapDataMock) mdm).getLoadDataVal());

        Assert.assertEquals(existingMarkers, ((MapDataMock) mdm).getExistingMarkerAmount());

        Assert.assertEquals(expectedOneCall, ((MapDataMock) mdm).getPublishProjetCount());
        Assert.assertEquals(someProject, ((MapDataMock) mdm).getPublishProjectVal());
    }

    @Test(expected = EmptyProjectException.class)
    public void testPublishProject0Marker() throws EmptyProjectException {

        int existingMarkers = 0;
        String someProject = "SomeProject0M";
        int expectedOneCall = 1;
        int expectedNoCall = 0;

        Modifiable mdm = new MapDataMock();
        ((MapDataMock) mdm).setExistingMarkerAmount(existingMarkers);
        Commandable geo = GeoQuizEngine.makeCommandableInstance(mdm);

        geo.publishProject(someProject);

        Assert.assertEquals(expectedOneCall, ((MapDataMock) mdm).getLoadDataCount());
        Assert.assertEquals(someProject, ((MapDataMock) mdm).getLoadDataVal());

        Assert.assertEquals(existingMarkers, ((MapDataMock) mdm).getExistingMarkerAmount());

        Assert.assertEquals(expectedNoCall, ((MapDataMock) mdm).getPublishProjetCount());
        Assert.assertNull(someProject, ((MapDataMock) mdm).getPublishProjectVal());
    }

    @Test
    public void testCorrectAnswer() {

        int gameState = 2;
        int firstMarker = 0;
        int secondMarker = 1;
        String someCorrectAnswer = "this is correct";
        String sameCorrectAnswer = "THIS IS CORRECT";
        int expectedOneCall = 1;

        Showable sm = new UIMock();
        Modifiable mdm = new MapDataMock();
        ((MapDataMock) mdm).setCorrectAnswer(someCorrectAnswer);
        Commandable geo = GeoQuizEngine.makeCommandableInstance(mdm);

        geo.setCurrentActivity(sm, gameState);
        geo.answer(sameCorrectAnswer, firstMarker);

        Assert.assertEquals(expectedOneCall, ((MapDataMock) mdm).getCorrectAnswerCount());
        Assert.assertEquals(firstMarker, ((MapDataMock) mdm).getCorrectAnswerVal());

        Assert.assertEquals(secondMarker, ((UIMock) sm).getShowOneMarkerVal());
    }

    @Test
    public void testWrongAnswer() {

        int gameState = 2;
        int firstMarker = 0;
        String someCorrectAnswer = "this is correct";
        String someWrongAnswer = "this is not correct";
        int expectedOneCall = 1;
        int expectedNoCall = 0;

        Showable sm = new UIMock();
        Modifiable mdm = new MapDataMock();
        ((MapDataMock) mdm).setCorrectAnswer(someCorrectAnswer);
        Commandable geo = GeoQuizEngine.makeCommandableInstance(mdm);

        geo.setCurrentActivity(sm, gameState);
        geo.answer(someWrongAnswer, firstMarker);

        Assert.assertEquals(expectedOneCall, ((MapDataMock) mdm).getCorrectAnswerCount());
        Assert.assertEquals(firstMarker, ((MapDataMock) mdm).getCorrectAnswerVal());

        Assert.assertEquals(expectedOneCall, ((UIMock) sm).getWrongAnswerCount());

    }


}
