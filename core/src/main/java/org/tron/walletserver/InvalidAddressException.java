package org.tron.walletserver;

public class InvalidAddressException extends Exception {
    public InvalidAddressException(String message) {
        super(message);
    }
}
