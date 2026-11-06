package org.tron.gasfree.sdk;

import org.tron.common.bip32.Numeric;
import org.tron.common.crypto.FunctionEncoder;
import org.tron.common.crypto.Hash;
import org.tron.common.crypto.StructuredDataEncoder;
import org.tron.common.crypto.datatypes.Address;
import org.tron.common.crypto.datatypes.DynamicBytes;
import org.tron.common.crypto.datatypes.Function;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.abi.DataWord;
import org.tron.config.Parameter;
import org.tron.walletserver.AddressUtil;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GasFreeGenerator {

    public static String generateGasFreeAddress(String userAddress, String chainId) {
        try {
            BigInteger network = ParamCheck.parseChainId(chainId);
            if (ParamCheck.parseChainId(Constant.NileNetCode).equals(network)) {
                return generateGasFreeAddress(userAddress, Constant.beaconAddressNile, Constant.GasFreeControllerAddressNile, Constant.creationCodeStr);
            } else if (ParamCheck.parseChainId(Constant.MainNetCode).equals(network)) {
                return generateGasFreeAddress(userAddress, Constant.beaconAddressRelease, Constant.GasFreeControllerAddressRelease, Constant.creationCodeStrRelease);
            } else if (ParamCheck.parseChainId(Constant.ShastaNetCode).equals(network)) {
                return generateGasFreeAddress(userAddress, Constant.beaconAddressShasta, Constant.GasFreeControllerAddressShasta, Constant.creationCodeStr);
            }
            return "";
        } catch (GasFreeException e) {
            return "";
        }
    }

    public static String generateGasFreeAddress(String user, String beaconAddress, String gasFreeControlAddress, String creationCodeStr) {
        try {
            if (creationCodeStr == null) {
                return "";
            }
            String creationCodeHex = creationCodeStr.startsWith("0x") || creationCodeStr.startsWith("0X")
                    ? creationCodeStr.substring(2) : creationCodeStr;
            // ByteArray accepts empty, odd-length and whitespace-containing input.
            // Require complete bytecode bytes before deriving a CREATE2 address.
            if (creationCodeHex.isEmpty() || creationCodeHex.length() % 2 != 0
                    || !creationCodeHex.matches("[0-9a-fA-F]+")) {
                return "";
            }
            byte[] creationCode = ByteArray.fromHexString(creationCodeHex);
            if (creationCode.length == 0) {
                return "";
            }
            user = ParamCheck.normalizeAddress(user);
            beaconAddress = ParamCheck.normalizeAddress(beaconAddress);
            gasFreeControlAddress = ParamCheck.normalizeAddress(gasFreeControlAddress);
            byte[] salt = byte32Address(user);
            Function function = new Function(
                    "initialize",
                    Arrays.asList(new Address(new BigInteger(user.substring(2), 16))),
                    Collections.emptyList());
            String initializeFun = FunctionEncoder.encode(function);

            String encodeCall = FunctionEncoder.encodeConstructor(
                    Arrays.asList(new Address(new BigInteger(beaconAddress.substring(2), 16)),
                            new DynamicBytes(ByteArray.fromHexString(initializeFun))));
            // encodePacked
            // cannot use FunctionEncoder.encodeConstructorPacked, because DynamicTypes packed function changed
            byte[] bytecodeHash = Hash.sha3(org.bouncycastle.util.Arrays.concatenate(creationCode, ByteArray.fromHexString(encodeCall)));

            // create2
            byte[] bytes = org.bouncycastle.util.Arrays.concatenate(ByteArray.fromHexString(Parameter.CommonConstant.ADD_PRE_FIX_STRING), ByteArray.fromHexString(gasFreeControlAddress.substring(2)));
            byte[] bytes1 = org.bouncycastle.util.Arrays.concatenate(bytes, salt);
            byte[] bytes2 = org.bouncycastle.util.Arrays.concatenate(bytes1, bytecodeHash);
            byte[] bytes3 = Hash.sha3omit12(bytes2);
            String gasFreeAddress = AddressUtil.encode58Check(bytes3);
            return gasFreeAddress;
        } catch (Exception e) {
            return "";
        }
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
        domain.put("name", ParamCheck.DOMAIN_NAME);
        domain.put("verifyingContract", verifyingContract);
        domain.put("version", ParamCheck.DOMAIN_VERSION);

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
        return permitTransferMessageHash(jsonString);
    }
    public static String permitTransferMessageHash(String eip712Message) throws Exception {
        String normalizedMessage = ParamCheck.validatedMessage(eip712Message).toString();
        try {
            StructuredDataEncoder dataEncoder = new StructuredDataEncoder(normalizedMessage);
            return Numeric.toHexString(dataEncoder.hashStructuredData());
        } catch (Exception e) {
            throw new GasFreeException("Unable to encode GasFree typed data", e);
        }
    }


    private static byte[] byte32Address(String normalizedAddress) {
        // Remove exactly the network byte from the validated 21-byte address.
        byte[] addressBytes = ByteArray.fromHexString(normalizedAddress.substring(2));
        return new DataWord(addressBytes).getData();
    }
}

