package org.example;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Application {
    public static void main(String[] args) {
        System.out.println("Hello");
        try {
            String[] inet = InetAddress.getLocalHost().toString().split("/");
            String address = inet[inet.length - 1];
            System.out.println(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try (Server server = new Server()) {
            server.start(8090, true);
        }
    }
}
