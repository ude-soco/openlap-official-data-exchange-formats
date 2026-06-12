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
import org.junit.Test;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CompatibilityBehaviorTests {

  @Test
  public void columnDataTypeToStringReturnsLegacyLabels() {
    Assert.assertEquals("STRING", OpenLAPColumnDataType.Text.toString());
    Assert.assertEquals("INTEGER", OpenLAPColumnDataType.Numeric.toString());
    Assert.assertEquals("BOOLEAN", OpenLAPColumnDataType.TrueFalse.toString());
  }

  @Test
  public void validationTypeMismatchMessageUsesLegacyTypeLabels()
      throws OpenLAPDataColumnException {
    OpenLAPDataSet destination = new OpenLAPDataSet();
    destination.addOpenLAPDataColumn(column("bananito", OpenLAPColumnDataType.Text, false));

    OpenLAPPortConfig configuration = new OpenLAPPortConfig();
    configuration
        .getMapping()
        .add(
            new OpenLAPPortMapping(
                config("outColumn", OpenLAPColumnDataType.Numeric, false),
                config("bananito", OpenLAPColumnDataType.Text, false)));

    OpenLAPDataSetConfigValidationResult result = destination.validateConfiguration(configuration);

    Assert.assertFalse(result.isValid());
    Assert.assertEquals(
        "Port bananito expected STRING, got INTEGER instead.",
        result.getValidationMessage());
  }

  @Test
  public void datasetColumnRejectionMessagesRemainCompatible()
      throws OpenLAPDataColumnException {
    OpenLAPDataSet dataSet = new OpenLAPDataSet();

    assertColumnRejected(dataSet, null, "Column already exists: null");
    assertColumnRejected(
        dataSet,
        column(null, OpenLAPColumnDataType.Text, false),
        "Column already exists: null");
    assertColumnRejected(
        dataSet,
        column("", OpenLAPColumnDataType.Text, false),
        "Column already exists: ");

    dataSet.addOpenLAPDataColumn(column("duplicate", OpenLAPColumnDataType.Text, false));
    assertColumnRejected(
        dataSet,
        column("duplicate", OpenLAPColumnDataType.Numeric, false),
        "Column already exists: duplicate");
  }

  @Test
  public void dynamicParameterRejectionMessagesRemainCompatible()
      throws OpenLAPDynamicParamException {
    OpenLAPDynamicParams params = new OpenLAPDynamicParams();

    assertDynamicParamRejected(params, null, "No dynamic parameter object to add.");
    assertDynamicParamRejected(params, dynamicParam(null), "Parameter already exists: null");
    assertDynamicParamRejected(params, dynamicParam(""), "Parameter already exists: ");

    params.addOpenLAPDynamicParam(dynamicParam("mode"));
    assertDynamicParamRejected(params, dynamicParam("mode"), "Parameter already exists: mode");
  }

  @Test
  public void nullDynamicParameterDataTypeMessageRemainsCompatible() {
    OpenLAPDynamicParamException exception =
        Assert.assertThrows(
            OpenLAPDynamicParamException.class,
            () ->
                OpenLAPDynamicParamFactory.createOpenLAPDataColumnOfType(
                    "mode",
                    OpenLAPDynamicParamType.Choice,
                    null,
                    "Mode",
                    "Execution mode",
                    "default",
                    null,
                    false));

    Assert.assertEquals("Data type not supported", exception.getMessage());
  }

  private void assertColumnRejected(
      OpenLAPDataSet dataSet, OpenLAPDataColumn column, String expectedMessage) {
    OpenLAPDataColumnException exception =
        Assert.assertThrows(
            OpenLAPDataColumnException.class, () -> dataSet.addOpenLAPDataColumn(column));

    Assert.assertEquals(expectedMessage, exception.getMessage());
  }

  private void assertDynamicParamRejected(
      OpenLAPDynamicParams params, OpenLAPDynamicParam param, String expectedMessage) {
    OpenLAPDynamicParamException exception =
        Assert.assertThrows(
            OpenLAPDynamicParamException.class, () -> params.addOpenLAPDynamicParam(param));

    Assert.assertEquals(expectedMessage, exception.getMessage());
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
}
