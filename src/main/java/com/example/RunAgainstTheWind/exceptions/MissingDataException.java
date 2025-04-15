package com.example.RunAgainstTheWind.exceptions;

/**
 * Thrown when required data is missing or incomplete during processing.
 */
public final class MissingDataException extends RuntimeException {

    public MissingDataException(String message) {
        super(message);
    }
}