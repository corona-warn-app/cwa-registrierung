package com.tsystems.mms.cwa.registration.cancellation.adapter.otc;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.InMemoryRegionImpl;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;


public class OtcObsClient extends AmazonS3Client {
    public OtcObsClient(AWSCredentials credentials, ClientConfiguration clientConfiguration) {
        super(credentials, clientConfiguration);
        this.setSignerRegionOverride("eu-de");
        this.setRegion(new Region(new InMemoryRegionImpl("eu-de", "otc.t-systems.com")));
    }

    @Override
    protected String getServiceNameIntern() {
        return "obs";
    }
}
