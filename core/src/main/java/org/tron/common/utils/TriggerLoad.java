package org.tron.common.utils;


import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.tron.config.Parameter;
import org.tron.protos.contract.SmartContractOuterClass.SmartContract.ABI;
import org.tron.walletserver.AddressUtil;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TriggerLoad {


    public static final int DATAWORD_UNIT_SIZE = 32;

    private enum Type {
        UNKNOWN,
        INT_NUMBER,
        BOOL,
        FLOAT_NUMBER,
        FIXED_BYTES,
        ADDRESS,
        STRING,
        BYTES,
    }

    private static void getTriggerData(String transactionId) {

    }

    public static Map<String, String> parseTriggerData(byte[] data, ABI.Entry entry) {
        Map<String, String> map = new LinkedHashMap<>();
        if (ArrayUtils.isEmpty(data)) {
            return map;
        }

        // the first is the signature.
        List<ABI.Entry.Param> list = entry.getInputsList();
        Integer startIndex = 0;
        try {
            // this one starts from the first position.
            int index = 0;
            for (Integer i = 0; i < list.size(); ++i) {
                ABI.Entry.Param param = list.get(i);
                if (param.getIndexed()) {
                    continue;
                }
                if (startIndex == 0) {
                    startIndex = i;
                }

                String str = parseDataBytes(data, param.getType(), index++);
                if (!AddressUtil.isEmpty(param.getName())) {
//                    map.put(param.getName(), str);
                    // todo     4.4.1 Modify, replace all keys with integers
                    map.put("" + i, str);
                }


            }
            if (list.size() == 0) {
                map.put("0", Hex.toHexString(data));
            }
        } catch (UnsupportedOperationException e) {
            map.clear();
            map.put(startIndex.toString(), Hex.toHexString(data));
        }
        return map;
    }


    public static Map<String, String> parseTriggerDataByFun(byte[] data, String fun) {
        Map<String, String> map = new LinkedHashMap<>();
        try {
            if (ArrayUtils.isEmpty(data)) {
                return map;
            }
            if (AddressUtil.isEmpty(fun) || !fun.contains("(") || !fun.contains(")")) return map;

            fun = fun.substring(fun.indexOf("(") + 1, fun.indexOf(")"));

            if (AddressUtil.isEmpty(fun) || fun.contains("(") || fun.contains(")")) return map;


            List<String> list = java.util.Arrays.asList(fun.split(","));
            Integer startIndex = 0;

            if (list != null
                    && list.size() == 1
                    && list.get(0).contains("[]")) {
                return parseDataByArray(data, fun);
            }
            try {
                // this one starts from the first position.
                int index = 0;
                for (Integer i = 0; i < list.size(); ++i) {

                    if (startIndex == 0) {
                        startIndex = i;
                    }

                    String str = parseDataBytes(data, list.get(i), index++);
                    map.put("" + i, str);

                }
                if (list.size() == 0) {
                    map.put("0", Hex.toHexString(data));
                }
            } catch (Exception e) {
                map.clear();
                map.put(startIndex.toString(), Hex.toHexString(data));
            }
            return map;

        } catch (Exception e) {
            LogUtils.e(e);
            return map;

        }

    }

    //TODO temporary
    //Handling the return value is a single case of an array (uint256[]), does not handle complex data types ,like（string,uint256[]）(string,uint256[],string)
    private static Map<String, String> parseDataByArray(byte[] data, String typeStr) {
        Map<String, String> map = new LinkedHashMap<>();
        try {
            if (typeStr.contains(("[]"))) {
                String typeSub = typeStr.substring(0, typeStr.indexOf("["));
                int length = (int) Math.ceil((double) data.length / DATAWORD_UNIT_SIZE);

                for (int i = 0; i < length; i++) {
                    String str = parseDataBytes(data, typeSub, i);
                    map.put("" + i, str);
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }

        return map;


    }

    private static String parseDataBytes(byte[] data, String typeStr, int index) {

        try {
            byte[] startBytes = subBytes(data, index * DATAWORD_UNIT_SIZE, DATAWORD_UNIT_SIZE);
            Type type = basicType(typeStr);
            if (type == Type.INT_NUMBER) {
                // maximum value：ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff
                // 115792089237316195423570985008687907853269984665640564039457584007913129639935
                //todo 4.4.1 Modify the output num to be a positive number (the original maximum output is -1）
                return new BigInteger(1, startBytes).toString();
            } else if (type == Type.BOOL) {
                return String.valueOf(!isZero(startBytes));
            } else if (type == Type.FIXED_BYTES) {
                return Hex.toHexString(startBytes);
            } else if (type == Type.ADDRESS) {
                byte[] last20Bytes = Arrays.copyOfRange(startBytes, 12, startBytes.length);
                return AddressUtil.encode58Check(convertToTronAddress(last20Bytes));
            } else if (type == Type.STRING || type == Type.BYTES) {
                int start = intValueExact(startBytes);
                byte[] lengthBytes = subBytes(data, start, DATAWORD_UNIT_SIZE);
                // this length is byte count. no need X 32
                int length = intValueExact(lengthBytes);
                int Max = 1024 * 1024 * 1;//1MB
                if (length == 0 || length > Max) {
                    return "";
                }
                byte[] realBytes = subBytes(data, start + DATAWORD_UNIT_SIZE, length);
                return type == Type.STRING ? new String(realBytes) : Hex.toHexString(realBytes);
            }
        } catch (OutputLengthException | ArithmeticException e) {
        }
        throw new UnsupportedOperationException("unsupported type:" + typeStr);
    }

    // don't support these type yet : bytes32[10][10]  OR  bytes32[][10]
    private static Type basicType(String type) {
        if (!Pattern.matches("^.*\\[\\d*\\]$", type)) {
            // ignore not valide type such as "int92", "bytes33", these types will be compiled failed.
            if ((type.startsWith("int") || type.startsWith("uint"))) {
                return Type.INT_NUMBER;
            } else if (type.equals("bool")) {
                return Type.BOOL;
            } else if (type.equals("address")) {
                return Type.ADDRESS;
            } else if (Pattern.matches("^bytes\\d+$", type)) {
                return Type.FIXED_BYTES;
            } else if (type.equals("string")) {
                return Type.STRING;
            } else if (type.equals("bytes")) {
                return Type.BYTES;
            }
        }
        return Type.UNKNOWN;
    }

    private static Integer intValueExact(byte[] data) {
        return new BigInteger(data).intValue();
    }

    private static byte[] subBytes(byte[] src, int start, int length) {
        if (ArrayUtils.isEmpty(src) || start >= src.length || length < 0) {
            throw new OutputLengthException("data start:" + start + ", length:" + length);
        }
        byte[] dst = new byte[length];
        System.arraycopy(src, start, dst, 0, Math.min(length, src.length - start));
        return dst;
    }

    private static boolean isZero(byte[] data) {
        for (byte tmp : data) {
            if (tmp != 0) {
                return false;
            }
        }
        return true;
    }

    public static byte[] convertToTronAddress(byte[] address) {
        if (address.length == 20) {
            byte[] newAddress = new byte[21];
            byte[] temp = new byte[]{Parameter.CommonConstant.ADD_PRE_FIX_BYTE};
            System.arraycopy(temp, 0, newAddress, 0, temp.length);
            System.arraycopy(address, 0, newAddress, temp.length, address.length);
            address = newAddress;
        }
        return address;
    }

    public static void main(String[] args) {
        TriggerLoad data = new TriggerLoad();
        TriggerLoad.getTriggerData("");
    }
}
