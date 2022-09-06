package com.javarush.island.model.common.exceptions;

public class AnimalTravellingException extends RuntimeException {

    private final String errorMessage;

    public AnimalTravellingException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
