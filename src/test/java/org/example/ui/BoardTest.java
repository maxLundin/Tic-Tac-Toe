package org.example.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void before() {
        board = new Board();
    }

    @Test
    void isGameOver1() {
        assertFalse(board.isGameOver());
        board.move(2, 2);
        board.move(2, 3);
        board.move(3, 3);
        board.move(3, 4);
        board.move(4, 4);
        board.move(4, 5);
        board.move(5, 5);
        board.move(5, 6);
        board.move(6, 6);
        board.move(6, 7);
        assertTrue(board.isGameOver());
    }

    @Test
    void isGameOver2() {
        assertFalse(board.isGameOver());
        board.move(2, 2);
        board.move(2, 3);
        board.move(3, 3);
        board.move(3, 4);
        board.move(4, 5);
        board.move(4, 6);
        board.move(5, 5);
        board.move(5, 6);
        board.move(6, 6);
        board.move(6, 7);
        board.move(4, 4);
        board.move(4, 5);
        assertTrue(board.isGameOver());
    }

    @Test
    void isGameOver3() {
        assertFalse(board.isGameOver());
        board.move(2, 2);
        board.move(2, 3);
        board.move(3, 2);
        board.move(3, 3);
        board.move(4, 2);
        board.move(4, 3);
        board.move(5, 2);
        board.move(5, 3);
        board.move(6, 2);
        board.move(6, 3);
        assertTrue(board.isGameOver());

    }

    @Test
    void isGameOver4() {
        assertFalse(board.isGameOver());
        board.move(2, 2);
        board.move(2, 3);
        board.move(3, 2);
        board.move(3, 3);
        board.move(4, 3);
        board.move(4, 4);
        board.move(5, 2);
        board.move(5, 3);
        board.move(6, 2);
        board.move(6, 3);
        board.move(4, 2);
        board.move(4, 3);
        assertTrue(board.isGameOver());
    }

    @Test
    void getTurn() {
        Board board = new Board();
        assertEquals(board.getTurn(), Board.State.X);
        board.move(0, 0);
        assertEquals(board.getTurn(), Board.State.O);
    }

    @Test
    void getWinner() {
        assertFalse(board.isGameOver());
        board.move(2, 2);
        board.move(2, 3);
        board.move(3, 3);
        board.move(3, 4);
        board.move(4, 4);
        board.move(4, 5);
        board.move(5, 5);
        board.move(5, 6);
        board.move(6, 6);
        assertTrue(board.isGameOver());
        assertEquals(board.getWinner(), Board.State.X);
    }

    @Test
    void moveTest1() {
        assertTrue(board.move(5, 5));
        assertFalse(board.move(5, 5));
        assertFalse(board.move(7, 7));
        assertFalse(board.move(8, 8));
        assertTrue(board.move(6, 6));
    }

    @Test
    void moveTest2() {
        assertTrue(board.move(0, 0));
        assertTrue(board.move(2, 2));
        assertTrue(board.move(1, 1));
        assertTrue(board.move(0, 0));
    }
}