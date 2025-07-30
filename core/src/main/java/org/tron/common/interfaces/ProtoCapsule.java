package org.tron.common.interfaces;

public interface ProtoCapsule<T> {

  byte[] getData();

  T getInstance();
}
