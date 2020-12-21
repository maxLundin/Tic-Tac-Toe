package org.example.players.bot;

import org.example.connections.Connection;
import org.example.players.Player;
import org.example.ui.Board;

public class BotPlayer extends Player {
    private final Bot bot;

    public BotPlayer(Bot bot, Connection connection, Board board) {
        super(connection, board);
        this.bot = bot;
    }

    public Board getBoard() {
        return board;
    }

    @Override
    protected Board.Point getMove() {
        System.out.println("getPoints:");
        Board.Point move = bot.decide(board);
        board.move(move.x, move.y);
        return move;
    }
}
