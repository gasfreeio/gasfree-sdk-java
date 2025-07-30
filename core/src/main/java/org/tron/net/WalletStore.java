package org.tron.net;

import lombok.Data;


@Data
public class WalletStore {
    private String walletAddress;
    private String walletName;
    private String privateKeyEncrypted;
    private String mnemonicEncrypted;
    private String mnemonic;
}
