package org.tron.walletserver;

import static org.tron.common.bip32.Bip32ECKeyPair.HARDENED_BIT;

import androidx.annotation.NonNull;


import org.tron.common.bip32.Bip32ECKeyPair;
import org.tron.common.crypto.ECKey;
import org.tron.common.crypto.MnemonicUtils;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.GsonFormatUtils;
import org.tron.common.utils.Utils;
import org.tron.config.Parameter;

import java.io.Serializable;
import java.math.BigInteger;

public class Wallet implements Comparable<Wallet>, Serializable {
    private static final long serialVersionUID = Wallet.class.hashCode();

    private ECKey mECKey = null;

    private boolean isWatchOnly = false;
    private boolean isShieldedWallet = false;
    private String walletName = "";
    private String encPassword = "";
    public String address = "";
    private byte[] encPrivateKey;
    private byte[] privateKeyBytes33;


    private byte[] publicKey;
    private String iconRes;//Avatar
    private boolean isBackUp;//whether  backed up
    private String mnemonic;
    private String keyStore;
    private int createType = -1;// tronconfig type
    private long createTime;
    private int color = -1;
    private static final String TAG = "Wallet";
    private String mnemonicPath;


    public String getMnemonicPathString() {
        return mnemonicPath;
    }

    /**
     * @param mnemonicPath WalletPath.class json
     */
    public void setMnemonicPath(String mnemonicPath) {
        this.mnemonicPath = mnemonicPath;
    }

    public int getMnemonicLength() {
        return mnemonicLength;
    }

    public void setMnemonicLength(int mnemonicLength) {
        this.mnemonicLength = mnemonicLength;
    }

    private int mnemonicLength;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public boolean isBackUp() {
        return isBackUp;
    }

    public void setBackUp(boolean backUp) {
        isBackUp = backUp;
    }

    public String getIconRes() {
        return iconRes;
    }

    public void setIconRes(String iconRes) {
        this.iconRes = iconRes;
    }

    public boolean isShieldedWallet() {
        return isShieldedWallet;
    }

    public void setShieldedWallet(boolean shieldedWallet) {
        isShieldedWallet = shieldedWallet;
    }


    public Wallet() {
    }

    public Wallet(boolean generateECKey) {
        if (generateECKey) {
            byte[] initialEntropy = new byte[16];
            Utils.getRandom().nextBytes(initialEntropy);

            mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
            byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);

            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
            Bip32ECKeyPair bip44Keypair = generateBip44KeyPair(masterKeypair);
            privateKeyBytes33 = bip44Keypair.getPrivateKeyBytes33();
            mECKey = ECKey.fromPrivate(privateKeyBytes33);
        }
    }


    public Wallet(I_TYPE I_Type, String key) {
        if (I_Type == I_TYPE.PRIVATE) {
            generateKeyForPrivateKey(key);
            privateKeyBytes33 = ByteArray.fromHexString(key);
        } else if (I_Type == I_TYPE.MNEMONIC)
            generateKeyForMnemonic(key);
    }


    public Wallet(String mnemonic, WalletPath walletPath) {
        if (AddressUtil.isEmpty(mnemonic) || walletPath == null) return;
        generateKeyForMnemonic(mnemonic, walletPath.purpose, walletPath.coinType, walletPath.account, walletPath.change, walletPath.accountIndex);
        if (mECKey != null && mECKey.hasPrivKey()) {
            setMnemonicPath(GsonFormatUtils.toGsonString(walletPath));
            setMnemonic(mnemonic);
        }
    }

    public boolean isOpen() {
        return mECKey != null && mECKey.getPrivKeyBytes() != null;
    }


    public byte[] getPublicKey() {
        return mECKey != null ? mECKey.getPubKey() : publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getPrivateKey() {
        return mECKey != null ? mECKey.getPrivKeyBytes() : null;
    }

    public void generateKeyForPrivateKey(String privateKey) {

        if (privateKey != null && !privateKey.isEmpty()) {
            ECKey tempKey = null;
            try {
                BigInteger priK = new BigInteger(privateKey, 16);
                tempKey = ECKey.fromPrivate(priK);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            mECKey = tempKey;
        } else {
            mECKey = null;
        }
    }


    public void generateKeyForMnemonic(String mnemonic) {
        generateKeyForMnemonic(mnemonic, 44, 195, 0, 0, 0);
    }

    /**
     * @param mnemonic
     * @param purpose      default 44 (Documentation source from  BIP43)
     * @param coinType     TRON default 195
     * @param account      default 0
     * @param change       default 0
     * @param accountIndex default 0
     */
    public void generateKeyForMnemonic(String mnemonic, int purpose, int coinType, int account, int change, int accountIndex) {
        if (mnemonic != null && !mnemonic.isEmpty()) {
            ECKey tempKey = null;
            try {
                byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);
                Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
                Bip32ECKeyPair bip44Keypair = generateBip44KeyPair(masterKeypair, purpose, coinType, account, change, accountIndex);
                privateKeyBytes33 = bip44Keypair.getPrivateKeyBytes33();
                tempKey = ECKey.fromPrivate(privateKeyBytes33);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            mECKey = tempKey;
        } else {
            mECKey = null;
        }
    }

    public int getCreateType() {
        return createType;
    }

    public byte[] getPrivateKeyBytes33() {
        return privateKeyBytes33;
    }

    public void setCreateType(int createType) {
        this.createType = createType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isWatchOnly() {
        return isWatchOnly;
    }

    public boolean isWatchCold() {
        return isWatchOnly && getCreateType() == Parameter.CreateWalletType.TYPE_IMPORT_COLD;
    }

    public boolean isWatchNotPaired() {
        return isWatchOnly
                && getCreateType() != Parameter.CreateWalletType.TYPE_IMPORT_LEDGER
                && getCreateType() != Parameter.CreateWalletType.TYPE_IMPORT_COLD
                && getCreateType() != Parameter.CreateWalletType.TYPE_IMPORT_SAMSUNG_HD;
    }

    public void setWatchOnly(boolean watchOnly) {
        isWatchOnly = watchOnly;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getEncryptedPassword() {
        return encPassword;
    }

    public void setEncryptedPassword(String password) {
        this.encPassword = password;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getAddress() {
        if (mECKey != null && AddressUtil.isEmpty(address)) {
            return AddressUtil.encode58Check(mECKey.getAddress());
        } else if (publicKey != null && AddressUtil.isEmpty(address)) {
            return AddressUtil.encode58Check(ECKey.fromPublicOnly(publicKey).getAddress());
        } else {
            return address;
        }
    }

    public byte[] getDecode58CheckAddress() {
        if (mECKey != null && AddressUtil.isEmpty(address)) {
            return mECKey.getAddress();
        } else if (publicKey != null && AddressUtil.isEmpty(address)) {
            return ECKey.fromPublicOnly(publicKey).getAddress();
        } else {
            return AddressUtil.decode58Check(address);
        }
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ECKey getECKey() {
        return mECKey;
    }

    public byte[] getEncryptedPrivateKey() {
        return encPrivateKey;
    }

    public void setEncryptedPrivateKey(byte[] encPrivateKey) {
        this.encPrivateKey = encPrivateKey;
    }

    /**
     * @param master
     * @param purpose      default44 (Documentation source from   BIP43)
     * @param coinType     TRON default 195
     * @param account      default 0
     * @param change       default 0
     * @param accountIndex default 0
     */
    public Bip32ECKeyPair generateBip44KeyPair(Bip32ECKeyPair master, int purpose, int coinType, int account, int change, int accountIndex) {
        // m/44'/60'/0'/0
        // m/44'/195'/0'/0/0
        final int[] path = {purpose | HARDENED_BIT, coinType | HARDENED_BIT, account | HARDENED_BIT, change, accountIndex};

        return Bip32ECKeyPair.deriveKeyPair(master, path);
    }

    public Bip32ECKeyPair generateBip44KeyPair(Bip32ECKeyPair master) {
        return generateBip44KeyPair(master, 44, 195, 0, 0, 0);
    }

    @Override
    public int compareTo(@NonNull Wallet o) {

        if (this.createTime == o.getCreateTime()) {
            return 0;
        } else if (this.createTime > o.getCreateTime()) {
            return 1;
        } else {
            return -1;

        }
    }

    //    ================ START: merge Samsung keystore sdk ==============================
    private boolean isSamsungWallet = false;

    public boolean isSamsungWallet() {
        return createType == Parameter.CreateWalletType.TYPE_IMPORT_SAMSUNG_HD;
    }

    public boolean isLedgerHDWallet() {
        return createType == Parameter.CreateWalletType.TYPE_IMPORT_LEDGER;
    }

    public void setSamsungWallet(boolean samsungWallet) {
        isSamsungWallet = samsungWallet;
    }

    private String seedHash;


    public String getSeedHash() {
        return seedHash;
    }

    public void setSeedHash(String seedHash) {
        this.seedHash = seedHash;
    }

    //    ================= END =============================
}