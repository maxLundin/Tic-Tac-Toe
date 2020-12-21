package org.example.players.bot;

import org.example.ui.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BotTest {

    private Board board;

    @BeforeEach
    void before() {
        board = new Board();
    }

    @Test
    void validRandomBotMove() {
        Bot bot = new RandomBot();
        board.move(2, 2);
        board.move(2, 3);
        board.move(3, 3);
        Board.Point move = bot.decide(board);
        assertTrue(board.validMove(move.x, move.y));
    }

    @Test
    void validGoodBotMove() {
        Bot bot = new GoodBot();
        board.move(4, 2);
        board.move(3, 1);
        board.move(3, 3);
        Board.Point move = bot.decide(board);
        assertTrue(board.validMove(move.x, move.y));
    }

    @Test
    void notLoseGoodBot() {
        Bot bot = new GoodBot();
        board.move(2, 2);
        board.move(3, 2);
        board.move(2, 3);
        board.move(3, 3);
        board.move(2, 4);
        Board.Point move = bot.decide(board);
        assertTrue(move.x == 2 && (move.y == 5 || move.y == 1));
    }

    @Test
    void moveToWinGoodBot() {
        Bot bot = new GoodBot();
        board.move(2, 2);
        board.move(3, 2);
        board.move(2, 3);
        board.move(3, 3);
        board.move(2, 4);
        board.move(3, 4);
        Board.Point move = bot.decide(board);
        assertTrue(move.x == 2 && (move.y == 5 || move.y == 1));
    }
}
