# OpenLAP Data Exchange Formats

Java data exchange objects for OpenLAP datasets, port mappings, and dynamic parameters.

This is a Maven Java library. It does not provide a Spring Boot application, web server, database
configuration, or deployment runtime.

## Installation

This repository builds the Maven artifact declared in `pom.xml`:

```xml
<dependency>
    <groupId>com.openlap</groupId>
    <artifactId>openlap-data-exchange-format</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

If consuming a published OpenLAP build, use the repository and version coordinates published for
that build.

## Basic Usage

`OpenLAPDataSet` groups `OpenLAPDataColumn` objects by column id. Each column has:

* `configurationData`, represented by `OpenLAPColumnConfigData`
* `data`, represented as an array of values for that column

Use `OpenLAPDataColumnFactory` to create columns with the current public enum values:

```java
import com.openlap.dataset.OpenLAPColumnDataType;
import com.openlap.dataset.OpenLAPDataColumnFactory;
import com.openlap.dataset.OpenLAPDataSet;

OpenLAPDataSet dataSet = new OpenLAPDataSet();
dataSet.addOpenLAPDataColumn(
    OpenLAPDataColumnFactory.createOpenLAPDataColumnOfType(
        "intColumn1", OpenLAPColumnDataType.Numeric, true, null, null));
dataSet.addOpenLAPDataColumn(
    OpenLAPDataColumnFactory.createOpenLAPDataColumnOfType(
        "stringColumn1", OpenLAPColumnDataType.Text, true, null, null));
dataSet.addOpenLAPDataColumn(
    OpenLAPDataColumnFactory.createOpenLAPDataColumnOfType(
        "column1", OpenLAPColumnDataType.Text, false, null, null));
```

`OpenLAPPortConfig` contains `OpenLAPPortMapping` entries that map output column configuration to
input column configuration. A dataset can validate whether a port config is compatible with it:

```java
import com.openlap.dataset.OpenLAPDataSetConfigValidationResult;

OpenLAPDataSetConfigValidationResult result = dataSet.validateConfiguration(configuration);
System.out.println("Message: " + result.getValidationMessage());
System.out.println("Validation status: " + result.isValid());
```

## JSON Contract

For `OpenLAPColumnConfigData.type`, new JSON payloads should use the current enum names:

* `Text`
* `Numeric`
* `TrueFalse`

With Jackson's default enum handling, serialization emits these current names.

### OpenLAPDataSet

```json
{
  "columns": {
    "column1": {
      "configurationData": {
        "type": "Text",
        "id": "column1",
        "required": false
      },
      "data": [
        "data1"
      ]
    },
    "intColumn1": {
      "configurationData": {
        "type": "Numeric",
        "id": "intColumn1",
        "required": true
      },
      "data": [
        1,
        2,
        3,
        4
      ]
    },
    "stringColumn1": {
      "configurationData": {
        "type": "Text",
        "id": "stringColumn1",
        "required": true
      },
      "data": [
        "value1",
        "value2"
      ]
    }
  }
}
```

### OpenLAPPortConfig

```json
{
  "mapping": [
    {
      "outputPort": {
        "type": "Numeric",
        "id": "outCol1",
        "required": false
      },
      "inputPort": {
        "type": "Numeric",
        "id": "inCol3",
        "required": true
      }
    },
    {
      "outputPort": {
        "type": "Text",
        "id": "outCol2",
        "required": true
      },
      "inputPort": {
        "type": "Text",
        "id": "inCol2",
        "required": true
      }
    },
    {
      "outputPort": {
        "type": "Text",
        "id": "outCol3",
        "required": true
      },
      "inputPort": {
        "type": "Text",
        "id": "inCol1",
        "required": false
      }
    }
  ]
}
```

## Legacy Compatibility

Older OpenLAP clients may send legacy type labels. Deserialization accepts these aliases:

* `STRING` -> `Text`
* `INTEGER`, `FLOAT`, `LONG`, `SHORT`, `BYTE` -> `Numeric`
* `BOOLEAN` -> `TrueFalse`

The legacy aliases are accepted as input for backward compatibility. New payloads should use
`Text`, `Numeric`, and `TrueFalse`.

`OpenLAPColumnDataType.toString()` currently returns legacy labels, and validation messages use
those labels:

* `OpenLAPColumnDataType.Text.toString()` -> `STRING`
* `OpenLAPColumnDataType.Numeric.toString()` -> `INTEGER`
* `OpenLAPColumnDataType.TrueFalse.toString()` -> `BOOLEAN`

For example, a type mismatch currently reports:

```text
Port bananito expected STRING, got INTEGER instead.
```

This behavior is compatibility-tested and should not be changed without an explicit migration plan.

## Development

Use the Maven Wrapper for reproducible local builds:

```sh
./mvnw clean test
./mvnw dependency:tree
git diff --check
```

The build enforces Java 11 or newer and Maven 3.9 or newer.

## References

* Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Design patterns:
  elements of reusable object-oriented software. Pearson Education.
* Jackson Project: https://github.com/FasterXML/jackson
