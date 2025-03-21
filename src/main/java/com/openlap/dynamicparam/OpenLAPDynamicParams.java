package com.openlap.dynamicparam;

import com.openlap.exceptions.OpenLAPDynamicParamException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OpenLAPDynamicParams {
  private HashMap<String, OpenLAPDynamicParam> params;

  public OpenLAPDynamicParams() {
    this.params = new HashMap<String, OpenLAPDynamicParam>();
  }

  public HashMap<String, OpenLAPDynamicParam> getParams() {
    return params;
  }

  public void setParams(HashMap<String, OpenLAPDynamicParam> params) {
    this.params = params;
  }

  public void addOpenLAPDynamicParam(OpenLAPDynamicParam param)
      throws OpenLAPDynamicParamException {
    if (param == null)
      throw new OpenLAPDynamicParamException("No dynamic parameter object to add.");

    String paramId = param.getId();
    if (paramId == null || paramId.isEmpty() || params.containsKey(paramId))
      throw new OpenLAPDynamicParamException(
          OpenLAPDynamicParamException.PARAM_ALREADY_EXISTS, paramId);
    else {
      params.put(paramId, param);
    }
  }

  public List<OpenLAPDynamicParam> getParamsAsList(boolean onlyRequiredColumns) {
    List<OpenLAPDynamicParam> params = new ArrayList<OpenLAPDynamicParam>(this.params.values());
    if (!onlyRequiredColumns) return params;
    else {
      List<OpenLAPDynamicParam> requiredParams = new ArrayList<OpenLAPDynamicParam>();
      for (OpenLAPDynamicParam param : params) {
        if (param.isRequired()) requiredParams.add(param);
      }
      return requiredParams;
    }
  }
}
