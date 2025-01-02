# Tronlink-gasFree-sdk-java

Tronlink-gasFree-sdk-java is a toolkit designed by TronLink to enable seamless integration with the gasFree transfer service. It offers a collection of utility classes and methods to streamline development and integration efficiency for Java-based apps.

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
