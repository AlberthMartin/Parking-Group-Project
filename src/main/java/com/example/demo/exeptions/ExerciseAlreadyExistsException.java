package com.example.demo.exeptions;

public class ExerciseAlreadyExistsException extends RuntimeException {
    public ExerciseAlreadyExistsException(String message) {
        super(message);
    }
}
