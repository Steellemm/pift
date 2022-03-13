package com.griddynamics.pift.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpHelperTest {

    private final static int PORT = 8998;

    static WireMockServer wireMockServer = new WireMockServer(PORT, -1);
    HttpHelper httpHelper = new HttpHelper("localhost", PORT,false);

    @BeforeAll
    static void serverUp() {
        wireMockServer.start();
        configureFor("localhost", PORT);
    }

    @AfterAll
    static void serverDown() {
        wireMockServer.stop();
    }

    @Test
    void getTest() {
        stubFor(get(urlEqualTo("/api/v1/test?key=value")).willReturn(aResponse().withBody("Done")));
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        String answer = httpHelper.get("/api/v1/test", params);
        assertEquals("Done", answer);
    }

    @Test
    void postTest() {
        stubFor(post(urlEqualTo("/api/v1/post")).willReturn(aResponse().withBody("Done")));
        String answer = httpHelper.post("/api/v1/post", "");
        assertEquals("Done", answer);
    }


}