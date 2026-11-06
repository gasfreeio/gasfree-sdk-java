package org.tron.gasfree.sdk;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GasFreeGeneratorTest {
    private String userAddress = "TLFXfejEMgivFDR2x8qBpukMXd56spmFhz";

    @Test
    public void testGeneratorGasFreeAddress() {
        String gasFreeAddress = GasFreeGenerator.generateGasFreeAddress(userAddress, Constant.MainNetCode);
        Assert.assertEquals("TYKTmMyTeAFrfdRTpYHjnAtFEJtMMotJJe", gasFreeAddress);
    }

    @Test
    public void testGeneratorGasFreeMessageHash() throws FileNotFoundException {
        String resourcePath = getClass().getClassLoader().getResource("eip712.json").getPath();
        File file = new File(resourcePath);
        String json = new BufferedReader((new FileReader(file))).lines().collect(Collectors.joining(""));
        System.out.println(json);
        String eipHash  = null;
        try {
            eipHash = GasFreeGenerator.permitTransferMessageHash(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals("0x4e0e1444d20768c286b9de66064e4e7311b5160871c8c0292ffeac9a16265622",eipHash);
    }

    @Test
    public void testGeneratorGasFreeMessageHashByParam() {

        String chainId="728126428";
        String verifyingContract="TFFAMQLZybALaLb4uxHA9RBE7pxhUAjF3U";
        String token="TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";
        String serviceProvider="TLntW9Z59LYY5KEi9cmwk3PKjQga828ird";
        String user="TFDP1vFeSYPT6FUznL7zUjhg5X7p2AA8vw";
        String receiver ="TSPrmJetAMo6S6RxMd4tswzeRCFVegBNig";
        String value = "20000000";
        String maxFee = "20000000";
        long deadline = 1740641152;
        long version = 1;
        long nonce = 1;

        String eipHash  = null;
        try {
            eipHash = GasFreeGenerator.permitTransferMessageHash(chainId,verifyingContract,token,serviceProvider,user,receiver,value,maxFee,deadline,version,nonce);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals("0x4e0e1444d20768c286b9de66064e4e7311b5160871c8c0292ffeac9a16265622",eipHash);
    }

    private static final String EXPECTED_HASH =
            "0x4e0e1444d20768c286b9de66064e4e7311b5160871c8c0292ffeac9a16265622";

    private String fixture() throws IOException {
        String resourcePath = getClass().getClassLoader().getResource("eip712.json").getPath();
        return new String(Files.readAllBytes(Paths.get(resourcePath)), StandardCharsets.UTF_8);
    }

    private static String repeat(char c, int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    private static void assertRejected(String json) {
        try {
            GasFreeGenerator.permitTransferMessageHash(json);
            Assert.fail("Expected GasFreeException");
        } catch (GasFreeException expected) {
            // Expected validation failure.
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception type", e);
        }
    }

    @Test
    public void testAddressFormsWithinLengthLimitAreAccepted() {
        String expected = GasFreeGenerator.generateGasFreeAddress(userAddress, Constant.MainNetCode);
        String hex = "70c77e8ac165d2980e9741cb4af2e40cf3c280de";
        for (String form : new String[]{"0X41" + hex.toUpperCase(), "0x" + hex, "41" + hex, " " + userAddress + "\n"}) {
            Assert.assertEquals(form, expected, GasFreeGenerator.generateGasFreeAddress(form, Constant.MainNetCode));
        }
    }

    @Test(timeout = 2000)
    public void testOverlongAddressIsRejectedBeforeBase58Decoding() throws IOException {
        String overlong = repeat('2', 200_000);
        Assert.assertEquals("", GasFreeGenerator.generateGasFreeAddress(overlong, Constant.MainNetCode));
        Assert.assertEquals("", GasFreeGenerator.generateGasFreeAddress(userAddress, overlong,
                Constant.GasFreeControllerAddressRelease, Constant.creationCodeStrRelease));
        assertRejected(fixture().replace("TSPrmJetAMo6S6RxMd4tswzeRCFVegBNig", overlong));
        assertRejected(fixture().replace("TFFAMQLZybALaLb4uxHA9RBE7pxhUAjF3U", overlong));
    }

    @Test
    public void testDuplicateJsonKeysAreRejectedAtEveryLevel() throws IOException {
        String json = fixture();
        String[][] injections = {
                {"\"primaryType\": \"PermitTransfer\"", "\"primaryType\": \"PermitTransfer\", \"primaryType\": \"PermitTransfer\""},
                {"\"domain\": {", "\"domain\": { \"chainId\": \"1\","},
                {"\"message\": {", "\"message\": { \"value\": \"1\","},
                {"\"message\": {", "\"message\": { \"\\u0076alue\": \"1\","},
                {"\"types\": {", "\"types\": { \"EIP712Domain\": [],"},
                {"\"name\": \"token\",", "\"name\": \"token\", \"name\": \"token\","}
        };
        for (String[] injection : injections) {
            String duplicated = json.replace(injection[0], injection[1]);
            Assert.assertNotEquals(injection[1], json, duplicated);
            assertRejected(duplicated);
        }
    }

    @Test
    public void testEscapedKeyWithoutDuplicateIsAccepted() throws Exception {
        String escaped = fixture().replace("\"value\": \"20000000\"", "\"\\u0076alue\": \"20000000\"");
        Assert.assertEquals(EXPECTED_HASH, GasFreeGenerator.permitTransferMessageHash(escaped));
    }
}