package com.flaregames.imports.facebook;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.AdSet;
import com.facebook.ads.sdk.AdsInsights;
import com.flaregames.imports.exceptions.HttpImportException;

import net.jodah.failsafe.RetryPolicy;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class RetryRequestWrapperTest {

    @Test
    public void retry_exception_thrown(){

        String json = "";
        try {
            json = readFile("src/test/resources/fixtures/facebook/facebook_historical_adset.json", Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        AdSet fakeAdset = AdSet.loadJSON(json,new APIContext(""), "");

        int maxRetries = 1;
        Duration duration = Duration.ofSeconds(1);
        AtomicBoolean retriesDone = new AtomicBoolean(false);

        RetryPolicy<Object> policy = new RetryPolicy<>().handle(HttpImportException.class)
                .withDelay(duration)
                .withMaxRetries(maxRetries)
                .onRetriesExceeded(e -> retriesDone.set(true));

        RequestWrapperMock mockReq = new RequestWrapperMock(fakeAdset.getInsights());

        RetryRequestWrapper<APINodeList<AdsInsights>> retryWrapper = new RetryRequestWrapper(mockReq);
        retryWrapper.setPolicy(policy);

        assertThatExceptionOfType(HttpImportException.class).isThrownBy(()->retryWrapper.execute());

        assertThat(mockReq.getFailure()).isInstanceOf(APIException.class);

        assertThat(retriesDone.get()).isTrue();
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
