# Java Encryption Library

This is a simple library to perform AES passphrase based encryption and decryption.

This library can be added to any Java project, to provide simple and reusable methods to encrypt and decrypt values using a secret passphrase as the key.

This library can also be used stand-alone to provide encryption and decryption functionality from the command line.

## Encryption Algorithm
This library uses the AES/CBC/NoPadding algorithm with the following default settings:
- Key Length: 256 bytes (currently the maximum allowed)
- Iterations: 100,000
- Salt Length: 16 bytes
- IV Length: 16 bytes (required size for AES)

Key length, iterations, and salt length can all be overridden from their default values during encryption.

Key size, iterations, salt, and IV are all stored in the metadata of the encrypted value, which is then used by the decryption method in order to properly decrypt the value.
This means that those values do not need to be passed along manually to the decryption method, and the decryption method is able to decrypt values using various settings without issue.

## Command Line Usage
#### Using the encrypt/decrypt scripts
The included [encrypt](encrypt) and [decrypt](decrypt) scripts are the simplest way to run this application.  They call Gradle tasks under the hood, ensuring that the project is properly built ahead of time.

In the examples below, replace `<data>` with the string data to encrypt or decrypt, and `<passphrase>` with the secret passphrase used for encryption/decryption.

###### Mac/Linux using command-line arguments
```shell
./encrypt <data> <passphrase>
```
```shell
./decrypt <data> <passphrase>
```

###### Mac/Linux using environment variables
```shell
data=<data> passphrase=<passphrase> ./encrypt
```
```shell
data=<data> passphrase=<passphrase> ./decrypt
```

###### Windows using command-line arguments
```batch
encrypt <data> <passphrase>
```
```batch
decrypt <data> <passphrase>
```

###### Windows using environment variables
```decrypt
set data=<data>
set passphrase=<passphrase>
encrypt
```
```decrypt
set data=<data>
set passphrase=<passphrase>
decrypt
```

#### Running with Gradle
The program can be run using the included gradle `encrypt` and `decrypt` tasks.

When run with Gradle, environment variables must be used for the parameters.

###### Mac/Linux using Gradle
```shell
data=<data> passphrase=<passphrase> ./gradlew encrypt
```
```shell
data=<data> passphrase=<passphrase> ./gradlew decrypt
```

###### Windows using Gradle
```batch
set data=<data>
set passphrase=<passphrase>
gradlew encrypt
```
```batch
set data=<data>
set passphrase=<passphrase>
gradlew decrypt
```

#### Running the JAR directly
The compiled fat jar can be executed from the command-line using `java -jar`.

###### Using command-line arguments
```shell
java -jar build/libs/java-encryption-*-all.jar encrypt <data> <passphrase>
```
```shell
java -jar build/libs/java-encryption-*-all.jar decrypt <data> <passphrase>
```

###### Using environment variables
```shell
method=encrypt data=<data> passphrase=<passphrase> java -jar build/libs/java-encryption-*-all.jar
```
```shell
method=decrypt data=<data> passphrase=<passphrase> java -jar build/libs/java-encryption-*-all.jar
```

## Technologies and Tools

### Development Tools
#### JDK 11
Even though we are trying to use JDK 21 for all new/updated internal projects,
libraries may be used in other systems running older versions of Java.
Therefore, we need to compile any libraries which may be used in systems outside our control using JDK 11 for best compatibility.

#### Grade
We are using [Gradle](https://gradle.org/) to manage all of our dependencies. A Gradle wrapper is included with the code.
The Gradle wrapper will download and use a local copy of Gradle, and all dependencies required by the project.
This means that a developer or build server does not need to install any special tools or frameworks other than JDK11 in order to get started.

However, installation and familiarity with the following tools __is recommended__.

- [Gradle](https://gradle.org/) - Build tool and dependency management
    - You _may_ install Gradle system-wide, but gradle commands for the project should always be run from the included Gradle wrapper:
        - `./gradlew`
    - The wrapper downloads a local version of Gradle for the project, and ensures that everyone is on the same version.

## Development Environment Setup
### Dependencies
Gradle will automatically download and install all dependencies when it is run using `./gradlew`.

All dependencies and versions are declared in `gradle/libs.versions.toml`, and then referenced in the `dependencies` block of `build.gradle.kts`.

This library relies on internal libraries hosted by [Artifactory](https://artifactory.apps.gevernova.net/)
This will require you to add your Artifactory login information to your `$HOME/.gradle/gradle.properties` file.
If this file does not exist, create a new one with the following contents.

```properties
artifactoryUser=<YOUR SSO>
artifactoryPassword=<YOUR ARTIFACTORY IDENTITY TOKEN>
```

You can generate an identity token by visiting your [user profile page on Artifactory](https://artifactory.apps.gevernova.net/ui/user_profile).