/*
 * Copyright 2012-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package hr.in2.ballerina.crypto;



import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClientBuilder;
import com.amazonaws.services.kinesisfirehose.model.PutRecordBatchRequest;
import com.amazonaws.services.kinesisfirehose.model.PutRecordBatchResult;
import com.amazonaws.services.kinesisfirehose.model.Record;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;







/**
 * xxx
 *
 * @since 0.980.0
 */
public class AmazonKinesisFirehoseDelivery {

    // DeliveryStream properties
    protected static AmazonKinesisFirehose firehoseClient;
    protected static String firehoseRegion;


    private static final int BATCH_PUT_MAX_SIZE = 500;

    // Logger
    private static final Log LOG = LogFactory.getLog(AmazonKinesisFirehoseDelivery.class);

    /**
     * Method to initialize the client using AWSCredentials set by awsCLI.
     *
     */
    protected static void initClients() throws Exception {
        /*
         * The ProfileCredentialsProvider will return your [default] credential
         * profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            //throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
            //        + "Please make sure that your credentials file is at the correct "
            //        + "location (~/.aws/credentials), and is in valid format.", e);
            throw new BallerinaException("Error initClients: " + e.getMessage());
        }



        // Firehose client
        firehoseClient = AmazonKinesisFirehoseClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion(firehoseRegion)
            .build();

    }


    /**
     * Method to put records in the specified delivery stream by reading
     * contents from sample input file using PutRecordBatch API.
     *
     * @throws IOException
     */
    protected static void putRecordBatchIntoDeliveryStream(String deliveryStreamName,
                                                           String inputString) throws IOException {
        //try (InputStream inputStream =
        //        Thread.currentThread().getContextClassLoader().getResourceAsStream("batchPutInput.txt")) {
        //    if (inputStream == null) {
        //        throw new FileNotFoundException("Could not find file " + BATCH_PUT_STREAM_SOURCE);
        //    }
        try (InputStream inputStream =
                     new ByteArrayInputStream(inputString.getBytes("UTF-8"))) {
            if (inputStream == null) {
                throw new FileNotFoundException("Could not find file ");
            }

            List<Record> recordList = new ArrayList<Record>();
            int batchSize = 0;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    String data = line + "\n";
                    Record record = createRecord(data);
                    recordList.add(record);
                    batchSize++;

                    if (batchSize == BATCH_PUT_MAX_SIZE) {
                        putRecordBatch(deliveryStreamName, recordList);

                        recordList.clear();
                        batchSize = 0;
                    }
                }

                if (batchSize > 0) {
                    putRecordBatch(deliveryStreamName, recordList);
                }
            }
        }
    }


    /**
     * Method to perform PutRecordBatch operation with the given record list.
     *
     * @param recordList the collection of records
     * @return the output of PutRecordBatch
     */
    private static PutRecordBatchResult putRecordBatch(String deliveryStreamName, List<Record> recordList) {
        PutRecordBatchRequest putRecordBatchRequest = new PutRecordBatchRequest();
        putRecordBatchRequest.setDeliveryStreamName(deliveryStreamName);
        putRecordBatchRequest.setRecords(recordList);

        // Put Record Batch records. Max No.Of Records we can put in a
        // single put record batch request is 500
        return firehoseClient.putRecordBatch(putRecordBatchRequest);
    }


    private static Record createRecord(String data) {
        return new Record().withData(ByteBuffer.wrap(data.getBytes()));
    }
    public static void main(String[] args) throws Exception {
        String deliveryStreamName;
        initClients();

        try {

            deliveryStreamName = "test";


            // Batch Put records into deliveryStream
            LOG.info("Putting records in deliveryStream : " + deliveryStreamName
                    + " via Put Record Batch method.");
            putRecordBatchIntoDeliveryStream(deliveryStreamName, "ABCD\nEFGH\nWERT");

        } catch (AmazonServiceException ase) {
            LOG.error("Caught Amazon Service Exception");
            LOG.error("Status Code " + ase.getErrorCode());
            LOG.error("Message: " + ase.getErrorMessage(), ase);
        } catch (AmazonClientException ace) {
            LOG.error("Caught Amazon Client Exception");
            LOG.error("Exception Message " + ace.getMessage(), ace);
        }
    }
}
