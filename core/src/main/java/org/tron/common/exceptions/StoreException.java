package org.tron.common.exceptions;

import org.tron.common.utils.abi.TronException;

public class StoreException extends TronException {

  public StoreException() {
    super();
  }

  public StoreException(String message) {
    super(message);
  }

  public StoreException(String message, Throwable cause) {
    super(message, cause);
  }
}
