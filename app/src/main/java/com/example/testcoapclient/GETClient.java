package com.example.testcoapclient;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;



import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.elements.UDPConnector;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.config.UdpConfig;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;

public class GETClient {

    private static final File CONFIG_FILE = new File("Californium3.properties");
    private static final String CONFIG_HEADER = "Californium CoAP Properties file for client";
    private static final int DEFAULT_MAX_RESOURCE_SIZE = 2 * 1024 * 1024; // 2 MB
    private static final int DEFAULT_BLOCK_SIZE = 512;
    private String response;
    public static final String CLIENT_NAME = "client";

    //SINGLETON PATTERN
    private static final GETClient pluginInstance=new GETClient();
    public static GETClient getInstance(){
        return pluginInstance;
    }

    static {
        CoapConfig.register();
        UdpConfig.register();
    }

    private static DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

        @Override
        public void applyDefinitions(Configuration config) {
            config.set(CoapConfig.MAX_RESOURCE_BODY_SIZE, DEFAULT_MAX_RESOURCE_SIZE);
            config.set(CoapConfig.MAX_MESSAGE_SIZE, DEFAULT_BLOCK_SIZE);
            config.set(CoapConfig.PREFERRED_BLOCK_SIZE, DEFAULT_BLOCK_SIZE);
        }
    };



    public String getResponse(String ip, String resource) {
        initCoapEndpoint();
        Configuration config = Configuration.createWithFile(CONFIG_FILE, CONFIG_HEADER, DEFAULTS);
        Configuration.setStandard(config);

        URI uri = null; // URI parameter of the request

        try {
            uri = new URI("coap://" + ip + ":5683/" + resource);
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI: " + e.getMessage());
            response= "invalid URI" + e.getMessage();
            System.exit(-1);
        }
        Log.i("[DEB]",uri.toString());
        CoapClient client = new CoapClient(uri);

        try {
            CoapResponse coapResponse = client.get();
            Log.i("[DEB]","got a response");

            if (coapResponse != null) {
                response= coapResponse.getResponseText();
            } else {
                response= "No response received.";
            }

        } catch (ConnectorException | IOException e) {
            response= "Got an error: " + e;
        }

        Log.i("[DEB]",response);
        client.shutdown();
        return response;
    }


    private void initCoapEndpoint() {
        CoapConfig.register();
        UdpConfig.register();
        DtlsConfig.register();
        Configuration config = Configuration.createStandardWithoutFile();
        // setup coap EndpointManager to dtls connector
        DtlsConnectorConfig.Builder dtlsConfig = DtlsConnectorConfig.builder(config);
        dtlsConfig.set(DtlsConfig.DTLS_ROLE, DtlsConfig.DtlsRole.CLIENT_ONLY);
        dtlsConfig.set(DtlsConfig.DTLS_AUTO_HANDSHAKE_TIMEOUT, 30, TimeUnit.SECONDS);
        ConfigureDtls.loadCredentials(dtlsConfig, CLIENT_NAME);
        DTLSConnector dtlsConnector = new DTLSConnector(dtlsConfig.build());

        CoapEndpoint.Builder dtlsEndpointBuilder = new CoapEndpoint.Builder();
        dtlsEndpointBuilder.setConfiguration(config);
        dtlsEndpointBuilder.setConnector(dtlsConnector);
        EndpointManager.getEndpointManager().setDefaultEndpoint(dtlsEndpointBuilder.build());
        // setup coap EndpointManager to udp connector
        CoapEndpoint.Builder udpEndpointBuilder = new CoapEndpoint.Builder();
        UDPConnector udpConnector = new UDPConnector(null, config);
        udpEndpointBuilder.setConfiguration(config);
        udpEndpointBuilder.setConnector(udpConnector);
        EndpointManager.getEndpointManager().setDefaultEndpoint(udpEndpointBuilder.build());
    }
}