package com.flaregames.imports.facebook;

import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.AdSet;
import com.facebook.ads.sdk.AdsInsights;
import com.flaregames.imports.exceptions.HttpImportException;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeExecutor;
import net.jodah.failsafe.RetryPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class RequestWrapperMock implements RequestWrapper {

    private Throwable failure = null;
    private AdSet.APIRequestGetInsights adSetRequest;

    public RequestWrapperMock(AdSet.APIRequestGetInsights adSetRequest) {
        this.adSetRequest = adSetRequest;
    }

    @Override
    public APINodeList<AdsInsights> execute(){
        try {
            return adSetRequest.execute();
        } catch (APIException e) {
            failure = e;
            throw new HttpImportException(e);
        }
    }

    public Throwable getFailure() {
        return failure;
    }
}
