package com.example.geoQuiz.mapData;

import java.util.ArrayList;

public class MapDataMock implements Modifiable{

    private int existingMarkerAmount;
    private ArrayList<String> availableProjectNames;
    private ArrayList<String> availableGameNames;
    private int getCreateProjectCount = 0;
    private String createProjectVal;
    private int getAvailableProjectNamesCount = 0;
    private int getAvailableGameNamesCount = 0;
    private int loadDataCount = 0;
    private String loadDataVal;
    private int publishProjetCount = 0 ;
    private String publishProjectVal;
    private int getAnswerCount = 0;
    private String correctAnswer;
    private int getAnswerVal;


    public void setExistingMarkerAmount(int existingMarkerAmount) {
        this.existingMarkerAmount = existingMarkerAmount;
    }

    public void setAvailableProjectNames(ArrayList<String> availableProjectNames) {
        this.availableProjectNames = availableProjectNames;
    }

    public void setAvailableGameNames(ArrayList<String> availableGameNames) {
        this.availableGameNames = availableGameNames;
    }

    public int getAvailableGameNamesCount() {
        return this.getAvailableGameNamesCount;
    }

    public int getAvailableProjectNamesCount() {
        return this.getAvailableProjectNamesCount;
    }

    public int getCreateProjectCount() {
        return this.getCreateProjectCount;
    }

    public String getCreateProjectVal() {
        return this.createProjectVal;
    }

    public int getLoadDataCount() {
        return this.loadDataCount;
    }

    public String getLoadDataVal() {
        return this.loadDataVal;
    }

    public int getPublishProjetCount() {
        return this.publishProjetCount;
    }

    public String getPublishProjectVal() {
        return this.publishProjectVal;
    }

    public void setCorrectAnswer(String answer) {
        this.correctAnswer = answer;
    }

    public int getCorrectAnswerCount() {
        return this.getAnswerCount;
    }

    public int getCorrectAnswerVal() {
        return this.getAnswerVal;
    }

    @Override
    public void createProject(String projectName) {
        this.getCreateProjectCount++;
        this.createProjectVal = projectName;

    }

    @Override
    public void deleteProjectGame(String projectGameName) {

    }

    @Override
    public void publishProject(String projectName) {
        this.publishProjetCount++;
        this.publishProjectVal = projectName;

    }

    @Override
    public void loadData(String name) {
        this.loadDataCount++;
        this.loadDataVal = name;
    }

    @Override
    public int createMarker(double latitude, double longitude) {
        return 0;
    }

    @Override
    public void setQuestionAnswer(int markerID, String question, String answer) {

    }

    @Override
    public void deleteMarker(int markerID) {

    }

    @Override
    public String getAnswer(int markerID) {
        this.getAnswerCount++;
        this.getAnswerVal = markerID;
        return this.correctAnswer;
    }

    @Override
    public void save() {

    }

    @Override
    public ArrayList<String> getAvailableProjectNames() {
        this.getAvailableProjectNamesCount++;
        return this.availableProjectNames;
    }

    @Override
    public ArrayList<String> getAvailableGameNames() {
        this.getAvailableGameNamesCount++;
        return this.availableGameNames;
    }

    @Override
    public int getExistingMarkerAmount() {
        return this.existingMarkerAmount;
    }
}
