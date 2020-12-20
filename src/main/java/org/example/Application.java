package org.example;

import org.example.ui.Window;

import javax.swing.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Application {
    private static Transmitter transmitter = new Transmitter();
    private static Server server = new Server(transmitter);
    private static Window window;

    public static Window getWindowInstance() {
        if (window == null) {
            window = new Window(server.getBoard(), transmitter);
        }
        return window;
    }

    public static void main(String[] args) {
        Transmitter transmitter = new Transmitter();
        System.out.println("Hello!");
        int val = 0;
        Scanner scanner = new Scanner(System.in);
        while (val != 1 && val != 2) {
            System.out.println("Find game? - 1");
            System.out.println("New game? - 2");
            val = scanner.nextInt();
        }

//        try (server = new Server(transmitter)) {
//        try {
//            final Window window = new Window(server.getBoard(), transmitter);
            SwingUtilities.invokeLater(Application::getWindowInstance);
            switch (val) {
                case 1:
                    server.start(8090, false, getWindowInstance());
                    break;
                case 2:
                    server.start(8090, true, getWindowInstance());
                    break;
            }
            try {
                String[] inet = InetAddress.getLocalHost().toString().split("/");
                String address = inet[inet.length - 1];
                System.out.println(address);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
//        }
    }
}
