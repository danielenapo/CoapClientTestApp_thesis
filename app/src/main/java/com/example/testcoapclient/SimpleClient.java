package com.example.testcoapclient;

import android.util.Log;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * A simple CoAP Synchronous Client implemented using Californium Java Library
 * The simple client send a GET request to a target CoAP Resource with some custom request parameters
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project coap-playground
 * @created 20/10/2020 - 09:19
 */
public class SimpleClient {

    private static final SimpleClient pluginInstance=new SimpleClient();
    public static SimpleClient getInstance(){
        Log.d("[DEB]", "getting instance");
        return pluginInstance;
    }

    private String response;

    public String getResponse(String ip, String resource) {
        URI uri = null; // URI parameter of the request
        try {
            uri = new URI("coap://" + ip + ":5683/" + resource);
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI: " + e.getMessage());
            response= "invalid URI" + e.getMessage();
            System.exit(-1);
        }
        Log.d("[DEB]",uri.toString());
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
}