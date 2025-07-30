package org.tron.walletserver;

public class ConnectErrorException extends Exception {
    public ConnectErrorException(String message) {
        super(message);
    }
}
