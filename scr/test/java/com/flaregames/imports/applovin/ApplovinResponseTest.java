package com.flaregames.imports.applovin;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ApplovinResponseTest {
    public static ApplovinResponse getApplovinResponse() {
        return loadTestData();
    }

    public static ApplovinResponse getMalformedApplovinResponse() {
        ApplovinResponse testResponse = loadTestData();
        List<ApplovinRecord> recs = testResponse.results;
        recs.get(0).clicks = "NaN";
        testResponse.results = recs;

        return testResponse;
    }

    private static ApplovinResponse loadTestData() {
        ApplovinResponse testResponse = new ApplovinResponse();
        ObjectMapper mapper = new ObjectMapper();
        try {
            testResponse = mapper.readValue(new URL("file:src/test/resources/fixtures/applovin/applovin_historical.json"), ApplovinResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testResponse;
    }
}
