import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.openlap.dataset.OpenLAPColumnConfigData;
import com.openlap.dataset.OpenLAPColumnDataType;
import com.openlap.dataset.OpenLAPDataColumn;
import com.openlap.dataset.OpenLAPDataColumnFactory;
import com.openlap.dataset.OpenLAPDataSet;
import com.openlap.dataset.OpenLAPDataSetConfigValidationResult;
import com.openlap.dataset.OpenLAPPortConfig;
import com.openlap.dataset.OpenLAPPortMapping;
import com.openlap.dynamicparam.OpenLAPDynamicParam;
import com.openlap.dynamicparam.OpenLAPDynamicParamDataType;
import com.openlap.dynamicparam.OpenLAPDynamicParamFactory;
import com.openlap.dynamicparam.OpenLAPDynamicParamType;
import com.openlap.dynamicparam.OpenLAPDynamicParams;
import com.openlap.exceptions.OpenLAPDataColumnException;
import com.openlap.exceptions.OpenLAPDynamicParamException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfigurationValidationTests {

  private OpenLAPDataColumn stringColumn1;
  private OpenLAPDataColumn stringColumn2;
  private OpenLAPDataColumn intColumn1;
  private OpenLAPDataColumn noNameColumn;
  private OpenLAPDataColumn nullColumn;
  private OpenLAPDataSet dataSet1;
  private ObjectMapper mapper;

  @Before
  public void beforeTest() throws OpenLAPDataColumnException {
    mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    mapper.getFactory().configure(JsonParser.Feature.ALLOW_COMMENTS, true);

    stringColumn1 = column("stringColumn1", OpenLAPColumnDataType.Text, false);
    stringColumn2 = column("stringColumn2", OpenLAPColumnDataType.Text, false);
    intColumn1 = column("intColumn1", OpenLAPColumnDataType.Numeric, false);
    noNameColumn = column("", OpenLAPColumnDataType.Numeric, false);
    nullColumn = column(null, OpenLAPColumnDataType.Numeric, false);
    dataSet1 = new OpenLAPDataSet();
  }

  @Test
  public void OpenLAPDataColumnValidationTest() {
    Assert.assertTrue(stringColumn1.validateConfigurationData(stringColumn1.getConfigurationData()));
    Assert.assertFalse(stringColumn1.validateConfigurationData(noNameColumn.getConfigurationData()));
    Assert.assertFalse(stringColumn1.validateConfigurationData(nullColumn.getConfigurationData()));
    Assert.assertFalse(stringColumn1.validateConfigurationData(intColumn1.getConfigurationData()));
  }

  @Test
  public void duplicateColumnIdsAreRejected() throws OpenLAPDataColumnException {
    dataSet1.addOpenLAPDataColumn(column("duplicate", OpenLAPColumnDataType.Text, false));

    OpenLAPDataColumnException exception =
        Assert.assertThrows(
            OpenLAPDataColumnException.class,
            () -> dataSet1.addOpenLAPDataColumn(column("duplicate", OpenLAPColumnDataType.Text, false)));

    Assert.assertEquals("Column already exists: duplicate", exception.getMessage());
  }

  @Test
  public void nullColumnIdIsRejected() {
    OpenLAPDataColumnException exception =
        Assert.assertThrows(
            OpenLAPDataColumnException.class, () -> dataSet1.addOpenLAPDataColumn(nullColumn));

    Assert.assertEquals("Column already exists: null", exception.getMessage());
  }

  @Test
  public void emptyColumnIdIsRejected() {
    OpenLAPDataColumnException exception =
        Assert.assertThrows(
            OpenLAPDataColumnException.class, () -> dataSet1.addOpenLAPDataColumn(noNameColumn));

    Assert.assertEquals("Column already exists: ", exception.getMessage());
  }

  @Test
  public void OpenLAPDataSetConfigurationValidationTest() throws OpenLAPDataColumnException {
    OpenLAPPortConfig configuration1 = new OpenLAPPortConfig();
    OpenLAPPortConfig configuration2 = new OpenLAPPortConfig();
    OpenLAPPortConfig configuration3 = new OpenLAPPortConfig();
    OpenLAPPortConfig configuration4 = new OpenLAPPortConfig();

    dataSet1.addOpenLAPDataColumn(column("intColumn1", OpenLAPColumnDataType.Numeric, true));
    dataSet1.addOpenLAPDataColumn(column("stringColumn1", OpenLAPColumnDataType.Text, true));
    dataSet1.addOpenLAPDataColumn(column("bananito", OpenLAPColumnDataType.Text, false));

    configuration1
        .getMapping()
        .add(new OpenLAPPortMapping(intColumn1.getConfigurationData(), config("intColumn1", OpenLAPColumnDataType.Numeric, false)));
    configuration1
        .getMapping()
        .add(new OpenLAPPortMapping(stringColumn1.getConfigurationData(), config("stringColumn1", OpenLAPColumnDataType.Text, false)));
    configuration1
        .getMapping()
        .add(new OpenLAPPortMapping(stringColumn1.getConfigurationData(), config("bananito", OpenLAPColumnDataType.Text, false)));

    configuration2
        .getMapping()
        .add(new OpenLAPPortMapping(intColumn1.getConfigurationData(), config("bananito", OpenLAPColumnDataType.Text, false)));
    configuration2
        .getMapping()
        .add(new OpenLAPPortMapping(intColumn1.getConfigurationData(), config("intColumn1", OpenLAPColumnDataType.Numeric, false)));
    configuration2
        .getMapping()
        .add(new OpenLAPPortMapping(stringColumn2.getConfigurationData(), config("stringColumn1", OpenLAPColumnDataType.Text, false)));

    configuration3
        .getMapping()
        .add(new OpenLAPPortMapping(intColumn1.getConfigurationData(), config("intColumn1", OpenLAPColumnDataType.Numeric, false)));
    configuration3
        .getMapping()
        .add(new OpenLAPPortMapping(stringColumn1.getConfigurationData(), config("bananito", OpenLAPColumnDataType.Text, false)));

    configuration4
        .getMapping()
        .add(new OpenLAPPortMapping(intColumn1.getConfigurationData(), config("intColumn1", OpenLAPColumnDataType.Numeric, false)));
    configuration4
        .getMapping()
        .add(new OpenLAPPortMapping(stringColumn1.getConfigurationData(), config("stringColumn1", OpenLAPColumnDataType.Text, false)));
    configuration4
        .getMapping()
        .add(new OpenLAPPortMapping(stringColumn1.getConfigurationData(), config("SomethingElse", OpenLAPColumnDataType.Text, false)));

    OpenLAPDataSetConfigValidationResult configurationValidationResult1 =
        dataSet1.validateConfiguration(configuration1);
    Assert.assertTrue("Expected true", configurationValidationResult1.isValid());
    Assert.assertEquals(
        OpenLAPDataSetConfigValidationResult.VALID_CONFIGURATION,
        configurationValidationResult1.getValidationMessage());

    OpenLAPDataSetConfigValidationResult configurationValidationResult2 =
        dataSet1.validateConfiguration(configuration2);
    Assert.assertFalse("Expected false", configurationValidationResult2.isValid());
    Assert.assertEquals(
        "Port bananito expected STRING, got INTEGER instead.",
        configurationValidationResult2.getValidationMessage());

    OpenLAPDataSetConfigValidationResult configurationValidationResult3 =
        dataSet1.validateConfiguration(configuration3);
    Assert.assertFalse("Expected false", configurationValidationResult3.isValid());
    Assert.assertEquals(
        "Required columns not found" + System.lineSeparator() + "Column: stringColumn1 is not found",
        configurationValidationResult3.getValidationMessage());

    OpenLAPDataSetConfigValidationResult configurationValidationResult4 =
        dataSet1.validateConfiguration(configuration4);
    Assert.assertFalse("Expected false", configurationValidationResult4.isValid());
    Assert.assertEquals(
        "Columns not present on the destination DataSet"
            + System.lineSeparator()
            + "Column: SomethingElse does not exist in the destination dataset",
        configurationValidationResult4.getValidationMessage());
  }

  @Test
  public void OpenLAPConfigurationSerializationTests() throws IOException {
    OpenLAPPortConfig expected = new OpenLAPPortConfig();
    expected
        .getMapping()
        .add(new OpenLAPPortMapping(config("intColumn1", OpenLAPColumnDataType.Numeric, false), config("intColumn1", OpenLAPColumnDataType.Numeric, false)));
    expected
        .getMapping()
        .add(new OpenLAPPortMapping(config("stringColumn1", OpenLAPColumnDataType.Text, false), config("stringColumn1", OpenLAPColumnDataType.Text, false)));
    expected
        .getMapping()
        .add(new OpenLAPPortMapping(config("stringColumn1", OpenLAPColumnDataType.Text, false), config("bananito", OpenLAPColumnDataType.Text, false)));

    OpenLAPPortConfig actual;
    try (InputStream input = fixture("ConfigurationSample.json")) {
      actual = mapper.readValue(input, OpenLAPPortConfig.class);
    }

    Assert.assertEquals(mapper.writeValueAsString(expected), mapper.writeValueAsString(actual));
  }

  @Test
  public void OpenLAPDataSetSerialization() throws IOException, OpenLAPDataColumnException {
    OpenLAPDataSet expected = new OpenLAPDataSet();
    expected.addOpenLAPDataColumn(column("intColumn1", OpenLAPColumnDataType.Numeric, true));
    expected.addOpenLAPDataColumn(column("stringColumn1", OpenLAPColumnDataType.Text, true));
    expected.addOpenLAPDataColumn(column("bananito", OpenLAPColumnDataType.Text, false));
    expected.getColumns().get("intColumn1").getData().add(1);
    expected.getColumns().get("stringColumn1").getData().add("value1");

    OpenLAPDataSet actual;
    try (InputStream input = fixture("DataSetSample.json")) {
      actual = mapper.readValue(input, OpenLAPDataSet.class);
    }

    Assert.assertEquals(mapper.writeValueAsString(expected), mapper.writeValueAsString(actual));
  }

  @Test
  public void legacyColumnTypeNamesDeserialize() throws IOException {
    String json = "{\"type\":\"STRING\",\"id\":\"legacy\",\"required\":true}";

    OpenLAPColumnConfigData config = mapper.readValue(json, OpenLAPColumnConfigData.class);

    Assert.assertEquals(OpenLAPColumnDataType.Text, config.getType());
    Assert.assertEquals("legacy", config.getId());
    Assert.assertTrue(config.isRequired());
  }

  @Test
  public void currentColumnTypeNamesStillDeserialize() throws IOException {
    String json = "{\"type\":\"TrueFalse\",\"id\":\"current\",\"required\":false}";

    OpenLAPColumnConfigData config = mapper.readValue(json, OpenLAPColumnConfigData.class);

    Assert.assertEquals(OpenLAPColumnDataType.TrueFalse, config.getType());
    Assert.assertEquals("current", config.getId());
    Assert.assertFalse(config.isRequired());
  }

  @Test
  public void addDynamicParamStoresParamById() throws OpenLAPDynamicParamException {
    OpenLAPDynamicParams params = new OpenLAPDynamicParams();
    OpenLAPDynamicParam param =
        OpenLAPDynamicParamFactory.createOpenLAPDataColumnOfType(
            "threshold",
            OpenLAPDynamicParamType.Textbox,
            OpenLAPDynamicParamDataType.STRING,
            "Threshold",
            "Minimum value",
            "10",
            null,
            true);

    params.addOpenLAPDynamicParam(param);

    Assert.assertSame(param, params.getParams().get("threshold"));
    Assert.assertEquals(1, params.getParamsAsList(false).size());
    Assert.assertEquals(1, params.getParamsAsList(true).size());
  }

  @Test
  public void duplicateDynamicParamIdsAreRejected() throws OpenLAPDynamicParamException {
    OpenLAPDynamicParams params = new OpenLAPDynamicParams();
    params.addOpenLAPDynamicParam(dynamicParam("mode"));

    OpenLAPDynamicParamException exception =
        Assert.assertThrows(
            OpenLAPDynamicParamException.class,
            () -> params.addOpenLAPDynamicParam(dynamicParam("mode")));

    Assert.assertEquals("Parameter already exists: mode", exception.getMessage());
  }

  @Test
  public void defaultConstructedObjectsAreNullSafeForHashAndEquality() {
    OpenLAPColumnConfigData configData = new OpenLAPColumnConfigData();
    OpenLAPDataColumn dataColumn = new OpenLAPDataColumn();
    OpenLAPPortMapping portMapping = new OpenLAPPortMapping();
    OpenLAPDataSetConfigValidationResult result = new OpenLAPDataSetConfigValidationResult(true, null);

    configData.hashCode();
    dataColumn.hashCode();
    portMapping.hashCode();

    Assert.assertEquals(configData, new OpenLAPColumnConfigData());
    Assert.assertEquals(dataColumn, new OpenLAPDataColumn());
    Assert.assertEquals(portMapping, new OpenLAPPortMapping());
    Assert.assertEquals("message", result.appendValidationMessage("message"));
  }

  private OpenLAPDataColumn column(String id, OpenLAPColumnDataType type, boolean required)
      throws OpenLAPDataColumnException {
    return OpenLAPDataColumnFactory.createOpenLAPDataColumnOfType(id, type, required, null, null);
  }

  private OpenLAPColumnConfigData config(String id, OpenLAPColumnDataType type, boolean required) {
    return new OpenLAPColumnConfigData(id, type, required, null, null);
  }

  private OpenLAPDynamicParam dynamicParam(String id) throws OpenLAPDynamicParamException {
    return OpenLAPDynamicParamFactory.createOpenLAPDataColumnOfType(
        id,
        OpenLAPDynamicParamType.Choice,
        OpenLAPDynamicParamDataType.STRING,
        "Mode",
        "Execution mode",
        "default",
        "default;advanced",
        false);
  }

  private InputStream fixture(String name) {
    InputStream input = getClass().getResourceAsStream(name);
    Assert.assertNotNull("Missing test fixture: " + name, input);
    return input;
  }
}
