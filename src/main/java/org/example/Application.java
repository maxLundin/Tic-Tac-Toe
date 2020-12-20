package org.example;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        System.out.println("Hello!");
        int val = 0;
        Scanner scanner = new Scanner(System.in);
        while (val != 1 && val != 2) {
            System.out.println("Find game? - 1");
            System.out.println("New game? - 2");
            val = scanner.nextInt();
        }
        switch (val) {
            case 1:
                try (Server server = new Server()) {
                    server.start(8090, false);
                }
                break;
            case 2:
                try (Server server = new Server()) {
                    server.start(8090, true);
                }
                break;
        }
        try {
            String[] inet = InetAddress.getLocalHost().toString().split("/");
            String address = inet[inet.length - 1];
            System.out.println(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
