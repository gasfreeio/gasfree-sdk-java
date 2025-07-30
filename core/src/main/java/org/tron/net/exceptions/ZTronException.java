package org.tron.net.exceptions;

import org.tron.common.utils.abi.TronException;



public class ZTronException extends TronException {

    public ZTronException() {
        super();
    }

    public ZTronException(String message) {
        super(message);
    }

    public ZTronException(String message, Throwable cause) {
        super(message, cause);
    }
}
