package org.tron.gasfree.sdk;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Collectors;

public class GasFreeGeneratorTest {
    private String userAddress = "TLFXfejEMgivFDR2x8qBpukMXd56spmFhz";

    @Test
    public void testGeneratorGasFreeAddress() {
        String gasFreeAddress = GasFreeGenerator.generateGasFreeAddress(userAddress, Constant.MainNetCode);
        Assert.assertEquals("TLQqSBqRPEeGc5PEiQizE9cocHfhcfg9tL", gasFreeAddress);
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
}