package org.tron.gasfree.sdk;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.tron.common.utils.ByteArray;
import org.tron.walletserver.AddressUtil;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/** Pure GasFree V1 protocol checks; live account and fee checks belong to the caller. */
public class ParamCheck {
    static final String DOMAIN_NAME = "GasFreeController";
    static final String DOMAIN_VERSION = "V1.0.0";
    private static final String[][] DOMAIN_FIELDS = {
            {"name", "string"}, {"version", "string"},
            {"chainId", "uint256"}, {"verifyingContract", "address"}
    };
    private static final String[][] MESSAGE_FIELDS = {
            {"token", "address"}, {"serviceProvider", "address"},
            {"user", "address"}, {"receiver", "address"},
            {"value", "uint256"}, {"maxFee", "uint256"},
            {"deadline", "uint256"}, {"version", "uint256"}, {"nonce", "uint256"}
    };

    /** Retained for source and binary compatibility with the original misspelled API. */
    public static boolean isValidParma(String eip712Message) throws Exception {
        return isValidParam(eip712Message);
    }

    public static boolean isValidParam(String eip712Message) throws GasFreeException {
        validatedMessage(eip712Message);
        return true;
    }

    /** Matches the main app's GasFreeChainIdValidator parsing. */
    static BigInteger parseChainId(String chainId) throws GasFreeException {
        require(chainId != null && !chainId.trim().isEmpty(), "Invalid chainId");
        String normalized = chainId.trim();
        int radix = 10;
        if (normalized.startsWith("0x") || normalized.startsWith("0X")) {
            normalized = normalized.substring(2);
            radix = 16;
        }
        require(!normalized.isEmpty(), "Invalid chainId");
        try {
            BigInteger parsed = new BigInteger(normalized, radix);
            require(parsed.signum() >= 0, "Invalid chainId");
            return parsed;
        } catch (NumberFormatException e) {
            throw new GasFreeException("Invalid chainId", e);
        }
    }

    private static JsonObject validatedMessage(String payload) throws GasFreeException {
        try {
            require(payload != null && !payload.trim().isEmpty(), "Invalid input message");
            JsonElement parsed = JsonParser.parseString(payload);
            require(parsed != null && parsed.isJsonObject(), "Invalid input message");
            JsonObject root = parsed.getAsJsonObject();
            exactString(root.get("primaryType"), "PermitTransfer", "primaryType");
            JsonObject types = object(root.get("types"), "types");
            exactKeys(types, "EIP712Domain", "PermitTransfer");
            checkFields(types.get("EIP712Domain"), DOMAIN_FIELDS);
            checkFields(types.get("PermitTransfer"), MESSAGE_FIELDS);

            JsonObject domain = object(root.get("domain"), "domain");
            exactKeys(domain, "name", "version", "chainId", "verifyingContract");
            exactString(domain.get("name"), DOMAIN_NAME, "domain.name");
            exactString(domain.get("version"), DOMAIN_VERSION, "domain.version");
            unsigned(domain.get("chainId"), true, "domain.chainId");
            address(domain.get("verifyingContract"), "domain.verifyingContract");

            JsonObject message = object(root.get("message"), "message");
            String[] names = new String[MESSAGE_FIELDS.length];
            for (int i = 0; i < names.length; i++) names[i] = MESSAGE_FIELDS[i][0];
            exactKeys(message, names);
            for (String[] field : MESSAGE_FIELDS) {
                if ("address".equals(field[1])) {
                    address(message.get(field[0]), "message." + field[0]);
                } else {
                    unsigned(message.get(field[0]), false, "message." + field[0]);
                }
            }
            return root;
        } catch (GasFreeException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new GasFreeException("Invalid GasFree typed data", e);
        }
    }

    private static void checkFields(JsonElement value, String[][] fields) throws GasFreeException {
        require(value != null && value.isJsonArray(), "Invalid type definition");
        JsonArray entries = value.getAsJsonArray();
        require(entries.size() == fields.length, "Invalid type field count");
        for (int i = 0; i < fields.length; i++) {
            JsonObject entry = object(entries.get(i), "type field");
            exactKeys(entry, "name", "type");
            exactString(entry.get("name"), fields[i][0], "type field name/order");
            exactString(entry.get("type"), fields[i][1], "type field type");
        }
    }

    private static JsonObject object(JsonElement element, String field) throws GasFreeException {
        require(element != null && element.isJsonObject(), "Invalid " + field);
        return element.getAsJsonObject();
    }

    private static void exactKeys(JsonObject object, String... names) throws GasFreeException {
        Set<String> keys = new HashSet<>();
        for (java.util.Map.Entry<String, JsonElement> entry : object.entrySet()) keys.add(entry.getKey());
        require(keys.equals(new HashSet<>(Arrays.asList(names))), "Missing or unexpected fields");
    }

    private static void exactString(JsonElement value, String expected, String field) throws GasFreeException {
        require(isString(value) && expected.equals(value.getAsString()), "Invalid " + field);
    }

    private static boolean isString(JsonElement value) {
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString();
    }

    private static BigInteger unsigned(JsonElement value, boolean hexAllowed, String field)
            throws GasFreeException {
        require(value != null && value.isJsonPrimitive()
                && (value.getAsJsonPrimitive().isString() || value.getAsJsonPrimitive().isNumber()),
                "Invalid " + field);
        return unsignedString(value.getAsString(), hexAllowed, field);
    }

    private static BigInteger unsignedString(String value, boolean hexAllowed, String field)
            throws GasFreeException {
        require(value != null, "Invalid unsigned integer: " + field);
        boolean hex = hexAllowed && value.matches("0[xX][0-9a-fA-F]+");
        require(hex || value.matches("[0-9]+"), "Invalid unsigned integer: " + field);
        BigInteger integer = new BigInteger(hex ? value.substring(2) : value, hex ? 16 : 10);
        require(integer.bitLength() <= 256, "Integer exceeds uint256: " + field);
        return integer;
    }

    private static void address(JsonElement value, String field) throws GasFreeException {
        require(isString(value), "Invalid address: " + field);
        normalizeAddress(value.getAsString());
    }

    /** Matches the main app's GasFreeAddressValidator normalization for validation only. */
    static String normalizeAddress(String address) throws GasFreeException {
        require(address != null && !address.trim().isEmpty(), "Invalid TRON address");
        String value = address.trim();
        if (value.startsWith("0x") || value.startsWith("0X")) {
            value = value.substring(2);
        }
        if (value.matches("[0-9a-fA-F]{40}")) return "41" + value.toLowerCase(Locale.US);
        if (value.matches("41[0-9a-fA-F]{40}")) return value.toLowerCase(Locale.US);
        byte[] bytes = AddressUtil.decode58Check(value);
        require(bytes != null && bytes.length == 21 && bytes[0] == 0x41, "Invalid TRON address");
        return ByteArray.toHexString(bytes).toLowerCase(Locale.US);
    }

    private static void require(boolean condition, String message) throws GasFreeException {
        if (!condition) throw new GasFreeException(message);
    }
}
