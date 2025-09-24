package com.flaregames.imports.facebook;

import com.google.inject.Inject;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.AdSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaregames.imports.applovin.ImportLogServiceMock;
import com.flaregames.imports.importlog.TransactioDao;
import com.flaregames.imports.integration.AbstractIntegrationTest;
import com.flaregames.imports.schema.ClassToTableMapper;
import com.flaregames.imports.tableslice.TableSliceFactory;
import com.flaregames.imports.tableslice.TableSliceProcessor;
import com.flaregames.microservice.SystemTimeRule;

import net.jodah.failsafe.FailsafeException;
import net.jodah.failsafe.RetryPolicy;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class FacebookImportIntegrationTest extends AbstractIntegrationTest {
    private final Instant fixedDate = Instant.parse("2020-01-01T00:00:00Z");

    @Inject
    TableSliceFactory tableSliceFactory;
    @Inject
    TableSliceProcessor tableSliceProcessor;
    @Inject
    ClassToTableMapper classToTableMapper;
    @Inject
    TransactioDao dao;

    @Rule
    public SystemTimeRule systemTimeRule = new SystemTimeRule();

    @Before
    public void before() {
        systemTimeRule.useFixed(fixedDate);
    }

    @Test
    public void run_dataImported() {
        /*
        ImportLogServiceMock logService = new ImportLogServiceMock(dao);
        clientMock.setup(getAdSet());
        FacebookImport facebookImport = new FacebookImport(clientMock, classToTableMapper, tableSliceFactory, logService,
                tableSliceProcessor);

        assertThatCode(() -> facebookImport.run()).doesNotThrowAnyException();
        */
    }

    @Test
    public void run_throwException_retry(){
        /*
        ImportLogServiceMock logService = new ImportLogServiceMock(dao);

        String json = "";
        try {
            json = readFile("src/test/resources/fixtures/facebook/facebook_historical_adset.json",Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }


        AdSet fake = AdSet.loadJSON(json,new APIContext(""), "");

        clientMock.setup(fake, Duration.ofSeconds(1), 1);

        FacebookImport facebookImport = new FacebookImport(clientMock, classToTableMapper, tableSliceFactory, logService,
                tableSliceProcessor);

        assertThatExceptionOfType(FailsafeException.class).isThrownBy(()->facebookImport.run());

        assertThat(clientMock.getFailure()).isInstanceOf(APIException.class);

        assertThat(clientMock.isRetriesDone()).isTrue();
        */


    }

    AdSet getAdSet(){
        String json = "";
        try {
            json = readFile("src/test/resources/fixtures/facebook/facebook_historical_adset.json",Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        AdSet fake = AdSet.loadJSON(json,new APIContext(""), "");
        return fake;
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
