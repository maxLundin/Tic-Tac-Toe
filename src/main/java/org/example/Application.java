package org.example;

import org.example.connections.SocketConnection;
import org.example.players.human.HumanPlayer;
import org.example.players.Player;
import org.example.players.human.Transmitter;
import org.example.ui.Board;
import org.example.ui.Window;

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

        Board board = new Board();
        Window window = new Window(board, transmitter);
        try (Player server = new HumanPlayer(new SocketConnection(), transmitter, window)) {
            switch (val) {
                case 1:
                    server.start(false);
                    break;
                case 2:
                    server.start(true);
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
}
