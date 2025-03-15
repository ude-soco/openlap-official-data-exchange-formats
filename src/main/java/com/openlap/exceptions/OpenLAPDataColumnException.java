package com.openlap.exceptions;

public class OpenLAPDataColumnException extends Exception {
  public static final String COLUMN_ALREADY_EXISTS = "Column already exists";

  public OpenLAPDataColumnException(String errorMessage, String columnId) {
    super(errorMessage + ": " + columnId);
  }

  public OpenLAPDataColumnException(String errorMessage) {
    super(errorMessage);
  }
}
