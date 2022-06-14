package com.javarush.island.model.common.exceptions;

public class ObjectInitializationException extends RuntimeException {

    private String errorMessage;

    public ObjectInitializationException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
