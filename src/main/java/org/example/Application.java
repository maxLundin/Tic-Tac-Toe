package org.example;

import org.example.connections.BotConnection;
import org.example.connections.SocketConnection;
import org.example.players.bot.Bot;
import org.example.players.bot.BotPlayer;
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
        while (val != 1 && val != 2 && val != 3) {
            System.out.println("Find game with human? - 1");
            System.out.println("New game with human? - 2");
            System.out.println("New game with bot? - 3");
            val = scanner.nextInt();
        }

        Board board = new Board();
        Window window = new Window(board, transmitter);

        if (val == 3) {
            BotConnection connection = new BotConnection();

            // Тут вставь своих ботов
            Bot bot = null;

            //Возможно нужно поменять true и false местами, надо тестить
            Player botPlayer = new BotPlayer(bot, connection, window.getBoard());
            botPlayer.start(true);
            Player server = new HumanPlayer(new BotConnection(), transmitter, window);
            server.start(false);
        }
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
