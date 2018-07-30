package hr.in2.ballerina.crypto;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

import java.io.IOException;


/**
 * Extern function in2/crypto:firehoseBatchPut.
 *
 * @since 0.980.0
 */
@BallerinaFunction(
        orgName = "in2",
        packageName = "crypto:0.0.0",
        functionName = "firehoseBatchPut",
        args = {
                @Argument(name = "streamName", type = TypeKind.STRING),
                @Argument(name = "data", type = TypeKind.STRING)
        },
        returnType = {@ReturnType(type = TypeKind.STRING)},
        isPublic = true)

public class FirehoseBatchPut extends BlockingNativeCallableUnit {
        String result = "OK";
        @Override
        public void execute(Context context)   {
        String streamName = context.getStringArgument(0);
        String data = context.getStringArgument(1);
        try {
            AmazonKinesisFirehoseDelivery.putRecordBatchIntoDeliveryStream(streamName, data);
        } catch (IOException e) {
            result = "ERROR";
        }

        context.setReturnValues(new BString(result));
        }
}
