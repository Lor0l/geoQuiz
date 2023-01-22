package com.example.geoQuiz.geoQuizUI;

/**
 * mock object for unit testing purposes
 */
public class UIMock implements Showable {

    private int showOneMarkerCount = 0;
    private int showOneMarkerVal;
    private int showAllMarkersCount = 0;
    private int showAllMarkersVal;
    private int wrongAnswerCount = 0;


    @Override
    public void showOneMarker(int markerID) {
        this.showOneMarkerCount++;
        this.showOneMarkerVal = markerID;
    }

    @Override
    public void showAllMarkers(int markerAmount) {
        this.showAllMarkersCount++;
        this.showAllMarkersVal = markerAmount;
    }

    @Override
    public void wrongAnswer() {
        this.wrongAnswerCount++;
    }

    /**
     * @return amount of showOneMarker method calls
     */
    public int getShowOneMarkerCount() {
        return this.showOneMarkerCount;
    }

    /**
     * @return last showOneMarker method parameter
     */
    public int getShowOneMarkerVal() {
        return this.showOneMarkerVal;
    }

    /**
     * @return amount of showAllMarkers method calls
     */
    public int getShowAllMarkersCount() {
        return this.showAllMarkersCount;
    }

    /**
     * @return last showAllMarkers methos parameter
     */
    public int getGetShowMarkersVal() {
        return this.showAllMarkersVal;
    }

    /**
     * @return amount of wrongAnswer method calls
     */
    public int getWrongAnswerCount() {
        return this.wrongAnswerCount;
    }
}
