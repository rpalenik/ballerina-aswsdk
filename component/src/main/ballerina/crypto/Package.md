## Package Overview

This package provides the functionality required to generate HMAC with plain text or Base64 encoded key.

Generating HMAC using plain text key:

```ballerina
import ballerina/io;
import in2/crypto;

function main(string[] args) {
  string stringToSign = "test";
  string signingKey = "test";

  string result = crypto:hmac(stringToSign, signingKey, crypto:SHA256);
  io:println(result);
}
```

Generating HMAC using Base64 encoded key:

```ballerina
import ballerina/io;
import in2/crypto;

function main(string[] args) {
  string stringToSign = "test";
  string signingKey = "dGVzdA==";

  string result = crypto:hmac(stringToSign, signingKey, crypto:SHA256, keyType = crypto:BASE64);
  io:println(result);
}
```