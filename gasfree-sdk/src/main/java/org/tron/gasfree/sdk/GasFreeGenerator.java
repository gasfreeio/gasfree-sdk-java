package org.tron.gasfree.sdk;

import org.tron.common.bip32.Numeric;
import org.tron.common.crypto.FunctionEncoder;
import org.tron.common.crypto.Hash;
import org.tron.common.crypto.StructuredDataEncoder;
import org.tron.common.crypto.datatypes.Address;
import org.tron.common.crypto.datatypes.DynamicBytes;
import org.tron.common.crypto.datatypes.Function;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.LogUtils;
import org.tron.common.utils.abi.DataWord;
import org.tron.config.Parameter;
import org.tron.walletserver.AddressUtil;
import java.util.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GasFreeGenerator {

    public static String generateGasFreeAddress(String userAddress, String chainId) {
        if (Constant.NileNetCode.equals(chainId)) {
            return generateGasFreeAddress(userAddress, Constant.beaconAddressNile, Constant.GasFreeControllerAddressNile, Constant.creationCodeStr);
        } else if (Constant.MainNetCode.equals(chainId)) {
            return generateGasFreeAddress(userAddress, Constant.beaconAddressRelease, Constant.GasFreeControllerAddressRelease, Constant.creationCodeStrRelease);
        } else if (Constant.ShastaNetCode.equals(chainId)) {
            return generateGasFreeAddress(userAddress, Constant.beaconAddressShasta, Constant.GasFreeControllerAddressShasta, Constant.creationCodeStr);
        } else {
            return generateGasFreeAddress(userAddress, Constant.beaconAddressRelease, Constant.GasFreeControllerAddressRelease, Constant.creationCodeStrRelease);
        }
    }

    public static String generateGasFreeAddress(String user, String beanconAddress, String gasFreeControlAddress, String creationCodeStr) {
        try {
            byte[] salt = byte32Address(user);
            Function function = new Function(
                    "initialize",
                    Arrays.asList(new Address(user)),
                    Collections.emptyList());
            String initializeFun = FunctionEncoder.encode(function);

            String encodeCall = FunctionEncoder.encodeConstructor(
                    Arrays.asList(new Address(AddressUtil.replace41Address(beanconAddress)),
                            new DynamicBytes(ByteArray.fromHexString(initializeFun))));
            byte[] creationCode = ByteArray.fromHexString(creationCodeStr);
            // encodePacked
            // cannot use FunctionEncoder.encodeConstructorPacked, because DynamicTypes packed function changed
            byte[] bytecodeHash = Hash.sha3( org.bouncycastle.util.Arrays.concatenate(creationCode, ByteArray.fromHexString(encodeCall)));

            // create2
            byte[] bytes =  org.bouncycastle.util.Arrays.concatenate(ByteArray.fromHexString(Parameter.CommonConstant.ADD_PRE_FIX_STRING), ByteArray.fromHexString(AddressUtil.replace41Address(gasFreeControlAddress)));
            byte[] bytes1 =  org.bouncycastle.util.Arrays.concatenate(bytes, salt);
            byte[] bytes2 =  org.bouncycastle.util.Arrays.concatenate(bytes1, bytecodeHash);
            byte[] bytes3 = Hash.sha3omit12(bytes2);
            String gasFreeAddress = AddressUtil.encode58Check(bytes3);
            return gasFreeAddress;
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return "";
    }
    private static Map<String, String> createType(String name, String type) {
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("name", name);
        typeMap.put("type", type);
        return typeMap;
    }
    public static String permitTransferMessageHash(String chainId,
                                                   String verifyingContract,
                                                   String token,
                                                   String serviceProvider,
                                                   String user,
                                                   String receiver,
                                                   String value,
                                                   String maxFee,
                                                   long deadline,
                                                   long version,
                                                   long nonce
                                                   ) throws Exception {
        // domain
        Map<String, Object> domain = new HashMap<>();
        domain.put("chainId", chainId);
        domain.put("name", "GasFreeController");
        domain.put("verifyingContract", verifyingContract);
        domain.put("version", "V1.0.0");

        // message
        Map<String, Object> message = new HashMap<>();
        message.put("deadline", deadline);
        message.put("maxFee", maxFee);
        message.put("nonce", nonce);
        message.put("receiver", receiver);
        message.put("serviceProvider", serviceProvider);
        message.put("token", token);
        message.put("user", user);
        message.put("value", value);
        message.put("version", version);

        // types
        Map<String, List<Map<String, String>>> types = new HashMap<>();
        List<Map<String, String>> permitTransfer = Arrays.asList(
                createType("token", "address"),
                createType("serviceProvider", "address"),
                createType("user", "address"),
                createType("receiver", "address"),
                createType("value", "uint256"),
                createType("maxFee", "uint256"),
                createType("deadline", "uint256"),
                createType("version", "uint256"),
                createType("nonce", "uint256")
        );

        List<Map<String, String>> eip712Domain = Arrays.asList(
                createType("name", "string"),
                createType("version", "string"),
                createType("chainId", "uint256"),
                createType("verifyingContract", "address")
        );

        types.put("PermitTransfer", permitTransfer);
        types.put("EIP712Domain", eip712Domain);

        Map<String, Object> json = new HashMap<>();
        json.put("domain", domain);
        json.put("message", message);
        json.put("primaryType", "PermitTransfer");
        json.put("types", types);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(json);
        System.out.println(jsonString);
        return permitTransferMessageHash(jsonString);
    }
    public static String permitTransferMessageHash(String eip712Message) throws Exception {
        StructuredDataEncoder dataEncoder = new StructuredDataEncoder(eip712Message);
        byte[] bytes = dataEncoder.hashDomain();
        String domainStr = Numeric.toHexString(bytes);

        byte[] hashMessage = dataEncoder.hashMessage();
        String messageStr = Numeric.toHexString(hashMessage);
        ParamCheck.isValidParma(eip712Message);
        byte[] hashStructuredData = dataEncoder.hashStructuredData();
        String hash = Numeric.toHexString(hashStructuredData);
        return hash;
    }


    private static byte[] byte32Address(String address) {
        String value = AddressUtil.replace41Address(address);
        byte[] addressBytes = ByteArray.fromHexString(value);
        if (addressBytes == null) {
            return null;
        }
        return new DataWord(addressBytes).getData();
    }
}

