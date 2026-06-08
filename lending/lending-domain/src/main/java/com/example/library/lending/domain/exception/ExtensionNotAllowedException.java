package com.example.library.lending.domain.exception;

public class ExtensionNotAllowedException extends RuntimeException {

  public ExtensionNotAllowedException(String reason) {
    super("Extension not allowed: " + reason);
  }
}
