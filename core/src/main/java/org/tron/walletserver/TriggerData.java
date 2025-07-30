package org.tron.walletserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;


@Data
public class TriggerData {
    private String method;//Example：transfer(unit256,address)
    private Map<String, String> parameterMap;

    public String getMethod() {
        return method;
    }

    /**
     * input: transfer(unit256,address)
     *
     * @return transfer
     */
    public String getMethodNoParams() {
        if (AddressUtil.isEmpty(method)) return "";
        String methodSub = method.contains("(") ? method.substring(0, method.indexOf("(")) : method;
        return methodSub;
    }

    /**
     * input: transfer(unit256,address)
     *
     * @return [unit256, address]
     */
    private List<String> getMethodParamsList() {
        List<String> paramsList = new ArrayList<>();
        if (AddressUtil.isEmpty(method) || !method.contains("(")) return paramsList;
        String methodSub = method.substring(method.indexOf("(") + 1, method.indexOf(")"));
        if (AddressUtil.isEmpty(methodSub) || methodSub.contains("(") || methodSub.contains(")"))
            return paramsList;
        paramsList = Arrays.asList(methodSub.split(","));
        return paramsList;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return Map<" 0 ", " Tdls...de "> from parseFun
     */
    public Map<String, String> getParameterMap() {
        return parameterMap == null ? new HashMap<>() : parameterMap;
    }

    public void setParameterMap(Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }

    /**
     * @return Returns metadata used for transaction parsing
     */
    public List<TypeValue> parseDataForTypeValueList() {

        List<TypeValue> typeValueList = new ArrayList<>();

        List<String> methodParamsList = getMethodParamsList();
        Map<String, String> parameterMapOld = getParameterMap();

        if (methodParamsList == null || parameterMapOld == null || methodParamsList.size() != parameterMapOld.size())
            return typeValueList;

        for (int i = 0; i < methodParamsList.size(); i++) {
            TypeValue typeValue = new TypeValue();
            String type = String.valueOf(methodParamsList.get(i));
            String value = String.valueOf(parameterMapOld.get(String.valueOf(i)));
            typeValue.setType(type);
            typeValue.setValue(value);
            typeValueList.add(typeValue);
        }
        return typeValueList;
    }

    @Data
    public static class TypeValue {
        String type;
        String value;
    }
}
