# gasfree-sdk-java

gasfree-sdk-java is a toolkit developed based on the GasFree API specification,
It facilitates the integration of the non-gas TRC20 token transfer service for the android platform.

This SDK is maintained by the gasfree.io developer community. For more information, visit [gasfree.io](gasfree.io).


Key Features:

- Generate GasFree Addresses from User Addresses
- Generate GasFree Transfer Message Hash

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
implementation 'com.github.Gasfreeio:gasfree-sdk:1.1.0@aar'
```

## Demo
- [testGeneratorGasFreeAddress](./gasfree-sdk-java/src/test/java/org/tron/gasfree/sdk/GasFreeGeneratorTest.java)
- [testGeneratorGasFreeMessageHash](./gasfree-sdk-java/src/test/java/org/tron/gasfree/sdk/GasFreeGeneratorTest.java)
- [testGeneratorGasFreeMessageHashByParam](./gasfree-sdk-java/src/test/java/org/tron/gasfree/sdk/GasFreeGeneratorTest.java)
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
### generate GasFree Message hash
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
## License
This project is licensed under the Apache License Version 2.0 - see the [LICENSE](LICENSE) file for details
