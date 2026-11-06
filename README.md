# gasfree-sdk-java

gasfree-sdk-java is a toolkit developed based on the GasFree API specification,
It facilitates the integration of the non-gas TRC20 token transfer service for the android platform.

This SDK is maintained by the gasfree.io developer community. For more information, visit [gasfree.io](gasfree.io).


Key Features:

- Generate GasFree Addresses from User Addresses
- Generate GasFree Transfer Message Hash

## Compatibility

The SDK uses `tronlink-android-core 1.0.14` and follows the GasFree validation
rules in TronLink Android. Address generation accepts decimal or hexadecimal
chain IDs for Mainnet, Nile, and Shasta:

| Network | Decimal chain ID | Hexadecimal chain ID |
| --- | --- | --- |
| Mainnet | `728126428` | `0x2b6653dc` |
| Nile | `3448148188` | `0xcd8690dc` |
| Shasta | `2494104990` | `0x94a9059e` |

Unknown or invalid chain IDs return an empty string from
`generateGasFreeAddress(userAddress, chainId)`. For a custom deployment, use the
overload with explicit beacon, controller, and creation code. Address generation
also returns an empty string when validation or encoding fails.
Custom deployment creation code must contain at least one byte of paired hex
digits; the optional `0x` or `0X` prefix is accepted.

Address generation and hashing accept Base58Check, 40-digit hex, and `41` followed
by 40 hex digits. Both hex forms may include a `0x` or `0X` prefix. Equivalent
representations are normalized to the same address before encoding.

Hash generation checks the GasFree V1 schema, domain, required message fields,
address formats, and unsigned `uint256` values before passing the normalized JSON
to the core encoder. The hash overloads declare `Exception`; expected validation
and encoding failures are reported as `GasFreeException`. Use integer strings
for large `uint256` values rather than converting through floating-point numbers.
Decimal `uint256` representations are limited to 78 digits. Hexadecimal chain
IDs are limited to 64 hex digits excluding the optional prefix. In both cases,
the numeric value must not exceed `2^256 - 1`.

Hash generation is an offline operation. Callers must check the selected network
and contract, token/provider support, current fees and activation status, message
version, account nonce, and deadline before requesting a signature or submitting
a transfer.

## Requirements
- Android 21+
- Java 1.8+ for SDK consumers
- JDK 17 to build this repository with Android Gradle Plugin 8.5.1
- Android SDK Platform 34 for the library; Platform 33 for the included demo
- Use the included Gradle 8.9 wrapper

## How to use
Add the JitPack maven repository.
```
maven { url "https://jitpack.io"  }
```
Add dependency.
```
implementation 'com.github.Gasfreeio:gasfree-sdk:1.1.1'
```
Keep the dependency's POM metadata so Gradle also resolves the SDK's required
runtime dependencies. Do not append `@aar`.

Version `1.1.1` is the next release and is not published yet. Release it from a
new immutable `1.1.1` tag and verify this coordinate resolves on JitPack before
using it. Do not move or replace the already published `v1.1.0` tag.

## Demo
- [testGeneratorGasFreeAddress](./gasfree-sdk/src/test/java/org/tron/gasfree/sdk/GasFreeGeneratorTest.java)
- [testGeneratorGasFreeMessageHash](./gasfree-sdk/src/test/java/org/tron/gasfree/sdk/GasFreeGeneratorTest.java)
- [testGeneratorGasFreeMessageHashByParam](./gasfree-sdk/src/test/java/org/tron/gasfree/sdk/GasFreeGeneratorTest.java)
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
    GasFreeGenerator.generateGasFreeAddress(userAddress, Constant.beaconAddressRelease, Constant.GasFreeControllerAddressRelease, Constant.creationCodeStrRelease);
```

## permitTransferMessageHash
### generate GasFree Message hash
import gasfree sdk GasFreeGenerator class
```
import org.tron.gasfree.sdk.GasFreeGenerator;
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
