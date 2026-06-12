import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.openlap.dataset.OpenLAPColumnConfigData;
import com.openlap.dataset.OpenLAPColumnDataType;
import com.openlap.dataset.OpenLAPDataSet;
import com.openlap.dataset.OpenLAPPortConfig;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class JsonContractTests {

  private static final String README_DATA_SET_JSON =
      "{"
          + "\"columns\":{"
          + "\"column1\":{\"configurationData\":{\"type\":\"Text\",\"id\":\"column1\",\"required\":false},\"data\":[\"data1\"]},"
          + "\"intColumn1\":{\"configurationData\":{\"type\":\"Numeric\",\"id\":\"intColumn1\",\"required\":true},\"data\":[1,2,3,4]},"
          + "\"stringColumn1\":{\"configurationData\":{\"type\":\"Text\",\"id\":\"stringColumn1\",\"required\":true},\"data\":[\"value1\",\"value2\"]}"
          + "}"
          + "}";

  private static final String README_PORT_CONFIG_JSON =
      "{"
          + "\"mapping\":["
          + "{\"outputPort\":{\"type\":\"Numeric\",\"id\":\"outCol1\",\"required\":false},\"inputPort\":{\"type\":\"Numeric\",\"id\":\"inCol3\",\"required\":true}},"
          + "{\"outputPort\":{\"type\":\"Text\",\"id\":\"outCol2\",\"required\":true},\"inputPort\":{\"type\":\"Text\",\"id\":\"inCol2\",\"required\":true}},"
          + "{\"outputPort\":{\"type\":\"Text\",\"id\":\"outCol3\",\"required\":true},\"inputPort\":{\"type\":\"Text\",\"id\":\"inCol1\",\"required\":false}}"
          + "]"
          + "}";

  private final ObjectMapper mapper = createMapper();

  @Test
  public void readmeDataSetExampleDeserializesAndSerializesWithCurrentEnumNames()
      throws IOException {
    OpenLAPDataSet dataSet = mapper.readValue(README_DATA_SET_JSON, OpenLAPDataSet.class);

    Assert.assertEquals(OpenLAPColumnDataType.Text, columnType(dataSet, "column1"));
    Assert.assertEquals(OpenLAPColumnDataType.Numeric, columnType(dataSet, "intColumn1"));
    Assert.assertEquals(OpenLAPColumnDataType.Text, columnType(dataSet, "stringColumn1"));
    Assert.assertEquals(4, dataSet.getColumns().get("intColumn1").getData().size());
    Assert.assertEquals(4, dataSet.getColumns().get("intColumn1").getData().get(3));

    JsonNode serialized = writeTree(dataSet);
    Assert.assertEquals("Text", serializedDataSetType(serialized, "column1"));
    Assert.assertEquals("Numeric", serializedDataSetType(serialized, "intColumn1"));
    Assert.assertEquals("Text", serializedDataSetType(serialized, "stringColumn1"));
  }

  @Test
  public void readmePortConfigExampleDeserializesAndSerializesWithCurrentEnumNames()
      throws IOException {
    OpenLAPPortConfig config = mapper.readValue(README_PORT_CONFIG_JSON, OpenLAPPortConfig.class);

    Assert.assertEquals(3, config.getMapping().size());
    Assert.assertEquals(OpenLAPColumnDataType.Numeric, config.getMapping().get(0).getOutputPort().getType());
    Assert.assertEquals(OpenLAPColumnDataType.Numeric, config.getMapping().get(0).getInputPort().getType());
    Assert.assertEquals(OpenLAPColumnDataType.Text, config.getMapping().get(1).getOutputPort().getType());
    Assert.assertEquals(OpenLAPColumnDataType.Text, config.getMapping().get(2).getInputPort().getType());

    JsonNode serialized = writeTree(config);
    Assert.assertEquals("Numeric", serializedPortType(serialized, 0, "outputPort"));
    Assert.assertEquals("Numeric", serializedPortType(serialized, 0, "inputPort"));
    Assert.assertEquals("Text", serializedPortType(serialized, 1, "outputPort"));
    Assert.assertEquals("Text", serializedPortType(serialized, 2, "inputPort"));
  }

  @Test
  public void legacyDataSetFixtureDeserializesAndSerializesWithCurrentEnumNames()
      throws IOException {
    OpenLAPDataSet dataSet;
    try (InputStream input = fixture("DataSetSample.json")) {
      dataSet = mapper.readValue(input, OpenLAPDataSet.class);
    }

    Assert.assertEquals(OpenLAPColumnDataType.Text, columnType(dataSet, "bananito"));
    Assert.assertEquals(OpenLAPColumnDataType.Numeric, columnType(dataSet, "intColumn1"));
    Assert.assertEquals(OpenLAPColumnDataType.Text, columnType(dataSet, "stringColumn1"));

    JsonNode serialized = writeTree(dataSet);
    Assert.assertEquals("Text", serializedDataSetType(serialized, "bananito"));
    Assert.assertEquals("Numeric", serializedDataSetType(serialized, "intColumn1"));
    Assert.assertEquals("Text", serializedDataSetType(serialized, "stringColumn1"));
  }

  @Test
  public void legacyPortConfigFixtureDeserializesAndSerializesWithCurrentEnumNames()
      throws IOException {
    OpenLAPPortConfig config;
    try (InputStream input = fixture("ConfigurationSample.json")) {
      config = mapper.readValue(input, OpenLAPPortConfig.class);
    }

    Assert.assertEquals(3, config.getMapping().size());
    Assert.assertEquals(OpenLAPColumnDataType.Numeric, config.getMapping().get(0).getOutputPort().getType());
    Assert.assertEquals(OpenLAPColumnDataType.Text, config.getMapping().get(1).getOutputPort().getType());
    Assert.assertEquals(OpenLAPColumnDataType.Text, config.getMapping().get(2).getInputPort().getType());

    JsonNode serialized = writeTree(config);
    Assert.assertEquals("Numeric", serializedPortType(serialized, 0, "outputPort"));
    Assert.assertEquals("Text", serializedPortType(serialized, 1, "outputPort"));
    Assert.assertEquals("Text", serializedPortType(serialized, 2, "inputPort"));
  }

  @Test
  public void defaultSerializationEmitsCurrentEnumNames() throws IOException {
    Assert.assertEquals("Text", serializedConfigType(OpenLAPColumnDataType.Text));
    Assert.assertEquals("Numeric", serializedConfigType(OpenLAPColumnDataType.Numeric));
    Assert.assertEquals("TrueFalse", serializedConfigType(OpenLAPColumnDataType.TrueFalse));
  }

  @Test
  public void currentEnumNamesDeserialize() throws IOException {
    Assert.assertEquals(OpenLAPColumnDataType.Text, deserializeType("Text"));
    Assert.assertEquals(OpenLAPColumnDataType.Numeric, deserializeType("Numeric"));
    Assert.assertEquals(OpenLAPColumnDataType.TrueFalse, deserializeType("TrueFalse"));
  }

  @Test
  public void legacyEnumAliasesDeserialize() throws IOException {
    Assert.assertEquals(OpenLAPColumnDataType.Text, deserializeType("STRING"));
    Assert.assertEquals(OpenLAPColumnDataType.Numeric, deserializeType("INTEGER"));
    Assert.assertEquals(OpenLAPColumnDataType.Numeric, deserializeType("FLOAT"));
    Assert.assertEquals(OpenLAPColumnDataType.Numeric, deserializeType("LONG"));
    Assert.assertEquals(OpenLAPColumnDataType.Numeric, deserializeType("SHORT"));
    Assert.assertEquals(OpenLAPColumnDataType.Numeric, deserializeType("BYTE"));
    Assert.assertEquals(OpenLAPColumnDataType.TrueFalse, deserializeType("BOOLEAN"));
  }

  private OpenLAPColumnDataType columnType(OpenLAPDataSet dataSet, String id) {
    return dataSet.getColumns().get(id).getConfigurationData().getType();
  }

  private OpenLAPColumnDataType deserializeType(String type) throws IOException {
    return mapper
        .readValue(
            "{\"type\":\"" + type + "\",\"id\":\"contract\",\"required\":false}",
            OpenLAPColumnConfigData.class)
        .getType();
  }

  private String serializedConfigType(OpenLAPColumnDataType type) throws IOException {
    OpenLAPColumnConfigData config = new OpenLAPColumnConfigData("contract", type, false, null, null);
    return writeTree(config).get("type").asText();
  }

  private JsonNode writeTree(Object value) throws IOException {
    return mapper.readTree(mapper.writeValueAsString(value));
  }

  private String serializedDataSetType(JsonNode dataSet, String columnId) {
    return dataSet
        .get("columns")
        .get(columnId)
        .get("configurationData")
        .get("type")
        .asText();
  }

  private String serializedPortType(JsonNode config, int mappingIndex, String portName) {
    return config.get("mapping").get(mappingIndex).get(portName).get("type").asText();
  }

  private InputStream fixture(String name) {
    InputStream input = getClass().getResourceAsStream(name);
    Assert.assertNotNull("Missing test fixture: " + name, input);
    return input;
  }

  private static ObjectMapper createMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    mapper.getFactory().configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    return mapper;
  }
}
