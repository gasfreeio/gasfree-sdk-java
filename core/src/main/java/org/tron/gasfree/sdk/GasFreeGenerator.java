package org.tron.gasfree.sdk;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.tron.common.bip32.Numeric;
import org.tron.common.crypto.FunctionEncoder;
import org.tron.common.crypto.Hash;
import org.tron.common.crypto.StructuredDataEncoder;
import org.tron.common.crypto.datatypes.Address;
import org.tron.common.crypto.datatypes.DynamicBytes;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.LogUtils;
import org.tron.walletserver.AddressUtil;
import java.util.Arrays;

public class GasFreeGenerator {

    public static String generateGasFreeAddress(String user, String beanconAddress, String gasFreeContrallAddress, String creationCodeStr) {
        try {
            byte[] salt = new byte[32];
            System.arraycopy(AddressUtil.decode58Check(user), 1, salt, 12, 20);

            byte[] creationCode = ByteArray.fromHexString(creationCodeStr);

            byte[] beacon = new byte[32];
            System.arraycopy(AddressUtil.decode58Check(beanconAddress), 1, beacon, 12, 20);

            byte[] selector = new byte[4];
            System.arraycopy(Hash.sha3("initialize(address)".getBytes()), 0, selector, 0, 4);
            byte[] initializeFun = ByteUtils.concatenate(selector, salt);

            String encodeCall = FunctionEncoder.encodeConstructor(
                    Arrays.asList(new Address(AddressUtil.replace41Address(beanconAddress)),
                            new DynamicBytes(initializeFun)));

            // encodePacked
            // cannot use FunctionEncoder.encodeConstructorPacked, because DynamicTypes packed function changed
            byte[] bytecodeHash = Hash.sha3(ByteUtils.concatenate(creationCode, ByteArray.fromHexString(encodeCall)));

            // create2
            byte[] bytes = ByteUtils.concatenate(ByteArray.fromHexString("41"), ByteArray.fromHexString(AddressUtil.replace41Address(gasFreeContrallAddress)));
            byte[] bytes1 = ByteUtils.concatenate(bytes, salt);
            byte[] bytes2 = ByteUtils.concatenate(bytes1, bytecodeHash);
            byte[] bytes3 = Hash.sha3(bytes2);

            byte[] result = new byte[21];
            result[0] = 0x41;
            System.arraycopy(bytes3, 12, result, 1, 20);
            String gasFreeAddress = AddressUtil.encode58Check(result);
            return gasFreeAddress;
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return "";
    }

    public static String generateHash(String eip712Message) {
        try {
            StructuredDataEncoder dataEncoder = new StructuredDataEncoder(eip712Message);
            byte[] bytes = dataEncoder.hashDomain();
            String domainStr = Numeric.toHexString(bytes);

            byte[] hashMessage = dataEncoder.hashMessage();
            String messageStr = Numeric.toHexString(hashMessage);

            byte[] hashStructuredData = dataEncoder.hashStructuredData();
            String hash = Numeric.toHexString(hashStructuredData);
            return hash;
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return "";
    }
}

