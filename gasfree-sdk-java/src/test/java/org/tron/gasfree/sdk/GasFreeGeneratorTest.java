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
        String gasFreeAddress = GasFreeGenerator.generateGasFreeAddress(userAddress, Constant.NileNetCode);
        Assert.assertEquals("TNXEYu8LmxdGqjgF9SgpeEgXV2aAhhsYcv", gasFreeAddress);
    }

    @Test
    public void testGeneratorGasFreeMessageHash() throws FileNotFoundException {
        String resourcePath = getClass().getClassLoader().getResource("eip712.json").getPath();
        File file = new File(resourcePath);
        String json = new BufferedReader((new FileReader(file))).lines().collect(Collectors.joining(""));
        String eipHash  = null;
        try {
            eipHash = GasFreeGenerator.permitTransferMessageHash(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals("0x18cc1af5a367707a4b514cb37c5f9b5be568c5761d6caed614c98b2e4943b210",eipHash);
    }

    @Test
    public void testGeneratorGasFreeMessageHashByParam() {
        String chainId="3448148188";// nile test
        String verifyingContract="TNtzqaE9p23tzpN1SHavUCCuzSwrzbHEHE";
        String token="TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf";
        String serviceProvider="TQ6qStrS2ZJ96gieZJC8AurTxwqJETmjfp";
        String user="TKtWbdzEq5ss9vTS9kwRhBp5mXmBfBns3E";
        String receiver ="TQ6qStrS2ZJ96gieZJC8AurTxwqJETmjfp";
        String value = "1000000";
        String maxFee = "1000000";
        long deadline = 1731066521;
        long version = 1;
        long nonce = 0;

        String eipHash  = null;
        try {
            eipHash = GasFreeGenerator.permitTransferMessageHash(chainId,verifyingContract,token,serviceProvider,user,receiver,value,maxFee,deadline,version,nonce);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals("0x18cc1af5a367707a4b514cb37c5f9b5be568c5761d6caed614c98b2e4943b210",eipHash);
    }
}