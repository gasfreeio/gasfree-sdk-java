# gasFree-Java-SDK-tmp

gasFree-SDK-Java is a comprehensive toolkit designed to streamline and simplify gasFree development by TronLink.
This SDK provides a collection of utility classes and methods to enhance the efficiency and ease of implementing gasFree transactions in Java-based applications.

Key Features:
- Generate gasFree Addresses from User Addresses
- Generate gasFree EIP-712 Transfer Message Hash

## Requirements
- Android 21+
- Java 1.8+
## How to use
Add the JitPack maven repository.
```
    maven { url "https://jitpack.io"  }
```
Add dependency.
```
    implementation 'com.github.TronLink:gasFree-Java-SDK:1.0.0@aar'
```
## Integrity Check
gasFree-Java-SDK.aar is signed by the gpg key as below. You can use the gpg public key to verify the integrity of the officially released core library.
  ```
  pub: 7B910EA80207596075E6D7BA5D34F7A6550473BA
  uid: build_tronlink <build@tronlink.org>
  ```
For example:
  ```
  #gpg --verify gasFree-Java-SDK-xxx.aar.asc gasFree-Java-SDK-xxx.aar
  gpg: Signature made 一  7/29 16:03:14 2024 CST
  gpg:                using RSA key 7B910EA80207596075E6D7BA5D34F7A6550473BA
  gpg: Good signature from "build_tronlink <build@tronlink.org>"
  ```
## Demo
- [GasFreeGeneratorTest](./gasfree-sdk-java/src/test/java/org/tron/gasfree/sdk/GasFreeGeneratorTest.java)
## for generateGasFreeAddress
import gasfree sdk GasFreeGenerator class
```
    import org.tron.gasfree.sdk.GasFreeGenerator;
```
use generateGasFreeAddress with userAddress and general NetCode (et. Constant.MainNetCode 0x2b6653dc)
```
String gasFreeAddress = GasFreeGenerator.generateGasFreeAddress(userAddress, Constant.MainNetCode);
```
gasFreeAddress is your gasFree Address

or more param
use userAddress, beaconAddress,GasFreeControllerAddress and creationCodeStr
```
GasFreeGenerator.generateGasFreeAddress(userAddress, Constant.beaconAddressRelease, Constant.GasFreeControllerAddressRelease, Constant.creationCodeStr);
```

## permitTransferMessageHash
### generate GasFree EIP 712 sign hash
import gasfree sdk GasFreeGenerator class
```
- import org.tron.gasfree.sdk.GasFreeGenerator;
```

with those params:
```
String chainId,
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
```
used permitTransferMessageHash function
```
String eipHash = GasFreeGenerator.permitTransferMessageHash(chainId,verifyingContract,token,serviceProvider,user,receiver,value,maxFee,deadline,version,nonce);
```
You will get the GasFree Transfer Message Hash.
or you just use the permitTransferMessageHash(eipJson) function.

and more, you should sign the hash for real transaction.
