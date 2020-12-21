package org.example.players.bot;

import org.example.ui.Board;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class GoodBot implements Bot {

    @Override
    public Board.Point decide(Board board) {
        try {
            FileWriter writer = new FileWriter("src/main/python/occupied_fields.txt");
            for (int row = 0; row < board.size(); row++) {
                for (int col = 0; col < board.size(); col++) {
                    if (board.getCell(row, col) == Board.State.X) {
                        writer.write(Integer.toString(row * 22 + col + 1));
                        writer.write(' ');
                    }
                }
            }
            writer.write('\n');
            for (int row = 0; row < board.size(); row++) {
                for (int col = 0; col < board.size(); col++) {
                    if (board.getCell(row, col) == Board.State.O) {
                        writer.write(Integer.toString(row * 22 + col + 1));
                        writer.write(' ');
                    }
                }
            }
            writer.close();
//            String command = "C:\\Users\\pwx779030\\AppData\\Local\\Microsoft\\WindowsApps\\ubuntu.exe run \"/usr/bin/python2 /mnt/d/Codes/MyCodes/Software-Design-ITMO/Tic-Tac-Toe/src/main/python/make_ai_turn.py\"";
            String command = "python2 src/main/python/make_ai_turn.py";
            final Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            Scanner scanner = new Scanner(new File("src/main/python/answer.txt"));
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            scanner.close();
            return new Board.Point(x, y);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
