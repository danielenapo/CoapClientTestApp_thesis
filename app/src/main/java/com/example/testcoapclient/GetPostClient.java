package com.example.testcoapclient;

import android.util.Log;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GetPostClient {
    private String ip, resource;
    private URI uri;
    private CoapClient coapClient;



    public GetPostClient(String ip, String resource){
        this.ip=ip;
        this.resource=resource;
        try {
            uri = new URI("coap://" + ip + ":5683/" + resource);
        } catch (URISyntaxException e) {
            Log.d("[DEB] error","Invalid URI: " + e.getMessage());
            //response= "invalid URI" + e.getMessage();
            //return response;
        }
        Log.d("[DEB]",uri.toString());
        coapClient = new CoapClient(uri);
    }


    public void post(){
        Request request = new Request(CoAP.Code.POST);

        //Set Request as Confirmable
        request.setConfirmable(true);

        Log.d("DEB",String.format("Request Pretty Print: \n%s", Utils.prettyPrint(request)));

        try {
            CoapResponse coapResp = coapClient.advanced(request);
            Log.d("DEB",String.format("Response Pretty Print: \n%s", Utils.prettyPrint(coapResp)));
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }
    }

    public String get(){
        Request request= new Request(CoAP.Code.GET);
        request.setOptions(new OptionSet().setAccept(MediaTypeRegistry.APPLICATION_JSON));

        //Set Request as Confirmable
        request.setConfirmable(true);

        //Synchronously send the GET message (blocking call)
        CoapResponse coapResp = null;

        try {
            coapResp = coapClient.advanced(request);
            //Pretty print for the received response
            Log.d("DEB",String.format("Response Pretty Print: \n%s", Utils.prettyPrint(coapResp)));
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }
        return coapResp.getResponseText();
    }

}
