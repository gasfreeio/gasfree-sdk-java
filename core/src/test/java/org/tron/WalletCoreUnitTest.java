package org.tron;

import org.junit.Assert;
import org.junit.Test;
import org.tron.common.utils.ByteArray;
import org.tron.config.Parameter;
import org.tron.net.CipherException;
import org.tron.walletserver.DuplicateNameException;
import org.tron.walletserver.I_TYPE;
import org.tron.walletserver.InvalidNameException;
import org.tron.walletserver.InvalidPasswordException;
import org.tron.walletserver.Wallet;

public class WalletCoreUnitTest {
    private static final String WALLET_NAME = "walletTest";
    private static String PRIVATE_KEY = "";
    private static String WALLET_ADDRESS = "";//
    private static String MNEMONIC = "";

    @Test
    public void createWallet() {
        Wallet testWallet = new Wallet(true);
        testWallet.setWalletName(WALLET_NAME);
        testWallet.setCreateType(Parameter.CreateWalletType.TYPE_CREATE_WALLET);
        testWallet.setCreateTime(System.currentTimeMillis());
        System.out.println("Address = " + testWallet.getAddress());
        Assert.assertTrue(testWallet.getAddress() != null);
        WALLET_ADDRESS = testWallet.getAddress();
        MNEMONIC = testWallet.getMnemonic();
        PRIVATE_KEY = ByteArray.toHexString(testWallet.getPrivateKey());
    }


    @Test
    public void importWalletWithMnemonic() {
        createWallet();
        Wallet testWallet = new Wallet(I_TYPE.MNEMONIC, MNEMONIC);
        testWallet.setWalletName(WALLET_NAME);
        testWallet.setCreateType(Parameter.CreateWalletType.TYPE_IMPORT_MNEMONIC_HD);
        testWallet.setCreateTime(System.currentTimeMillis());
        System.out.println("Address = " + testWallet.getAddress());
        Assert.assertTrue(testWallet.getAddress() != null);
        Assert.assertEquals(WALLET_ADDRESS, testWallet.getAddress());
    }

    @Test
    public void importWalletWithPrivateKey() {
        createWallet();
        Wallet testWallet = new Wallet(I_TYPE.PRIVATE, PRIVATE_KEY);
        testWallet.setWalletName(WALLET_NAME);
        testWallet.setCreateType(Parameter.CreateWalletType.TYPE_IMPORT_PRIKEY);
        testWallet.setCreateTime(System.currentTimeMillis());
        System.out.println("Address = " + testWallet.getAddress());
        Assert.assertTrue(testWallet.getAddress() != null);
        Assert.assertEquals(WALLET_ADDRESS, testWallet.getAddress());
    }

}
