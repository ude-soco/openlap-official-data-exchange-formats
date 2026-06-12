package com.openlap.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Types that can be used on OpenLAPColumnConfigData. Ideally, they should correspond to DataBase
 * primitive types
 */
// public enum OpenLAPColumnDataType {
//    BYTE,
//    SHORT,
//    STRING,
//    INTEGER,
//    BOOLEAN,
//    LONG,
//    FLOAT,
//    LOCAL_DATE_TIME,
//    CHAR
// }
public enum OpenLAPColumnDataType {
  Text("STRING"),
  Numeric("INTEGER"),
  TrueFalse("BOOLEAN");

  private final String legacyName;

  OpenLAPColumnDataType(String legacyName) {
    this.legacyName = legacyName;
  }

  @JsonCreator
  public static OpenLAPColumnDataType fromJson(String value) {
    if (value == null) {
      return null;
    }

    String normalized = value.trim();
    for (OpenLAPColumnDataType type : values()) {
      if (type.name().equalsIgnoreCase(normalized)
          || type.legacyName.equalsIgnoreCase(normalized)) {
        return type;
      }
    }

    switch (normalized.toUpperCase()) {
      case "BYTE":
      case "SHORT":
      case "LONG":
      case "FLOAT":
        return Numeric;
      default:
        throw new IllegalArgumentException("Unsupported OpenLAP column data type: " + value);
    }
  }

  @Override
  public String toString() {
    // Kept for legacy validation messages; Jackson's default JSON output uses enum names.
    return legacyName;
  }
}
