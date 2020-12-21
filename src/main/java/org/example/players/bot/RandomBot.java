package org.example.players.bot;

import org.example.ui.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomBot implements Bot {

    @Override
    public Board.Point decide(Board board) {
        List<Board.Point> possibleMoves = new ArrayList<>();
        for (int row = 0; row < board.size(); row++) {
            for (int col = 0; col < board.size(); col++) {
                if (board.validMove(row, col)) {
                    possibleMoves.add(new Board.Point(row, col));
                }
            }
        }
        Random rand = new Random();
        return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
}
