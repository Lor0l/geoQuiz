package com.example.geoQuiz.mapData;

/**
 * represents a geoQuiz marker with all related information
 */
public class MapMarker {

    private int id;
    private double latitude;
    private double longitude;
    private String question;
    private String answer;

    /**
     * creates a Marker with given id and coordinates
     * @param id
     * @param latitude
     * @param longitude
     */
    MapMarker(int id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

}
