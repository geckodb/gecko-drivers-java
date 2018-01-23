package com.geckodb.drivers.java.playground;

import com.geckodb.drivers.java.GeckoConnection;
import com.geckodb.drivers.java.GeckoDB;
import com.geckodb.drivers.java.GeckoDatabase;

import java.io.IOException;
import java.net.MalformedURLException;

public class Main {

    public static void main(String[] args) {

        try {
            GeckoConnection connection = GeckoDB.connect("http://localhost", 35497);

            System.out.print("Ports: ");
            for (Integer port : connection.getAvailablePorts()) {
                System.out.print(port + " ");
            }

            System.out.println("\nClient port: " + connection.getClientPort());

            GeckoDatabase database = connection.createDatabase("testdb");


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // write your code here
    }
}
