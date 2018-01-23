package com.geckodb.drivers.java;

import java.net.MalformedURLException;

/**
 * Created by marcus on 05.12.17.
 */
public final class GeckoDB {

    public static GeckoConnection connect(String url, int port) throws MalformedURLException {
        return new GeckoConnection(url, port);
    }

}
