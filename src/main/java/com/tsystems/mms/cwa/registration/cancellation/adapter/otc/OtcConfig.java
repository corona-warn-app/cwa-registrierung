package com.tsystems.mms.cwa.registration.cancellation.adapter.otc;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.SignerFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtcConfig {

    @Bean
    public AWSCredentials basicAWSCredentials(@Value("${obs.access-key}") String accessKey,
                                              @Value("${obs.secret-key}") String secretKey) {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public ClientConfiguration clientConfiguration() {
        final var clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTPS);
        clientConfig.setSignerOverride("OtcObsSigner");
        SignerFactory.registerSigner("OtcObsSigner", OtcObsSigner.class);

        return clientConfig;
    }

    @Bean
    public AmazonS3 amazonS3(AWSCredentials awsCredentials, ClientConfiguration clientConfiguration) {
        return new OtcObsClient(awsCredentials, clientConfiguration);
    }

    @Bean
    public Bucket bucket(AmazonS3 amazonS3, @Value("test") String bucketName) {
        return amazonS3.listBuckets()
                .stream()
                .filter(bucket -> bucket.getName().equals(bucketName))
                .findFirst()
                .orElseThrow();
    }
}
