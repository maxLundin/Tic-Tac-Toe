package org.example.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Represents the Tic Tac Toe board.
 */
public class Board {

    static final int INIT_BOARD_SIZE = 10;
    static final int DIFF_BOARD_SIZE = 5;
    static final int WIN_VALUE = 5;

    public enum State {Blank, X, O}

    private List<List<State>> board;
    private State playersTurn;
    private State winner;

    private int moveCount;
    private boolean gameOver;

    private void initBoard() {
        board = new ArrayList<>(INIT_BOARD_SIZE);
        for (int row = 0; row < INIT_BOARD_SIZE; row++) {
            board.add(new ArrayList<>(INIT_BOARD_SIZE));
            for (int col = 0; col < INIT_BOARD_SIZE; col++) {
                board.get(row).add(State.Blank);
            }
        }
        moveCount = 0;
        gameOver = false;
        winner = State.Blank;
        playersTurn = State.X;
    }

    /**
     * Construct the Tic Tac Toe board.
     */
    public Board() {
        initBoard();
    }

    public void reset() {
        initBoard();
    }

    public int size() {
        return board.size();
    }

    private void resize() {
        int newSize = board.size() + 2 * DIFF_BOARD_SIZE;
        List<List<State>> boardTmp = new ArrayList<>(newSize);
        for (int row = 0; row < newSize; row++) {
            boardTmp.add(new ArrayList<>(newSize));
            for (int col = 0; col < newSize; col++) {
                boardTmp.get(row).add(State.Blank);
            }
        }
        for (int i = 0; i < board.size(); ++i) {
            for (int j = 0; j < board.size(); ++j) {
                boardTmp.get(i + DIFF_BOARD_SIZE).set(j + DIFF_BOARD_SIZE, board.get(i).get(j));
            }
        }
        board = boardTmp;
    }

    /**
     * Places an X or an O on the specified location depending on who turn it is.
     *
     * @param x the x coordinate of the location
     * @param y the y coordinate of the location
     * @return true if the move has not already been played
     */
    public boolean move(int x, int y) {

        if (gameOver) {
            return false;
        }
        boolean valid = false;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (checkPoint(new Point(x + i, y + j))) {
                    if (board.get(x + i).get(y + j) != State.Blank) {
                        valid = true;
                        break;
                    }
                }
            }
        }
        if (!valid && moveCount != 0) {
            return false;
        }

        if (board.get(x).get(y) == State.Blank) {
            board.get(x).set(y, playersTurn);
        } else {
            return false;
        }

        moveCount++;

        check(new Point(x, y));

        if (x == 0 || y == 0 || (x == board.size() - 1) || (y == board.size() - 1)) {
            resize();
        }

        playersTurn = (playersTurn == State.X) ? State.O : State.X;
        return true;
    }

    /**
     * Check to see if the game is over (if there is a winner or a draw).
     *
     * @return true if the game is over
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Check to see who's turn it is.
     *
     * @return the player who's turn it is
     */
    public State getTurn() {
        return playersTurn;
    }

    /**
     * Check to see who won.
     *
     * @return the player who won (or Blank if the game is a draw)
     */
    public State getWinner() {
        return winner;
    }

    public static class Point {
        public int x, y;

        public Point(int a, int b) {
            this.x = a;
            this.y = b;
        }
    }

    public boolean eq(Point self, Point other) {
        return board.get(self.x).get(self.y) != State.Blank && board.get(self.x).get(self.y) == board.get(other.x).get(other.y);
    }

    private boolean checkPoint(Point point) {
        if (point.x < 0 || point.x >= board.size()) {
            return false;
        }
        if (point.y < 0 || point.y >= board.size()) {
            return false;
        }
        return true;
    }

    private int countEqRange(Point point, Function<Point, Point> next) {
        Point init = point;
        int count = 0;
        while (true) {
            point = next.apply(point);
            if (!checkPoint(point)) {
                break;
            }
            if (!eq(init, point)) {
                break;
            }
            count++;
        }
        return count;
    }

    private boolean checkOne(Point p, Function<Point, Point> prev, Function<Point, Point> next) {
        int countNext = countEqRange(p, next);
        int countPrev = countEqRange(p, prev);
        if (countNext + countPrev + 1 >= WIN_VALUE) {
            winner = board.get(p.x).get(p.y);
            gameOver = true;
            return true;
        }
        return false;
    }

    private void check(Point p) {
        if (checkOne(p, (Point point) -> new Point(point.x - 1, point.y), (Point point) -> new Point(point.x + 1, point.y))) {
            return;
        }
        if (checkOne(p, (Point point) -> new Point(point.x, point.y - 1), (Point point) -> new Point(point.x, point.y + 1))) {
            return;
        }
        if (checkOne(p, (Point point) -> new Point(point.x + 1, point.y - 1), (Point point) -> new Point(point.x - 1, point.y + 1))) {
            return;
        }
        if (checkOne(p, (Point point) -> new Point(point.x - 1, point.y - 1), (Point point) -> new Point(point.x + 1, point.y + 1))) {
            return;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.size(); ++i) {
            for (int j = 0; j < board.size(); ++j) {
                switch (board.get(i).get(j)) {
                    case Blank:
                        sb.append("_");
                        break;
                    case X:
                        sb.append("X");
                        break;
                    case O:
                        sb.append("O");
                        break;
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public State getCell(int x, int y) {
        return board.get(x).get(y);
    }
}
