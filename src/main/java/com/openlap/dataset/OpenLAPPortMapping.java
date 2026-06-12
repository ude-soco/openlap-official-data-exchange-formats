package com.openlap.dataset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

/**
 * This class represents a tuple or 'mapping' entry between two ports. It has a outputPort which
 * represents an entry of the configuration of the macro component GIVING data and an inputPort,
 * which represents the entry of the configuration that is to receive the outputPort of the
 * receiving macro component. In simple terms, this class represents (outputPortColumn.ConfigData ->
 * inputPortColumn.ConfigData)
 */
public class OpenLAPPortMapping {
  private final OpenLAPColumnConfigData outputPort;
  private final OpenLAPColumnConfigData inputPort;

  /** Constructor for serialization purposes */
  public OpenLAPPortMapping() {
    this.outputPort = null;
    this.inputPort = null;
  }

  /**
   * Standard constructor with in- and out- port.
   *
   * @param outputPort The OpenLAPColumnConfigData considered the output of the tuple (origin)
   * @param inputPort The OpenLAPColumnConfigData considered the input of the tuple (destination)
   */
  public OpenLAPPortMapping(OpenLAPColumnConfigData outputPort, OpenLAPColumnConfigData inputPort) {
    this.outputPort = outputPort;
    this.inputPort = inputPort;
  }

  /**
   * @return The output port of the tuple (origin)
   */
  public OpenLAPColumnConfigData getOutputPort() {
    return outputPort;
  }

  /**
   * @return The input port of the tuple (destination)
   */
  public OpenLAPColumnConfigData getInputPort() {
    return inputPort;
  }

  @Override
  public int hashCode() {
    return Objects.hash(outputPort, inputPort);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof OpenLAPPortMapping)) return false;
    OpenLAPPortMapping mapping = (OpenLAPPortMapping) o;
    return Objects.equals(this.outputPort, mapping.getOutputPort())
        && Objects.equals(this.inputPort, mapping.getInputPort());
  }

  /**
   * ToString method attempts to use the json representation of the object.
   *
   * @return JSON representation of the object
   */
  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      return "OpenLAPPortMapping{" + "outputPort=" + outputPort + ", inputPort=" + inputPort + '}';
    }
  }
}
