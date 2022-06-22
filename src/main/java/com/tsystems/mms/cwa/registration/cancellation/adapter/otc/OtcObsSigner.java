package com.tsystems.mms.cwa.registration.cancellation.adapter.otc;

import com.amazonaws.services.s3.internal.AWSS3V4Signer;

public class OtcObsSigner extends AWSS3V4Signer {
    public OtcObsSigner() {
        super();
        this.serviceName = "s3";
    }

    @Override
    public void setServiceName(String serviceName) {
        // we ignore other service names
    }
}
