package org.example;

import org.example.ui.Window;

import javax.swing.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Application {
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
        try (Server server = new Server(transmitter)) {
            switch (val) {
                case 1:
                    server.start(8090, false);
                    break;
                case 2:
                    server.start(8090, true);
                    break;
            }
            try {
                String[] inet = InetAddress.getLocalHost().toString().split("/");
                String address = inet[inet.length - 1];
                System.out.println(address);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            SwingUtilities.invokeLater(() -> new Window(transmitter));
        }
    }
}
