/*
 * Copyright (c) 2018, IN2 Ltd. (http://www.in2.hr) All Rights Reserved.
 *
 * IN2 Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package hr.in2.ballerina.crypto;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.stdlib.crypto.util.HashUtils;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Extern function in2/crypto:hmac.
 *
 * @since 0.980.0
 */
@BallerinaFunction(
        orgName = "in2",
        packageName = "crypto:0.0.0",
        functionName = "hmac",
        args = {
            @Argument(name = "baseString", type = TypeKind.STRING),
            @Argument(name = "keyString", type = TypeKind.STRING),
            @Argument(name = "algorithm", type = TypeKind.STRING),
            @Argument(name = "keyType", type = TypeKind.STRING)
        },
        returnType = { @ReturnType(type = TypeKind.STRING) },
        isPublic = true
)
public class Hmac extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        String baseString = context.getStringArgument(0);
        String keyString = context.getStringArgument(1);
        BString algorithm = context.getNullableRefArgument(0) != null ?
                (BString) context.getNullableRefArgument(0) : new BString("");
        BString keyType = context.getNullableRefArgument(1) != null ?
                (BString) context.getNullableRefArgument(1) : new BString("");
        String hmacAlgorithm;

        //This is the only change I made to Marko's function

        try {
            AmazonKinesisFirehoseDelivery.initClients();
        } catch (Exception e) {
            throw new BallerinaException("Error initClients: " + e.getMessage(),
                    context);
        }

        //end of the change


        switch (algorithm.stringValue()) {
            case "SHA1":
                hmacAlgorithm = "HmacSHA1";
                break;
            case "SHA256":
                hmacAlgorithm = "HmacSHA256";
                break;
            case "MD5":
                hmacAlgorithm = "HmacMD5";
                break;
            default:
                throw new BallerinaException("Unsupported algorithm " + algorithm + " for HMAC calculation");
        }

        String result;
        try {
            byte[] keyBytes;
            switch (keyType.stringValue()) {
                case "TEXT":
                    keyBytes = keyString.getBytes(Charset.defaultCharset());
                    break;
                case "BASE64":
                    keyBytes = Base64.getDecoder().decode(keyString.getBytes(Charset.defaultCharset()));
                    break;
                default:
                    throw new BallerinaException("Unsupported key type " + keyType + " for HMAC calculation");
            }

            SecretKey secretKey = new SecretKeySpec(keyBytes, hmacAlgorithm);
            Mac mac = Mac.getInstance(hmacAlgorithm);
            mac.init(secretKey);
            byte[] baseStringBytes = baseString.getBytes(Charset.defaultCharset());
            result = HashUtils.toHexString(mac.doFinal(baseStringBytes));
        } catch (IllegalArgumentException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new BallerinaException("Error while calculating HMAC for " + hmacAlgorithm + ": " + e.getMessage(),
                    context);
        }
        context.setReturnValues(new BString(result));
    }
}
