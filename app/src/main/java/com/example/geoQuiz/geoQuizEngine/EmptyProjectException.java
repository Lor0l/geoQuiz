package com.example.geoQuiz.geoQuizEngine;

public class EmptyProjectException extends Exception {
    public EmptyProjectException() { super(); }
    public EmptyProjectException(String message) { super(message); }
    public EmptyProjectException(String message, Throwable t) { super(message, t); }
}
