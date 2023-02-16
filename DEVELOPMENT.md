# Development

## Publishing

Create a GPG key with `gpg --gen-key`, and export it with `gpg --keyring secring.gpg --export-secret-keys > ~/.gnupg/secring.gpg`. Be sure to publish it with `gpg --keyserver keyserver.ubuntu.com --send-keys <your-key-id>`.

Create an user token for your Nexus account on https://s01.oss.sonatype.org/ (in your profile) to use as `sonatypeUsername` and `sonatypePassword`.

Configure your `~/.gradle/gradle.properties`:

```properties
signing.keyId=<last-8-chars>
signing.password=<private-key-password>
signing.secretKeyRingFile=<path-of-your-secring>
sonatypeUsername=<user-token-name>
sonatypePassword=<user-token-password>
```

Publish with:

```shell
./gradlew clean publishToSonatype
```

