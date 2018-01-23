package com.geckodb.drivers.java;

import com.sun.deploy.net.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.net.www.http.HttpClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeckoConnection {

    private enum ClientState { SETUP_SUCCESS, SETUP_FAILED }

    private final static String API_ENTRY_POINT = "/";
    private final static String API_ENTRY_POINT_PORT_DELGATOR_KEY = "{use-port}";
    private final static String API_ENTRY_POINT_ALL_PORT_KEY = "ports";

    private final static String API_DATABASES = "/api/1.0/databases";

    private int clientPort;
    private ClientState state;
    private StringBuilder stringBuilder;
    private URL apiEntryPointURL;
    private URL apiDatabasesURL;
    private List<Integer> availablePorts;

    GeckoConnection(final String url, int port) throws MalformedURLException {

        this.apiEntryPointURL = new URL(url + ":" + port + API_ENTRY_POINT);

        stringBuilder = new StringBuilder();

        try  {
            HttpURLConnection connection = (HttpURLConnection) this.apiEntryPointURL.openConnection();
            String readStream = responseToString(connection.getInputStream());
            JSONObject response = new JSONObject(readStream);
            this.clientPort = response.getInt(API_ENTRY_POINT_PORT_DELGATOR_KEY);

            JSONArray array = response.getJSONArray(API_ENTRY_POINT_ALL_PORT_KEY);
            availablePorts = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++) {
                availablePorts.add(array.getInt(i));
            }

            this.state = ClientState.SETUP_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            this.state = ClientState.SETUP_FAILED;
        }

        this.apiDatabasesURL = new URL(url + ":" + clientPort + API_DATABASES);


    }

    public List<Integer> getAvailablePorts() {
        return Collections.unmodifiableList(availablePorts);
    }

    public boolean hasDatabase(String name) {
        return false;
    }

    public GeckoDatabase createDatabase(String name) throws IOException {

        System.out.println(apiDatabasesURL);

        URLConnection connection = this.apiDatabasesURL.openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json;charset=" + Charset.defaultCharset());

        String request = "{\"database\": \n{ \t\"name\": \"" + name + "\" }\n}";

        connection.setRequestProperty("Gecko-Operation", "create database");

        try (OutputStream output = connection.getOutputStream()) {
            output.write(request.getBytes(Charset.defaultCharset()));
        }

        InputStream response = connection.getInputStream();

        return null;
    }

    public boolean deleteDatabase(String name) {
        return false;
    }

    public Iterable<String> getDatabaseNames() {
        return null;
    }

    public GeckoDatabase getDatabase(String name) {
        return new GeckoDatabase(this, name);
    }

    public int getClientPort() {
        return clientPort;
    }

    private String responseToString(InputStream in) {
        this.stringBuilder.setLength(0);
        String nextLine;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {
            while ((nextLine = reader.readLine()) != null) {
                stringBuilder.append(nextLine + nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }



}
