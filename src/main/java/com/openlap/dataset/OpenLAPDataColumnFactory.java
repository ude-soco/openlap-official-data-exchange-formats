package com.openlap.dataset;

import com.openlap.exceptions.OpenLAPDataColumnException;

/**
 * This Factory should be used to create the OpenLAPDataColumns on OpenLAPDataSets. IT holds a method that accepts
 * ID, type, required or not, title, description and returns a generated OpenLAPDataColumn.
 */
public class OpenLAPDataColumnFactory {

    /**
     * Returns an OpenLAPDataColumn with the ID, Type, required parameter, title, and description.
     *
     * @param id
     * @param type
     * @param isRequired
     * @param title
     * @param description
     * @return
     * @throws OpenLAPDataColumnException
     */
    public static final OpenLAPDataColumn createOpenLAPDataColumnOfType(String id, OpenLAPColumnDataType type, boolean isRequired, String title, String description) throws OpenLAPDataColumnException {
        switch (type) {
            case Text:
                return new OpenLAPDataColumn<String>(id, OpenLAPColumnDataType.Text, isRequired, title, description);
            case Numeric:
                return new OpenLAPDataColumn<Float>(id, OpenLAPColumnDataType.Numeric, isRequired, title, description);
            case TrueFalse:
                return new OpenLAPDataColumn<Boolean>(id, OpenLAPColumnDataType.TrueFalse, isRequired, title, description);
            default:
                throw new OpenLAPDataColumnException("Data type not supported");
        }
    }
}
