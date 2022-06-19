package com.javarush.island.model.common.exceptions;

public class AnimalReproducingException extends RuntimeException {

    private final String errorMessage;

    public AnimalReproducingException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
