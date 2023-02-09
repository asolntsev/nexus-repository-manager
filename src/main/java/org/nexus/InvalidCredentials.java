package org.nexus;

public class InvalidCredentials extends IllegalArgumentException {
  public InvalidCredentials(String message) {
    super(message);
  }
}
