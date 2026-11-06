package org.tron.gasfree.sdk;

public class GasFreeException extends Exception {
    public GasFreeException(String message) {
        super(message);
    }

    public GasFreeException(String message, Throwable cause) {
        super(message, cause);
    }
}
