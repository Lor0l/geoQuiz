package com.example.geoQuiz.geoQuizEngine;

public class NameAlreadyExistsException extends Exception {
    public NameAlreadyExistsException() { super(); }
    public NameAlreadyExistsException(String message) { super(message); }
    public NameAlreadyExistsException(String message, Throwable t) { super(message, t); }
}
