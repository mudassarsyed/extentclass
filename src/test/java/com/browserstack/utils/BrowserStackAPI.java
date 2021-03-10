package com.browserstack.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Parameters;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;


// This class contains methods relating to the browserstack REST api
public class BrowserStackAPI {

    String AUTOMATE_USERNAME,AUTOMATE_ACCESS_KEY;

    public BrowserStackAPI(String user_name, String access_key){
        AUTOMATE_USERNAME = user_name;
        AUTOMATE_ACCESS_KEY = access_key;
    }


    public void setup(String config_file) throws FileNotFoundException, IOException, ParseException {

        JSONParser parser = new JSONParser();

    }

    public void markTestStatus(String session_id, String status,String reason) throws URISyntaxException, IOException {
        URI uri = new URI("https://"+ AUTOMATE_USERNAME + ":" + AUTOMATE_ACCESS_KEY+"@api.browserstack.com/automate/sessions/"+session_id+".json");
        HttpPut putRequest = new HttpPut(uri);

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add((new BasicNameValuePair("status", status)));
        nameValuePairs.add((new BasicNameValuePair("reason", reason)));

        putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpClientBuilder.create().build().execute(putRequest);
    }

    public JSONObject getSessionDetails(String session_id) throws URISyntaxException, IOException {
        URI uri = new URI("https://"+ AUTOMATE_USERNAME + ":" + AUTOMATE_ACCESS_KEY+"@api.browserstack.com/automate/sessions/"+session_id+".json");
        HttpGet getRequest = new HttpGet(uri);
        HttpResponse response =  HttpClientBuilder.create().build().execute(getRequest);
        String json_string = EntityUtils.toString(response.getEntity());
        JSONObject json_response = new JSONObject(json_string);
        return(json_response);
    }


    public  JSONObject getBuildDetails(String build_id) throws URISyntaxException, IOException {
        URI uri = new URI("https://"+ AUTOMATE_USERNAME + ":" + AUTOMATE_ACCESS_KEY+"@api.browserstack.com/automate/builds/"+build_id+"/sessions.json");
        HttpGet getRequest = new HttpGet(uri);
        HttpResponse response =  HttpClientBuilder.create().build().execute(getRequest);
        String json_string = EntityUtils.toString(response.getEntity());
        JSONObject json_response = new JSONObject(json_string);
        return(json_response);
    }


    // this function gets the value associated with the key from a json object
    public String getValue(JSONObject json, String key) {

        boolean exists = json.has(key);
        Iterator<?> keys;
        String nextKeys;

        if (!exists) {
            keys = json.keys();
            while (keys.hasNext()) {
                nextKeys = (String) keys.next();
                try {

                    if (json.get(nextKeys) instanceof JSONObject) {

                        if (exists == false) {
                            return getValue(json.getJSONObject(nextKeys), key);
                        }

                    } else if (json.get(nextKeys) instanceof JSONArray) {
                        JSONArray jsonarray = json.getJSONArray(nextKeys);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            String jsonarrayString = jsonarray.get(i).toString();
                            JSONObject innerJSOn = new JSONObject(jsonarrayString);

                            if (exists == false) {
                                return getValue(innerJSOn, key);
                            }

                        }

                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }

            }

        } else {
            return (String) json.get(key);
        }
        return (String) json.get(key);

    }
}
